package mySymbol;

import java.util.*;
import exceptions.*;

/**
 * 环境/作用域 - 用于符号表管理
 */
public class Env {
    private Hashtable<String, TableSymbol> table;
    private Env father;
    private String scopeName;
    private static Env currentEnv = null; 

    /**
     * 构造器
     * @param scopeName 作用域名称
     * @param father 父作用域
     */
    private Env(String scopeName, Env father) {
        this.table = new Hashtable<>();
        this.scopeName = scopeName;
        this.father = father;
    }

    /**
     * 进入新作用域
     */
    public static void enterEnv(String scopeName) {
        Env newEnv = new Env(scopeName, currentEnv);
        currentEnv = newEnv;
    }

    /**
     * 退出当前作用域
     */
    public static void exitEnv() {
        if (currentEnv != null && currentEnv.father != null) {
            currentEnv = currentEnv.father;
        }
    }

    /**
     * 获取当前环境
     */
    public static Env getCurrentEnv() {
        if (currentEnv == null) {
            currentEnv = new Env("GLOBAL", null);
        }
        return currentEnv;
    }

    /**
     * 重置环境（用于测试）
     */
    public static void reset() {
        currentEnv = new Env("GLOBAL", null);
    }

    /**
     * 添加符号到当前作用域
     */
    public void addSymbol(String name, TableSymbol symbol) throws SyntacticException {
        if (table.containsKey(name)) {
            // TODO: 错误恢复
            // throw new SyntacticException( "Symbol '" + name + "' already defined in scope '" + scopeName + "'");
        }
        table.put(name, symbol);
    }

    /**
     * 静态方法添加符号到当前作用域
     * @param name 符号名称
     * @param symbol 符号对象
     * @throws SyntacticException 如果符号已存在于当前作用域中
     */
    public static void addSymbol_s(String name, TableSymbol symbol) throws SyntacticException {
        getCurrentEnv().addSymbol(name, symbol);
    }

    /**
     * 在当前作用域查找符号
     */
    public TableSymbol lookupLocal(String name) {
        return table.get(name);
    }

    /**
     * 递归查找符号（包含父作用域）
     * @return 符号对象，如果未找到则返回null
     */
    public TableSymbol lookup(String name) {
        TableSymbol symbol = table.get(name);
        Env pre = this.father;
        while(symbol == null && pre != null) {
            symbol = pre.table.get(name);
            pre = pre.father;
        }
        return symbol;
    }

    /**
     * 获取作用域名称
     */
    public String getScopeName() {
        return scopeName;
    }

    /**
     * 获取所有符号
     */
    public Map<String, TableSymbol> getSymbols() {
        return new Hashtable<>(table);
    }

    public Env getFather() {
        return father;
    }

    /**
     * 打印符号表
     */
    public void print() {
        System.out.println("=== Scope: " + scopeName + " ===");
        for (Map.Entry<String, TableSymbol> entry : table.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    /**
     * 返回作用域的全路径名称（包含父作用域）
     */
    @Override
    public String toString() {
        Env pre = this;
        if(pre.father == null) {
            return this.scopeName;
        }
        String sn = this.scopeName;
        while(pre.father.father != null) { // 除去GLOBAL作用域
            pre = pre.father;
            sn = pre.scopeName + "." + sn;
        }
        return sn;
    }
}