import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
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
		String mainFile = request.getParameter("mainfile");
		String name = request.getParameter("name");
		System.out.println("asdasd: "  + mainFile + " assd : " + name);
		mainFile = "test.xlsx";
		//main file
		int mainFileColumns = 0, mainFileRows = 0;
		int otherFileColumns = 0;

		try {
			//main file
			FileInputStream file = new FileInputStream(new File("C:/Users/Jon/Desktop/upload/" + mainFile));
			//second file
			FileInputStream file1 = new FileInputStream(new File("C:/Users/Jon/Desktop/upload/test1.xlsx"));

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
	            XSSFComment comment1 = xpt.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));  
	            
				//compare
				Iterator<Row> rowIterator = sheet.iterator();
				Iterator<Row> rowIterator1 = sheet1.iterator();
				while (rowIterator.hasNext() && rowIterator1.hasNext()) {
					Row currentRow = rowIterator.next();
					Row currentRow1 = rowIterator1.next();
					Iterator<Cell> cellIterartor = currentRow.iterator();
					Iterator<Cell> cellIterator1 = currentRow1.iterator();
					while (cellIterartor.hasNext() && cellIterator1.hasNext()) {
						Cell currentCell = cellIterartor.next();
						Cell currentCell1 = cellIterator1.next();
						 
						//logic to compare values
						//if diff
						if(!currentCell.toString().toLowerCase().equals(currentCell1.toString().toLowerCase()))
						{
							System.out.println("cell diff");
							// Setting comment text  
					        comment1.setString(new XSSFRichTextString(currentCell.getStringCellValue()));  
					        // Associating comment to the cell  
					        currentCell1.setCellComment(comment1); 
					        // highlighting the cell
							currentCell1.setCellStyle(style);
							
							// write back to file
							try(OutputStream fileOut = new FileOutputStream("C:/Users/Jon/Desktop/upload/test1.xlsx"))
							{
								workbook1.write(fileOut);
								request.setAttribute("message", "Files are compared ");
							}catch(Exception e) {  
								System.out.println(e.getMessage());  
								request.setAttribute("message", "Files failed to compare " + e);
							}  

						}
						else // same
						{

						}
					}
				}
			}
			else //file has different format
			{
				System.out.println("files are diff");
				request.setAttribute("message", "No. Of Columns are different ");
			}


		}catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//        request.getRequestDispatcher("/result.jsp").forward(request, response);
		request.getRequestDispatcher("/").forward(request, response);

	}


}