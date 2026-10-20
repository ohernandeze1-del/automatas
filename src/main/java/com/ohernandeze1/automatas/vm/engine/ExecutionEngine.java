package com.ohernandeze1.automatas.vm.engine;

import com.ohernandeze1.automatas.simulator.SimulationResult;
import com.ohernandeze1.automatas.simulator.VendingMachine;
import com.ohernandeze1.automatas.vm.ast.*;

import java.util.ArrayList;
import java.util.List;

public class ExecutionEngine {
    public ExecutionResult execute(List<Instruction> instructions, VendingMachine machine) {
        List<TraceEvent> trace = new ArrayList<>();
        List<String> diagnostics = new ArrayList<>();

        for (Instruction instruction : instructions) {
            SimulationResult result = dispatch(machine, instruction);
            String instructionName = instruction.getClass().getSimpleName();
            String status = result.success() ? "[OK]" : "[ERROR SEMANTICO]";
            trace.add(new TraceEvent(instruction.line(), instructionName,
                    status + " " + result.message() + (result.returnedChange() > 0 ? " (cambio=Q" + result.returnedChange() + ")" : "")));
            if (!result.success()) diagnostics.add("[ERROR SEMANTICO] Línea " + instruction.line() + ": " + result.message());
        }
        return new ExecutionResult(trace, diagnostics);
    }

    private SimulationResult dispatch(VendingMachine machine, Instruction instruction) {
        if (instruction instanceof ProductInstruction p) return machine.addProduct(p.code(), p.name(), p.price(), p.quantity());
        if (instruction instanceof LoadInstruction l) return machine.loadProduct(l.code(), l.quantity());
        if (instruction instanceof CoinInstruction c) return machine.insertCoin(c.value());
        if (instruction instanceof SelectInstruction s) return machine.selectProduct(s.code());
        if (instruction instanceof BuyInstruction) return machine.purchase();
        if (instruction instanceof CancelInstruction) return machine.cancel();
        if (instruction instanceof ShowInstruction) return SimulationResult.ok(machine.showStatus(), 0);
        return SimulationResult.fail("Instrucción no soportada");
    }
}
