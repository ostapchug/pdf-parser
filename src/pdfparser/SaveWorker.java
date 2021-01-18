package pdfparser;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	
	private int findIndex (String r, String s) {
		int position = 0;
		Pattern pattern = Pattern.compile(r);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find())
			   position = matcher.start();
		
		return position;		
	}

	private void writeToExcel(String data, String savePath) throws FileNotFoundException, IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		
		ArrayList<String> cell2 = findData ("P\\d{3}(?=\\r)", data);
		List<String> cells = new ArrayList<>(Arrays.asList(data.split("P\\d{3}(?=\\r)")));
		cells.removeAll(Arrays.asList("",null));
		
		ArrayList<String> tmp0 = new ArrayList<>();
		ArrayList<String> tmp1 = new ArrayList<>();

		ArrayList<String> cell0 = new ArrayList<>();
		ArrayList<String> cell1 = new ArrayList<>();
		ArrayList<String> cell3 = new ArrayList<>();
		ArrayList<String> cell4 = new ArrayList<>();
		
		int i=0;
		
		int rowCount = 1;
		Row row = sheet.createRow(rowCount);
		
		for (String s : cells) {
			cell3.add(s.substring(0, findIndex("[A-Z][a-z]|\\n[A-Z]-",s)-2));
		}		
		i=0;
		for (String s : cells) {
			tmp0.add(s.replace(cell3.get(i++), ""));
		}	
		
		for (String s : tmp0) {
			cell0.add(s.substring(0, findIndex("[a-z]\\v|\\d\\v",s)+1));
		}		
		i=0;
		for (String s : tmp0) {
			tmp1.add(s.replace(cell0.get(i++), ""));
		}
		
		for (String s : tmp1) {
			cell1.add(s.substring(0, findIndex("\\w\\v|\\)\\v",s)+1));
		}
		i=0;
		for (String s : tmp1) {
			cell4.add(s.replace(cell1.get(i++), ""));
		}
			
		for (String s : cell2) {
			row = sheet.createRow(rowCount++);
			writeData(s, row, 2); 
		}		
		
		if(rowCount<=cells.size())
			row = sheet.createRow(rowCount++);
		
		i=0;
		for (Row r : sheet) {
			writeData(cell0.get(i++), r, 0);
		}
		i=0;
		for (Row r : sheet) {
			writeData(cell1.get(i++), r, 1);	
		}
		i=0;
		for (Row r : sheet) {
			writeData(cell3.get(i++), r, 3);
		}
		i=0;
		for (Row r : sheet) {
			writeData(cell4.get(i++), r, 4);
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
