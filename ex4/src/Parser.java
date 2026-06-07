import exceptions.ErrorReport;
import exceptions.ErrorReport.ErrorType;
import flowchart.*;

import java.util.*;

/**
 * 负责解析 Oberon 代码，构建符号表，进行类型检查，并生成流程图
 */
public class Parser {

    public OberonScanner scan;
    public Symbols lookahead;

    // 内置类型
    public mySymbol.Type intType   = mySymbol.PrimitiveType.INTEGER;
    public mySymbol.Type boolType  = mySymbol.PrimitiveType.BOOLEAN;
    public mySymbol.Type errorType = mySymbol.PrimitiveType.ERROR;

    /** 顶层 Module，对应整个 Oberon 模块 */
    private flowchart.Module flowModule;

    /** 当前正在填充语句的 Procedure */
    private Procedure currentProc;

    /**
     * 语句序列栈：栈顶是当前活跃的 StatementSequence
     * 栈为空时直接向 currentProc 追加。
     */
    private final Deque<StatementSequence> seqStack = new ArrayDeque<>();

    /**
     * 构造函数：初始化符号表，预定义内置类型和过程
     * @param scan 词法分析器
     * @throws Exception
     */
    public Parser(OberonScanner scan) throws Exception {
        this.scan = scan;

        mySymbol.Env.reset();
        mySymbol.Env.addSymbol_s("INTEGER", intType);
        mySymbol.Env.addSymbol_s("BOOLEAN", boolType);

        mySymbol.Parameter readParam  = new mySymbol.Parameter("value", intType, true);
        mySymbol.Parameter writeParam = new mySymbol.Parameter("value", intType, false);
        mySymbol.Env.addSymbol_s("WRITE",   new mySymbol.FormalParameters(writeParam));
        mySymbol.Env.addSymbol_s("READ",    new mySymbol.FormalParameters(readParam));
        mySymbol.Env.addSymbol_s("WRITELN", new mySymbol.FormalParameters());
    }

    /**
     * 添加语句到当前活跃的 StatementSequence 或 Procedure
     * @param stmt 要添加的语句
     */
    private void addStmt(AbstractStatement stmt) {
        if (seqStack.isEmpty()) {
            currentProc.add(stmt);
        } else {
            seqStack.peek().add(stmt);
        }
    }

    /**
     * 解析开始
     * @return 解析成功返回 true
     * @throws Exception
     */
    public boolean module(boolean isShow) throws Exception {
        lookahead = get_token();                  // MODULE
        lookahead = get_token();                  // 模块名
        String name1 = lookahead.getValue();

        // 创建流程图 Module
        flowModule = new flowchart.Module(name1);

        mySymbol.Env.enterEnv(name1);

        lookahead = get_token();                  // SEMI
        lookahead = get_token();                  // 进入 declarations

        declarations();

        if (lookahead.getToken() == Token.BEGIN) {
            // 模块主体：作为名为 "Main" 的 Procedure
            currentProc = flowModule.add("Main");
            lookahead = get_token();              // 跳过 BEGIN
            statement_sequence();
        }

        // END 模块名
        lookahead = get_token();
        String name2 = lookahead.getValue();
        if (!name1.equals(name2)) {
            ErrorReport.reportError(ErrorType.SyntacticException, lookahead.getLine(),
                    "Module name mismatch: expected " + name1 + " but found " + name2);
        }
        mySymbol.Env.exitEnv();
        lookahead = get_token();                  // DOT

        if(isShow) flowModule.show();
        return true;
    }

    /**
     * 声明模块，已经预读了一个 token
     * @throws Exception
     */
    public void declarations() throws Exception {
        const_part();
        type_part();
        var_part();
        procedure_part();
    }

    /**
     * CONST 声明部分，已经预读了一个 token
     * @throws Exception
     */
    public void const_part() throws Exception {
        if (lookahead.getToken() != Token.CONST) return;
        lookahead = get_token();                  // Identifier
        while (lookahead.getToken() == Token.IDENTIFIER) {
            String name = lookahead.getValue();
            lookahead = get_token();              // EQ
            lookahead = get_token();              // expression
            mySymbol.Expression e = expression();
            mySymbol.Env.addSymbol_s(name, new mySymbol.Const(name, e.getType()));
            if (lookahead.getToken() == Token.SEMI) lookahead = get_token();
        }
    }

