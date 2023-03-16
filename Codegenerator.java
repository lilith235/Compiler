
//代码生成器

import java.util.List;

public class Codegenerator extends NodeVisitor{


//    将' n '四舍五入到' align '的最接近倍数。如Align_to(5,8)返回8,Align_to(11,8)返回16。
    public int align_to(int n, int align){
        return (int)(((n + align - 1) / align) * align);
    }

    public void visit_UnaryOp_Node(UnaryOp_Node node){
        this.visit(node.right);
        if(node.op.type == Compiler.TokenType.TK_MINUS){
            System.out.println("  neg %rax");
        }
    }

    public void visit_Return_Node(Return_Node node){
        this.visit(node.right);
        if(node.token.type == Compiler.TokenType.TK_RETURN){
            System.out.println("  jmp ." + node.function_name + ".return");
        }
    }

    public void visit_BinaryOp_Node(BinaryOp_Node node){
        this.visit(node.right);
        System.out.println("  push %rax");
        this.visit(node.left);
        System.out.println("  pop %rdi");
        if(node.op.type == Compiler.TokenType.TK_PLUS){
            System.out.println("  add %rdi, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_MINUS){
            System.out.println("  sub %rdi, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_MUL){
            System.out.println("  imul %rdi, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_DIV){
            System.out.println("  cqo");
            System.out.println("  idiv %rdi");
        }
        else if(node.op.type == Compiler.TokenType.TK_EQ){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  sete %al");
            System.out.println("  movzb %al, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_NE){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  setne %al");
            System.out.println("  movzb %al, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_NE){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  setne %al");
            System.out.println("  movzb %al, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_LT){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  setl %al");
            System.out.println("  movzb %al, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_GT){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  setg %al");
            System.out.println("  movzb %al, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_LE){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  setle %al");
            System.out.println("  movzb %al, %rax");
        }
        else if(node.op.type == Compiler.TokenType.TK_GE){
            System.out.println("  cmp %rdi, %rax");
            System.out.println("  setge %al");
            System.out.println("  movzb %al, %rax");
        }


    }

    public void visit_Assign_Node(Assign_Node node){
        if(node.left.token != null){
            if(node.left.token.type == Compiler.TokenType.TK_IDENT){//var is left-value
                Var_Node var_node = (Var_Node) node.left;
                Parameter_Symbol vp= (Parameter_Symbol) var_node.symbol;
                int var_offset = vp.offset;
                System.out.println("  lea "+ var_offset +"(%rbp), %rax");
//            left-value
                System.out.println("  push %rax");

                this.visit(node.right);
                System.out.println("  pop %rdi");
                System.out.println("  mov %rax, (%rdi)");
            }
            else {
                System.out.println("错误: 不是左值");
            }
        }
    }

    public void visit_Num_Node(Num_Node node){
        System.out.println("  mov $"+ node.value +", %rax");
    }

    public void visit_If_Node(If_Node node){
        Compiler.Count_i += 1;
        this.visit(node.condition);
        System.out.println("  cmp $0, %rax");
        System.out.println("  je  .L.else." + Compiler.Count_i);
        if(node.then_statement != null){
            this.visit(node.then_statement);
        }
        System.out.println("  jmp .L.end." + Compiler.Count_i);
        System.out.println(".L.else." + ":");
        if(node.else_statement != null){
            this.visit(node.else_statement);
        }
        System.out.println(".L.end." +  Compiler.Count_i + ":");
    }

    public void visit_Block_Node(Block_Node node){
        for(ASTNodeType eachnode: node.statement_nodes){
            this.visit(eachnode);
        }
    }

    public void visit_Var_Node(Var_Node node){
//        var is right-value
        int var_offset;
        if(node.symbol.getClass().getSimpleName() == "Var_Symbol"){
            Var_Symbol p = (Var_Symbol) node.symbol;
            var_offset = p.offset;
        }
        else {
            Parameter_Symbol p = (Parameter_Symbol) node.symbol;
            var_offset = p.offset;
        }
        System.out.println("  l  ea " + var_offset + "(%rbp), %rax");
//        right-value
        System.out.println("  mov (%rax), %rax");
    }

    //这里改过去
    public void visit_VarDecl_Node(VarDecl_Node node){
//        System.out.println("好像可以");
    }

    public void visit_FormalParam_Node(FormalParam_Node node){

    }

    public void visit_FunctionCall_Node(FunctionCall_Node node){
        int nparams = 0;
        for(ASTNodeType eachnode: node.actual_parameter_nodes){
            this.visit(eachnode);
            System.out.println("  push %rax");
            nparams += 1;
        }
//        range一般是三个参数，最后的1通常省略。如range(1,6,1)可简写为range(1,6)表示1到5的序列。
//        就像是数学中的区间---前闭后开
//        如果是5到1倒着取，则应写为range(5,0,-1)
        for(int i=nparams-1; i>=0; i--){
            System.out.println("  pop %" + Compiler.parameter_registers[i]);
        }
        System.out.println("  mov $0, %rax");
        System.out.println("  call " + node.function_name);
    }

    public void visit_FunctionDef_Node(FunctionDef_Node node){
//        leon:初始化每个函数的偏移量
        Compiler.Offset_num = 0;
        System.out.println("  .text");
        System.out.println("  .globl " + node.function_name);
        System.out.println(node.function_name);
//        Prologue
        System.out.println("  push %rbp");
        System.out.println("  mov %rsp, %rbp");
        int stack_size = this.align_to(node.offset, 16);
        System.out.println("  sub $"+stack_size+", %rsp");

        int i=0;
        for(ASTNodeType eachparam: node.formal_parameters){
            FormalParam_Node fpn = (FormalParam_Node) eachparam;
            int parameter_offset = fpn.parameter_symbol.offset;
            System.out.println("  mov %" + Compiler.parameter_registers[i] + "," + parameter_offset + "(%rbp)");
            i += 1;
        }

//        Visit function block
        this.visit(node.block_node);
        System.out.println("." + node.function_name + ".return:");
        System.out.println("  mov %rbp, %rsp");
        System.out.println("  pop %rbp");
        System.out.println("  ret");
    }

    public void code_generate(List<ASTNodeType> tree){
//        遍历AST以打印出程序集
        for(ASTNodeType node: tree){
            if(node != null){
                this.visit(node);
            }
        }
    }

















}
