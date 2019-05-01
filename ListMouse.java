package fileexplorer;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
/**
 *
 * @author USER
 */
public class ListMouse extends MouseAdapter {
   private JList list;
   
   public ListMouse(JList list) {
        this.list = list;
       
    }
   
   @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        
    }
}
