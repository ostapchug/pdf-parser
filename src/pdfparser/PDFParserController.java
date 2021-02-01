package pdfparser;


import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class PDFParserController implements ActionListener, ChangeListener, PropertyChangeListener {
	
	private PDFParser parent;
	private LoadWorker loadWorker;
	private ExtractWorker extractWorker;
	private SaveWorker saveWorker;
    private String openPath;
	
	PDFParserController(PDFParser parent){
		this.parent=parent;		
	}
	
	private void loadPage(String path, int page){
        if(path!=null){
            loadWorker= new LoadWorker(path, page);
            loadWorker.addPropertyChangeListener(this);
            loadWorker.execute();
        }
    }
	
	private void extractText(String path, ArrayList<Shape> sRegions, int startPage, int endPage){
        if(path!=null){
        	extractWorker= new ExtractWorker(path, sRegions, startPage, endPage);
        	extractWorker.addPropertyChangeListener(this);
        	extractWorker.execute();
        }
    }
	
	private void saveText(String path, String data){
        if(path!=null){
        	saveWorker= new SaveWorker(path, data);
        	saveWorker.addPropertyChangeListener(this);
        	saveWorker.execute();
        }
    }

	// This method gets called when a state property is changed
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		// Get the source object of this action
        if (evt.getSource().equals(loadWorker)){
        	
        	// Returns true if this task completed
            if (!loadWorker.isDone()){
                parent.setProgress(true);
                parent.setStatus(" Loading...");
            }else{
                parent.setProgress(false);
                parent.setStatus(" Done");
                try{
                    parent.viewPicture(loadWorker.get());
                }catch(InterruptedException | ExecutionException ex){
                    System.out.println(ex.getMessage());
                }     
            }
        	
        }else if (evt.getSource().equals(extractWorker)) {
            if (!extractWorker.isDone()){
                parent.setProgress(true);
                parent.setStatus(" Loading...");
            }else{
                parent.setProgress(false);
                parent.setStatus(" Done");
                try{
                	saveText(openPath.substring(0, openPath.lastIndexOf(File.separator)+1)+"DataEntry.xlsx", extractWorker.get());
                }catch(InterruptedException | ExecutionException ex){
                    System.out.println(ex.getMessage());
                }     
            }
        	
        }else {
        	
        	if (!saveWorker.isDone()){
                parent.setProgress(true);
                parent.setStatus(" Loading...");
            }else{
                parent.setProgress(false);
                parent.setStatus(" Done");
            }
        }
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		loadPage(openPath,parent.getPage()-1);
		
	}
	
	// Invoked when a button has been pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Get the source object of this action
        switch (e.getActionCommand()) {
            case "Open":
                openPath=parent.getPath();
                loadPage(openPath,0);
                parent.setSaveButton(true);
                break;
            case "Save":
            	String range = JOptionPane.showInputDialog("Enter page range:", "43-63");
            	if(null!=range) {
            		String [] rangeArr = range.split("-");
                	int startPage = Integer.parseInt(rangeArr[0]);
                	int endPage = Integer.parseInt(rangeArr[1]);
                	if(startPage<=endPage)
                	extractText(openPath, parent.getShapes(), startPage, endPage);
            	}
                break;
            case "Cancel":
                break;
            default:
                break;
        }
	}

}
