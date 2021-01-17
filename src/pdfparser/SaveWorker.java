package pdfparser;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SaveWorker extends SwingWorker<Void, Void> {
	
	private String savePath;
	private String data;
	
	SaveWorker(String savePath, String data){
        this.savePath=savePath;
        this.data=data;
    }
	
	private ArrayList<String> findData(String r, String text) {
		ArrayList<String> data = new ArrayList <> ();
		Pattern pattern = Pattern.compile(r);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			data.add(matcher.group(0));
		}	
		return data;
	}
	
	private void writeData(String entry, Row row, int cellCount) {
	    Cell cell = row.createCell(cellCount);
	    cell.setCellValue(entry);
	}
		

	private void writeToExcel(String data, String savePath) throws FileNotFoundException, IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		
		ArrayList<String> cell0 = findData ("P\\d{3}(?=\\r)", data);
		ArrayList<String> cell1 = new ArrayList<String>(Arrays.asList(data.split("P\\d{3}(?=\\r)")));
		
		int rowCount = 1;
		Row row = sheet.createRow(rowCount);
		
		for (String s : cell0) {
			row = sheet.createRow(rowCount++);
			writeData(s, row, 0); 
		}		
		
		if(rowCount<=cell1.size())
			row = sheet.createRow(rowCount++);
		
		int i=0;
		for (Row r : sheet) {
			writeData(cell1.get(i++), r, 1); 
		}
		
		try (OutputStream os = new FileOutputStream(savePath)) {
			workbook.write(os);
			workbook.close();
		}		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		writeToExcel (data, savePath);
		return null;
	}

}
