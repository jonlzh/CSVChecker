package com.csvservice;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import com.csvclass.FileDetails;
import com.csvclass.Files;
import com.csvclass.Message;
import com.google.common.io.CharStreams;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.core.header.FormDataContentDisposition;

@Path("/CSVService")
public class FileUploadService extends Application implements ContainerResponseFilter{

	@Override
	public void filter(ContainerRequestContext request,
			ContainerResponseContext response) throws IOException {
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHeaders().add("Access-Control-Allow-Headers","origin, X-Requested-With, content-type, accept, authorization");
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS, HEAD");
	}

	// http://localhost:8080/CSVProcessingWeb/rest/CSVService/compareFiles?main=Employee_Details_1.xlsx&sec=Employee_Details_2.xlsx
	@GET
	@Path("/compareFiles")
	@Produces(MediaType.TEXT_PLAIN)
	public String compareFiles(@DefaultValue("") @QueryParam("main")String mFile, @DefaultValue("") @QueryParam("sec")String sFile) {
		
		String msg = "";
		
		//main file
		int mainFileColumns = 0, mainFileRows = 0;
		int otherFileColumns = 0;

		//header checker
		Boolean headerDiff = false;

		try {
			//main file
			FileInputStream file = new FileInputStream(new File("C:/Users/Jon/Desktop/upload/" + mFile));
			//second file
			FileInputStream file1 = new FileInputStream(new File("C:/Users/Jon/Desktop/upload/" + sFile));

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
								msg = "Headers are different";
								System.out.println("c1: " + currentCell.toString().toLowerCase() + " c2: " + currentCell1.toString().toLowerCase());

								headerDiff = true;
								return msg;
							}
						}
						//								System.out.println("Comparing sheet 1 Row:" + currentCell.getRowIndex() + "Col:" + currentCell.getColumnIndex() + " with sheet 2 Row" + currentCell1.getRowIndex() + "Col:" +currentCell1.getColumnIndex());
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
						msg = "Headers are different";
						return msg;
					}
				}

			}
			else //file has different format
			{
				System.out.println("files are diff");
				msg = "No. Of Columns are different";
			}
			// write back to file
			try(OutputStream fileOut = new FileOutputStream("C:/Users/Jon/Desktop/upload/" + sFile))
			{
				workbook1.write(fileOut);
				msg = "Files are compared";
			}
			catch(Exception e) 
			{  
				System.out.println(e.getMessage());  
				msg = "Files failed to compare " + e;
			}

			//forward request only if t
			if(headerDiff != true)
			{
				return msg;
			}
		}
		catch(FileNotFoundException e) 
		{
			msg = e.getMessage();
			return msg;
		} 
		catch (IOException e) 
		{
			msg = e.getMessage();
		}
		return msg;
	}

	@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getFilePath")
	public Files getFilePath(@Context HttpServletRequest request) {
		
//		try {
//			System.out.println("eq:" + CharStreams.toString(request.getReader()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		final String UPLOAD_DIRECTORY = "C:/Users/Jon/Desktop/upload";
		Files f = new Files();		
		System.out.println("a:" + "getFilePath");
		ArrayList<String> fileList = new ArrayList<String>();
		int counter = 0;
		//process only if its multipart content
		if(ServletFileUpload.isMultipartContent(request)){
			try {
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);
				System.out.println("b:" + multiparts.size());
				for(FileItem item : multiparts){
					if(!item.isFormField()){
						String name = new File(item.getName()).getName();
						item.write( new File(UPLOAD_DIRECTORY + File.separator + name));
						fileList.add(name);
						
						if(counter == 0)
						{
							f.setFile1(name);
						}
						else if(counter == 1)
						{
							f.setFile2(name);
						}
						counter++;
					}
				}
				
				request.setAttribute("files", fileList);
				f.setMsg("Files Uploaded Successfully");
			} catch (Exception ex) {
				f.setMsg("File Upload Failed due to " + ex);
			}          

		}else{
			f.setMsg("Sorry this Servlet only handles file upload request");
		}

		
		return f;
	}
	

	// http://localhost:8080/CSVProcessingWeb/rest/CSVService/fileupload
	@GET
	@Path("/fileupload")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonArray fileupload() 
	{
		JsonArrayBuilder array = Json.createArrayBuilder();

		ArrayList<String> aList = new ArrayList<String>();
		aList.add("1");
		aList.add("2");

		for(String a : aList)
		{
			array.add(a);
		}
		System.out.println(array);
		return array.build();	
	}

	// sample url
	// http://localhost:8080/CSVProcessingWeb/rest/CSVService/asd?main=aaa.csv&sec=213.csv
	// main file = aaa.csv
	// second file = 213.csv
	@GET
	@Path("/asd")
	@Produces(MediaType.APPLICATION_JSON)
	public FileDetails displayFileNames(@DefaultValue("") @QueryParam("main")String mFile, @DefaultValue("") @QueryParam("sec")String sFile) {

		FileDetails fd = new FileDetails();
		fd.setMainFile(mFile);
		fd.setSecondFile(sFile);


		System.out.println(fd.getMainFile() + " " + fd.getSecondFile());
		return fd;
	}

	// http://localhost:8080/CSVProcessingWeb/rest/CSVService/upload?status=1
	@GET
	@Path("/upload")
	@Produces(MediaType.APPLICATION_JSON)
	public Message getReturnMsg(@DefaultValue("") @QueryParam("status")String status) {

		Message msg = new Message();
		System.out.println("status: " + status);
		if(status.equals("1"))
		{
			msg.setMsg("msg1");
		}
		else
		{
			msg.setMsg("msg2");
		}	

		return msg;
	}


	
//	@POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Path("/getFilePath")
//	public Response uploadFile(
//			@FormDataParam("file") InputStream uploadedInputStream,
//			@FormDataParam("file") FormDataContentDisposition fileDetail) {
//		// check if all form parameters are provided
//		if (uploadedInputStream == null || fileDetail == null)
//			return Response.status(400).entity("Invalid form data").build();
//		// create our destination folder, if it not exists
////		try {
////			createFolderIfNotExists("C:/Users/Jon/Desktop/upload");
////		} catch (SecurityException se) {
////			return Response.status(500)
////					.entity("Can not create destination folder on server")
////					.build();
////		}
//		String uploadedFileLocation = "C:/Users/Jon/Desktop/upload" + fileDetail.getFileName();
//		try {
//			saveToFile(uploadedInputStream, uploadedFileLocation);
//		} catch (IOException e) {
//			return Response.status(500).entity("Can not save file").build();
//		}
//		return Response.status(200)
//				.entity("File saved to " + uploadedFileLocation).build();
//	}
	
	private void saveToFile(InputStream inStream, String target)
			throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}
}
