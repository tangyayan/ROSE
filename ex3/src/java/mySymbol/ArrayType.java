package mySymbol;

/**
 * 数组类型 - ARRAY 10 OF INTEGER
 */
public class ArrayType implements Type {
    private Type elementType;

    /**
     * 构造器
     * @param elementType 元素类型
     */
    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    @Override
    public String getTypeName() {
        // return "ARRAY " + length + " OF " + elementType.getTypeName();
        return "ARRAY OF " + elementType.getTypeName();
    }

    /**
     * 获取元素类型
     */
    public Type getElementType() {
        return elementType;
    }

    @Override
    public boolean isPrimitiveType() {
        return false;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ArrayType)) return false;
        ArrayType other = (ArrayType) obj;
        return elementType.equals(other.elementType);
    }

    @Override
    public int hashCode() {
        return elementType.hashCode() * 31;
    }
}