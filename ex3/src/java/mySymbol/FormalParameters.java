package mySymbol;

import java.util.*;

/**
 * 形式参数表 - (x: INTEGER; VAR y: BOOLEAN)
 */
public class FormalParameters implements TableSymbol {
    private List<Parameter> parameterList;

    /**
     * 构造器
     */
    public FormalParameters() {
        this.parameterList = new ArrayList<>();
    }

    /**
     * 添加形式参数段
     */
    public void addFpSection(FpSection section) {
        for (String ident : section.getIdentList()) {
            // TODO: 可以添加检查，确保同一参数列表中没有重复的标识符
            parameterList.add(new Parameter(ident, section.getType(), section.isVar()));
        }
    }

    /**
     * 获取所有参数段
     */
    public List<Parameter> getParameterList() {
        return new ArrayList<>(parameterList);
    }

    /**
     * 获取总参数个数
     */
    public int getSize() {
        return parameterList.size();
    }

    /**
     * 获取指定名称的参数
     */
    public Parameter getParameter(String paramName) {
        for (Parameter param : parameterList) {
            if (param.getName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }

    /**
     * 获取参数段数量
     */
    public int getSectionCount() {
        return parameterList.size();
    }

    /**
     * 检查实参类型是否与形参匹配
     * @param argTypes
     * @return true if matches, false otherwise
     */
    public boolean checkTypes(List<Type> argTypes) {
        if (argTypes.size() != parameterList.size()) {
            System.out.println("Argument count mismatch: expected " + parameterList.size() + ", got " + argTypes.size());
            return false;
        }
        for (int i = 0; i < argTypes.size(); i++) {
            if (!argTypes.get(i).equals(parameterList.get(i).getType())) {
                System.out.println("Type mismatch for parameter " + parameterList.get(i).getName() + ": expected " + parameterList.get(i).getType() + ", got " + argTypes.get(i));
                return false;
            }
        }
        return true;
    }

    @Override
    public String getKind() {
        return "FORMALPARAMETERS";
    }

    @Override
    public String toString() {
        if (parameterList.isEmpty()) {
            return "()";
        }
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < parameterList.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(parameterList.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FormalParameters)) return false;
        FormalParameters other = (FormalParameters) obj;
        return parameterList.equals(other.parameterList);
    }

    @Override
    public int hashCode() {
        return parameterList.hashCode();
    }
}