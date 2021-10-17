package pdfparser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PDFParser {
	
	private JPanel windowContent;
    private JButton openButton;
    private JButton extractButton;
    private JLabel nameLabel;
    private PaintLabel previewLabel;
    private SpinnerNumberModel pagerSNM;
    private JSpinner pager;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JLabel regexLabel;
    private JTextField regexField;
    private JButton addRegexButton;
    private JButton removeRegexButton;
    private JCheckBox editCheckBox;
    private JButton saveButton;
    private JTable table;
    private DefaultTableModel tableModel;
    
    
    private PDFParserController pdfParserController= new PDFParserController(this);
    private File userDir = new File(System.getProperty("user.dir"));
    private JFileChooser fileChooser = new JFileChooser(userDir);
    
    PDFParser(){
    	// Create the main panel
    	windowContent = new JPanel();
    	windowContent.setLayout(new BorderLayout());
    	
    	JTabbedPane tabbedPane = new JTabbedPane();
    	
    	// Create the preview panel
    	JPanel selectPanel= new JPanel();
        // Set a layout manager for this panel
    	selectPanel.setLayout(new BorderLayout());
    	
        JPanel selectControlPanel = new JPanel();
        selectControlPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        
        // Create and add controls to the panel
        nameLabel = new JLabel("Choose file:");
        selectControlPanel.add(nameLabel);
        openButton= new JButton("Open");
        openButton.addActionListener(pdfParserController);
        selectControlPanel.add(openButton);
        extractButton= new JButton("Extract");
        extractButton.addActionListener(pdfParserController);
        extractButton.setEnabled(false);
        selectControlPanel.add(extractButton);        
        pagerSNM=new SpinnerNumberModel(1, 1, 99, 1);
        pager= new JSpinner(pagerSNM);
        pager.addChangeListener(pdfParserController);
        pager.setEnabled(false);
        selectControlPanel.add(pager);
        
        // Add the control panel to the main panel
        selectPanel.add("North",selectControlPanel);
                       
        // Create the preview panel
        JPanel labelPanel = new JPanel();
        previewLabel= new PaintLabel();
        labelPanel.add(previewLabel);
        
        // Create the scroll panel and add the label panel to it
        selectPanel.add("Center", new JScrollPane(labelPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        
        tabbedPane.addTab("Select", selectPanel);
        
        // Create the editor panel
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        
        JPanel editControlPanel = new JPanel();
        editControlPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        regexLabel = new JLabel("Enter regex:");
        regexLabel.setEnabled(false);
        editControlPanel.add(regexLabel);
        regexField = new JTextField(20);
        regexField.setEnabled(false);
        editControlPanel.add(regexField);
        addRegexButton = new JButton("Add");
        addRegexButton.setEnabled(false);
        addRegexButton.addActionListener(pdfParserController);
        editControlPanel.add(addRegexButton);
        removeRegexButton = new JButton("Remove");
        removeRegexButton.setEnabled(false);
        removeRegexButton.addActionListener(pdfParserController);
        editControlPanel.add(removeRegexButton);
        editCheckBox = new JCheckBox("Edit");
        editCheckBox.setEnabled(false);
        editCheckBox.addItemListener(pdfParserController);
        editControlPanel.add(editCheckBox);
        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(pdfParserController);
        editControlPanel.add(saveButton);
        editPanel.add("North",editControlPanel);
        
        tableModel = new DefaultTableModel(0,0);   
        table = new JTable(tableModel);
        editPanel.add("Center",new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));        
        
        tabbedPane.addTab("Edit", editPanel);
        
        windowContent.add("Center",tabbedPane);
        
        // Create the status panel
       	JPanel statusPanel = new JPanel();
       	statusPanel.setLayout(new GridLayout(1,2));
       	
       	statusLabel = new JLabel();	
        statusPanel.add(statusLabel);
        progressBar = new JProgressBar();
        statusPanel.add(progressBar);
        windowContent.add("South",statusPanel);
                
        // Create the frame and add the main panel to it
        JFrame frame = new JFrame("PDF Parser");
        frame.add(windowContent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(960, 540));
        frame.setVisible(true);
               
    }
    
    // Inserts the status message
    public void setStatus(String str) {
        statusLabel.setText(str);
    }
    
    // Enables (or disables) the progress bar
    public void setProgress(boolean value) {
        progressBar.setIndeterminate(value);
    }
    
    public void viewPicture (ImageIcon imageIcon) {
        previewLabel.setIcon(imageIcon);
        pager.setEnabled(true);
        pagerSNM.setMaximum(Integer.parseInt(imageIcon.getDescription()));
        windowContent.revalidate();
    }
    
    // Pops up a file chooser for the user to open the file
    public String getPath () {
    	String path=null;
    	int returnVal = fileChooser.showOpenDialog(windowContent);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		File file = fileChooser.getSelectedFile();
    		
    		path=file.getAbsolutePath();
    		nameLabel.setText(file.getName());
    		fileChooser.setCurrentDirectory(file);
    	}
    	return path;
    }
    
    public int getPage () {
        int pageNum=(Integer)pager.getValue();
        return pageNum;
    }
    
    public ArrayList<Shape> getShapes () {
        return previewLabel.getShapes();
    }
    
    public void setExtractButton(boolean isEnabled) {
        extractButton.setEnabled(isEnabled);
    }
    
    public void setSaveButton(boolean isEnabled) {
        saveButton.setEnabled(isEnabled);
    }
    
    public void setEditCheckBox(boolean isEnabled) {
    	editCheckBox.setEnabled(isEnabled);
    }
    
    public void setRegexLabel(boolean isEnabled){
    	regexLabel.setEnabled(isEnabled);
    }
    
    public void setRegexField(boolean isEnabled){
    	regexField.setEnabled(isEnabled);
    }
    
    public void setAddRegexButton(boolean isEnabled) {
    	addRegexButton.setEnabled(isEnabled);
    }
    
    public void setTable (ArrayList<String> data) {
    	clearTable();
    	int colCount = previewLabel.getShapes().size();
    	String header[] = new String[colCount];
    	for(int i=0; i<header.length; i++) {
    		header[i] = "Column "+i;
    	}
    	tableModel.setColumnIdentifiers(header);
    	
    	Object [] rowData = new Object[header.length];
    	ListIterator<String> listIter = data.listIterator();
    	while(listIter.hasNext()) {
    		for(int i=0; i<rowData.length; i++) {
    			if(listIter.hasNext())
    				rowData[i] = listIter.next();
    		}
    		tableModel.addRow(rowData);
    	}        
    }
    
    public List<List<String>> getTableData(){
    	List <List<String>> data = new ArrayList<>();
    	for(int i=0; i<tableModel.getRowCount(); i++) {
    		List <String> colData = new ArrayList<>();
    		for(int j=0; j<tableModel.getColumnCount(); j++) {
    			colData.add(tableModel.getValueAt(i, j).toString());
    		}
    		data.add(colData);
    	}
    	
    	return data;
    }
    
    public void updateTable(List<List<String>> data){
    	clearTable();
    	int colCount = data.get(0).size();
    	int rowCount = data.size();
    	String header[] = new String[colCount];
    	for(int i=0; i<header.length; i++) {
    		header[i] = "Column "+i;
    	}
    	tableModel.setColumnIdentifiers(header);
    	
    	for(int i=0; i<rowCount; i++) {
    		tableModel.addRow(data.get(i).toArray());    		
    	}
    	
    }
    
    public void clearTable () {
    	int rowCount = tableModel.getRowCount();
    	while (rowCount > 0){
            for (int i = 0; i < rowCount; i++){
            	tableModel.removeRow(i);
            }
    	}
    }
    
    
    // Loads the swing elements on the event dispatch thread
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PDFParser();
            }
        });
	}

}
