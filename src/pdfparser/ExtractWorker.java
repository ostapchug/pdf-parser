package pdfparser;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

class ExtractWorker extends SwingWorker<String, Void> { 
	
	private String openPath;
    private int startPage, endPage;
    private ArrayList<Shape> sRegions;
    private StringBuffer text = new StringBuffer();
    
    ExtractWorker(String openPath, ArrayList<Shape> sRegions, int startPage, int endPage){
        this.openPath=openPath;
        this.sRegions=sRegions;
        this.startPage=startPage;
        this.endPage=endPage;
    }
    
    private String extractText(String openPath, ArrayList<Shape> sRegions, int startPage, int endPage) throws IOException{
    	//Loading an existing document
        File file = new File(openPath);
        PDDocument document = PDDocument.load(file);
        
        //Instantiate PDFTextStripper class
        PDFTextStripperByArea pdfStripper = new PDFTextStripperByArea();
        pdfStripper.setSortByPosition(true);
        
        for (Shape s : sRegions) {
            pdfStripper.addRegion("region"+s.hashCode(), (Rectangle2D) s);
        }
        
        for (int i=startPage; i<=endPage; i++) {
        	PDPage page = document.getPage(i);
            pdfStripper.extractRegions(page);
            
            //Retrieving text from PDF document
            for (Shape s : sRegions) {
                text.append(pdfStripper.getTextForRegion("region"+s.hashCode()));
            }
        }
        
        //Closing the document
        document.close();
        
        return text.toString();
    }

	@Override
	protected String doInBackground() throws Exception {
		return extractText(openPath, sRegions, startPage, endPage);
	}
    
    

}
