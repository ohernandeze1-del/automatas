package com.ohernandeze1.automatas.vm.ast;

public record ProductInstruction(String code, String name, int price, int quantity, int line) implements Instruction {}
