import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;


//语法分析器
public class Parser {
    public Lexer lexer;
    public Token current_token;
    public String current_function_name;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.current_token = this.get_next_token();
        this.current_function_name = "";
//        将当前token设置为从输入中提取的第一个token
    }
    public Token get_next_token(){
        return this.lexer.get_next_token();
    }

    public void error(Compiler.ErrorCode error_code, Token token){
        Error error = new ParserError();
        error.error_code = error_code;
        error.token = token;
        error.message = error_code.name() + "   ->   " + token.toString();
        try{
            throw error;
        }
        catch (Exception e){
//            e.printStackTrace();
            System.out.println(error.message);
        }
    }


//    比较当前token类型与传递的token类型，如果它们匹配，
//    则eat当前令牌并将下一个令牌分配给this.current_token，否则引发异常
    public void eat(Compiler.TokenType token_type){
        if(this.current_token.type == token_type){
            this.current_token = this.get_next_token();
        }
        else{
            this.error(Compiler.ErrorCode.UNEXPECTED_TOKEN, this.current_token);
        }
    }

//    primary = "(" expr ")" | identifier args? | num
//    args = "(" (assign ("," assign)*)? ")"
    public ASTNodeType primary(){
        Token token = this.current_token;
//        "(" expr ")"
        if(token.type == Compiler.TokenType.TK_LPAREN){
            this.eat(Compiler.TokenType.TK_LPAREN);
            ASTNodeType node = this.expression();
            this.eat(Compiler.TokenType.TK_RPAREN);
            return node;
        }
//        identifier
        else if(token.type == Compiler.TokenType.TK_IDENT){
            this.eat(Compiler.TokenType.TK_IDENT);
//            函数调用
            if(this.current_token.type == Compiler.TokenType.TK_LPAREN){
                String function_name = token.value;
                this.eat(Compiler.TokenType.TK_LPAREN);
                List<ASTNodeType> actual_parameter_nodes = new ArrayList<>();
                if(this.current_token.type != Compiler.TokenType.TK_RPAREN){
                    ASTNodeType node = this.assign();
                    actual_parameter_nodes.add(node);
                }
                while(this.current_token.type == Compiler.TokenType.TK_COMMA){
                    this.eat(Compiler.TokenType.TK_COMMA);
                    ASTNodeType node = this.assign();
                    actual_parameter_nodes.add(node);
                }
                this.eat(Compiler.TokenType.TK_RPAREN);
                ASTNodeType node = new FunctionCall_Node(function_name, actual_parameter_nodes, token);
                return node;
            }
//            变量
            return new Var_Node(token);
        }

//        数
        else if(token.type == Compiler.TokenType.TK_INTEGER_CONST){
            this.eat(Compiler.TokenType.TK_INTEGER_CONST);
            return new Num_Node(token);
        }
        else{
            return null;
        }
    }

//    unary = ("+" | "-") unary
//            | primary
    public ASTNodeType unary(){
        Token token = this.current_token;
        if(token.type == Compiler.TokenType.TK_PLUS){
            this.eat(Compiler.TokenType.TK_PLUS);
            return new UnaryOp_Node(token, this.unary());
        }
        else if(token.type == Compiler.TokenType.TK_MINUS){
            this.eat(Compiler.TokenType.TK_MINUS);
            return new UnaryOp_Node(token, this.unary());
        }
        else{
            return this.primary();
        }
    }

//     mul_div = unary ("*" unary | "/" unary)*
    public ASTNodeType mul_div(){
        ASTNodeType node = this.unary();
        while(true){
            Token token = this.current_token;
            if(this.current_token.type == Compiler.TokenType.TK_MUL){
                this.eat(Compiler.TokenType.TK_MUL);
                node = new BinaryOp_Node(node, token, this.unary());
                continue;
            }
            else if(this.current_token.type == Compiler.TokenType.TK_DIV){
                this.eat(Compiler.TokenType.TK_DIV);
                node = new BinaryOp_Node(node, token, this.unary());
                continue;
            }
            else{
              return node;
            }
        }
    }

