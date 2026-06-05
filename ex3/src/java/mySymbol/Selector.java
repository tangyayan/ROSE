package mySymbol;

import java.util.List;

/**
 * 用于临时存储表示访问记录字段或数组元素的选择器
 */
public class Selector {
    /**
     * 表示选择器链中的一个节点，可以是字段选择器（.identifier）或索引选择器（[expr]）
     */
    public static class SelectorNode {
        /**
         * 选择器类型，FIELD 表示字段选择器，例如 ".fieldName"，INDEX 表示索引选择器，例如 "[indexExpr]"
         */
        enum Type { FIELD, INDEX }
        Type type;
        String fieldName; // 对应 ".identifier"
        // 暂时不存索引
    
        public boolean isFieldSelector() {
            return type == Type.FIELD;
        }

        public String getFieldName() {            
            return fieldName;
        }
    }

    String code;
    List<SelectorNode> nodes;

    public Selector() {
        this.code = "";
        this.nodes = new java.util.ArrayList<>();
    }

    @Override public String toString() {
        return code;
    }

    /**
     * 获取当前选择器的代码表示
     * @return 选择器的代码表示，例如 ".field1.field2[expr]"，不包含初始变量名
     */
    public String getCode() {
        return code;
    }

    /**
     * 添加一个字段选择器，例如 ".fieldName"
     * @param fieldName 字段名称
     */
    public void addFieldSelector(String fieldName) {
        code += "." + fieldName;
        SelectorNode node = new SelectorNode();
        node.type = SelectorNode.Type.FIELD;
        node.fieldName = fieldName;
        nodes.add(node);
    }

    /**
     * 添加一个索引选择器，例如 "[indexExpr]"
     * @param indexExpr 索引表达式的代码表示，例如 "i + 1"
     */
    public void addIndexSelector(String indexExpr) {
        code += "[" + indexExpr + "]";
        SelectorNode node = new SelectorNode();
        node.type = SelectorNode.Type.INDEX;
        nodes.add(node);
    }

    public List<SelectorNode> getNodes() {
        return nodes;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
