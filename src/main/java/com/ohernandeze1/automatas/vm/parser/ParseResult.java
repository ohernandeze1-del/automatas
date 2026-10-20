package com.ohernandeze1.automatas.vm.parser;

import com.ohernandeze1.automatas.vm.ast.Instruction;

import java.util.List;

public record ParseResult(List<Instruction> instructions, List<String> errors) {}
