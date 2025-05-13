package src;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> table = new HashMap<>();

    public void declare(String name, String type) {
        if (table.containsKey(name)) {
            throw new RuntimeException("Erro: variável '" + name + "' já declarada.");
        }
        table.put(name, new Symbol(name, type));
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
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
    }
}