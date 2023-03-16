import java.util.List;


//抽象语法树（Abstract Syntax Tree）
public class ASTNodeType {
    public Token token;
}

class UnaryOp_Node extends ASTNodeType{
    public Token token;
    public Token op;
    public ASTNodeType right;
    public UnaryOp_Node(Token op, ASTNodeType right){
        this.token = op;
        this.op = op;
        this.right = right;
    }
}

class If_Node extends ASTNodeType{
    public ASTNodeType condition;
    public ASTNodeType then_statement;
    public ASTNodeType else_statement;
    public If_Node(ASTNodeType condition,ASTNodeType then_statement,ASTNodeType else_statement){
        this.condition = condition;
        this.then_statement = then_statement;
        this.else_statement = else_statement;
    }
}

class Return_Node extends ASTNodeType{
    public Token token;
    public ASTNodeType right;
    public String function_name;
    public Return_Node(Token tok, ASTNodeType right, String function_name){
        this.token = tok;
        this.right = right;
        this.function_name = function_name;
    }
}

class Block_Node extends ASTNodeType{
    public Token ltok;
    public Token rtok;
    public List<ASTNodeType> statement_nodes;
    public Block_Node(Token ltok, Token rtok, List<ASTNodeType> statement_nodes){
        this.ltok = ltok;
        this.rtok = rtok;
        this.statement_nodes = statement_nodes;
    }
}

class BinaryOp_Node extends ASTNodeType {
    public ASTNodeType left;
    public ASTNodeType right;
    public Token token;
    public Token op;
    public BinaryOp_Node(ASTNodeType left, Token op, ASTNodeType right){
        this.left = left;
        this.token = op;
        this.op = op;
        this.right = right;
    }
}

class Assign_Node extends ASTNodeType{
    public ASTNodeType left;
    public ASTNodeType right;
    public Token token;
    public Token op;
    public Assign_Node(ASTNodeType left, Token op, ASTNodeType right){
        this.left = left;
        this.token = op;
        this.op = op;
        this.right = right;
    }
}

class FunctionCall_Node extends ASTNodeType{
    public String function_name;
    public List<ASTNodeType> actual_parameter_nodes;
    public Token token;
    public FunctionCall_Node(String function_name, List<ASTNodeType> actual_parameter_nodes, Token token){
        this.function_name = function_name;
        this.actual_parameter_nodes = actual_parameter_nodes;
        this.token = token;
    }
}


class Num_Node extends ASTNodeType{
    public Token token;
    public String value;
    public Num_Node(Token token){
        this.token = token;
        this.value = token.value;
    }
}

class Var_Node extends ASTNodeType{
//    Var节点是由ID token构建的。
    public Token token;
    public String value;
    public Symbol symbol;
    public Var_Node(Token token){
        this.token = token;
        this.value = token.value;
        this.symbol = null;
    }
}

class Type_Node extends ASTNodeType{
    public Token token;
    public String value;
    public Type_Node(Token token){
        this.token = token;
        this.value = token.value;
    }
}

class VarDecl_Node extends ASTNodeType{
    public Type_Node type_node;
    public Var_Node var_node;
    public VarDecl_Node(Type_Node type_node, Var_Node var_node){
        this.type_node = type_node;
        this.var_node = var_node;
    }
}

class FormalParam_Node extends ASTNodeType{
    public Type_Node type_node;
    public Var_Node parameter_node;
    public Parameter_Symbol parameter_symbol;
    public FormalParam_Node(Type_Node type_node, Var_Node parameter_node){
        this.type_node = type_node;
        this.parameter_node = parameter_node;
        this.parameter_symbol = null;
    }
}


class FunctionDef_Node extends ASTNodeType{
    public Type_Node type_node;
    public String function_name;
    public List<ASTNodeType> formal_parameters;
    public Block_Node block_node;
    public int offset;
    public FunctionDef_Node(Type_Node type_node, String function_name, List<ASTNodeType> formal_parameters, Block_Node block_node){
        this.type_node = type_node;
        this.function_name = function_name;
        this.formal_parameters = formal_parameters;
        this.block_node = block_node;
        this.offset = 0;
    }
}
