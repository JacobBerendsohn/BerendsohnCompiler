public class token {

    private String type = "";
    private String value = "";
    private String line = "";
    private int newPos = 0;

    public token(String type, String value, String line, int newPos) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.newPos = newPos;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public String getLine() {
        return this.line;
    }

    public int getNewPos(){
        return this.newPos;
    }
}
