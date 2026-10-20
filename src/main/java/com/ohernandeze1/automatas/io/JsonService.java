package com.ohernandeze1.automatas.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.ohernandeze1.automatas.automata.VendingState;
import com.ohernandeze1.automatas.model.Product;
import com.ohernandeze1.automatas.simulator.SimulationResult;
import com.ohernandeze1.automatas.simulator.VendingMachine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonService {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void exportMachine(VendingMachine machine, Path path) throws IOException {
        MachineSnapshot snapshot = new MachineSnapshot();
        snapshot.balance = machine.getBalance();
        snapshot.selectedCode = machine.getSelectedCode();
        snapshot.automatonState = machine.getStateMachine().current().name();
        snapshot.products = new ArrayList<>();
        for (Product p : machine.products()) {
            snapshot.products.add(new ProductSnapshot(p.getCode(), p.getName(), p.getPrice(), p.getStock()));
        }
        Files.writeString(path, gson.toJson(snapshot), StandardCharsets.UTF_8);
    }

    public VendingMachine importMachine(Path path) throws IOException {
        String json = Files.readString(path, StandardCharsets.UTF_8);
        if (json == null || json.isBlank()) {
            throw new IOException("El archivo JSON está vacío.");
        }

        MachineSnapshot snapshot;
        try {
            snapshot = gson.fromJson(json, MachineSnapshot.class);
        } catch (JsonParseException ex) {
            throw new IOException("JSON mal formado: " + ex.getMessage(), ex);
        }

        if (snapshot == null) {
            throw new IOException("No se pudo interpretar el contenido JSON.");
        }

        VendingMachine machine = new VendingMachine();
        if (snapshot.products != null) {
            for (ProductSnapshot p : snapshot.products) {
                if (p == null) {
                    throw new IOException("Se encontró un producto nulo en el JSON.");
                }
                SimulationResult addResult = machine.addProduct(p.code, p.name, p.price, p.stock);
                if (!addResult.success()) {
                    throw new IOException("Producto inválido en JSON: " + addResult.message());
                }
            }
        }

        VendingState state;
        try {
            state = snapshot.automatonState == null ? VendingState.E0_ESPERANDO_MONEDAS : VendingState.valueOf(snapshot.automatonState);
        } catch (IllegalArgumentException ex) {
            throw new IOException("Estado de autómata inválido en JSON: " + snapshot.automatonState, ex);
        }

        machine.restoreState(snapshot.balance, snapshot.selectedCode, state);
        return machine;
    }

    private static class MachineSnapshot {
        int balance;
        String selectedCode;
        String automatonState;
        List<ProductSnapshot> products;
    }

    private static class ProductSnapshot {
        String code;
        String name;
        int price;
        int stock;

        ProductSnapshot() {
        }

        ProductSnapshot(String code, String name, int price, int stock) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }
}
