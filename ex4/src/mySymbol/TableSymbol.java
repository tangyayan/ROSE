package mySymbol;

/**
 * SymbolEntry 接口 - 所有符号的基接口
 */
public interface TableSymbol {
    /**
     * 获取符号的类型
     * @return 符号类型（VAR, CONST, PROCEDURE, FORMALPARAMETERS等）
     */
    String getKind();
}