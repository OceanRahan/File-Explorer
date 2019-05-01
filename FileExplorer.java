package fileexplorer;


import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class FileExplorer{
    public FileSystemView fileSystemView;
    public File currentFile;
    public JTree tree;
    public DefaultTreeModel treeModel;
    public JTable table;
    private FileTableModel Model;
    public ListSelectionListener lsl;
    public ListSelectionListener lsl1;
    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private static final String TITLE = "FILE EXPLORER";
    private JSplitPane split_pane;
    private JList list;
  
 
    public Container GetGui(){
     
        JFileChooser j=new JFileChooser();
        fileSystemView = FileSystemView.getFileSystemView();
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);
        list=new JList(new File(j.getCurrentDirectory().toString()).listFiles());
        list.setCellRenderer(new FileListCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lsl = new ListSelectionListener() {
        @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    SetFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
                }
            };
        lsl1=new ListSelectionListener()
        {

           @Override
           public void valueChanged(ListSelectionEvent lse) {
           int row = list.getSelectionModel().getLeadSelectionIndex();
           SetListDetails( ((ListViewModel)list.getModel()).getFile(row) );
        }
                                 
        };
            
            table.getSelectionModel().addListSelectionListener(lsl);
            JScrollPane tableScroll = new JScrollPane(table);  
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);
            list.getSelectionModel().addListSelectionListener(lsl1);
            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent tse){
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    ShowChildren(node);
                    SetFileDetails((File)node.getUserObject());
                }
            };

        
             TreeSelectionListener treeSelectionListener1 = new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent tse){
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    ShowChildren(node);
                    SetListDetails((File)node.getUserObject());
                }
            };
            
            
            
            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add( node );
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for (File file : files) {
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }
                
            }

            tree = new JTree(treeModel);
            tree.setRootVisible(false);
            tree.addTreeSelectionListener(treeSelectionListener);
            tree.addTreeSelectionListener(treeSelectionListener1);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            JScrollPane treeScroll = new JScrollPane(tree);

            
            
            path=new JTextField(5);
            path.setEditable(false);
            JPopupMenu menu=new JPopupMenu();
            JMenuItem menuItemAdd = new JMenuItem("List View");
            JMenuItem menuItem_detail=new JMenuItem("Details View");
            JPanel filedetail_l = new JPanel(new GridLayout(0,1,2,2));
            JPanel filedetail_v=new JPanel(new GridLayout(0,1,2,2));
            filedetail_l.add(new JLabel("Current dire....", JLabel.TRAILING));
            filedetail_v.add(path);
            split_pane=new JSplitPane();
            split_pane.setSize(350,300);
            split_pane.setDividerSize(4);
            split_pane.setDividerLocation(230);
            split_pane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        
     
          
      
            JScrollPane list_view= new JScrollPane(list);
            ListMouse tbl1=new ListMouse(list);
            split_pane.setLeftComponent(treeScroll);
            split_pane.setRightComponent(tableScroll);
         
            menuItemAdd.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e) {
         
              split_pane.setRightComponent(list_view);
            }

        });
        
            menu.add(menuItemAdd);
            menuItem_detail.addActionListener(new ActionListener() {
 
            @Override
            public void actionPerformed(ActionEvent e) {
                 split_pane.setRightComponent(tableScroll);                    ;
            }
        });
             
            menu.add(menuItem_detail);
            table.setComponentPopupMenu(menu);
            list.setComponentPopupMenu(menu);
            TableMouseListener tbl=new TableMouseListener(table);
   

        return split_pane;
    }

    private void SetTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (Model==null) {
                    Model = new FileTableModel();
                    table.setModel(Model);
                }
                table.getSelectionModel().removeListSelectionListener(lsl);
                Model.setFiles(files);
                table.getSelectionModel().addListSelectionListener(lsl);
              

                }
            
        });
    }

    private void ShowChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); 
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    SetTableData(files);
                    
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
           
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void SetFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
       

        JFrame f = (JFrame)split_pane.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                TITLE +
                fileSystemView.getSystemDisplayName(file) );
        }

        split_pane.repaint();
    }


    private void SetListDetails(File file) {
        File[] files=file.listFiles();
        Object[] ob=files;
        list.setListData(ob);
        path.setText(file.getPath());
        JFrame f = (JFrame)split_pane.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                TITLE +   
                 " :: " +
                fileSystemView.getSystemDisplayName(file) );
           
        }

        split_pane.repaint();
    }

    
 public void ShowRootFile() {
       
        tree.setSelectionInterval(0,0);
    }

    
 public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
               
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch(Exception weTried) {
                }
                JFrame f = new JFrame(TITLE);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                FileExplorer FileBrowser = new FileExplorer();
                f.setContentPane(FileBrowser.GetGui());
              

                try {
                    URL urlBig = FileBrowser.getClass().getResource("fb-icon-32x32.png");
                    URL urlSmall = FileBrowser.getClass().getResource("fb-icon-16x16.png");
                    ArrayList<Image> images = new ArrayList<Image>();
                    images.add( ImageIO.read(urlBig) );
                    images.add( ImageIO.read(urlSmall) );
                    f.setIconImages(images);
                } catch(Exception weTried) {}

                f.pack();
                f.setLocationByPlatform(true);
                f.setMinimumSize(f.getSize());
                f.setVisible(true);

                FileBrowser.ShowRootFile();
            }
        });
    }
}
