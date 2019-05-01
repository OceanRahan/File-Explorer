package fileexplorer;

import java.io.File;
import javax.swing.AbstractListModel;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author USER
 */
public class ListViewModel extends AbstractListModel  {
    public File[] files;
    public FileSystemView fsv = FileSystemView.getFileSystemView();
  
    public ListViewModel()
    {
        
    }
    public ListViewModel(File[] files)
    {
        this.files=files;
    }
  
    @Override
    public int getSize() {
       return files.length;
    }

    @Override
    public Object getElementAt(int index) {
       return fsv.getSystemDisplayName(files[index]);
    }
    
   public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireContentsChanged(this,0,getSize());
    }
}
