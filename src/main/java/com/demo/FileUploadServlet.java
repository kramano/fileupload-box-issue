package com.demo;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFolder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@WebServlet(name = "FileUploadServlet", urlPatterns = {"upload"}, loadOnStartup = 1)
public class FileUploadServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);

    public static final String BOX_CONFIG_JSON = "box_config.json";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.getWriter().print("Ahoy!");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            throw new RuntimeException("HTTP request is not a multipart request");
        }

        var connection = getConnection();
        var folder = new BoxFolder(connection, "0");
//        folder.createFolder("fileupload-test");

        try {
            var upload = new JakartaServletFileUpload<>();
            logger.info("Created file upload");
            upload.getItemIterator(request).forEachRemaining(item -> {
                String name = item.getFieldName();
                InputStream stream = item.getInputStream();
                if (item.isFormField()) {
                    logger.info("Form field " + name + " detected.");
                } else {
                    logger.info("File field " + name + " with file name " + item.getName() + " detected.");
                    try {
                        folder.uploadFile(stream, name);
                    } catch (Exception e) {
                        logger.error("Unexpected exception", e);
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("Unexpected exception", e);
            throw new RuntimeException(e);
        }
    }

    private BoxDeveloperEditionAPIConnection getConnection() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(BOX_CONFIG_JSON))) {
            BoxConfig boxConfig = BoxConfig.readFrom(inputStreamReader);
            return BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}