    /**
     * TYPE 声明部分，已经预读了一个 token
     * @throws Exception
     */
    public void type_part() throws Exception {
        if (lookahead.getToken() != Token.TYPE) return;
        lookahead = get_token();
        while (lookahead.getToken() == Token.IDENTIFIER) {
            String name = lookahead.getValue();
            lookahead = get_token();              // EQ
            lookahead = get_token();              // 跳过 EQ
            mySymbol.Type t = type();
            mySymbol.Env.addSymbol_s(name, new mySymbol.AliasType(name, t));
            if (lookahead.getToken() == Token.SEMI) lookahead = get_token();
        }
    }

    /**
     * VAR 声明部分，已经预读了一个 token
     * @throws Exception
     */
    public void var_part() throws Exception {
        if (lookahead.getToken() != Token.VAR) return;
        lookahead = get_token();
        while (lookahead.getToken() == Token.IDENTIFIER) {
            List<String> ids = identifier_list();
            lookahead = get_token();              // 跳过 COLON
            mySymbol.Type t = type();
            for (String id : ids)
                mySymbol.Env.addSymbol_s(id, new mySymbol.Var(id, t));
            if (lookahead.getToken() == Token.SEMI) lookahead = get_token();
        }
    }

    /**
     * PROCEDURE 声明部分，已经预读了一个 token
     * @throws Exception
     */
    public void procedure_part() throws Exception {
        while (lookahead.getToken() == Token.PROCEDURE) {
            procedure_declaration();
            if (lookahead.getToken() == Token.SEMI) {
                lookahead = get_token();
            } else {
                ErrorReport.reportError(ErrorType.SyntacticException, lookahead.getLine(),
                        "Missing semicolon after procedure declaration");
            }
        }
    }

    /**
     * 过程声明，已经预读了 PROCEDURE token
     * @throws Exception
     */
    public void procedure_declaration() throws Exception {
        procedure_heading();
        if (lookahead.getToken() == Token.SEMI) lookahead = get_token();
        procedure_body();
    }

    /**
     * 过程头部，已经预读了 PROCEDURE token
     * @throws Exception
     */
    public void procedure_heading() throws Exception {
        lookahead = get_token();                  // 过程名
        String name = lookahead.getValue();
        lookahead = get_token();                  // LPAREN 或 SEMI

        mySymbol.FormalParameters fp;
        if (lookahead.getToken() == Token.LPAREN) {
            fp = formal_parameters(true); // 跳过 LPAREN
        } else if(lookahead.getToken() == Token.SEMI) {
            fp = new mySymbol.FormalParameters();
        } else {
            ErrorReport.reportError(ErrorType.MissingLeftParenthesisException, lookahead.getLine(),
                    "Expected '(' in parameter list");
            if(lookahead.getToken() == Token.IDENTIFIER || lookahead.getToken() == Token.VAR) { // fp_section_list 的起始 token
                fp = formal_parameters(false);
            }
            else if(lookahead.getToken() == Token.RPAREN) { // 直接缺失参数列表
                lookahead = get_token(); // 跳过 RPAREN
                fp = new mySymbol.FormalParameters();
            }
            else  fp = new mySymbol.FormalParameters();
        }

        mySymbol.Env.addSymbol_s(name, fp);
        mySymbol.Env.enterEnv(name);
        for (mySymbol.Parameter param : fp.getParameterList())
            mySymbol.Env.addSymbol_s(param.getName(),
                    new mySymbol.Var(param.getName(), param.getType()));

        // 在流程图 Module 中为该过程创建 Procedure 节点
        currentProc = flowModule.add(name);
    }
    
    /**
     * 过程体，已经预读了SEMI后的第一个token
     * @throws Exception
     */
    public void procedure_body() throws Exception {
        Procedure savedProc = currentProc;
        Deque<StatementSequence> savedStack = new ArrayDeque<>(seqStack);
        seqStack.clear();

        declarations();

        if (lookahead.getToken() == Token.BEGIN) {
            lookahead = get_token();
            statement_sequence();
        }

        // END 过程名
        lookahead = get_token();
        String endName = lookahead.getValue();
        mySymbol.Env currentEnv = mySymbol.Env.getCurrentEnv();
        if (!currentEnv.getScopeName().equals(endName)) {
            ErrorReport.reportError(ErrorType.SyntacticException, lookahead.getLine(),
                    "Procedure name mismatch, expected "
                            + currentEnv.getScopeName() + " but found " + endName);
        }
        mySymbol.Env.exitEnv();
        lookahead = get_token();                  // 移过过程名，指向 SEMI

        // 恢复外层状态
        seqStack.clear();
        seqStack.addAll(savedStack);
        currentProc = savedProc;
    }

