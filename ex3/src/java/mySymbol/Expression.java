package mySymbol;

public class Expression {
    String code;
    Type type;

    public Expression(String code, Type type) {
        this.code = code;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
