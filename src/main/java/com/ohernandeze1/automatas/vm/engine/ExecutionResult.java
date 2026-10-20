package com.ohernandeze1.automatas.vm.engine;

import java.util.List;

public record ExecutionResult(List<TraceEvent> trace, List<String> diagnostics) {}
