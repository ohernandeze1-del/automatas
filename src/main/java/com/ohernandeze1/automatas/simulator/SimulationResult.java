package com.ohernandeze1.automatas.simulator;

public record SimulationResult(boolean success, String message, int returnedChange) {
    public static SimulationResult ok(String message, int change) {
        return new SimulationResult(true, message, change);
    }

    public static SimulationResult fail(String message) {
        return new SimulationResult(false, message, 0);
    }
}
