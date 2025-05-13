package src;

public class Symbol {
    public final String name;
    public final String type;
    public final boolean isArray;
    public final int size;

    public Symbol(String name, String type) {
        this(name, type, false, -1);
    }
    
    public Symbol(String name, String type, boolean isArray, int size) {
        this.name = name;
        this.type = type;
        this.isArray = isArray;
        this.size = size;
    }

    @Override
    public String toString() {
        if (isArray) {
            return type + "[" + (size > 0 ? size : "") + "]";
        }
        return type;
    }
}