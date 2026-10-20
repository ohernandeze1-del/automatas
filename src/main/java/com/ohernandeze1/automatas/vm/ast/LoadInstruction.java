package com.ohernandeze1.automatas.vm.ast;

public record LoadInstruction(String code, int quantity, int line) implements Instruction {}
