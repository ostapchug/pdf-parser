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
	
	private void writeColumn(List<String> col, XSSFSheet sh, int n) {
		int i=0;
		for (Row r : sh) {
			writeData(col.get(i++), r, n);
		}
		
	}
	
	private int findIndex (String r, String s) {
		int position = 0;
		Pattern pattern = Pattern.compile(r);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find())
			   position = matcher.start();
		
		return position;		
	}
	
	private ArrayList<String> findColumn(List<String> data, String r, int n){
		ArrayList<String> res = new ArrayList<>();
		for (String s : data) {
			res.add(s.substring(0, findIndex(r,s)+n));
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

	private void writeToExcel(String data, String savePath) throws FileNotFoundException, IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		
		ArrayList<String> cell2 = findData ("P\\d{3}(?=\\r)", data);
		List<String> cells = new ArrayList<>(Arrays.asList(data.split("P\\d{3}(?=\\r)")));
		cells.removeAll(Arrays.asList("",null));
		
		ArrayList<String> cell0 = new ArrayList<>();
		ArrayList<String> cell1 = new ArrayList<>();
		ArrayList<String> cell3 = new ArrayList<>();
		
		int rowCount = 1;
		sheet.createRow(rowCount);

		cell3=findColumn(cells,"[A-Z][a-z]|\\n[A-Z]-",-2);
		cells=subtractColumn(cells,cell3);
		
		cell0=findColumn(cells,"[a-z]\\v|\\d\\v",1);
		cells=subtractColumn(cells,cell0);
		
		cell1=findColumn(cells,"\\w\\v|\\)\\v",1);
		cells=subtractColumn(cells,cell1);
		
		while(rowCount<=cells.size())
			sheet.createRow(rowCount++);
		
		writeColumn(cell0, sheet, 0);
		writeColumn(cell1, sheet, 1);
		writeColumn(cell2, sheet, 2);
		writeColumn(cell3, sheet, 3);
		writeColumn(cells, sheet, 4);
		
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
