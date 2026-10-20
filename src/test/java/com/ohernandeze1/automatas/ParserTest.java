package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.vm.lexer.Lexer;
import com.ohernandeze1.automatas.vm.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {
    @Test
    void shouldParseValidProgram() {
        var tokens = new Lexer().tokenize("PRODUCTO(A1,Agua,10,1);\nCOMPRAR();");
        var result = new Parser(tokens).parse();
        assertTrue(result.errors().isEmpty());
        assertFalse(result.instructions().isEmpty());
    }

    @Test
    void shouldReportMissingSemicolon() {
        var tokens = new Lexer().tokenize("MONEDA(10)");
        var result = new Parser(tokens).parse();
        assertFalse(result.errors().isEmpty());
    }
}
