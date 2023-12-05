package mingu.sp.spfileupload.config;

import me.desair.tus.server.TusFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Configuration
@EnableScheduling
public class TusCleanupScheduler {
    private final TusFileUploadService tusFileUploadService;
    private final Logger log = LoggerFactory.getLogger(TusCleanupScheduler.class);

    public TusCleanupScheduler(TusFileUploadService tusFileUploadService) {
        this.tusFileUploadService = tusFileUploadService;
    }

    @Scheduled(fixedDelayString = "PT12H")
    public void cleanup() {
        log.info("START :: clean up");
        try {
            tusFileUploadService.cleanup();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
