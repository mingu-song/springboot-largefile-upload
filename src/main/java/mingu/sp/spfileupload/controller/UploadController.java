package mingu.sp.spfileupload.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@CrossOrigin(exposedHeaders = { "Upload-Offset", "Location" })
public class UploadController {
    private final TusFileUploadService tusFileUploadService;

    @Value("${tus.upload.dir}")
    private String tusStoragePath;

    public UploadController(TusFileUploadService tusFileUploadService) {
        this.tusFileUploadService = tusFileUploadService;
    }

    @RequestMapping(value = {"/upload", "/upload/**"}, method = { GET, POST, HEAD, PATCH, DELETE})
    public void tusUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, TusException {
        tusFileUploadService.process(request, response);

        var requestURI = request.getRequestURI();
        var uploadInfo = tusFileUploadService.getUploadInfo(requestURI);

        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
            var filePath = Files.createFile(Path.of(tusStoragePath, uploadInfo.getFileName()));
            try (InputStream is = tusFileUploadService.getUploadedBytes(requestURI)) {
                FileUtils.copyInputStreamToFile(is, filePath.toFile());
            }
            tusFileUploadService.deleteUpload(requestURI);
        }
    }
}