//    add-sub = mul_div ("+" mul_div | "-" mul_div)*
    public ASTNodeType add_sub(){
        ASTNodeType node = this.mul_div();
        while (true){
            Token token = this.current_token;
            if(this.current_token.type == Compiler.TokenType.TK_PLUS){
                this.eat(Compiler.TokenType.TK_PLUS);
                node = new BinaryOp_Node(node, token, this.mul_div());
                continue;
            }
            else if(this.current_token.type == Compiler.TokenType.TK_MINUS){
                this.eat(Compiler.TokenType.TK_MINUS);
                node = new BinaryOp_Node(node, token, this.mul_div());
                continue;
            }
            else{
                return node;
            }
        }
    }

//     relational = add_sub ("<" add_sub | "<=" add_sub | ">" add_sub | ">=" add_sub)*
    public ASTNodeType relational(){
        ASTNodeType node = this.add_sub();
        while(true){
            Token token = this.current_token;
            if(this.current_token.type == Compiler.TokenType.TK_LT){
                this.eat(Compiler.TokenType.TK_LT);
                node = new BinaryOp_Node(node, token, this.add_sub());
                continue;
            }
            else if(this.current_token.type == Compiler.TokenType.TK_LE){
                this.eat(Compiler.TokenType.TK_LE);
                node = new BinaryOp_Node(node, token, this.add_sub());
                continue;
            }
            else if(this.current_token.type == Compiler.TokenType.TK_GT){
                this.eat(Compiler.TokenType.TK_GT);
                node = new BinaryOp_Node(node, token, this.add_sub());
                continue;
            }
            else if(this.current_token.type == Compiler.TokenType.TK_GE){
                this.eat(Compiler.TokenType.TK_GE);
                node = new BinaryOp_Node(node, token, this.add_sub());
                continue;
            }
            else{
                return node;
            }
        }
    }

//    equality = relational ("==" relational | "! =" relational)*
    public ASTNodeType equality(){
        ASTNodeType node = this.relational();
        while(true){
            Token token = this.current_token;
            if(this.current_token.type == Compiler.TokenType.TK_EQ){
                this.eat(Compiler.TokenType.TK_EQ);
                node = new BinaryOp_Node(node, token, this.relational());
                continue;
            }
            else if(this.current_token.type == Compiler.TokenType.TK_NE){
                this.eat(Compiler.TokenType.TK_NE);
                node = new BinaryOp_Node(node, token, this.relational());
                continue;
            }
            else {
                return node;
            }
        }
    }

//    assign = equality ("=" assign)?
    public ASTNodeType assign(){
        ASTNodeType node = this.equality();
        Token token = this.current_token;
        if(token.type == Compiler.TokenType.TK_ASSIGN){
            this.eat(Compiler.TokenType.TK_ASSIGN);
            node = new Assign_Node(node, token, this.assign());
        }
        return node;
    }

//    expression = assign
    public ASTNodeType expression(){
        ASTNodeType node = this.assign();
        return node;
    }

//    expression-statement = expression? ";"
    public ASTNodeType expression_statement(){
        Token token = this.current_token;
        ASTNodeType node = null;
        if(token.type == Compiler.TokenType.TK_SEMICOLON){
            this.eat(Compiler.TokenType.TK_SEMICOLON);
        }
        else {
            node = this.expression();
            if(this.current_token.type == Compiler.TokenType.TK_SEMICOLON){
                this.eat(Compiler.TokenType.TK_SEMICOLON);
            }
            else{
                Error.show_error_at(token.lineno, (token.column-token.width+1), "expect \";\"");
            }
        }
        return node;
    }

