package objects;

public class genTable {
    String tempName = "";
    String varName = "";
    int address = 0x00;
    token varToken = null;

    int jumpDist = 0;

    public genTable(String tempName, String varName, int address, token varToken) {
        this.tempName = tempName;
        this.varName = varName;
        this.address = address;
        this.varToken = varToken;
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

    public void setAddress(int address) {
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

    public int getAddress() {
        return this.address;
    }

    public int getJumpDist() {
        return this.jumpDist;
    }
}
