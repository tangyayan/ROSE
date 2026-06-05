package mySymbol;

import java.util.*; 

/**
 * 记录类型 - RECORD ... END
 * isVar 统一为 false
 */
public class RecordType implements Type {
    private List<Parameter> fields;
    private String typeName;

    /**
     * 构造器
     */
    public RecordType() {
        this.fields = new ArrayList<>();
        this.typeName = "RECORD";
    }

    public Type getTargetType() {
        return this;
    }

    /**
     * 添加字段
     */
    public void addField(FpSection field) {
        for (String ident : field.getIdentList()) {
            // TODO: 可以添加检查，确保同一参数列表中没有重复的标识符
            fields.add(new Parameter(ident, field.getType(), false));
        }
    }

    /**
     * 获取所有字段
     */
    public List<Parameter> getFields() {
        return new ArrayList<>(fields);
    }

    /**
     * 获取指定名称的字段
     */
    public Parameter getField(String fieldName) {
        for (Parameter param : fields) {
            if (param.getName().equals(fieldName)) {
                return param;
            }
        }
        return null;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean isPrimitiveType() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RECORD\n");
        for (Parameter param : fields) {
            sb.append("  ").append(param.getName()).append(": ")
              .append(param.getType()).append(";\n");
        }
        sb.append("END");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else return false;
        // if (!(obj instanceof RecordType)) return false;
        // RecordType other = (RecordType) obj;
        // return fields.equals(other.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }
}