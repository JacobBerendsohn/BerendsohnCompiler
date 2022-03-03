import java.util.ArrayList;

public class node {
    String name = "";
    node parent = null;
    ArrayList<node> children = new ArrayList<>();
    token leafToken = null;
    Boolean isLeaf = false;

    /*
     * public node(String name, node parent, node child, token leafToken, Boolean
     * isLeaf) {
     * this.name = name;
     * this.parent = parent;
     * this.children.add(child);
     * if (isLeaf) {
     * this.leafToken = leafToken;
     * }
     * }
     */

    public node() {
    }
}
