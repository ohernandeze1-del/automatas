package com.ohernandeze1.automatas.simulator;

import com.ohernandeze1.automatas.automata.VendingState;
import com.ohernandeze1.automatas.automata.VendingStateMachine;
import com.ohernandeze1.automatas.model.Product;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VendingMachine {
    private final Map<String, Product> inventory = new LinkedHashMap<>();
    private final VendingStateMachine stateMachine = new VendingStateMachine();
    private int balance;
    private String selectedCode;
    private boolean dispensedInSelection;
    private String lastDispensedProduct = "Ninguno";
    private int lastReturnedAmount;

    public Map<String, Product> getInventory() { return inventory; }
    public Collection<Product> products() { return inventory.values(); }
    public VendingStateMachine getStateMachine() { return stateMachine; }
    public int getBalance() { return balance; }
    public String getSelectedCode() { return selectedCode; }
    public String getLastDispensedProduct() { return lastDispensedProduct; }
    public int getLastReturnedAmount() { return lastReturnedAmount; }

    public SimulationResult addProduct(String code, String name, int price, int quantity) {
        if (inventory.containsKey(code)) return SimulationResult.fail("Código duplicado: " + code);
        if (price <= 0) return SimulationResult.fail("El precio debe ser mayor que 0");
        if (quantity < 0) return SimulationResult.fail("La cantidad no puede ser negativa");
        inventory.put(code, new Product(code, name, price, quantity));
        return SimulationResult.ok("Producto agregado", 0);
    }

    public SimulationResult loadProduct(String code, int quantity) {
        Product product = inventory.get(code);
        if (product == null) return SimulationResult.fail("Producto desconocido: " + code);
        if (quantity <= 0) return SimulationResult.fail("La carga debe ser mayor que 0");
        product.addStock(quantity);
        return SimulationResult.ok("Inventario actualizado", 0);
    }

    public SimulationResult insertCoin(int value) {
        if (value != 1 && value != 5 && value != 10) {
            return SimulationResult.fail("Moneda inválida: " + value);
        }
        balance += value;
        stateMachine.onCoinInserted();
        return SimulationResult.ok("Moneda ingresada", 0);
    }

    public SimulationResult selectProduct(String code) {
        if (!inventory.containsKey(code)) return SimulationResult.fail("Producto desconocido: " + code);
        selectedCode = code;
        dispensedInSelection = false;
        if (balance > 0) {
            stateMachine.onSelection();
        }
        return SimulationResult.ok("Producto seleccionado: " + code, 0);
    }

    public SimulationResult purchase() {
        if (selectedCode == null) return SimulationResult.fail("No hay producto seleccionado");
        if (dispensedInSelection) return SimulationResult.fail("Compra duplicada: ya se dispensó el producto seleccionado");
        Product product = inventory.get(selectedCode);
        if (product == null) return SimulationResult.fail("Producto desconocido: " + selectedCode);
        if (product.getStock() <= 0) return SimulationResult.fail("Producto agotado: " + selectedCode);
        if (balance < product.getPrice()) {
            int missing = product.getPrice() - balance;
            return SimulationResult.fail("Saldo insuficiente: precio Q" + product.getPrice() + ", saldo Q" + balance + ", faltan Q" + missing);
        }

        stateMachine.onPurchaseStart();
        product.removeOne();
        balance -= product.getPrice();
        dispensedInSelection = true;
        stateMachine.onDispense();

        int change = balance;
        balance = 0;
        lastDispensedProduct = product.getCode() + " - " + product.getName();
        lastReturnedAmount = change;
        selectedCode = null;
        stateMachine.onReturnMoney();
        stateMachine.reset();
        return SimulationResult.ok("Producto dispensado: " + lastDispensedProduct + " | Ruta: E3->E4->E5->E0", change);
    }

    public SimulationResult cancel() {
        int refund = balance;
        balance = 0;
        selectedCode = null;
        dispensedInSelection = false;
        lastReturnedAmount = refund;
        stateMachine.onReturnMoney();
        stateMachine.reset();
        return SimulationResult.ok("Operación cancelada", refund);
    }

    public String showStatus() {
        String inventoryStatus = inventory.values().stream()
                .map(p -> p.getCode() + "(" + p.getStock() + ")")
                .collect(Collectors.joining(", "));
        return "Saldo=Q" + balance + ", selección=" + (selectedCode == null ? "ninguna" : selectedCode)
                + ", estado=" + stateMachine.current() + ", inventario=[" + inventoryStatus + "]";
    }

    public void restoreState(int balance, String selectedCode, VendingState state) {
        this.balance = Math.max(balance, 0);
        this.selectedCode = selectedCode;
        this.dispensedInSelection = false;
        this.lastReturnedAmount = 0;
        this.lastDispensedProduct = "Ninguno";
        this.stateMachine.setCurrent(state);
    }

    public void resetMachine() {
        inventory.clear();
        balance = 0;
        selectedCode = null;
        dispensedInSelection = false;
        lastDispensedProduct = "Ninguno";
        lastReturnedAmount = 0;
        stateMachine.reset();
    }
}
