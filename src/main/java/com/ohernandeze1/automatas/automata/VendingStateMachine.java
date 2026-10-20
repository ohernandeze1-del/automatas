package com.ohernandeze1.automatas.automata;

import java.util.EnumMap;
import java.util.Map;

public class VendingStateMachine {
    public enum Event {
        MONEDA, SELECCION, INICIO_COMPRA, DISPENSAR, DEVOLVER, RESET
    }

    private final Map<VendingState, Map<Event, VendingState>> transitions = new EnumMap<>(VendingState.class);
    private VendingState current = VendingState.E0_ESPERANDO_MONEDAS;
    private String lastTransition = "Inicio -> E0";

    public VendingStateMachine() {
        for (VendingState state : VendingState.values()) {
            transitions.put(state, new EnumMap<>(Event.class));
        }
        transitions.get(VendingState.E0_ESPERANDO_MONEDAS).put(Event.MONEDA, VendingState.E1_MONEDA_INGRESADA);
        transitions.get(VendingState.E1_MONEDA_INGRESADA).put(Event.MONEDA, VendingState.E1_MONEDA_INGRESADA);
        transitions.get(VendingState.E1_MONEDA_INGRESADA).put(Event.SELECCION, VendingState.E2_PRODUCTO_SELECCIONADO);
        transitions.get(VendingState.E2_PRODUCTO_SELECCIONADO).put(Event.SELECCION, VendingState.E2_PRODUCTO_SELECCIONADO);
        transitions.get(VendingState.E2_PRODUCTO_SELECCIONADO).put(Event.INICIO_COMPRA, VendingState.E3_PROCESANDO_COMPRA);
        transitions.get(VendingState.E3_PROCESANDO_COMPRA).put(Event.DISPENSAR, VendingState.E4_ENTREGANDO_PRODUCTO);
        transitions.get(VendingState.E1_MONEDA_INGRESADA).put(Event.DEVOLVER, VendingState.E5_DEVOLVIENDO_DINERO);
        transitions.get(VendingState.E2_PRODUCTO_SELECCIONADO).put(Event.DEVOLVER, VendingState.E5_DEVOLVIENDO_DINERO);
        transitions.get(VendingState.E3_PROCESANDO_COMPRA).put(Event.DEVOLVER, VendingState.E5_DEVOLVIENDO_DINERO);
        transitions.get(VendingState.E4_ENTREGANDO_PRODUCTO).put(Event.DEVOLVER, VendingState.E5_DEVOLVIENDO_DINERO);
        for (VendingState state : VendingState.values()) {
            transitions.get(state).put(Event.RESET, VendingState.E0_ESPERANDO_MONEDAS);
        }
    }

    public VendingState current() {
        return current;
    }

    public String lastTransition() {
        return lastTransition;
    }

    public void onCoinInserted() { transition(Event.MONEDA); }

    public void onSelection() { transition(Event.SELECCION); }

    public void onPurchaseStart() { transition(Event.INICIO_COMPRA); }

    public void onDispense() { transition(Event.DISPENSAR); }

    public void onReturnMoney() { transition(Event.DEVOLVER); }

    public void reset() { transition(Event.RESET); }

    public void setCurrent(VendingState state) {
        VendingState previous = current;
        current = state == null ? VendingState.E0_ESPERANDO_MONEDAS : state;
        lastTransition = previous + " -> " + current;
    }

    private void transition(Event event) {
        VendingState next = transitions.get(current).get(event);
        if (next == null) {
            return;
        }
        VendingState previous = current;
        current = next;
        lastTransition = previous + " -> " + next;
    }
}
