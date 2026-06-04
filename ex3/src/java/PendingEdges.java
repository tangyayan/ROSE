import mySymbol.Env;
import mySymbol.Type;
import java.util.List;

public class PendingEdges {
    String callSiteId;
    Env callerEnv;
    int callSiteLine;
    List<Type> paramTypes;

    String calleeName;

    public PendingEdges(String callSiteId, Env callerEnv, int callSiteLine, List<Type> paramTypes, String calleeName) {
        this.callSiteId = callSiteId;
        this.callerEnv = callerEnv;
        this.callSiteLine = callSiteLine;
        this.paramTypes = paramTypes;
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

    public List<Type> getParamTypes() {
        return paramTypes;
    }

    public String getCalleeName() {
        return calleeName;
    }
}