    /**
     * 形式参数列表，已经预读了 LPAREN token
     * @param passLparen 是否跳过 LPAREN token（根据调用情况决定）  
     * @return 解析得到的形式参数列表对象，出错时返回空列表
     * @throws Exception
     */
    public mySymbol.FormalParameters formal_parameters(boolean passLparen) throws Exception {
        if( passLparen) lookahead = get_token(); // 根据调用情况决定是否跳过 LPAREN
        mySymbol.FormalParameters fp = new mySymbol.FormalParameters();
        if (lookahead.getToken() != Token.RPAREN) {
            for (mySymbol.FpSection sec : fp_section_list()) fp.addFpSection(sec);
        }
        if (lookahead.getToken() == Token.RPAREN) {
            lookahead = get_token();
        } else {
            ErrorReport.reportError(ErrorType.MissingRightParenthesisException,
                    lookahead.getLine(), "in formal parameter list");
        }
        return fp;
    }

    public List<mySymbol.FpSection> fp_section_list() throws Exception {
        List<mySymbol.FpSection> list = new ArrayList<>();
        list.add(fp_section());
        while (lookahead.getToken() == Token.SEMI) {
            lookahead = get_token();
            list.add(fp_section());
        }
        return list;
    }

    public mySymbol.FpSection fp_section() throws Exception {
        boolean isVar = false;
        if (lookahead.getToken() == Token.VAR) { isVar = true; lookahead = get_token(); }
        List<String> ids = identifier_list();
        lookahead = get_token();                  // 跳过 COLON
        mySymbol.Type t = type();
        return new mySymbol.FpSection(ids, t, isVar);
    }

    /**
     * 类型解析，已经预读了一个 token
     * @return 解析得到的类型对象，出错时返回 errorType
     * @throws Exception
     */
    public mySymbol.Type type() throws Exception {
        if (lookahead.getToken() == Token.IDENTIFIER) {
            String typeName = lookahead.getValue();
            int line = lookahead.getLine();
            lookahead = get_token();
            mySymbol.TableSymbol sym = mySymbol.Env.getCurrentEnv().lookup(typeName);
            if (sym == null) {
                ErrorReport.reportError(ErrorType.SemanticException, line,
                        "Undefined type \"" + typeName + "\"");
                return errorType;
            } else if (sym instanceof mySymbol.Type) {
                return (mySymbol.Type) sym;
            } else {
                ErrorReport.reportError(ErrorType.SemanticException, line,
                        "Identifier \"" + typeName + "\" does not denote a type");
                return errorType;
            }
        } else if (lookahead.getToken() == Token.ARRAY) {
            return array_type();
        } else if (lookahead.getToken() == Token.RECORD) {
            return record_type();
        } else {
            ErrorReport.reportError(ErrorType.SyntacticException, lookahead.getLine(),
                    "Expected a type, found: " + lookahead.getValue());
            lookahead = get_token();
            return errorType;
        }
    }

    public mySymbol.ArrayType array_type() throws Exception {
        lookahead = get_token();                  // 跳过 ARRAY
        expression();                             // 数组长度表达式
        lookahead = get_token();                  // 跳过 OF
        return new mySymbol.ArrayType(type());
    }

    public mySymbol.RecordType record_type() throws Exception {
        mySymbol.RecordType rt = new mySymbol.RecordType();
        lookahead = get_token();                  // Identifier
        mySymbol.FpSection fl = field_list();
        if (fl != null) rt.addField(fl);
        while (lookahead.getToken() == Token.SEMI) {
            lookahead = get_token();
            mySymbol.FpSection flNext = field_list();
            if (flNext != null) rt.addField(flNext);
        }
        if (lookahead.getToken() == Token.END) lookahead = get_token();
        return rt;
    }

    public mySymbol.FpSection field_list() throws Exception {
        if (lookahead.getToken() != Token.IDENTIFIER) return null;
        List<String> ids = identifier_list();
        lookahead = get_token();                  // 跳过 COLON
        return new mySymbol.FpSection(ids, type(), false);
    }

    /**
     * 标识符列表(id1,id2)，已经预读了一个 token
     * @return 解析得到的标识符列表，出错时返回空列表
     * @throws Exception
     */
    public List<String> identifier_list() throws Exception {
        List<String> ids = new ArrayList<>();
        if (lookahead.getToken() != Token.IDENTIFIER) return ids;
        ids.add(lookahead.getValue());
        lookahead = get_token();
        while (lookahead.getToken() == Token.COMMA) {
            lookahead = get_token();
            if (lookahead.getToken() == Token.IDENTIFIER) {
                ids.add(lookahead.getValue());
                lookahead = get_token();
            }
        }
        return ids;
    }

