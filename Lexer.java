import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;

public class Lexer{
    public String text;
    public int pos;
    public char current_char;//这个是获取text的第pos个字符


    //token数组
    public List<Token> tokens;


    public Lexer(String text){
        this.text = text;
        this.pos = 0;
        this.current_char = this.text.charAt(pos);
        tokens = new ArrayList<>();
    }
    //        前进' pos '指针并设置' current_char '变量＂＂
    public void advance(){
        if(this.current_char == '\n'){
            Error.lineno += 1;
            Error.column = 0;
        }
        this.pos += 1;
        if (this.pos > text.length() - 1){
//                表示输入结束
            this.current_char = '。';
        }
        else{
            this.current_char = this.text.charAt(pos);
            Error.column += 1;
        }
    }

    public void skip_whitespace(){
        while (this.current_char != '。' && this.current_char == ' '){
            this.advance();
        }
    }

    //        如果c作为标识符的第一个字符有效，则返回true。
    public boolean _is_ident1(char c){
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_';
    }

    //        如果c作为标识符的非首字符有效，则返回true
    public boolean _is_ident2(char c){
        return this._is_ident1(c) || ('0' <= c && c <= '9');
    }

    public Token number(){
//            返回从输入中消耗的(多位数)整数或浮点数。
//            # Create a new token
        Token token = new Token(null, null);
        int old_column = Error.column;
        String result = "";
        while(this.current_char != '。' && isDigit(this.current_char)){
            result += this.current_char;
            this.advance();
        }
        token.type = Compiler.TokenType.TK_INTEGER_CONST;
        token.value = result;
        token.lineno = Error.lineno;
        token.column = Error.column;
        token.width = Error.column - old_column;
        return token;
    }

    //        从p中读取标点符号并返回
    public int read_punct(String p){
        String str = this.text.substring(this.pos, pos+2);
        if(str.equals("/*") ){
            return 4;
        }
        if(str.equals("//")){
            return 3;
        }
        if (p.substring(this.pos) == "==" || p.substring(this.pos) == "!=" || p.substring(this.pos) == "<=" || p.substring(this.pos) == ">="){
            return 2;
        }
//            C 语言区域中被视为标点符号的 ASCII 字符组成的字符串。
        String punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        if (punctuation.contains(this.current_char + "")){
            return 1;
        }
        else{
            return 0;
        }
    }

    //        词法分析器 将一个句子分解成token。一次一个字符。
    public Token get_next_token(){
        while (this.current_char != '。'){
//                Skip whitespace characters
            if (this.current_char == ' '){
                this.skip_whitespace();
                continue;
            }
            if (this.current_char == '\n'){
                advance();
                continue;
            }
//                数值文字
            if (Character.isDigit(this.current_char)){
                return this.number();
            }
//                标识符(以- z或a - z或_开头，不能是数字，不能是除_以外的标点符号)
            if (this._is_ident1(this.current_char)){
//                    Create a new token
                Token token = new Token(null, null);
//                    create a token with two-characters lexeme as its value
//                    token.type = new TokenType(this.text.substring(this.pos,(this.pos + 2)));
                int old_column = Error.column;
                String result = this.current_char + "";
                this.advance();
                while (this._is_ident2(this.current_char)){
                    result += (this.current_char + "");
                    this.advance();
                }
//                    if keyword, not common identifier
                if (Compiler.contains(result)){
//                        get enum member by value, e.g.
                    token.type = Compiler.getTokenType(result);
                    token.value = token.type.getData();//如 "return", etc
                    token.lineno = Error.lineno;
                    token.column = Error.column;
                    token.width = Error.column - old_column;
                    return token;
                }
//                    if not keyword, but identifier
                else{
                    token.type = Compiler.TokenType.TK_IDENT;
                    token.value = result;
                    token.lineno = Error.lineno;
                    token.column = Error.column;
                    token.width = Error.column - old_column;
                    return token;
                }
            }



//                Punctuators
//            two-characters punctuator
            if (this.read_punct(this.text) == 2){
//                     Create a new token
                Token token = new Token(null, null);
//                     create a token with two-characters lexeme as its value
                token.type = Compiler.getTokenType(this.text.substring(this.pos,(this.pos + 2)));
                token.value= token.type.getData(); // '!=', '==', etc
                token.lineno = Error.lineno;
                token.column = Error.column;
                token.width = 2;
                this.advance();
                this.advance();
                return token;
            }
            //            多行注释
            else if (this.read_punct(this.text) == 4){
                this.pos += 2;
                while(!this.text.substring(this.pos, this.pos + 2).equals("*/")){
                    String str = this.text.substring(this.pos, pos+2);
                    advance();
                }
                advance();
                advance();
            }
            //            单行注释
            else if (this.read_punct(this.text) == 3){
                this.pos += 2;
                while(current_char != '\n'){
                    advance();
                }
                advance();
            }
//                single-character punctuator
            else  if (Compiler.contains(this.current_char + "")){
//                     Create a new token
                Token token = new Token(null, null);
//                        get enum member by value, e.g.
//                       TokenType('+') --> TokenType.PLUS
//                    create a token with a single-character lexeme as its value
                token.type = Compiler.getTokenType(this.current_char + "");
                token.value= token.type.getData();  // e.g. '+', '-', etc
                token.lineno = Error.lineno;
                token.column = Error.column;
                token.width = 1;
                this.advance();
                return token;
            }
//                没有值等于self.current_char的enum成员
            else{
                Error.show_error_at(Error.lineno, Error.column, "无效的token");
            }
        }
        //EOF(文件结束符)标记表示没有更多用于词法分析的输入了
        return new Token(Compiler.TokenType.TK_EOF, null);

    }

    public List<Token> gather_all_tokens(){
        Token token = this.get_next_token();
        this.tokens.add(token);
        while (token.type != Compiler.TokenType.TK_EOF){
            token = this.get_next_token();
            this.tokens.add(token);
        }
        return this.tokens;
    }

}