import java.io.*;

import static java.lang.Character.isDigit;

public class Compiler {

//    输入文件
    public static Inputfile inputfile;
    public static File file;

    public static boolean _SHOULD_LOG_SCOPE = false;
//    参数寄存器
    public static String parameter_registers[] = {"rdi", "rsi", "rdx", "rcx", "r8", "r9"};
//    用于代码生成
    public static int Offset_num = -999;
//    用于“if”语句
    public static int Count_i = 0;

    public Compiler(String str)  {
        file = new File(str);
        inputfile = new Inputfile(str);
    }

    //    数组用以展示错误信息
    public class  Inputfile{
    public static BufferedReader buffers;
    public static String name = "";

        public Inputfile(String str) {
            try{
                buffers = new BufferedReader(new FileReader(str));
                name = str;
            }
            catch(IOException e){
                e.printStackTrace();
            }

        }
    }

    public enum ErrorCode{
        UNEXPECTED_TOKEN("Unexpected token");
        private String a;
        private ErrorCode(String a){
        }
    }

//    词法分析器

//    一个枚举类
    public enum TokenType{
        //    单个字符的词法单元类型
        TK_PLUS("+"),
        TK_MINUS("-"),
        TK_MUL("*"),
        TK_DIV("/"),
        TK_NEG("unary-"),
        TK_LT("<"),
        TK_GT(">"),
        TK_EQ("=="),
        TK_NE("!="),
        TK_GE(">="),
        TK_LE("<="),
        TK_LPAREN("("),
        TK_RPAREN(")"),
        TK_LBRACE("{"),
        TK_RBRACE("}"),
        TK_LBRACK("["),
        TK_RBRACK("]"),
        TK_COMMA(","),
        TK_SEMICOLON(";"),
        //    保留字
        TK_RETURN("return"),
        TK_INT("int"),
        TK_IF("if"),
        TK_THEN("then"),
        TK_ELSE("else"),
        //misc
        TK_IDENT("IDENT"),
        TK_INTEGER_CONST("INTEGER_CONST"),
        TK_ASSIGN("="),
        TK_EOF("EOF");
        // 定义一个 private 修饰的实例变量
        private String data;
        // 定义一个带参数的构造器，枚举类的构造器只能使用 private 修饰
        private TokenType(String data) {
            this.data = data;
        }
        // 定义 get set 方法
        public String getData() {
            return data;
        }
    }

//    判断是否在TokenType内，返回对应的对象
    public static TokenType getTokenType(String k){
        int ding = 0;
        for (int i = 0; i< TokenType.values().length; i++){
            if (k.equals(TokenType.values()[i].getData())){
                ding = i;
                break;
            }
        }
        return TokenType.values()[ding];
    }

//    判断test在不在枚举类TokenType里
    public static boolean contains(String test) {
        for (TokenType t : TokenType.values()) {
            if (t.getData().equals(test)) {
                return true;
            }
        }
        return false;
    }

    //        读文件
    public String readFile() throws IOException {
        String str,allline = "";
        while ((str = Inputfile.buffers.readLine()) != null) {
            allline+=str;
            allline+='\n';
        }
        return allline;
    }

}

