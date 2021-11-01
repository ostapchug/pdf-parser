package pdfparser;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.util.List;
import javax.swing.SwingWorker;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SaveWorker extends SwingWorker<Void, Void> {
	
	private String savePath;
	private List<List<String>> data;
	
	SaveWorker(String savePath, List<List<String>> data){
        this.savePath = savePath;
        this.data = data;
    }

	private void writeToExcel(List<List<String>> data, String savePath) throws FileNotFoundException, IOException {
		
		try (XSSFWorkbook workbook = new XSSFWorkbook()){
			
			XSSFSheet sheet = workbook.createSheet();
			
			int rowCount = data.size();
			int colCount = data.get(0).size();
			
			for(int i = 0; i < rowCount; i++) {
				sheet.createRow(i);
			}
			
			for(int i = 0; i < colCount; i++) {
				for(int j = 0; j < rowCount; j++) {
					sheet.getRow(j).createCell(i).setCellValue(data.get(j).get(i));
					
				}
			}
			
			try (OutputStream os = new FileOutputStream(savePath)) {
				workbook.write(os);
			}
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		writeToExcel (data, savePath);
		return null;
	}
}
