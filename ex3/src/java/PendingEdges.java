import mySymbol.Env;
import mySymbol.Type;
import mySymbol.Expression;
import java.util.List;

public class PendingEdges {
    String callSiteId;
    Env callerEnv;
    int callSiteLine;
    List<Expression> params;

    String calleeName;

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