//    statement = expression-statement
//                | "return" expression-statement
//                | block
//                | "if" "(" expression ")" statement ("else" statement)?
    public ASTNodeType statement(){
        Token token = this.current_token;
//        "return" expression-statement
        if(token.type == Compiler.TokenType.TK_RETURN){
            this.eat(Compiler.TokenType.TK_RETURN);
            ASTNodeType node = new Return_Node(token, this.expression_statement(), this.current_function_name);
            return node;
        }
//        语句块
        else if (token.type == Compiler.TokenType.TK_LBRACE) {
            return this.block();
        }
        else if (token.type == Compiler.TokenType.TK_IF) {
            ASTNodeType condition = null;
            ASTNodeType then_statement = null;
            ASTNodeType else_statement = null;
            this.eat(Compiler.TokenType.TK_IF);
            if(this.current_token.type == Compiler.TokenType.TK_LPAREN){
                this.eat(Compiler.TokenType.TK_LPAREN);
                condition = this.expression();
                this.eat(Compiler.TokenType.TK_RPAREN);
                if(this.current_token.type == Compiler.TokenType.TK_THEN){
                    this.eat(Compiler.TokenType.TK_THEN);
                    then_statement = this.statement();
                    if(this.current_token.type == Compiler.TokenType.TK_ELSE){
                        this.eat(Compiler.TokenType.TK_ELSE);
                        else_statement = this.statement();
                    }
                }
            }
            return new If_Node(condition, then_statement, else_statement);
        }
//        expression-statement
        else {
            return this.expression_statement();
        }
    }

//    type_specification = int
    public Type_Node type_specification(){
        Token token = this.current_token;
        if(this.current_token.type == Compiler.TokenType.TK_INT){
            this.eat(Compiler.TokenType.TK_INT);
        }
        Type_Node node = new Type_Node(token);
        return node;
    }

//    variable_declaration = type_specification (indentifier ("=" expr)? ("," indentifier ("=" expr)?)*)? ";"
    public List<ASTNodeType> variable_declaration(){
        Type_Node type_node = this.type_specification();
        List<ASTNodeType> variable_nodes = new ArrayList<>();
        while (this.current_token.type != Compiler.TokenType.TK_SEMICOLON){
            if(this.current_token.type == Compiler.TokenType.TK_IDENT){
                Var_Node var_node = new Var_Node(this.current_token);
                ASTNodeType node = new VarDecl_Node(type_node, var_node);
                this.eat(Compiler.TokenType.TK_IDENT);
                variable_nodes.add(node);
                if(this.current_token.type == Compiler.TokenType.TK_COMMA){
                    this.eat(Compiler.TokenType.TK_COMMA);
                }
            }
        }
        this.eat(Compiler.TokenType.TK_SEMICOLON);
        return variable_nodes;
    }

//    compound_statement = (variable_declaration | statement)*
    public List<ASTNodeType> compound_statement(){
        List<ASTNodeType> statement_nodes = new ArrayList<>();
        while((this.current_token.type != Compiler.TokenType.TK_RBRACE) && (this.current_token.type != Compiler.TokenType.TK_EOF)){
            if(this.current_token.type == Compiler.TokenType.TK_INT){
                List<ASTNodeType> variable_nodes = this.variable_declaration();
                for(ASTNodeType node : variable_nodes){
                    statement_nodes.add(node);
                }
            }
            else {
                ASTNodeType node = this.statement();
//                去除 "  ;",  null statement
                if(node !=null){
                    statement_nodes.add(node);
                }
            }
        }
        return statement_nodes;
    }

//    block = "{" compound_statement "}"
    public Block_Node block(){
        if(this.current_token.type == Compiler.TokenType.TK_LBRACE){
            Token ltok = this.current_token; //"{"左括号
            this.eat(Compiler.TokenType.TK_LBRACE);
            List<ASTNodeType> statement_nodes = this.compound_statement();
            Token rtok = this.current_token;//"}"右括号
            this.eat(Compiler.TokenType.TK_RBRACE);
            return new Block_Node(ltok, rtok, statement_nodes);
        }
        return null;
    }

