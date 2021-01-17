package pdfparser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class PDFParser {
	
	private JPanel windowContent;
    private JButton openButton;
    private JButton saveButton;
    private JLabel nameLabel;
    private PaintLabel previewLabel;
    private SpinnerNumberModel pagerSNM;
    private JSpinner pager;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    private PDFParserController pdfParserController= new PDFParserController(this);
    private File userDir = new File(System.getProperty("user.dir"));
    private JFileChooser fileChooser = new JFileChooser(userDir);
    
    PDFParser(){
    	// Create the main panel
        windowContent= new JPanel();
        
        // Set a layout manager for this panel
    	BorderLayout windowLayout = new BorderLayout();
    	windowContent.setLayout(windowLayout);
    	
    	// Create the control panel
        JPanel conrolPanel = new JPanel();
        FlowLayout controlLayout =new FlowLayout(FlowLayout.LEADING);
        conrolPanel.setLayout(controlLayout);
        
        // Create and add controls to the panel
        nameLabel= new JLabel("Choose file:");
        conrolPanel.add(nameLabel);
        openButton= new JButton("Open");
        openButton.addActionListener(pdfParserController);
        conrolPanel.add(openButton);
        saveButton= new JButton("Save");
        saveButton.addActionListener(pdfParserController);
        saveButton.setEnabled(false);
        conrolPanel.add(saveButton);        
        pagerSNM=new SpinnerNumberModel(1, 1, 99, 1);
        pager= new JSpinner(pagerSNM);
        pager.addChangeListener(pdfParserController);
        pager.setEnabled(false);
        conrolPanel.add(pager);
        
        // Add the control panel to the main panel
        windowContent.add("North",conrolPanel);
        
        // Create the preview panel
        JPanel previewPanel = new JPanel();
        previewLabel= new PaintLabel();
        previewPanel.add(previewLabel);
        
        // Create the scroll panel and add the preview panel to it
        JScrollPane scrollPanel = new JScrollPane(previewPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        windowContent.add("Center",scrollPanel);
        
        // Create the status panel
       	JPanel statusPanel = new JPanel();
       	GridLayout statusLayout = new GridLayout(1,2);
       	statusPanel.setLayout(statusLayout);
       	
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
    
    public void setSaveButton(boolean val) {
        saveButton.setEnabled(val);
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
