package objects;

public class scope {
    String name;
    String type;
    String scope;
    String position;
    boolean isInit;
    boolean isUsed;

    public scope(String name, String type, String scope, String position, boolean isInit, boolean isUsed) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.position = position;
        this.isInit = isInit;
        this.isUsed = isUsed;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setUsed(boolean isUsed) {
        this.isInit = isUsed;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
