package mySymbol;

/**
 * 常数符号 - CONST N = 10;
 */
public class Const implements TableSymbol {
    private String name;
    private Type type;

    /**
     * 构造器
     * @param name 常数名
     * @param type 常数类型
     */
    public Const(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * 获取常数名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取常数类型
     */
    public Type getType() {
        return type;
    }

    @Override
    public String getKind() {
        return "CONST";
    }

    @Override
    public String toString() {
        // return name + " = " + value;
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Const)) return false;
        Const other = (Const) obj;
        return name.equals(other.name) && 
               type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31;
    }
}