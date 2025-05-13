package src;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> table = new HashMap<>();

    public void declare(String name, String type) {
        declare(name, type, false, -1);
    }

    public void declare(String name, String type, boolean isArray, int size) {
        if (table.containsKey(name)) {
            throw new RuntimeException("Erro: variável '" + name + "' já declarada.");
        }
        table.put(name, new Symbol(name, type, isArray, size));
    }

    public Symbol lookup(String name) {
        return table.get(name);
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }

    public void printTable() {
        System.out.println("Tabela de Símbolos:");
        for (Map.Entry<String, Symbol> entry : table.entrySet()) {
            Symbol sym = entry.getValue();
            String typeInfo = sym.isArray ? 
                sym.type + "[" + (sym.size > 0 ? sym.size : "") + "]" : 
                sym.type;
            System.out.println("  " + entry.getKey() + " -> " + typeInfo);
        }
    }
}