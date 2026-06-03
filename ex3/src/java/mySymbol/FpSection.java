package mySymbol;

import java.util.*;

/**
 * 形式参数段 - VAR x, y: INTEGER;
 */
public class FpSection {
    private List<String> identList;
    private Type type;
    private boolean isVar;

    /**
     * 构造器
     * @param identList 标识符列表
     * @param type 类型
     * @param isVar 是否为 VAR 参数
     */
    public FpSection(List<String> identList, Type type, boolean isVar) {
        this.identList = new ArrayList<>(identList);
        this.type = type;
        this.isVar = isVar;
    }

    /**
     * 获取标识符列表
     */
    public List<String> getIdentList() {
        return new ArrayList<>(identList);
    }

    /**
     * 获取类型
     */
    public Type getType() {
        return type;
    }

    /**
     * 是否为 VAR 参数
     */
    public boolean isVar() {
        return isVar;
    }

    /**
     * 获取参数数量
     */
    public int getSize() {
        return identList.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isVar) sb.append("VAR ");
        sb.append(String.join(", ", identList)).append(": ").append(type);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FpSection)) return false;
        FpSection other = (FpSection) obj;
        return identList.equals(other.identList) && 
               type.equals(other.type) && 
               isVar == other.isVar;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identList, type, isVar);
    }
}