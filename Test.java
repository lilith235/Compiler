import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Compiler compiler = new Compiler("test01.txt");
        try{
            String alllines = compiler.readFile();
//            词法分析器
            Lexer lexer = new Lexer(alllines);
//            List<Token> lists = lexer.gather_all_tokens();
//            System.out.println("value   type   width   lineno   column");
//            for (int i=0; i< lists.size();i++){
//                System.out.println(lists.get(i).value + "   " + lists.get(i).type + "   " + lists.get(i).width + "   " + lists.get(i).lineno + "   " + lists.get(i).column);
//            }

//            语法分析器 内部调用词法分析器的get_next_token()方法对token流进行逐个遍历
            Parser parser = new Parser(lexer);
            List<ASTNodeType> tree = parser.parse();

//            语义分析器
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.semantic_analyze(tree);

//            代码生成器 在语义分析器的基础上加入汇编指令的生成
            Codegenerator codegenerator = new Codegenerator();
            codegenerator.code_generate(tree);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
