package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.simulator.VendingMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VendingMachineTest {
    @Test
    void shouldPurchaseAndReturnChange() {
        VendingMachine vm = new VendingMachine();
        vm.addProduct("A1", "Agua", 10, 1);
        vm.insertCoin(10);
        vm.insertCoin(5);
        vm.selectProduct("A1");
        var result = vm.purchase();
        assertTrue(result.success());
        assertEquals(5, result.returnedChange());
    }

    @Test
    void shouldRefundOnCancel() {
        VendingMachine vm = new VendingMachine();
        vm.insertCoin(10);
        var result = vm.cancel();
        assertEquals(10, result.returnedChange());
        assertEquals(0, vm.getBalance());
    }

    @Test
    void shouldPreventDuplicatePurchase() {
        VendingMachine vm = new VendingMachine();
        vm.addProduct("A1", "Agua", 10, 1);
        vm.insertCoin(10);
        vm.selectProduct("A1");
        assertTrue(vm.purchase().success());
        var second = vm.purchase();
        assertFalse(second.success());
    }
}
