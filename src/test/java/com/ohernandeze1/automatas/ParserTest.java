package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.vm.lexer.Lexer;
import com.ohernandeze1.automatas.vm.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void shouldParseValidProgram() {
        var tokens = new Lexer().tokenize("PRODUCTO(A1,Agua,10,1);\nCOMPRAR();");
        var result = new Parser(tokens).parse();
        assertTrue(result.errors().isEmpty());
        assertFalse(result.instructions().isEmpty());
    }

    @Test
    void shouldReportMissingSemicolonWithInstructionLine() {
        String script = "PRODUCTO(A1,Agua,10,1);\nMONEDA(5)\nSELECCIONAR(A1);";
        var tokens = new Lexer().tokenize(script);
        var result = new Parser(tokens).parse();
        assertFalse(result.errors().isEmpty());
        assertTrue(result.errors().get(0).contains("Línea 2"));
    }

    @Test
    void shouldReportInvalidBigNumberInsteadOfSilentlySkipping() {
        String script = "PRODUCTO(A9,NUEVO,9999999999,1);";
        var tokens = new Lexer().tokenize(script);
        var result = new Parser(tokens).parse();
        assertFalse(result.errors().isEmpty());
        assertTrue(result.errors().get(0).contains("Número inválido"));
    }
}
