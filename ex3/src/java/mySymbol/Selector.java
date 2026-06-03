package mySymbol;

import java.util.List;

public class Selector {
    public static class SelectorNode {
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

    public String getCode() {
        return code;
    }

    public void addFieldSelector(String fieldName) {
        code += "." + fieldName;
        SelectorNode node = new SelectorNode();
        node.type = SelectorNode.Type.FIELD;
        node.fieldName = fieldName;
        nodes.add(node);
    }

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
