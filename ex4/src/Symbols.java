public class Symbols {
    private int line, column;
    private int type;
    private Object value;
  
    public Symbols(int type, int line, int column) {
        this.type = type;
        this.line = line;
        this.column = column;
    }
  
    public Symbols(int type, int line, int column, Object value) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = value;
    }
  
    public int getLine() {
      return line;
    }

    public int getColumn() {
        return column;
    }
  
    public String toString() {
        return "Symbol(type=" + type + ", line=" + line + ", column=" + column + ", value=" + value + ")";
    }
}
