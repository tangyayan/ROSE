import java.lang.reflect.Array;

import mySymbol.*;

public class TypeExample {
    public static void main(String[] args) throws Exception {
        // 初始化全局环境
        Env.reset();
        Env globalEnv = Env.getCurrentEnv();

        // ========== 1. 添加原始类型 ==========
        System.out.println("=== Adding Primitive Types ===");
        
        Type intType = new PrimitiveType("INTEGER");
        Type boolType = new PrimitiveType("BOOLEAN");
        
        // 原始类型也可以作为"类型符号"添加
        globalEnv.addSymbol("INTEGER", intType);
        globalEnv.addSymbol("BOOLEAN", boolType);

        // ========== 2. 创建并添加别名类型 ==========
        System.out.println("\n=== Adding Alias Types ===");
        
        // 别名 1: MyInt = INTEGER
        AliasType myInt = new AliasType("MyInt", intType);
        globalEnv.addSymbol("MyInt", myInt);
        System.out.println("Added: " + myInt);
        
        // 别名 2: MyBool = BOOLEAN
        AliasType myBool = new AliasType("MyBool", boolType);
        globalEnv.addSymbol("MyBool", myBool);
        System.out.println("Added: " + myBool);
        
        // 别名 3: 基于别名的别名
        AliasType myInt2 = new AliasType("MyInt2", myInt);
        globalEnv.addSymbol("MyInt2", myInt2);
        System.out.println("Added: " + myInt2);

        AliasType myInt3 = new AliasType("MyInt3", myInt2);
        globalEnv.addSymbol("MyInt3", myInt3);
        System.out.println("Added: " + myInt3);

        System.out.println(myInt3.equals(myInt2));

        // ========== 3. 查找别名类型 ==========
        System.out.println("\n=== Looking up Alias Types ===");
        
        TableSymbol found = globalEnv.lookup("MyInt3");
        if (found instanceof AliasType) {
            AliasType aliasType = (AliasType) found;
            System.out.println("Found: " + aliasType.getTypeName());
            System.out.println("Target Type: " + aliasType.getTargetType());
            System.out.println("Is Primitive: " + aliasType.isPrimitiveType());
        }

        // ========== 4. 创建数组类型 ==========
        System.out.println("\n=== Adding Array Types ===");
        
        // 使用别名创建数组: ARRAY 10 OF MyInt
        ArrayType myIntArray = new ArrayType(myInt);
        globalEnv.addSymbol("MyIntArray", myIntArray);
        System.out.println("Added: " + myIntArray.getTypeName());

        ArrayType myInt2Array = new ArrayType(myInt2);
        globalEnv.addSymbol("MyInt2Array", myInt2Array);
        System.out.println(myInt2Array.equals(myIntArray));

        // ========== 5. 打印符号表 ==========
        System.out.println("\n=== Global Symbol Table ===");
        globalEnv.print();

        // ========== 6. 进入函数作用域 ==========
        System.out.println("\n=== Entering Function Scope ===");
        Env.enterEnv("FUNCTION add");
        Env funcEnv = Env.getCurrentEnv();
        System.out.println(funcEnv);
        
        // 在函数作用域中使用别名类型声明变量
        Var x = new Var("x", myInt);
        Var y = new Var("y", myInt);
        
        funcEnv.addSymbol("x", x);
        funcEnv.addSymbol("y", y);
        
        funcEnv.print();

        // ========== 7. 在函数作用域中查找（会查找到全局） ==========
        System.out.println("\n=== Lookup from Function Scope ===");
        TableSymbol xSymbol = funcEnv.lookup("x");
        System.out.println("Found in function: " + xSymbol);
        
        TableSymbol myIntFromFunc = funcEnv.lookup("MyInt");
        System.out.println("Found in global (from function): " + myIntFromFunc);

        // ========== 8. 退出函数作用域 ==========
        System.out.println("\n=== Exiting Function Scope ===");
        Env.exitEnv();
        System.out.println("Current scope: " + Env.getCurrentEnv().getScopeName());
    }
}