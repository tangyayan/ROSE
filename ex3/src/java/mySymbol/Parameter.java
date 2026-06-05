package mySymbol;

import java.util.Objects;

/**
 * 参数类，表示过程或函数的参数
 */
public class Parameter {
    private String name;
    private Type type;
    private boolean isVar;

    /**
     * 构造器
     * @param name 标识符
     * @param type 类型
     * @param isVar 是否为 VAR 参数
     */
    public Parameter(String name, Type type, boolean isVar) {
        this.name = name;
        this.type = type;
        this.isVar = isVar;
    }

    /**
     * 获取标识符
     */ 
    public String getName() {
        return name;
    }

    /**
     * 获取类型
     */
    public Type getType() {
        return type;
    }

    /**
     * 是否为 VAR 参数
     */
    public boolean isVar() {
        return isVar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isVar) sb.append("VAR ");
        sb.append(name).append(": ").append(type);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(!(obj instanceof Parameter)) return false;
        Parameter other = (Parameter) obj;
        return name.equals(other.name) &&
               type.equals(other.type) &&
               isVar == other.isVar;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, isVar);
    }
}
