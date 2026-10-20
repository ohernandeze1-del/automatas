package com.ohernandeze1.automatas.vm.parser;

import com.ohernandeze1.automatas.vm.ast.*;
import com.ohernandeze1.automatas.vm.lexer.Token;
import com.ohernandeze1.automatas.vm.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final List<String> errors = new ArrayList<>();
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ParseResult parse() {
        List<Instruction> instructions = new ArrayList<>();
        while (!isAtEnd()) {
            if (peek().type() == TokenType.EOF) break;
            Instruction instruction = parseInstruction();
            if (instruction != null) instructions.add(instruction);
        }
        return new ParseResult(instructions, errors);
    }

    private Instruction parseInstruction() {
        Token start = advance();
        try {
            return switch (start.type()) {
                case PRODUCTO -> parseProduct(start.line());
                case CARGAR -> parseLoad(start.line());
                case MONEDA -> parseCoin(start.line());
                case SELECCIONAR -> parseSelect(start.line());
                case COMPRAR -> parseNoArg(start.line(), "COMPRAR", BuyInstruction::new);
                case CANCELAR -> parseNoArg(start.line(), "CANCELAR", CancelInstruction::new);
                case MOSTRAR -> parseNoArg(start.line(), "MOSTRAR", ShowInstruction::new);
                default -> {
                    error(start, "Instrucción desconocida: " + start.lexeme());
                    synchronize();
                    yield null;
                }
            };
        } catch (ParseAbort ignored) {
            synchronize();
            return null;
        }
    }

    private Instruction parseProduct(int line) {
        consume(TokenType.LPAREN, "Falta '(' en PRODUCTO", line);
        String code = consume(TokenType.IDENTIFIER, "Falta código en PRODUCTO", line).lexeme();
        consume(TokenType.COMMA, "Falta ',' después del código", line);
        String name = consume(TokenType.IDENTIFIER, "Falta nombre en PRODUCTO", line).lexeme();
        consume(TokenType.COMMA, "Falta ',' después del nombre", line);
        int price = parseInt(consume(TokenType.NUMBER, "Falta precio en PRODUCTO", line), "Número inválido en precio de PRODUCTO");
        consume(TokenType.COMMA, "Falta ',' después del precio", line);
        int quantity = parseInt(consume(TokenType.NUMBER, "Falta cantidad en PRODUCTO", line), "Número inválido en cantidad de PRODUCTO");
        consume(TokenType.RPAREN, "Falta ')' en PRODUCTO", line);
        consume(TokenType.SEMICOLON, "Falta ';' al final de PRODUCTO", line);
        return new ProductInstruction(code, name, price, quantity, line);
    }

    private Instruction parseLoad(int line) {
        consume(TokenType.LPAREN, "Falta '(' en CARGAR", line);
        String code = consume(TokenType.IDENTIFIER, "Falta código en CARGAR", line).lexeme();
        consume(TokenType.COMMA, "Falta ',' en CARGAR", line);
        int quantity = parseInt(consume(TokenType.NUMBER, "Falta cantidad en CARGAR", line), "Número inválido en cantidad de CARGAR");
        consume(TokenType.RPAREN, "Falta ')' en CARGAR", line);
        consume(TokenType.SEMICOLON, "Falta ';' al final de CARGAR", line);
        return new LoadInstruction(code, quantity, line);
    }

    private Instruction parseCoin(int line) {
        consume(TokenType.LPAREN, "Falta '(' en MONEDA", line);
        int value = parseInt(consume(TokenType.NUMBER, "Falta valor en MONEDA", line), "Número inválido en MONEDA");
        consume(TokenType.RPAREN, "Falta ')' en MONEDA", line);
        consume(TokenType.SEMICOLON, "Falta ';' al final de MONEDA", line);
        return new CoinInstruction(value, line);
    }

    private Instruction parseSelect(int line) {
        consume(TokenType.LPAREN, "Falta '(' en SELECCIONAR", line);
        String code = consume(TokenType.IDENTIFIER, "Falta código en SELECCIONAR", line).lexeme();
        consume(TokenType.RPAREN, "Falta ')' en SELECCIONAR", line);
        consume(TokenType.SEMICOLON, "Falta ';' al final de SELECCIONAR", line);
        return new SelectInstruction(code, line);
    }

    private Instruction parseNoArg(int line, String name, NoArgFactory factory) {
        consume(TokenType.LPAREN, "Falta '(' en " + name, line);
        consume(TokenType.RPAREN, "Falta ')' en " + name, line);
        consume(TokenType.SEMICOLON, "Falta ';' al final de " + name, line);
        return factory.create(line);
    }

    private int parseInt(Token token, String message) {
        try {
            return Integer.parseInt(token.lexeme());
        } catch (NumberFormatException ex) {
            error(token, message + ": " + token.lexeme());
            throw new ParseAbort();
        }
    }

    private Token consume(TokenType expected, String message, int instructionLine) {
        if (check(expected)) return advance();
        error(instructionLine, peek().column(), message);
        throw new ParseAbort();
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private void error(Token token, String message) {
        errors.add("Línea " + token.line() + ", columna " + token.column() + ": " + message);
    }

    private void error(int line, int column, String message) {
        errors.add("Línea " + line + ", columna " + column + ": " + message);
    }

    private void synchronize() {
        while (!isAtEnd()) {
            if (previous().type() == TokenType.SEMICOLON) return;
            if (peek().type() == TokenType.PRODUCTO || peek().type() == TokenType.CARGAR || peek().type() == TokenType.MONEDA
                    || peek().type() == TokenType.SELECCIONAR || peek().type() == TokenType.COMPRAR || peek().type() == TokenType.CANCELAR
                    || peek().type() == TokenType.MOSTRAR) {
                return;
            }
            advance();
        }
    }

    @FunctionalInterface
    private interface NoArgFactory {
        Instruction create(int line);
    }

    private static class ParseAbort extends RuntimeException {
    }
}