//    formal_parameter = type_specification identifier
    public FormalParam_Node formal_parameter(){
        Type_Node type_node = this.type_specification();
        Var_Node parameter_node = new Var_Node(this.current_token);
        this.eat(Compiler.TokenType.TK_IDENT);
        return new FormalParam_Node(type_node, parameter_node);
    }

//    formal_parameters = formal_parameter ("," formal_parameter)*
    public List<ASTNodeType> formal_parameters(){
        List<ASTNodeType> formal_params = new ArrayList<>();
        formal_params.add(this.formal_parameter());
        while(this.current_token.type != Compiler.TokenType.TK_RPAREN){
            if(this.current_token.type == Compiler.TokenType.TK_COMMA){
                this.eat(Compiler.TokenType.TK_COMMA);
                formal_params.add(this.formal_parameter());
            }
            else {
                System.out.println("参数列表错误");
                exit(1);
            }
        }
        return formal_params;
    }

//    function_definition= type_specification identifier "(" formal_parameters? ")" block
    public FunctionDef_Node function_definition(){
        Type_Node type_node = this.type_specification();
        String function_name = this.current_token.value;
        this.eat(Compiler.TokenType.TK_IDENT);
        List<ASTNodeType> formal_params = new ArrayList<>();
        Block_Node block_node = new Block_Node(null, null, null);
        if(this.current_token.type == Compiler.TokenType.TK_LPAREN){
            this.eat(Compiler.TokenType.TK_LPAREN);
            if(this.current_token.type != Compiler.TokenType.TK_RPAREN){
                formal_params = this.formal_parameters();
            }
            this.eat(Compiler.TokenType.TK_RPAREN);
        }
        this.current_function_name = function_name;
        if(this.current_token.type == Compiler.TokenType.TK_LBRACE){
            block_node = this.block();
        }
        else {
            Error.show_error_at(this.current_token.lineno, (this.current_token.column-this.current_token.width), ("eapect"+"\""+ Compiler.TokenType.TK_LBRACE +"\""));
        }
        return new FunctionDef_Node(type_node, function_name, formal_params, block_node);
    }

//    program = function_definition*

    /*
    program = function_definition*
    function_definition = type_specification identifier "(" formal_parameters? ")" block
            formal_parameters = formal_parameter ("," formal_parameter)*
            formal_parameter = type_specification identifier
            type_specification = "int"
    block = "{" compound_statement "}"
    compound_statement = (variable_declaration | statement)*
    statement = expression-statement
                    | "return" expression-statement
                    | block
                    | "if" "(" expression ")" statement ("else" statement)?
    variable_declaration = type_specification (identifier ("=" expr)? ("," identifier ("=" expr)?)*)? ";"
    expression-statement = expression? ";"
    expression = assign
            assign = equality ("=" assign)?
            equality = relational ("==" relational | "! =" relational)*
                    relational = add_sub ("<" add_sub | "<=" add_sub | ">" add_sub | ">=" add_sub)*
                    add_sub = mul_div ("+" mul_div | "-" mul_div)*
                    mul_div = unary ("*" unary | "/" unary)*
                    unary = ("+" | "-") primary | primary
            primary = "(" expr ")" | identifier args?| num
            args = "(" (assign ("," assign)*)? ")"
    */
    public List<ASTNodeType> parse(){
        List<ASTNodeType> function_definition_nodes = new ArrayList<>();
        while(this.current_token.type != Compiler.TokenType.TK_EOF){
            ASTNodeType node = this.function_definition();
            function_definition_nodes.add(node);
        }
        if(this.current_token.type != Compiler.TokenType.TK_EOF){
            this.error(Compiler.ErrorCode.UNEXPECTED_TOKEN, this.current_token);
        }
        return function_definition_nodes;
    }
}
