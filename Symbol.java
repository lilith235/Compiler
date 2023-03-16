import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//符号表用于语义分析
public class Symbol {
    String name;
    String type;
    public Symbol(String name, String type){
        this.name = name;
        this.type = type;
    }
}

class Function_Symbol extends Symbol{
    public List<Symbol> formal_parameters; //VarSymbol对象的列表
    public Block_Node block_ast; //对过程主体的引用(AST子树)
    public Function_Symbol(String name, List<Symbol> formal_params){
        super(name, null);
        if(formal_params == null){
            this.formal_parameters = new ArrayList<>();
        }
        else{
            this.formal_parameters = formal_params;
        }
        block_ast = null;
    }

}

class Var_Symbol extends Symbol{
    public int offset;
    public Symbol symbol; //offset from RBP
    public  Var_Symbol(String var_name, String var_type, int var_offset){
        super(var_name, var_type);
        this.offset = var_offset;
        this.symbol = null;
    }
}

class Parameter_Symbol extends Symbol{
    public int offset;
    public Parameter_Symbol(String parameter_name, String parameter_type, int parameter_offset){
        super(parameter_name,parameter_type);
        this.offset = parameter_offset;
    }
}

class ScopedSymbolTable{
    public Map<String, Symbol> _symbols;
    public String scope_name;
    public int scope_level;
    public ScopedSymbolTable enclosing_scope;
    public boolean current_scope_only = false;
    public ScopedSymbolTable(String scope_name, int scope_level, ScopedSymbolTable enclosing_scope){
        _symbols = new HashMap();
        this.scope_name = scope_name;
        this.scope_level = scope_level;
        this.enclosing_scope = enclosing_scope;
    }

    public void insert(Symbol symbol){
        this._symbols.put(symbol.name, symbol);
    }

//    'symbol'要么是symbol类的实例，要么是null
    public Symbol lookup(String name, boolean current_scope_only){
        Symbol symbol = this._symbols.get(name);
        this.current_scope_only = current_scope_only; //false
        if(symbol != null){
            return symbol;
        }
        if(current_scope_only){
            return null;
        }
//        递归地沿着链向上查找名称
        if(this.enclosing_scope != null){
            return this.enclosing_scope.lookup(name, false);
        }
        return null;
    }
}
