package exceptions;

/**
 * 错误报告类，用于统一处理词法、语法和语义错误
 */
public class ErrorReport {
    /**
     * 错误类型枚举，包含词法错误、语法错误和语义错误的具体类型
     */
    public static enum ErrorType {
        LexicalException,                   // 词法错误
        IllegalSymbolException,             // 非法符号异常
        IllegalIntegerException,            // 非法整数异常
        IllegalIntegerRangeException,       // 整数范围异常
        IllegalOctalException,              // 非法八进制数异常   
        IllegalIdentifierLengthException,   // 非法标识符长度异常
        MismatchedCommentException,         // 注释不匹配异常

        SyntacticException,                 // 语法错误
        MissingLeftParenthesisException,    // 缺少左括号异常
        MissingRightParenthesisException,   // 缺少右括号异常
        MissingOperatorException,           // 缺少运算符异常
        MissingOperandException,            // 缺少操作数异常

        SemanticException,                  // 语义错误
        TypeMismatchedException,            // 类型不匹配异常
        ParameterMismatchedException        // 参数数量不匹配异常
    }

    /**
     * 报告错误的方法，根据错误类型、行号和错误信息输出相应的错误消息
     * @param type 错误类型
     * @param line 错误所在行号
     * @param message 错误信息
     */
    public static void reportError(ErrorType type, int line, String message) {
        switch(type) {
            case LexicalException:
                System.err.println("LexicalException at line " + line + ": " + message);
                break;
            case IllegalSymbolException:
                System.err.println("IllegalSymbolException at line " + line + ": " + message);
                break;
            case IllegalIntegerException:
                System.err.println("IllegalIntegerException at line " + line + ": " + message);
                break;
            case IllegalIntegerRangeException:
                System.err.println("IllegalIntegerRangeException at line " + line + ": " + message);
                break;
            case IllegalOctalException:
                System.err.println("IllegalOctalException at line " + line + ": " + message);
                break;
            case IllegalIdentifierLengthException:
                System.err.println("IllegalIdentifierLengthException at line " + line + ": " + message);
                break;
            case MismatchedCommentException:
                System.err.println("MismatchedCommentException at line " + line + ": " + message);
                break;
            case SyntacticException:
                System.err.println("SyntacticException at line " + line + ": " + message);
                break;
            case MissingLeftParenthesisException:
                System.err.println("MissingLeftParenthesisException at line " + line + ": " + message);
                break;
            case MissingRightParenthesisException:
                System.err.println("MissingRightParenthesisException at line " + line + ": " + message);
                break;
            case MissingOperatorException:
                System.err.println("MissingOperatorException at line " + line + ": " + message);
                break;
            case MissingOperandException:
                System.err.println("MissingOperandException at line " + line + ": " + message);
                break;
            case SemanticException:
                System.err.println("SemanticException at line " + line + ": " + message);
                break;
            case TypeMismatchedException:
                System.err.println("TypeMismatchedException at line " + line + ": " + message);
                break;
            case ParameterMismatchedException:
                System.err.println("ParameterMismatchedException at line " + line + ": " + message);
                break;
            }
    }
}
