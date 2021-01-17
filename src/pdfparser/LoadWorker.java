package pdfparser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

class LoadWorker extends SwingWorker<ImageIcon, Void> {
	
	private String openPath;
    private int pageNumber;
    
    LoadWorker(String openPath, int pageNumber){
        this.openPath=openPath;
        this.pageNumber=pageNumber;
    }
    
    private ImageIcon loadPDF(String path, int pageNumber) throws IOException{
    	//Loading an existing document
        File file = new File(path);
        PDDocument document = PDDocument.load(file);
        
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage image = renderer.renderImage(pageNumber, 1);
        ImageIcon imageIcon = new ImageIcon(image);
        imageIcon.setDescription(String.valueOf(document.getNumberOfPages()));
        
        //Closing the document
        document.close();
        
        return imageIcon;
    }

	@Override
	protected ImageIcon doInBackground() throws Exception {
		return loadPDF(openPath, pageNumber);
	}

}
