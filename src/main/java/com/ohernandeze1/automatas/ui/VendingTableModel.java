package com.ohernandeze1.automatas.ui;

import com.ohernandeze1.automatas.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VendingTableModel extends AbstractTableModel {
    private final String[] columns = {"Código", "Nombre", "Precio", "Stock"};
    private final List<Product> rows = new ArrayList<>();

    public void setProducts(Collection<Product> products) {
        rows.clear();
        rows.addAll(products);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getCode();
            case 1 -> p.getName();
            case 2 -> p.getPrice();
            case 3 -> p.getStock();
            default -> "";
        };
    }
}
