public class Symbols {
    private int line, column;
    private int type;
    private String value;// id和num的值,其他token的值为token的名字
  
    public Symbols(int type, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = Token.terminalNames[type];
    }
  
    public Symbols(int type, int line, int column, Object value) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = value.toString();
    }
  
    public int getLine() {
      return line;
    }

    public int getColumn() {
        return column;
    }
    
    public int getToken() {
        return type;
    }

    public String getValue() {
        return value;
    }
  
    public String toString() {
        return "Symbol(type=" + type + ", line=" + line + ", column=" + column + ", value=" + value + ")";
    }
}
