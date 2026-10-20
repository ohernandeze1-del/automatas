package com.ohernandeze1.automatas;

import com.ohernandeze1.automatas.io.JsonService;
import com.ohernandeze1.automatas.simulator.VendingMachine;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonServiceTest {
    @Test
    void shouldExportAndImportStateIncludingAutomaton() throws Exception {
        JsonService service = new JsonService();
        VendingMachine vm = new VendingMachine();
        vm.addProduct("A1", "Agua", 10, 2);
        vm.insertCoin(5);

        Path temp = Files.createTempFile("vm-state", ".json");
        service.exportMachine(vm, temp);

        VendingMachine imported = service.importMachine(temp);
        assertEquals(1, imported.products().size());
        assertEquals(5, imported.getBalance());
        assertNotNull(imported.getStateMachine().current());
    }

    @Test
    void shouldFailOnMalformedJson() throws Exception {
        JsonService service = new JsonService();
        Path temp = Files.createTempFile("bad", ".json");
        Files.writeString(temp, "{invalido");
        assertThrows(IOException.class, () -> service.importMachine(temp));
    }

    @Test
    void shouldFailOnDuplicateProductJson() throws Exception {
        JsonService service = new JsonService();
        Path temp = Files.createTempFile("dup", ".json");
        Files.writeString(temp, """
                {
                  "balance": 0,
                  "selectedCode": null,
                  "automatonState": "E0_ESPERANDO_MONEDAS",
                  "products": [
                    {"code":"A1","name":"Agua","price":10,"stock":1},
                    {"code":"A1","name":"Agua2","price":20,"stock":1}
                  ]
                }
                """);
        assertThrows(IOException.class, () -> service.importMachine(temp));
    }
}
