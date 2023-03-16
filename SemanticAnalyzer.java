import java.util.List;

import static java.lang.System.exit;

//语义分析器
public class SemanticAnalyzer extends NodeVisitor {
    public ScopedSymbolTable current_scope;
    public ScopedSymbolTable global_scope;

    public SemanticAnalyzer(){
        this.current_scope = null;
//        全局作用域，级别为0  其中包含全局变量和函数
        this.global_scope = new ScopedSymbolTable("global", 0, this.current_scope);
        this.current_scope = global_scope;
    }

    public void log(String msg){
        if(Compiler._SHOULD_LOG_SCOPE){
            System.out.println(msg);
        }
    }

    public void visit_UnaryOp_Node(UnaryOp_Node node){

    }

    public void visit_Return_Node(Return_Node node){
        this.visit(node.right);
    }

    public void visit_BinaryOp_Node(BinaryOp_Node node){
        this.visit(node.left);
        this.visit(node.right);
    }

    public void visit_Assign_Node(Assign_Node node){
//        确保assign的左边是一个变量
        if(node.left.token != null){
            if(node.left.token.type != Compiler.TokenType.TK_IDENT){
                System.out.println("assign的左边不是一个变量");
            }
        }
        this.visit(node.left);
        this.visit(node.right);
    }

    public void visit_If_Node(If_Node node){
        this.visit(node.condition);
        if(node.then_statement != null){
            this.visit(node.then_statement);
        }
        if(node.else_statement != null){
            this.visit(node.else_statement);
        }
    }

    public void visit_Block_Node(Block_Node node){
        String block_name = this.current_scope.scope_name + "block" + (this.current_scope.scope_level+1);
        this.log("ENTER scope:"+block_name);
        ScopedSymbolTable block_scope = new ScopedSymbolTable(block_name, (this.current_scope.scope_level+1), this.current_scope);
        this.current_scope = block_scope;
        for(ASTNodeType eachnode : node.statement_nodes){
            this.visit(eachnode);
        }
        this.current_scope = this.current_scope.enclosing_scope;
        this.log("LEAVE scope:"+block_name);
    }

    public void visit_Num_Node(Num_Node node){

    }

    public void visit_Var_Node(Var_Node node){
        String var_name = node.value;
        Symbol var_symbol = this.current_scope.lookup(var_name,false);
        if(var_symbol == null){
            System.out.println("语义错误，变量未声明");
            exit(1);
        }
        else {
            node.symbol = var_symbol;
        }
    }

    public void visit_VarDecl_Node(VarDecl_Node node){
        String var_name = node.var_node.value;
        String var_type = node.type_node.value;
        Compiler.Offset_num += 8;
        int var_offset = -Compiler.Offset_num;
        Var_Symbol var_symbol = new Var_Symbol(var_name, var_type, var_offset);
        this.current_scope.insert(var_symbol);
    }

    public void visit_FormalParam_Node(FormalParam_Node node){
        String parameter_name = node.parameter_node.value;
        String parameter_type = node.type_node.value;
        Compiler.Offset_num += 8;
        int parameter_offset = -Compiler.Offset_num;
        Parameter_Symbol parameter_symbol = new Parameter_Symbol(parameter_name, parameter_type, parameter_offset);
        this.current_scope.insert(parameter_symbol);
        node.parameter_symbol = parameter_symbol;
    }

    public void visit_FunctionDef_Node(FunctionDef_Node node){
//        leon: 初始化每个函数的偏移量
        Compiler.Offset_num = 0;
        String function_name = node.function_name;
        Function_Symbol function_symbol = new Function_Symbol(function_name,null);
        this.current_scope.insert(function_symbol);
        this.log("ENTER scope:" + function_name);
        ScopedSymbolTable function_scope = new ScopedSymbolTable(function_name, (this.current_scope.scope_level + 1), this.current_scope);
        this.current_scope = function_scope;

//       将formal_parameters插入函数作用域
        for(ASTNodeType eachparam: node.formal_parameters){
            this.visit(eachparam);
        }
        this.visit(node.block_node); //访问函数块
        node.offset = Compiler.Offset_num;
        this.current_scope = this.current_scope.enclosing_scope;
        this.log("LEAVE scope:"+function_name);
//        由解释器在执行过程调用时访问
        function_symbol.block_ast = node.block_node;
    }

    public void visit_FunctionCall_Node(FunctionCall_Node node){

    }

    public void semantic_analyze(List<ASTNodeType> tree){
//        遍历AST构造符号表
        for(ASTNodeType node: tree){
            if(node != null){
                this.visit(node);
            }
        }
    }
}


