package com.cooperative.cabin.infrastructure.scheduler;

import com.cooperative.cabin.infrastructure.repository.WaitingListJpaRepository;
import com.cooperative.cabin.application.service.WaitingListApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WaitingListScheduler {

    private static final Logger log = LoggerFactory.getLogger(WaitingListScheduler.class);

    private final WaitingListJpaRepository waitingListRepository;
    private final WaitingListApplicationService waitingListService;

    public WaitingListScheduler(WaitingListJpaRepository waitingListRepository,
            WaitingListApplicationService waitingListService) {
        this.waitingListRepository = waitingListRepository;
        this.waitingListService = waitingListService;
    }

    // Expira items NOTIFIED cuya ventana ya venció. Corre cada 5 minutos por
    // defecto
    @Scheduled(fixedDelayString = "${waitinglist.expire.delay-ms:300000}")
    public void expireNotified() {
        int updated = waitingListRepository.expireNotifiedOlderThan(LocalDateTime.now());
        if (updated > 0) {
            log.info("WaitingListScheduler: expirados {} items NOTIFIED", updated);
        }
    }
}
