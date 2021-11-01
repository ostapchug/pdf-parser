package pdfparser;


import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class PDFParserController implements ActionListener, ChangeListener, PropertyChangeListener, ItemListener {
	
	private PDFParser parent;
	private LoadWorker loadWorker;
	private ExtractWorker extractWorker;
	private SaveWorker saveWorker;
    private String openPath;
	
	PDFParserController(PDFParser parent){
		this.parent=parent;		
	}
	
	private void loadPage(String path, int page){
        if(path != null){
            loadWorker = new LoadWorker(path, page);
            loadWorker.addPropertyChangeListener(this);
            loadWorker.execute();
        }
    }
	
	private void extractText(String path, List<Shape> selectedRegions, int startPage, int endPage){
        if(path != null){
        	extractWorker = new ExtractWorker(path, selectedRegions, startPage, endPage);
        	extractWorker.addPropertyChangeListener(this);
        	extractWorker.execute();
        }
    }
	
	private void saveText(String path, List<List<String>> data){
        if(path != null){
        	saveWorker = new SaveWorker(path, data);
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
                	parent.setTable(extractWorker.get());
                }catch(InterruptedException | ExecutionException ex){
                    System.out.println(ex.getMessage());
                }     
            }
        	
        }else if (evt.getSource().equals(saveWorker)) {
        	
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
                parent.setExtractButton(true);
                break;
            case "Extract":
            	String range = JOptionPane.showInputDialog("Enter page range:", "43-63");
            	if(null!=range) {
            		String [] rangeArr = range.split("-");
                	int startPage = Integer.parseInt(rangeArr[0]);
                	int endPage = Integer.parseInt(rangeArr[1]);
                	if((endPage-startPage)>=0) {
                		extractText(openPath, parent.getShapes(), startPage, endPage);
                        parent.setSaveButton(true);
                        parent.setEditCheckBox(true);
                	}	
            	}
                break;
            case "Save":
            	saveText(openPath.substring(0, openPath.lastIndexOf(File.separator)+1)+"DataEntry.xlsx", parent.getTableData());
                break;
            default:
                break;
        }
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED) {
            parent.setRegexLabel(true);
            parent.setRegexField(true);
            parent.setAddRegexButton(true);
        } else {
            parent.setRegexLabel(false);
            parent.setRegexField(false);
        	parent.setAddRegexButton(false);
        }
	}
	
/*	For the future realization
 
	private List<List<String>> editText(String regex){
		List<List<String>> text = new ArrayList<>();
		
		return text;
	}
	
	private ArrayList<String> findData(String regex, String text) {
		ArrayList<String> data = new ArrayList <> ();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			data.add(matcher.group(0));
		}	
		return data;
	}
	
	private int findIndex (String regex, String text) {
		int position = 0;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find())
			   position = matcher.start();
		
		return position;		
	}
	
	private ArrayList<String> findColumn(List<String> data, String regex, int n){
		ArrayList<String> res = new ArrayList<>();
		for (String s : data) {
			res.add(s.substring(0, findIndex(regex,s)+n));
		}
		return res;
	}
	
	private ArrayList<String> subtractColumn(List<String> l0, List<String> l1){
		ArrayList<String> res = new ArrayList<>();
		int i=0;
		for (String s : l0) {
			res.add(s.replace(l1.get(i++), ""));
		}
		return res;		
	}
*/

}
