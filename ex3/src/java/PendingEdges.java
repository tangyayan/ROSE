import mySymbol.Env;
import mySymbol.Type;
import mySymbol.Expression;
import java.util.List;

/**
 * 用于记录在构建调用图过程中尚未处理的边
 */
public class PendingEdges {
    String callSiteId;
    Env callerEnv;
    int callSiteLine;
    List<Expression> params;

    String calleeName;

    /**
     * 构造器
     * @param callSiteId 调用点唯一标识
     * @param callerEnv 调用点所在环境
     * @param callSiteLine 调用点所在行号
     * @param params 调用点参数列表
     * @param calleeName 被调用函数名称
     */
    public PendingEdges(String callSiteId, Env callerEnv, int callSiteLine, List<Expression> params, String calleeName) {
        this.callSiteId = callSiteId;
        this.callerEnv = callerEnv;
        this.callSiteLine = callSiteLine;
        this.params = params;
        this.calleeName = calleeName;
    }

     public String getCallSiteId() {
        return callSiteId;
    }

    public Env getCallerEnv() {
        return callerEnv;
    }

    public int getCallSiteLine() {
        return callSiteLine;
    }

    public List<Expression> getParams() {
        return params;
    }

    public String getCalleeName() {
        return calleeName;
    }
}
