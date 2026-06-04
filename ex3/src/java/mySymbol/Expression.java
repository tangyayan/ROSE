package mySymbol;

public class Expression {
    String code;
    Type type;
    boolean isLValue; // 是否是左值

    public Expression(String code, Type type, boolean isLValue) {
        this.code = code;
        this.type = type;
        this.isLValue = isLValue;
    }

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
