public class Token {
    public Compiler.TokenType type;
    public String value;
    public int lineno;
    public int column;
    public int width;

    public Token(Compiler.TokenType type, String value, int lineno, int column, int width) {
        this.type = type;
        this.value = value;
        this.lineno = lineno;
        this.column = column;
        this.width = width;
    }

    public Token(Compiler.TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        String str = "lineno:" + this.lineno + "  column:" + this.column + "  width:" + this.width + "  type:" + this.type + "  value:" + this.value;
        return str;

    }
}