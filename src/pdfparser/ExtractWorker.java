package pdfparser;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

class ExtractWorker extends SwingWorker<List<String>, Void> { 
	
	private String openPath;
    private int startPage, endPage;
    private List<Shape> selectedRegions;
    private List<String> text = new ArrayList <>();
    
    ExtractWorker(String openPath, List<Shape> selectedRegions, int startPage, int endPage){
        this.openPath = openPath;
        this.selectedRegions = selectedRegions;
        this.startPage = startPage;
        this.endPage = endPage;
    }
    
    private List<String> extractText(String openPath, List<Shape> selectedRegions, int startPage, int endPage) throws IOException{
    	//Loading an existing document
        File file = new File(openPath);
        
        try (PDDocument document = PDDocument.load(file)){
        	
        	//Instantiate PDFTextStripper class
            PDFTextStripperByArea pdfStripper = new PDFTextStripperByArea();
            pdfStripper.setSortByPosition(true);
            
            for (int i = 0; i < selectedRegions.size(); i++) {
                pdfStripper.addRegion("region" + i, (Rectangle2D) selectedRegions.get(i));
            }
            
            for (int i = startPage; i <= endPage; i++) {
            	PDPage page = document.getPage(i);
                pdfStripper.extractRegions(page);
                
                //Retrieving text from PDF document
                for (int j = 0; j < selectedRegions.size(); j++) {
                    text.add(pdfStripper.getTextForRegion("region" + j));
                }
            }
            
        }
        
        return text;
    }

	@Override
	protected List<String> doInBackground() throws Exception {
		return extractText(openPath, selectedRegions, startPage, endPage);
	}
    
    

}
