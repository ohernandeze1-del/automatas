package com.ohernandeze1.automatas.vm.lexer;

public record Token(TokenType type, String lexeme, int line, int column) {}
