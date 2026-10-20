package com.ohernandeze1.automatas.automata;

public class VendingStateMachine {
    private VendingState current = VendingState.E0_ESPERANDO_MONEDAS;
    private String lastTransition = "Inicio -> E0";

    public VendingState current() {
        return current;
    }

    public String lastTransition() {
        return lastTransition;
    }

    public void onCoinInserted() {
        if (current == VendingState.E0_ESPERANDO_MONEDAS || current == VendingState.E1_MONEDA_INGRESADA) {
            move(VendingState.E1_MONEDA_INGRESADA);
        }
    }

    public void onSelection() {
        if (current == VendingState.E1_MONEDA_INGRESADA || current == VendingState.E2_PRODUCTO_SELECCIONADO) {
            move(VendingState.E2_PRODUCTO_SELECCIONADO);
        }
    }

    public void onPurchaseStart() {
        if (current == VendingState.E2_PRODUCTO_SELECCIONADO) {
            move(VendingState.E3_PROCESANDO_COMPRA);
        }
    }

    public void onDispense() {
        if (current == VendingState.E3_PROCESANDO_COMPRA) {
            move(VendingState.E4_ENTREGANDO_PRODUCTO);
        }
    }

    public void onReturnMoney() {
        if (current == VendingState.E1_MONEDA_INGRESADA || current == VendingState.E2_PRODUCTO_SELECCIONADO
                || current == VendingState.E4_ENTREGANDO_PRODUCTO || current == VendingState.E3_PROCESANDO_COMPRA) {
            move(VendingState.E5_DEVOLVIENDO_DINERO);
        }
    }

    public void reset() {
        move(VendingState.E0_ESPERANDO_MONEDAS);
    }

    private void move(VendingState next) {
        VendingState prev = current;
        current = next;
        lastTransition = prev + " -> " + next;
    }
}
