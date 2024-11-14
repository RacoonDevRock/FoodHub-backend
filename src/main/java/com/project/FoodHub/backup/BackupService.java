package com.project.FoodHub.backup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class BackupService {
    private static final String BACKUP_DIR = "backups/";

//    @Scheduled(cron = "0 */15 * * * ?") // cada 15 min
    @Scheduled(cron = "0 0 0 */15 * ?")
    public void backup() {
        try {
            String backupFile = BACKUP_DIR + "backup_" + UUID.randomUUID().toString() + ".sql";

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump", "-u", "root", "-p129837465lg", "--databases", "foodhub_db"
            );
            processBuilder.redirectOutput(new File(backupFile));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Wait to finalize the process
            int processComplete = process.waitFor();

            if (processComplete == 0) {
                log.info("Copia de seguridad realizada correctamente: {}", backupFile);
            } else {
                log.error("Error al realizar la copia de seguridad.");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Ocurrió un error durante la copia de seguridad", e);
        }
    }
}
