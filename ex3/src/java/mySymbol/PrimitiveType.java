package mySymbol;

/**
 * 原始类型 - INTEGER, BOOLEAN等
 */
public class PrimitiveType implements Type {
    private String typeName;

    public static final PrimitiveType INTEGER = new PrimitiveType("INTEGER");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("BOOLEAN");
    public static final PrimitiveType ERROR = new PrimitiveType("ERROR");

    /**
     * 构造器 - 创建原始类型
     * @param typeName 类型名称（INTEGER, BOOLEAN等）
     */
    public PrimitiveType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean isPrimitiveType() {
        return true;
    }

    @Override
    public String toString() {
        return typeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PrimitiveType)) return false;
        PrimitiveType other = (PrimitiveType) obj;
        return typeName.equals(other.typeName);
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }
}