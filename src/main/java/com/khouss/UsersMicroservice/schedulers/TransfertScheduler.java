package com.khouss.UsersMicroservice.schedulers;

import com.khouss.UsersMicroservice.entities.Transfert;
import com.khouss.UsersMicroservice.services.TransfertService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TransfertScheduler {

    private final TransfertService transfertService;
    private final Logger log = LoggerFactory.getLogger(TransfertScheduler.class);

    @Scheduled(cron = "0 */1 * * * *")
    public void executeScheduledTransfers() {
        LocalDateTime now = LocalDateTime.now();


        List<Transfert> scheduled = transfertService.listScheduledTransfers();
        for (Transfert transfert : scheduled) {
            if (transfert.getDateProgrammee().isBefore(now.plusMinutes(1)) && transfert.getDateProgrammee().isAfter(now.minusMinutes(1))) {
                // À la minute près
                try {
                    transfertService.executer(transfert);
                    log.info("Transfert exécuté: {}", transfert.getId());
                } catch (Exception e) {
                    log.error("Erreur lors de l'exécution du transfert {}: {}", transfert.getId(), e.getMessage());
                }
            }
        }
    }
}