    /**
     * 语句序列，已经预读了一个 token
     * @throws Exception
     */
    public void statement_sequence() throws Exception {
        statement();
        while (lookahead.getToken() == Token.SEMI) {
            lookahead = get_token();
            statement();
        }
    }

    /**
     * 语句，已经预读了一个 token
     * @throws Exception
     */
    public void statement() throws Exception {
        int tok = lookahead.getToken();

        if (tok == Token.IDENTIFIER) {
            String name = lookahead.getValue();
            int    line = lookahead.getLine();
            lookahead = get_token();

            if (lookahead.getToken() == Token.ASSIGN
                    || lookahead.getToken() == Token.DOT
                    || lookahead.getToken() == Token.LBRACK) {
                assignment(name, line);
            } else {
                procedure_call(name, line);
            }
        } else if (tok == Token.IF) {
            if_statement();
        } else if (tok == Token.WHILE) {
            while_statement();
        } 
    }

    /**
     * 赋值语句，已经预读了标识符 token
     * @param name 标识符名称
     * @param line 标识符所在行号，用于错误报告
     * @throws Exception
     */
    private void assignment(String name, int line) throws Exception {
        mySymbol.Env currentEnv = mySymbol.Env.getCurrentEnv();
        mySymbol.TableSymbol sym = currentEnv.lookup(name);
        mySymbol.Type currentType;
        boolean isLValue = false;

        if (sym == null) {
            ErrorReport.reportError(ErrorType.SemanticException, line,
                    "Undefined identifier \"" + name + "\"");
            currentType = errorType;
        } else if (sym.getKind().equals("VAR")) {
            currentType = ((mySymbol.Var) sym).getType();
            isLValue = true;
        } else {
            ErrorReport.reportError(ErrorType.SemanticException, line,
                    "Identifier \"" + name + "\" is not a variable");
            currentType = errorType;
        }

        mySymbol.Selector sel = selectorWithCode(currentType);
        mySymbol.Expression lhsExpr = applySelector(name, currentType, sel, line, isLValue);
        currentType = lhsExpr.getType();
        String lhsCode = lhsExpr.getCode();

        if (!isLValue) {
            ErrorReport.reportError(ErrorType.SemanticException, line,
                    "Left-hand side of assignment must be a variable");
        }

        if (lookahead.getToken() == Token.ASSIGN) {
            lookahead = get_token();
        } else {
            ErrorReport.reportError(ErrorType.SyntacticException, lookahead.getLine(),
                    "Missing expression in assignment");
            return;
        }

        mySymbol.Expression rhs = expression();
        if (currentType != errorType && rhs.getType() != errorType) {
            if (!currentType.getTargetType().equals(rhs.getType().getTargetType())) {
                ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                        "in assignment, cannot assign " + rhs.getType().getTypeName()
                                + " to " + currentType.getTypeName());
            }
        }

