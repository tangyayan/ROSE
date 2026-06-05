package mySymbol;

/**
 * 表达式 - 包含生成的代码、类型信息和是否是左值
 */
public class Expression {
    String code;
    Type type;
    boolean isLValue; // 是否是左值

    /**
     * 构造器
     * @param code 生成的代码
     * @param type 表达式的类型
     * @param isLValue 是否是左值
     */
    public Expression(String code, Type type, boolean isLValue) {
        this.code = code;
        this.type = type;
        this.isLValue = isLValue;
    }

    /**
     * 简化构造器 - 默认不是左值
     * @param code 生成的代码
     * @param type 表达式的类型
     */
    public Expression(String code, Type type) {
        this(code, type, false);
    }

    public Type getType() {
        return type;
    }

    public String getCode() {
        return code;
    }
    
    public boolean isLValue() {
        return isLValue;
    }

    @Override
    public String toString() {
        return code;
    }
}
