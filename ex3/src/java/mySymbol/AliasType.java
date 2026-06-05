package mySymbol;

/**
 * 别名类型 - TYPE MyInt = INTEGER;
 */
public class AliasType implements Type {
    private String typeName;
    private Type targetType;

    /**
     * 构造器
     * @param typeName 类型别名名称
     * @param targetType 目标类型
     */
    public AliasType(String typeName, Type targetType) {
        this.typeName = typeName;
        // this.targetType = targetType;
        if(targetType instanceof AliasType) { // 展开别名的别名
            this.targetType = ((AliasType) targetType).getTargetType();
        } else {
            this.targetType = targetType;
        }
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public Type getTargetType() {
        return targetType;
    }

    @Override
    public boolean isPrimitiveType() {
        return targetType.isPrimitiveType();
        // return false;
    }

    @Override
    public String toString() {
        return typeName + " = " + targetType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AliasType)) return false;
        AliasType other = (AliasType) obj;
        // return typeName.equals(other.typeName) && 
        //        targetType.equals(other.targetType);
        return targetType.equals(other.targetType);
    }

    @Override
    public int hashCode() {
        // return typeName.hashCode() * 31 + targetType.hashCode();
        return targetType.hashCode() * 31;
    }
}