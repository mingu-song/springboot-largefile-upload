package mingu.sp.spfileupload.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import me.desair.tus.server.TusFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@EnableScheduling
public class TusConfiguration {

    @Value("${tus.upload.dir}")
    private String tusStoragePath;

    @Value("${tus.upload.expiration}")
    private long tusUploadExpirationPeriod;

    private final ServletContext servletContext;

    private final Logger log = LoggerFactory.getLogger(TusConfiguration.class);

    public TusConfiguration(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @PostConstruct
    public void init() throws IOException {
        Path uploads = Path.of(tusStoragePath, "uploads");
        Path locks = Path.of(tusStoragePath, "locks");
        if (!uploads.toFile().exists()) Files.createDirectories(uploads);
        if (!locks.toFile().exists()) Files.createDirectories(locks);
    }

    @Bean
    public TusFileUploadService tusFileUploadService() {
        return new TusFileUploadService()
                .withUploadUri(servletContext.getContextPath() + "/upload")
                .withStoragePath(tusStoragePath)
                .withDownloadFeature()
                .withUploadExpirationPeriod(tusUploadExpirationPeriod);
    }
}
