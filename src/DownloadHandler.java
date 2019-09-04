import java.io.BufferedInputStream;
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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

public class DownloadHandler extends HttpServlet {
	private final String UPLOAD_DIRECTORY = "C:/Users/Jon/Desktop/upload";
	private final String DOWNLOAD_DIRECTORY = "C:/Users/Jon/Desktop/downloaded_csv";
	Exception exception;
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int bytesRead = 0;
        int count = 0;
        byte[] buff = new byte[1];
  
        OutputStream out = response.getOutputStream();
  
        //---------------------------------------------------------------
        // Set the output data's mime type
        //---------------------------------------------------------------
  
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");//application/pdf" ); // MIME
                                              // type for pdf doc
  
        //---------------------------------------------------------------
        // create an input stream from fileURL
        //---------------------------------------------------------------
  
        String fileURL = UPLOAD_DIRECTORY+"/"+request.getParameter("sFile").toString();
        System.out.println("file url: " + fileURL);
        //------------------------------------------------------------
        // Content-disposition header - don't open in browser and
        // set the "Save As..." filename.
        // *There is reportedly a bug in IE4.0 which ignores this...
        //------------------------------------------------------------
  
        //-----------------------------------------------------------------
        // PROXY_HOST and PROXY_PORT should be your proxy host and port
        // that will let you go through the firewall without authentication.
        // Otherwise set the system properties and use
        // URLConnection.getInputStream().
        //-----------------------------------------------------------------
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        boolean download = false;
  
        response.setHeader("Content-disposition", "attachment; filename="+request.getParameter("sFile").toString());
  
        try
        {
  
            URL url = new URL(fileURL);
            bis = new BufferedInputStream(url.openStream());
            bos = new BufferedOutputStream(out);
  
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
            {
                try
                {
                    bos.write(bytesRead);
                    bos.flush();
                }//end of try for while loop
                catch (SocketException e)
                {
                    setError(e);
                    break;
                }
                catch (Exception e)
                {
                    System.out.println("Exception in while of TestServlet is " + e.getMessage());
                    if (e != null)
                    {
                        System.out.println("File not downloaded properly");
  
                        setError(e);
                        break;
  
                    }//if ends
  
                }//end of catch for while loop
  
            }//while ends
  
            Exception eError = getError();
  
            if (eError != null)
            {
  
                System.out.println("\n\n\n\nFile Not DownLoaded properly\n\n\n\n");
            }
            else if (bytesRead == -1)
            {
                System.out.println("\n\n\n\ndownload successful\n\n\n\n");
  
            }
            else
            {
                System.out.println("\n\n\n\ndownload not successful\n\n\n\n");
            }
  
        }
        catch (MalformedURLException e)
        {
            System.out.println("Exception inside TestServlet is " + e.getMessage());
  
        }
        catch (IOException e)
        {
            System.out.println("IOException inside TestServlet is " + e.getMessage());
        }
  
        finally
        {
            try
            {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            }
            catch (Exception e)
            {
                System.out.println("here =" + e);
            }
        }
	}
	public void setError(Exception e)
    {
        exception = e;
        System.out.println("\n\n\nException occurred is " + e + "\n\n\n");
  
    }
  
    public Exception getError()
    {
        return exception;
    }

}