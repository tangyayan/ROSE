/**
 * 符号类，包含token类型、行号、列号和token值
 */
public class Symbols {
    private int line, column;
    private int type;
    /**
     * token值，默认为token的终结符名称，如果有传入value则使用传入的value
     */
    private String value;
  
    /**
     * 构造函数，传入token类型、行号和列号，token值默认为token的终结符名称
     * @param type token类型
     * @param line 行号
     * @param column 列号
     */
    public Symbols(int type, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = Token.terminalNames[type];
    }
  
    /**
     * 构造函数，传入token类型、行号、列号和token值
     * @param type token类型
     * @param line 行号
     * @param column 列号
     * @param value token值
     */
    public Symbols(int type, int line, int column, Object value) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = value.toString();
    }
  
    /**
     * 获取行号
     * @return 行号
     */
    public int getLine() {
      return line;
    }

    /**
     * 获取列号
     * @return 列号
     */
    public int getColumn() {
        return column;
    }
    
    /**
     * 获取token类型
     * @return token类型
     */
    public int getToken() {
        return type;
    }

    /**
     * 获取token值
     * @return token值
     */
    public String getValue() {
        return value;
    }
  
    public String toString() {
        return "Symbol(type=" + type + ", line=" + line + ", column=" + column + ", value=" + value + ")";
    }
}
