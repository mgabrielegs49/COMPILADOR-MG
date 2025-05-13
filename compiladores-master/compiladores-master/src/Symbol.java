package src;

public class Symbol {
    public final String name;
    public final String type;

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Symbol{name='" + name + "', type='" + type + "'}";
    }
}