        // 生成流程图语句：lhs := rhs
        addStmt(new PrimitiveStatement(lhsCode + " := " + rhs.getCode()));
    }

    /**
     * 过程调用语句，已经预读了标识符和左括号
     * @param name 过程名
     * @param line 过程名所在行号，用于错误报告
     * @throws Exception
     */
    private void procedure_call(String name, int line) throws Exception {
        List<mySymbol.Expression> ap = actual_parameters_opt(name, line);

        // 类型检查
        mySymbol.Env calleeEnv = mySymbol.Env.getCurrentEnv();
        while (calleeEnv != null) {
            mySymbol.TableSymbol s = calleeEnv.lookupLocal(name);
            if (s != null && s.getKind().equals("FORMALPARAMETERS")) {
                mySymbol.FormalParameters fp = (mySymbol.FormalParameters) s;
                String errorString = fp.checkTypesWithMessage(ap);
                if (errorString != null) {
                    if (errorString.split("\\s+")[0].equals("expected"))
                    {
                        if(name.equals("READ") || name.equals("WRITE") || name.equals("WRITELN"))
                            ErrorReport.reportError(ErrorType.MissingOperatorException, line, errorString);
                        else
                            ErrorReport.reportError(ErrorType.ParameterMismatchedException, line, errorString);
                    }
                    else
                        ErrorReport.reportError(ErrorType.TypeMismatchedException, line, errorString);
                }
                break;
            }
            calleeEnv = calleeEnv.getFather();
        }
        if (calleeEnv == null) {
            ErrorReport.reportError(ErrorType.SemanticException, line,
                    "Undefined procedure \"" + name + "\"");
        }

        // 生成流程图语句：name(arg1, arg2, ...)
        StringBuilder code = new StringBuilder(name + "(");
        if (ap != null) {
            for (int i = 0; i < ap.size(); i++) {
                if (i > 0) code.append(", ");
                code.append(ap.get(i).getCode());
            }
        }
        code.append(")");
        addStmt(new PrimitiveStatement(code.toString()));
    }

    /**
     * 实际参数列表，已经预读了 LPAREN 或其他 token
     * @param name 调用的过程名
     * @param line 调用所在行号（用于错误报告）
     * @return
     * @throws Exception
     */
    private List<mySymbol.Expression> actual_parameters_opt(String name, int line) throws Exception {
        if(lookahead.getToken() == Token.SEMI || lookahead.getToken() == Token.END
                || lookahead.getToken() == Token.ELSIF || lookahead.getToken() == Token.ELSE) {
            return new ArrayList<>();
        }
        if(lookahead.getToken() != Token.LPAREN) {
            ErrorReport.reportError(ErrorType.MissingLeftParenthesisException, lookahead.getLine(),
                    "Expected '(' before actual parameters in call to \"" + name + "\"");
        }
        else lookahead = get_token(); // 跳过 LPAREN
        List<mySymbol.Expression> el = expression_list_opt();
        if (lookahead.getToken() == Token.RPAREN) {
            lookahead = get_token();
        } else {
            ErrorReport.reportError(ErrorType.MissingRightParenthesisException,
                    lookahead.getLine(), "in procedure call");
        }
        return el;
    }

    private List<mySymbol.Expression> expression_list_opt() throws Exception {
        List<mySymbol.Expression> list = new ArrayList<>();
        if (lookahead.getToken() == Token.RPAREN) return list;
        list.add(expression());
        while (lookahead.getToken() == Token.COMMA) {
            lookahead = get_token();
            list.add(expression());
        }
        return list;
    }

    /**
     * IF 语句，已经预读了 IF token
     * @throws Exception
     */
    private void if_statement() throws Exception {
        lookahead = get_token();                  // 跳过 IF
        mySymbol.Expression cond = expression();
        if (!cond.getType().getTargetType().equals(boolType)) {
            ErrorReport.reportError(ErrorType.TypeMismatchedException, lookahead.getLine(),
                    "Condition expression in IF statement must be BOOLEAN");
        }

        IfStatement ifStmt = new IfStatement(cond.getCode());
        addStmt(ifStmt);

        // THEN 分支
        if (lookahead.getToken() == Token.THEN) lookahead = get_token();
        seqStack.push(ifStmt.getTrueBody());
        statement_sequence();
        seqStack.pop();

        // ELSIF
        IfStatement current = ifStmt;
        while (lookahead.getToken() == Token.ELSIF) {
            lookahead = get_token();
            mySymbol.Expression elsifCond = expression();
            if (!elsifCond.getType().getTargetType().equals(boolType)) {
                ErrorReport.reportError(ErrorType.TypeMismatchedException, lookahead.getLine(),
                        "Condition expression in ELSIF clause must be BOOLEAN");
            }
            IfStatement elsifStmt = new IfStatement(elsifCond.getCode());
            current.getFalseBody().add(elsifStmt);      // 放入上一级 falseBody

            if (lookahead.getToken() == Token.THEN) lookahead = get_token();
            seqStack.push(elsifStmt.getTrueBody());
            statement_sequence();
            seqStack.pop();
            current = elsifStmt;
        }

        // ELSE 
        if (lookahead.getToken() == Token.ELSE) {
            lookahead = get_token();
            seqStack.push(current.getFalseBody());
            statement_sequence();
            seqStack.pop();
        }

        if (lookahead.getToken() == Token.END) lookahead = get_token();
    }

    /**
     * WHILE 语句，已经预读了 WHILE token
     * @throws Exception
     */
    private void while_statement() throws Exception {
        lookahead = get_token();                  // 跳过 WHILE
        mySymbol.Expression cond = expression();
        if (!cond.getType().getTargetType().equals(boolType)) {
            ErrorReport.reportError(ErrorType.TypeMismatchedException, lookahead.getLine(),
                    "Condition expression in WHILE statement must be BOOLEAN");
        }

        WhileStatement whileStmt = new WhileStatement(cond.getCode());
        addStmt(whileStmt);

        if (lookahead.getToken() == Token.DO) lookahead = get_token();
        seqStack.push(whileStmt.getLoopBody());
        statement_sequence();
        seqStack.pop();

        if (lookahead.getToken() == Token.END) lookahead = get_token();
    }

    /**
     * 表达式，已经预读了一个 token
     * @return 解析得到的表达式对象，出错时返回类型为 errorType 的表达式
     * @throws Exception
     */
    public mySymbol.Expression expression() throws Exception {
        mySymbol.Expression left = simple_expression();
        int t = lookahead.getToken();
        if (t == Token.EQ || t == Token.NEQ || t == Token.LT ||
                t == Token.LEQ || t == Token.GT || t == Token.GEQ) {
            int line = lookahead.getLine();
            String op = tokenToOp(t);
            lookahead = get_token();
            if (lookahead.getToken() == Token.END  || lookahead.getToken() == Token.THEN || lookahead.getToken() == Token.OF
                    || lookahead.getToken() == Token.DO || lookahead.getToken() == Token.SEMI || lookahead.getToken() == Token.RBRACK
                    || lookahead.getToken() == Token.RPAREN || lookahead.getToken() == Token.COMMA || lookahead.getToken() == Token.ELSE || lookahead.getToken() == Token.ELSIF) {
                ErrorReport.reportError(ErrorType.MissingOperandException, line, "Missing right operand for '" + op + "'");
                return new mySymbol.Expression("ERROR", errorType);
            }
            mySymbol.Expression right = simple_expression();
            if (left.getType().getTargetType().equals(intType)
                    && right.getType().getTargetType().equals(intType)) {
                return new mySymbol.Expression(
                        left.getCode() + " " + op + " " + right.getCode(), boolType);
            } else {
                ErrorReport.reportError(ErrorType.TypeMismatchedException, line, "Type mismatch in '" + op + "': expected INTEGER");
                return new mySymbol.Expression("ERROR", errorType);
            }
        }
        else if(t == Token.IDENTIFIER || t == Token.INTEGER || t == Token.LPAREN || t == Token.MINUS || t == Token.NOT || t == Token.PLUS) {
            ErrorReport.reportError(ErrorType.MissingOperatorException, lookahead.getLine(),
                    "Missing operator between expressions");
            simple_expression(); 
        }
        return left;
    }

    private mySymbol.Expression simple_expression() throws Exception {
        if (lookahead.getToken() == Token.PLUS) {
            int line = lookahead.getLine(); lookahead = get_token();
            mySymbol.Expression e = term();
            if (!e.getType().getTargetType().equals(intType)) {
                ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                        "Unary '+' requires INTEGER operand");
                return new mySymbol.Expression("ERROR", errorType);
            }
            return new mySymbol.Expression("+" + e.getCode(), intType);
        }
        if (lookahead.getToken() == Token.MINUS) {
            int line = lookahead.getLine(); lookahead = get_token();
            mySymbol.Expression e = term();
            if (!e.getType().getTargetType().equals(intType)) {
                ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                        "Unary '-' requires INTEGER operand");
                return new mySymbol.Expression("ERROR", errorType);
            }
            return new mySymbol.Expression("-" + e.getCode(), intType);
        }

        mySymbol.Expression left = term();
        int t = lookahead.getToken();
        while (t == Token.PLUS || t == Token.MINUS || t == Token.OR
                || t == Token.IDENTIFIER || t == Token.INTEGER || t == Token.LPAREN || t == Token.NOT) {
            if(t==Token.IDENTIFIER || t==Token.INTEGER || t==Token.LPAREN || t==Token.NOT) {
                ErrorReport.reportError(ErrorType.MissingOperatorException, lookahead.getLine(),
                        "Missing operator in " + left.getCode());
                term(); t = lookahead.getToken(); continue;
            }
            int line = lookahead.getLine();
            String op = tokenToOp(t);
            lookahead = get_token();
            mySymbol.Expression right = term();
            if (t == Token.OR) {
                if (left.getType().getTargetType().equals(boolType)
                        && right.getType().getTargetType().equals(boolType)) {
                    left = new mySymbol.Expression(
                            left.getCode() + " OR " + right.getCode(), boolType);
                } else {
                    ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                            "Type mismatch in 'OR': expected BOOLEAN");
                    left = new mySymbol.Expression("ERROR", errorType);
                }
            } else {
                if (left.getType().getTargetType().equals(intType)
                        && right.getType().getTargetType().equals(intType)) {
                    left = new mySymbol.Expression(
                            left.getCode() + " " + op + " " + right.getCode(), intType);
                } else {
                    ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                            "Type mismatch in '" + op + "': expected INTEGER");
                    left = new mySymbol.Expression("ERROR", errorType);
                }
            }
            t = lookahead.getToken();
        }
        return left;
    }

    private mySymbol.Expression term() throws Exception {
        mySymbol.Expression left = factor();
        while (lookahead.getToken() == Token.TIMES
                || lookahead.getToken() == Token.DIV
                || lookahead.getToken() == Token.MOD
                || lookahead.getToken() == Token.AND) {
            int t = lookahead.getToken();
            int line = lookahead.getLine();
            String op = tokenToOp(t);
            lookahead = get_token();
            mySymbol.Expression right = factor();
            if (t == Token.AND) {
                if (left.getType().getTargetType().equals(boolType)
                        && right.getType().getTargetType().equals(boolType)) {
                    left = new mySymbol.Expression(
                            left.getCode() + " & " + right.getCode(), boolType);
                } else {
                    ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                            "Type mismatch in 'AND': expected BOOLEAN");
                    left = new mySymbol.Expression("ERROR", errorType);
                }
            } else {
                if (left.getType().getTargetType().equals(intType)
                        && right.getType().getTargetType().equals(intType)) {
                    left = new mySymbol.Expression(
                            left.getCode() + " " + op + " " + right.getCode(), intType);
                } else {
                    ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                            "Type mismatch in '" + op + "': expected INTEGER");
                    left = new mySymbol.Expression("ERROR", errorType);
                }
            }
        }
        return left;
    }

    private mySymbol.Expression factor() throws Exception {
        int tok  = lookahead.getToken();
        int line = lookahead.getLine();

        if (tok == Token.INTEGER) {
            String val = lookahead.getValue();
            lookahead = get_token();
            return new mySymbol.Expression(val, intType);
        }
        if (tok == Token.NOT) {
            lookahead = get_token();
            mySymbol.Expression e = factor();
            if (e.getType().getTargetType().equals(boolType))
                return new mySymbol.Expression("NOT " + e.getCode(), boolType);
            ErrorReport.reportError(ErrorType.TypeMismatchedException, line,
                    "Type mismatch in 'NOT': expected BOOLEAN");
            return new mySymbol.Expression("ERROR", errorType);
        }
        if (tok == Token.LPAREN) {
            lookahead = get_token();
            if (lookahead.getToken() == Token.RPAREN) {
                ErrorReport.reportError(ErrorType.MissingOperandException, line, "Empty parentheses");
                lookahead = get_token();
                return new mySymbol.Expression("ERROR", errorType);
            }
            mySymbol.Expression e = expression();
            if (lookahead.getToken() == Token.RPAREN) lookahead = get_token();
            else ErrorReport.reportError(ErrorType.MissingRightParenthesisException,
                    lookahead.getLine(), "Missing right parenthesis");
            return new mySymbol.Expression("( " + e.getCode() + " )", e.getType());
        }
        if (tok == Token.IDENTIFIER) {
            return identifier_selector_expr();
        }
        if (isOperator(tok)) {
            ErrorReport.reportError(ErrorType.MissingOperandException, line,
                    "Missing operand before operator '" + lookahead.getValue() + "'");
            lookahead = get_token();
            return new mySymbol.Expression("ERROR", errorType);
        }
        ErrorReport.reportError(ErrorType.MissingOperandException, line,
                "Unexpected token: " + lookahead.getValue());
        lookahead = get_token();
        return new mySymbol.Expression("ERROR", errorType);
    }

    /**
     * 标识符选择器表达式，已经预读了标识符 token
     * @return 解析得到的表达式对象，出错时返回类型为 errorType 的表达式
     * @throws Exception
     */
    private mySymbol.Expression identifier_selector_expr() throws Exception {
        String id   = lookahead.getValue();
        int    line = lookahead.getLine();
        lookahead = get_token();

        mySymbol.TableSymbol sym = mySymbol.Env.getCurrentEnv().lookup(id);
        mySymbol.Type currentType;
        boolean isLValue = false;

        if (sym == null) {
            ErrorReport.reportError(ErrorType.SemanticException, line,
                    "Undefined identifier \"" + id + "\"");
            return new mySymbol.Expression(id, errorType);
        } else if (sym.getKind().equals("VAR")) {
            currentType = ((mySymbol.Var) sym).getType();
            isLValue = true;
        } else if (sym.getKind().equals("CONST")) {
            currentType = ((mySymbol.Const) sym).getType();
        } else {
            ErrorReport.reportError(ErrorType.SemanticException, line,
                    "Identifier \"" + id + "\" is not a variable or constant");
            return new mySymbol.Expression(id, errorType);
        }

        mySymbol.Selector sel = selectorWithCode(currentType);
        return applySelector(id, currentType, sel, line, isLValue);
    }

    /**
     * 解析选择器链并构建对应的代码字符串，已经预读了一个 token
     * @param baseType 选择器链的基类型（即标识符的类型，继承属性）
     * @return 解析得到的选择器对象，包含选择器链的信息
     * @throws Exception
     */
    private mySymbol.Selector selectorWithCode(mySymbol.Type baseType) throws Exception {
        mySymbol.Selector sel = new mySymbol.Selector();
        while (lookahead.getToken() == Token.DOT
                || lookahead.getToken() == Token.LBRACK) {
            if (lookahead.getToken() == Token.DOT) {
                lookahead = get_token();
                String field = lookahead.getValue();
                sel.addFieldSelector(field);
                lookahead = get_token();
            } else {
                lookahead = get_token();
                mySymbol.Expression idx = expression();
                if(!idx.getType().getTargetType().equals(intType)) {
                    ErrorReport.reportError(ErrorType.TypeMismatchedException, lookahead.getLine(),
                            "Array index must be of type INTEGER");
                }
                sel.addIndexSelector(idx.getCode());
                if (lookahead.getToken() == Token.RBRACK) lookahead = get_token();
            }
        }
        return sel;
    }

    /**
     * 根据选择器链解析出最终的类型，已经预读了一个 token
     * @param idName 标识符名称，用于错误报告和代码生成
     * @param t 选择器链的基类型（即标识符的类型）
     * @param sel 选择器链对象，包含选择器链的信息
     * @param line 选择器链所在行号，用于错误报告
     * @param isLValue 标识符是否为左值（变量），用于错误报告和代码生成
     * @return 
     */
    private mySymbol.Expression applySelector(String idName, mySymbol.Type t, mySymbol.Selector sel, int line, boolean isLValue) {
        if (sel == null || sel.isEmpty()) return new mySymbol.Expression(idName, t, isLValue);
        mySymbol.Type current = t; String code = idName;
        for (mySymbol.Selector.SelectorNode node : sel.getNodes()) {
            if (current instanceof mySymbol.AliasType)
                current = ((mySymbol.AliasType) current).getTargetType();
            if (node.isFieldSelector()) {
                if (!(current instanceof mySymbol.RecordType)) {
                    ErrorReport.reportError(ErrorType.SemanticException, line,
                            "Field selector applied to non-record type");
                    current = errorType; break;
                }
                mySymbol.Parameter f = ((mySymbol.RecordType) current).getField(node.getFieldName());
                if (f == null) {
                    ErrorReport.reportError(ErrorType.SemanticException, line,
                            "Undefined field \"" + node.getFieldName() + "\"");
                    current = errorType; break;
                }
                current = f.getType();
            } else {
                if (!(current instanceof mySymbol.ArrayType)) {
                    ErrorReport.reportError(ErrorType.SemanticException, line,
                            "Index selector applied to non-array type");
                    current = errorType; break;
                }
                current = ((mySymbol.ArrayType) current).getElementType();
            }
        }
        code += sel.getCode();
        return new mySymbol.Expression(code, current, isLValue);
    }

    /**
     * 将 token 转换为对应的操作符字符串（用于生成代码）
     * @param t token
     * @return 对应的操作符字符串
     */
    private String tokenToOp(int t) {
        switch (t) {
            case Token.EQ:    return "=";
            case Token.NEQ:   return "!=";
            case Token.LT:    return "&lt";
            case Token.LEQ:   return "&lt=";
            case Token.GT:    return "&gt";
            case Token.GEQ:   return "&gt=";
            case Token.PLUS:  return "+";
            case Token.MINUS: return "-";
            case Token.TIMES: return "*";
            case Token.DIV:   return "DIV";
            case Token.MOD:   return "MOD";
            case Token.AND:   return "&";
            case Token.OR:    return "OR";
            default:          return "?";
        }
    }

    private boolean isOperator(int t) {
        return t == Token.EQ  || t == Token.NEQ || t == Token.LT  || t == Token.LEQ
            || t == Token.GT  || t == Token.GEQ || t == Token.PLUS || t == Token.MINUS
            || t == Token.TIMES || t == Token.DIV || t == Token.MOD
            || t == Token.AND || t == Token.OR  || t == Token.NOT;
    }

    public Symbols get_token() throws Exception {
        Symbols tok = scan.yylex();
        return tok;
    }

}