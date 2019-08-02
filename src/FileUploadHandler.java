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
     //  int counter = 0;
       ArrayList<String> fileList = new ArrayList<String>();
        //process only if its multipart content
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                         new DiskFileItemFactory()).parseRequest(request);
                //String mainFile = "";
                
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = new File(item.getName()).getName();
       //                 if(counter == 0)
                    	//	mainFile = name;
                        item.write( new File(UPLOAD_DIRECTORY + File.separator + name));
                 //       counter++;
                        fileList.add(name);
                    }
                }
                //System.out.println("main file " + mainFile);
                request.setAttribute("files", fileList);
               //File uploaded successfully
             //  if(counter > 1)
            //	   request.setAttribute("message", "File Uploaded Successfully");
            //   else
            	   request.setAttribute("message", "Files Uploaded Successfully");
            } catch (Exception ex) {
           // 	if(counter > 1)
           // 		request.setAttribute("message", "Files Upload Failed due to " + ex);
           // 	else
            		request.setAttribute("message", "File Upload Failed due to " + ex);
            }          
          
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }
     
//        request.getRequestDispatcher("/result.jsp").forward(request, response);
        request.getRequestDispatcher("/").forward(request, response);
      
    }
   
}