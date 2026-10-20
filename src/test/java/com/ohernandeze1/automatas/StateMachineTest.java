package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.automata.VendingState;
import com.ohernandeze1.automatas.automata.VendingStateMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateMachineTest {
    @Test
    void shouldFollowMainTransitions() {
        VendingStateMachine machine = new VendingStateMachine();
        machine.onCoinInserted();
        machine.onSelection();
        machine.onPurchaseStart();
        machine.onDispense();
        machine.onReturnMoney();
        machine.reset();
        assertEquals(VendingState.E0_ESPERANDO_MONEDAS, machine.current());
    }
}
