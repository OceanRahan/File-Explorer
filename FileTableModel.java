package fileexplorer;

import java.io.File;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author USER
 */

class FileTableModel extends AbstractTableModel {

    public File[] files;
    public FileSystemView fileSystemView = FileSystemView.getFileSystemView();
  
    public String[] column_name= 
    {
        "Icon",
        "Name",
        "Date Modified",
        "Size"
    };
    FileTableModel() {
        this(new File[0]);
    }

    FileTableModel(File[] files) {
        this.files = files;
    }

    @Override
    public Object getValueAt(int row, int col) {
        File f = files[row];
         switch(col) {
             case 0: return fileSystemView.getSystemIcon(f);
             case 1: return fileSystemView.getSystemDisplayName(f);
             case 2: return f.lastModified();
             case 3: return f.length();
             default: return null;
         }
    }

    @Override
    public int getColumnCount() {
        return column_name.length;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 1:
                return String.class;
            case 2:
                return Date.class;
            
            case 3:
                return long.class;
        }
        return boolean.class;
    }

   
    @Override
    public String getColumnName(int column) {
        return column_name[column];
    }

    @Override
    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}
