import java.io.*;


public class Error extends Exception{
    public static int lineno = 1;
    public static int column = 1;

    public Token token;
    public Compiler.ErrorCode error_code;
    public String message;

    public static void show_error_at(int lineno, int pos, String error_list){
        error_list = "some error";
        Compiler compiler = new Compiler("test01.txt");
        System.out.println(Compiler.Inputfile.name + ": 第" + lineno + "行" + pos + "个字符 输入不合法" );
        System.exit(1);

    }

    // 读取文件指定行。
    static String readAppointedLineNumber(File sourceFile, int lineNumber){
        try{
            FileReader in = new FileReader(sourceFile);
            LineNumberReader reader = new LineNumberReader(in);
            String s = null;
            int line = 1;
            if (lineNumber < 0 || lineNumber > getTotalLines(sourceFile)) {
                s = "不在文件的行数范围之内。";
            } else {
//                System.out.println("当前行号为:" + reader.getLineNumber());
//                reader.setLineNumber(23);
//                System.out.println("更改后行号为:" + reader.getLineNumber());
                long i = reader.getLineNumber();
                while (reader.readLine() != null) {
                    line++;
                    if (i == line) {
                        s += reader.readLine();
//                        System.out.println(s);
                        break;
                    }

                }

            }
            reader.close();
            in.close();
            return s;
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    // 文件内容的总行数。
    static int getTotalLines(File file) throws IOException {
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
        }
        reader.close();
        in.close();
        return lines;
    }



}
class LexerError extends Error{

}

class ParserError extends Error{

}

class SemanticError extends Error{

}
