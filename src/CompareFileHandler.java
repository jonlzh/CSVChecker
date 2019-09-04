import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class CompareFileHandler extends HttpServlet {
	private final String UPLOAD_DIRECTORY = "C:/Users/Jon/Desktop/upload";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//getting value from jsp
		String mainFile= request.getParameter("mainfile");
		String secondFile ="";
		
		HttpSession session = request.getSession();
		ArrayList<String> fileList = (ArrayList<String>) session.getAttribute("fileList");
		
		for(String f : fileList)
		{
			if(f != mainFile)
				secondFile = f;
		}
		System.out.println("asdasd: "  + mainFile + " assd : " + secondFile);

		//main file
		int mainFileColumns = 0, mainFileRows = 0;
		int otherFileColumns = 0;
		
		//header checker
		Boolean headerDiff = false;
		
		try {
			//main file
			FileInputStream file = new FileInputStream(new File("C:/Users/Jon/Desktop/upload/" + mainFile));
			//second file
			FileInputStream file1 = new FileInputStream(new File("C:/Users/Jon/Desktop/upload/" + secondFile));

			//Get the workbook instance for XLS file 
			XSSFWorkbook workbook = new XSSFWorkbook (file);
			XSSFWorkbook workbook1 = new XSSFWorkbook (file1);

			//Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFSheet sheet1 = workbook1.getSheetAt(0);

			mainFileColumns = sheet.getRow(0).getPhysicalNumberOfCells();
			otherFileColumns = sheet1.getRow(0).getPhysicalNumberOfCells();
			mainFileRows = sheet.getLastRowNum() + 1;
			//compare difference
			if(mainFileColumns == otherFileColumns)
			{
				//style to highlight diff
				CellStyle style = workbook1.createCellStyle();
				style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());  
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				// Setting size and position of the comment in worksheet 
				XSSFDrawing xpt = sheet1.createDrawingPatriarch();
				

				//compare
				Iterator<Row> rowIterator = sheet.iterator();
				Iterator<Row> rowIterator1 = sheet1.iterator();
				
				while (rowIterator.hasNext() && rowIterator1.hasNext()) 
				{
					Row currentRow = rowIterator.next();
					Row currentRow1 = rowIterator1.next();
					Iterator<Cell> cellIterartor = currentRow.iterator();
					Iterator<Cell> cellIterator1 = currentRow1.iterator();
					
					while (cellIterartor.hasNext() && cellIterator1.hasNext()) 
					{
						Cell currentCell = cellIterartor.next();
						Cell currentCell1 = cellIterator1.next();
						
						//check header
						if(currentCell.getRowIndex() == 0 && currentCell1.getRowIndex() == 0)
						{
							System.out.println("Comparing sheet 1 Row:" + currentCell.getRowIndex() + "Col:" + currentCell.getColumnIndex() + " with sheet 2 Row" + currentCell1.getRowIndex() + "Col:" +currentCell1.getColumnIndex());
							//compare header && header is different
							if(!currentCell.toString().toLowerCase().equals(currentCell1.toString().toLowerCase()))
							{
								System.out.println("files header are different");
								request.setAttribute("message1", "Headers are different ");
								System.out.println("c1: " + currentCell.toString().toLowerCase() + " c2: " + currentCell1.toString().toLowerCase());
								
								headerDiff = true;
								request.getRequestDispatcher("/").forward(request, response);
								break;
							}
						}
//						System.out.println("Comparing sheet 1 Row:" + currentCell.getRowIndex() + "Col:" + currentCell.getColumnIndex() + " with sheet 2 Row" + currentCell1.getRowIndex() + "Col:" +currentCell1.getColumnIndex());
						//logic to compare values
						//if diff
						if (!currentCell.toString().toLowerCase().equals(currentCell1.toString().toLowerCase()))
						{
							System.out.println("cell diff");
							XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5);
							anchor.setCol1(currentCell.getColumnIndex());
							anchor.setRow1(currentCell.getRowIndex());
							// highlighting the cell
							currentCell1.setCellStyle(style);
						
							// Setting comment text
							String cellType = currentCell.getCellType().toString();
							if(cellType.equalsIgnoreCase("STRING"))
							{
								XSSFComment comment1 = xpt.createCellComment(anchor);  
								comment1.setString(new XSSFRichTextString(currentCell.getStringCellValue()));//					
								currentCell1.setCellComment(comment1);
								System.out.println("String V: " + currentCell.getStringCellValue().toString());
							}
							else if (cellType.equalsIgnoreCase("NUMERIC"))
							{
								XSSFComment comment2 = xpt.createCellComment(anchor);  
								comment2.setString(NumberToTextConverter.toText(currentCell.getNumericCellValue()));
								System.out.println("NUM V: " + currentCell.getNumericCellValue());
							}
							else if (cellType.equalsIgnoreCase("BLANK"))
							{
								XSSFComment comment3 = xpt.createCellComment(anchor);  
								comment3.setString(new XSSFRichTextString(currentCell.getStringCellValue()));
								System.out.println("Blank V: " + currentCell.getStringCellValue());
							}
						}
					}
					if(headerDiff == true)
					{
						break;
					}
				}
				
			}
			else //file has different format
			{
				System.out.println("files are diff");
				request.setAttribute("message1", "No. Of Columns are different ");
			}
			// write back to file
			try(OutputStream fileOut = new FileOutputStream("C:/Users/Jon/Desktop/upload/" + secondFile))
			{
				workbook1.write(fileOut);
				request.setAttribute("message1", "Files are compared ");
				request.setAttribute("download", secondFile);
				
			}
			catch(Exception e) 
			{  
				System.out.println(e.getMessage());  
				request.setAttribute("message1", "Files failed to compare " + e);
			}
			
			//forward request only if t
			if(headerDiff != true)
			{
				request.getRequestDispatcher("/").forward(request, response);
			}
					
			
		}
		catch(FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}


}