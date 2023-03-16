import java.lang.reflect.Method;

public class NodeVisitor {
//   反射机制
    public String method_name;
    public Object visit(ASTNodeType node) {
        try{
            this.method_name = "visit_" + node.getClass().getSimpleName();
//            this.method_name = "visit_VarDecl_Node";
//        使用反射根据当前对象的函数名method_name获取对应的方法并执行
//            getMethod(String a, ParameterTypes b) a是方法名，b是该方法的参数列表的类型(这个b的类型要求非常严格，例如A是B的子类，参数对应的是A，用B就不行)
            Class argClass = node.getClass();
            Method method = this.getClass().getMethod(method_name, argClass);
            //第一个参数是说明哪个实例调用这个方法，第二个参数是该方法的参数列表
            return method.invoke(this,node);
        }
        catch (Exception e){
            generic_visit(node);
        }
        return null;
    }
    public void generic_visit(ASTNodeType node){
        System.out.println("没有 visit_" +  node.getClass().getSimpleName() + " 这个方法");
    }
}
