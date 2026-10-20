package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.vm.lexer.Lexer;
import com.ohernandeze1.automatas.vm.parser.Parser;
import com.ohernandeze1.automatas.vm.semantic.SemanticValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SemanticValidatorTest {
    @Test
    void shouldDetectInvalidCoinAndUnknownLoad() {
        String script = "PRODUCTO(A1,Agua,10,1);\nMONEDA(3);\nCARGAR(X1,2);";
        var parse = new Parser(new Lexer().tokenize(script)).parse();
        var errors = new SemanticValidator().validate(parse.instructions());
        assertFalse(errors.isEmpty());
    }
}
