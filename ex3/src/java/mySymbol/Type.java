package mySymbol;

/**
 * Type 接口 - 所有类型的基接口
 */
public interface Type extends TableSymbol {
    /**
     * 获取类型名称
     */
    String getTypeName();

    /**
     * 判断是否为原始类型
     */
    boolean isPrimitiveType();

    /**
     * 获取目标类型 - 对于别名类型，返回其指向的实际类型；对于非别名类型，返回自身
     * @return 目标类型
     */
    Type getTargetType();

    /**
     * 获取符号类型
     */
    @Override
    default String getKind() {
        return "TYPE";
    }
}