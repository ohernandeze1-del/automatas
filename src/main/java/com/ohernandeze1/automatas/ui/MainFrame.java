package com.ohernandeze1.automatas.ui;

import com.ohernandeze1.automatas.io.JsonService;
import com.ohernandeze1.automatas.simulator.SimulationResult;
import com.ohernandeze1.automatas.simulator.VendingMachine;
import com.ohernandeze1.automatas.vm.engine.ExecutionEngine;
import com.ohernandeze1.automatas.vm.engine.ExecutionResult;
import com.ohernandeze1.automatas.vm.lexer.Lexer;
import com.ohernandeze1.automatas.vm.lexer.Token;
import com.ohernandeze1.automatas.vm.parser.ParseResult;
import com.ohernandeze1.automatas.vm.parser.Parser;
import com.ohernandeze1.automatas.vm.semantic.SemanticValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private final VendingTableModel vendingTableModel = new VendingTableModel();
    private final DefaultTableModel tokensTable = new DefaultTableModel(new String[]{"Tipo", "Lexema", "Línea", "Columna"}, 0);
    private final JTextArea editor = new JTextArea(14, 40);
    private final JTextArea diagnostics = new JTextArea(8, 40);
    private final JTextArea trace = new JTextArea(8, 40);
    private final JLabel balanceLabel = new JLabel("Saldo: Q0");
    private final JLabel stateLabel = new JLabel();
    private final JLabel transitionLabel = new JLabel();
    private final JTextField codeField = new JTextField(10);
    private final JsonService jsonService = new JsonService();
    private final Lexer lexer = new Lexer();
    private final SemanticValidator semanticValidator = new SemanticValidator();
    private final ExecutionEngine executionEngine = new ExecutionEngine();

    private VendingMachine machine = new VendingMachine();

    public MainFrame() {
        super("Simulador de Máquina Expendedora - Autómatas y Lenguajes Formales");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTable inventoryTable = new JTable(vendingTableModel);
        inventoryTable.setFillsViewportHeight(true);
        inventoryTable.setRowHeight(24);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.add(createMachinePanel(inventoryTable));
        topPanel.add(createControlPanel());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createEditorPanel(), createAnalysisPanel());
        splitPane.setResizeWeight(0.5);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        diagnostics.setEditable(false);
        trace.setEditable(false);
        refresh();
    }

    private JPanel createMachinePanel(JTable inventoryTable) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Inventario"));
        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Operación"));

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.add(balanceLabel);
        info.add(stateLabel);
        info.add(transitionLabel);

        JPanel coins = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton q1 = new JButton("Moneda Q1");
        JButton q5 = new JButton("Moneda Q5");
        JButton q10 = new JButton("Moneda Q10");
        q1.addActionListener(e -> action(machine.insertCoin(1)));
        q5.addActionListener(e -> action(machine.insertCoin(5)));
        q10.addActionListener(e -> action(machine.insertCoin(10)));
        coins.add(q1); coins.add(q5); coins.add(q10);

        JPanel selection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selection.add(new JLabel("Código:"));
        selection.add(codeField);
        JButton select = new JButton("Seleccionar");
        JButton buy = new JButton("Comprar");
        JButton cancel = new JButton("Cancelar");
        JButton show = new JButton("Mostrar");
        select.addActionListener(e -> action(machine.selectProduct(codeField.getText().trim())));
        buy.addActionListener(e -> action(machine.purchase()));
        cancel.addActionListener(e -> action(machine.cancel()));
        show.addActionListener(e -> appendTrace(machine.showStatus()));
        selection.add(select); selection.add(buy); selection.add(cancel); selection.add(show);

        JPanel wrappers = new JPanel(new GridLayout(3, 1));
        wrappers.add(info);
        wrappers.add(coins);
        wrappers.add(selection);
        panel.add(wrappers, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Editor de instrucciones .vm"));

        editor.setText("PRODUCTO(A1,Agua,10,2);\nPRODUCTO(B2,Galleta,5,1);\nMONEDA(10);\nSELECCIONAR(A1);\nCOMPRAR();\nMOSTRAR();\n");
        panel.add(new JScrollPane(editor), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton open = new JButton("Abrir .vm");
        JButton save = new JButton("Guardar .vm");
        JButton execute = new JButton("Ejecutar secuencial");
        JButton importJson = new JButton("Importar JSON");
        JButton exportJson = new JButton("Exportar JSON");
        open.addActionListener(e -> openVmFile());
        save.addActionListener(e -> saveVmFile());
        execute.addActionListener(e -> executeScript());
        importJson.addActionListener(e -> importJson());
        exportJson.addActionListener(e -> exportJson());
        buttons.add(open); buttons.add(save); buttons.add(execute); buttons.add(importJson); buttons.add(exportJson);

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Análisis y trazas"));

        JTable table = new JTable(tokensTable);
        panel.add(wrap("Tabla de tokens", new JScrollPane(table)));
        panel.add(wrap("Errores léxicos/sintácticos/semánticos", new JScrollPane(diagnostics)));
        panel.add(wrap("Traza secuencial", new JScrollPane(trace)));
        return panel;
    }

    private JPanel wrap(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void executeScript() {
        tokensTable.setRowCount(0);
        diagnostics.setText("");
        trace.setText("");

        List<Token> tokens = lexer.tokenize(editor.getText());
        for (Token token : tokens) {
            tokensTable.addRow(new Object[]{token.type(), token.lexeme(), token.line(), token.column()});
        }

        List<String> lexicalErrors = tokens.stream()
                .filter(t -> t.type().name().equals("UNKNOWN"))
                .map(t -> "Línea " + t.line() + ", columna " + t.column() + ": token inválido '" + t.lexeme() + "'")
                .collect(Collectors.toList());

        ParseResult parseResult = new Parser(tokens).parse();
        List<String> semanticErrors = semanticValidator.validate(parseResult.instructions());

        String allDiagnostics = String.join("\n", lexicalErrors) +
                (lexicalErrors.isEmpty() ? "" : "\n") +
                String.join("\n", parseResult.errors()) +
                ((lexicalErrors.isEmpty() && parseResult.errors().isEmpty()) ? "" : "\n") +
                String.join("\n", semanticErrors);
        diagnostics.setText(allDiagnostics.strip());

        if (!lexicalErrors.isEmpty() || !parseResult.errors().isEmpty() || !semanticErrors.isEmpty()) {
            appendTrace("Ejecución detenida por errores.");
            return;
        }

        ExecutionResult result = executionEngine.execute(parseResult.instructions(), machine);
        for (var e : result.trace()) {
            appendTrace("L" + e.line() + " " + e.instruction() + " => " + e.result());
        }
        if (!result.diagnostics().isEmpty()) {
            String previous = diagnostics.getText();
            diagnostics.setText((previous.isBlank() ? "" : previous + "\n") + String.join("\n", result.diagnostics()));
        }
        refresh();
    }

    private void action(SimulationResult result) {
        appendTrace(result.message() + (result.returnedChange() > 0 ? " | Cambio/Reembolso: Q" + result.returnedChange() : ""));
        refresh();
    }

    private void refresh() {
        vendingTableModel.setProducts(machine.products());
        balanceLabel.setText("Saldo: Q" + machine.getBalance());
        stateLabel.setText("Estado autómata: " + machine.getStateMachine().current());
        transitionLabel.setText("Última transición: " + machine.getStateMachine().lastTransition());
    }

    private void appendTrace(String text) {
        trace.append(text + "\n");
    }

    private void openVmFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                editor.setText(Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo: " + e.getMessage());
            }
        }
    }

    private void saveVmFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(chooser.getSelectedFile().toPath(), editor.getText(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    private void exportJson() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                jsonService.exportMachine(machine, chooser.getSelectedFile().toPath());
                appendTrace("Estado exportado a JSON.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "No se pudo exportar JSON: " + e.getMessage());
            }
        }
    }

    private void importJson() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                machine = jsonService.importMachine(Path.of(chooser.getSelectedFile().toURI()));
                refresh();
                appendTrace("Estado importado desde JSON.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "No se pudo importar JSON: " + e.getMessage());
            }
        }
    }
}
