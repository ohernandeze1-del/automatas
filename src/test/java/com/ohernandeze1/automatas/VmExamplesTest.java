package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.simulator.VendingMachine;
import com.ohernandeze1.automatas.vm.engine.ExecutionEngine;
import com.ohernandeze1.automatas.vm.lexer.Lexer;
import com.ohernandeze1.automatas.vm.parser.Parser;
import com.ohernandeze1.automatas.vm.semantic.SemanticValidator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class VmExamplesTest {
    @Test
    void shouldRunSuccessfulPurchaseExample() throws Exception {
        String script = load("examples/compra_exitosa.vm");
        var parse = new Parser(new Lexer().tokenize(script)).parse();
        assertTrue(parse.errors().isEmpty());
        assertTrue(new SemanticValidator().validate(parse.instructions()).isEmpty());

        var vm = new VendingMachine();
        var result = new ExecutionEngine().execute(parse.instructions(), vm);
        assertTrue(result.diagnostics().isEmpty());
        assertEquals(0, vm.getBalance());
    }

    @Test
    void shouldReportOutOfStockExample() throws Exception {
        String script = load("examples/sin_stock.vm");
        var parse = new Parser(new Lexer().tokenize(script)).parse();
        var vm = new VendingMachine();
        var result = new ExecutionEngine().execute(parse.instructions(), vm);
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.contains("agotado")));
    }

    @Test
    void shouldReportInsufficientMoneyExample() throws Exception {
        String script = load("examples/saldo_insuficiente.vm");
        var parse = new Parser(new Lexer().tokenize(script)).parse();
        var vm = new VendingMachine();
        var result = new ExecutionEngine().execute(parse.instructions(), vm);
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.contains("Saldo insuficiente")));
    }

    private String load(String resourcePath) throws IOException {
        try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                fail("No se encontró recurso: " + resourcePath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
