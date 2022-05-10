package objects;

public class genTable {
    String tempName = "";
    String varName = "";
    String address = "";

    int jumpDist = 0;

    public genTable(String tempName, String varName, String address) {
        this.tempName = tempName;
        this.varName = varName;
        this.address = address;
    }

    public genTable(String tempName, int jumpDist) {
        this.tempName = tempName;
        this.jumpDist = jumpDist;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setJumpDist(int jumpDist) {
        this.jumpDist = jumpDist;
    }

    public String getTempName() {
        return this.tempName;
    }

    public String getVarName() {
        return this.varName;
    }

    public String getAddress() {
        return this.address;
    }

    public int getJumpDist() {
        return this.jumpDist;
    }
}
