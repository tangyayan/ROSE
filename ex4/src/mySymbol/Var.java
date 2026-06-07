package mySymbol;

/**
 * 变量符号 - VAR x: INTEGER;
 */
public class Var implements TableSymbol {
    private String name;
    private Type type;

    /**
     * 构造器
     * @param name 变量名
     * @param type 变量类型
     */
    public Var(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * 获取变量名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取变量类型
     */
    public Type getType() {
        return type;
    }

    @Override
    public String getKind() {
        return "VAR";
    }

    @Override
    public String toString() {
        return name + ": " + type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Var)) return false;
        Var other = (Var) obj;
        return name.equals(other.name) && type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + type.hashCode();
    }
}