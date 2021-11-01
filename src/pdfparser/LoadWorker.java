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
        this.openPath = openPath;
        this.pageNumber = pageNumber;
    }
    
    private ImageIcon createPdfImage(String path, int pageNumber) throws IOException{
    	ImageIcon pdfImage = null;
    	
    	//Loading an existing document
        File file = new File(path);
        
        try (PDDocument document = PDDocument.load(file)){
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImage(pageNumber, 1);
            pdfImage = new ImageIcon(image);
            pdfImage.setDescription(String.valueOf(document.getNumberOfPages()));
        }
        
        return pdfImage;
    }

	@Override
	protected ImageIcon doInBackground() throws Exception {
		return createPdfImage (openPath, pageNumber);
	}

}
