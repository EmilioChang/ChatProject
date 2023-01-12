package chatClient.presentation;

import chatProtocol.User;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TableModel extends AbstractTableModel {
    public static final int ID = 0;
    public static final int CONNECTED = 1;
    private ImageIcon icon;
    String[] colNames = new String[2];

    private void inicializarColumnas(){
        colNames[ID] = "Contactos";
        colNames[CONNECTED] = "Conectado";
    }

    List<User> rows;
    int[]cols;

    public TableModel(int[]cols,List<User> rows){
        if (rows != null){
            // System.out.println(rows.toString());
        }
        inicializarColumnas();
        this.cols = cols;
        this.rows = rows;
    }
    public int getRowCount(){
        return rows.size();
    }
    public int getColumnCount(){
        return cols.length;
    }
    public String getColumnName(int col){
        return colNames[cols[col]];
    }

    public Class<?> getColumnClass(int col) {
        switch (cols[col]) {
            case CONNECTED:
                return Icon.class;
            default:
                return super.getColumnClass(col);
        }
    }

    ImageIcon connection(boolean connected){
        if(connected){
            icon = new ImageIcon("connected.png");
           return icon;

        }else{
            icon = new ImageIcon("disconnected.png");
            return icon;
        }
    }

    public Object getValueAt(int row, int col) {
        User user = rows.get(row);
        switch (cols[col]){
            case ID: return user.getId();
            case CONNECTED: return connection(user.isConnected());
            default: return "";
        }
    }
}
