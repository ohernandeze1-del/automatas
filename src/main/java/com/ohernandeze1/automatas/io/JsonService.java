package com.ohernandeze1.automatas.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ohernandeze1.automatas.model.Product;
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
        snapshot.products = new ArrayList<>();
        for (Product p : machine.products()) {
            snapshot.products.add(new ProductSnapshot(p.getCode(), p.getName(), p.getPrice(), p.getStock()));
        }
        Files.writeString(path, gson.toJson(snapshot), StandardCharsets.UTF_8);
    }

    public VendingMachine importMachine(Path path) throws IOException {
        String json = Files.readString(path, StandardCharsets.UTF_8);
        MachineSnapshot snapshot = gson.fromJson(json, MachineSnapshot.class);
        VendingMachine machine = new VendingMachine();
        if (snapshot.products != null) {
            for (ProductSnapshot p : snapshot.products) {
                machine.addProduct(p.code, p.name, p.price, p.stock);
            }
        }
        machine.restoreState(snapshot.balance, snapshot.selectedCode);
        return machine;
    }

    private static class MachineSnapshot {
        int balance;
        String selectedCode;
        List<ProductSnapshot> products;
    }

    private static class ProductSnapshot {
        String code;
        String name;
        int price;
        int stock;

        ProductSnapshot(String code, String name, int price, int stock) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }
}
