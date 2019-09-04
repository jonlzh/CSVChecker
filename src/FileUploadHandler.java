import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadHandler extends HttpServlet {
	private final String UPLOAD_DIRECTORY = "C:/Users/Jon/Desktop/upload";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ArrayList<String> fileList = new ArrayList<String>();
		String status = "0";
		int counter = 0;
		String file1 = "", file2 = "";
		//process only if its multipart content
		if(ServletFileUpload.isMultipartContent(request)){
			try {
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);
				
				for(FileItem item : multiparts){
					if(!item.isFormField()){
						String name = new File(item.getName()).getName();
						item.write( new File(UPLOAD_DIRECTORY + File.separator + name));
						fileList.add(name);
						
						if(counter == 0)
						{
							file1 = name;
						}
						else if(counter == 1)
						{
							file2 = name;
						}
					}
				}
				
				request.setAttribute("files", fileList);
				
				request.setAttribute("message", "Files Uploaded Successfully");
				status = "1";
			} catch (Exception ex) {
				request.setAttribute("message", "File Upload Failed due to " + ex);
				status = "2";
			}          

		}else{
			request.setAttribute("message",
					"Sorry this Servlet only handles file upload request");
		}

//		response.sendRedirect("http://localhost:3000/compare");
//		        request.getRequestDispatcher("http://localhost:3000/compare").forward(request, response);
		request.getRequestDispatcher("/compare.jsp").forward(request, response);
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("message", "test");
		request.getRequestDispatcher("/").forward(request, response);
	
	}

}