package com.ohernandeze1.automatas.vm.semantic;

import com.ohernandeze1.automatas.vm.ast.*;

import java.util.*;

public class SemanticValidator {
    public List<String> validate(List<Instruction> instructions) {
        List<String> errors = new ArrayList<>();
        Set<String> declaredProducts = new HashSet<>();

        for (Instruction instruction : instructions) {
            if (instruction instanceof ProductInstruction product) {
                if (product.price() <= 0) errors.add(lineErr(product.line(), "El precio del producto debe ser > 0"));
                if (product.quantity() < 0) errors.add(lineErr(product.line(), "La cantidad inicial no puede ser negativa"));
                if (!declaredProducts.add(product.code())) errors.add(lineErr(product.line(), "Código de producto duplicado: " + product.code()));
            } else if (instruction instanceof LoadInstruction load) {
                if (!declaredProducts.contains(load.code())) errors.add(lineErr(load.line(), "CARGAR usa producto desconocido: " + load.code()));
                if (load.quantity() <= 0) errors.add(lineErr(load.line(), "La cantidad de CARGAR debe ser > 0"));
            } else if (instruction instanceof CoinInstruction coin) {
                if (coin.value() != 1 && coin.value() != 5 && coin.value() != 10) errors.add(lineErr(coin.line(), "MONEDA inválida: " + coin.value()));
            } else if (instruction instanceof SelectInstruction select) {
                if (!declaredProducts.contains(select.code())) errors.add(lineErr(select.line(), "SELECCIONAR usa producto desconocido: " + select.code()));
            }
        }

        return errors;
    }

    private String lineErr(int line, String msg) {
        return "Línea " + line + ": " + msg;
    }
}
