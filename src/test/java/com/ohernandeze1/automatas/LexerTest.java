package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.vm.lexer.Lexer;
import com.ohernandeze1.automatas.vm.lexer.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {
    @Test
    void shouldGenerateTokensWithLocation() {
        var tokens = new Lexer().tokenize("MONEDA(10);\nCOMPRAR();");
        assertEquals(TokenType.MONEDA, tokens.get(0).type());
        assertEquals(1, tokens.get(0).line());
        var comprar = tokens.stream().filter(t -> t.type() == TokenType.COMPRAR).findFirst().orElseThrow();
        assertEquals(2, comprar.line());
    }
}
