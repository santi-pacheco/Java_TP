package servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/uploads/*")
public class ImageServlet extends HttpServlet {

    private String uploadPath = "C:" + File.separator + "fatmovies_uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String requestedFile = request.getPathInfo();
        if (requestedFile == null || requestedFile.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(uploadPath, requestedFile);
        if (file.exists() && file.isFile()) {
            String contentType = getServletContext().getMimeType(file.getName());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            Files.copy(file.toPath(), response.getOutputStream());
            
        } else {
            response.setContentType("image/png");
            try (InputStream in = getServletContext().getResourceAsStream("/utils/default_profile.png");
                 OutputStream out = response.getOutputStream()) {
                
                if (in == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }
        }
    }
}