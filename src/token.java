public class token {

    private String type = "";
    private String value = "";
    private String line = "";

    public token(String type, String value, String line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public String getType(){
        return this.type;
    }

    public String getValue(){
        return this.value;
    }

    public String getLine(){
        return this.line;
    }
}
