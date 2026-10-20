package com.ohernandeze1.automatas.vm.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {
    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "PRODUCTO", TokenType.PRODUCTO,
            "CARGAR", TokenType.CARGAR,
            "MONEDA", TokenType.MONEDA,
            "SELECCIONAR", TokenType.SELECCIONAR,
            "COMPRAR", TokenType.COMPRAR,
            "CANCELAR", TokenType.CANCELAR,
            "MOSTRAR", TokenType.MOSTRAR
    );

    public List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int line = 1;
        int col = 1;
        while (i < source.length()) {
            char c = source.charAt(i);
            if (c == '\n') { line++; col = 1; i++; continue; }
            if (Character.isWhitespace(c)) { i++; col++; continue; }
            int startCol = col;

            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (i < source.length()) {
                    char cc = source.charAt(i);
                    if (Character.isLetterOrDigit(cc) || cc == '_' || cc == '-') {
                        i++; col++;
                    } else break;
                }
                String lexeme = source.substring(start, i);
                TokenType type = KEYWORDS.getOrDefault(lexeme.toUpperCase(), TokenType.IDENTIFIER);
                tokens.add(new Token(type, lexeme, line, startCol));
                continue;
            }

            if (Character.isDigit(c)) {
                int start = i;
                while (i < source.length() && Character.isDigit(source.charAt(i))) { i++; col++; }
                tokens.add(new Token(TokenType.NUMBER, source.substring(start, i), line, startCol));
                continue;
            }

            TokenType type = switch (c) {
                case '(' -> TokenType.LPAREN;
                case ')' -> TokenType.RPAREN;
                case ',' -> TokenType.COMMA;
                case ';' -> TokenType.SEMICOLON;
                default -> TokenType.UNKNOWN;
            };
            tokens.add(new Token(type, String.valueOf(c), line, startCol));
            i++;
            col++;
        }
        tokens.add(new Token(TokenType.EOF, "", line, col));
        return tokens;
    }
}
