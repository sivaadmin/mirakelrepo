package com.macys.mirakl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.macys.mirakl.util.OrchConstants.CRON_NOTIFICATION_CLEANUP;
import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.X_CORRELATION_ID;
import static com.macys.mirakl.util.OrchUtil.getCorrelationId;

@Transactional
@Service
public class NotificationCleanupCronService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationCleanupCronService.class);

    @Value("${notification.cleanup.cron.job.enabled:no}")
    private String notificationCleanupCronEnabled;

    @Value("${notification.cleanup.minutes:60}")
    private int notificationCleanUpMinutes;

    @Autowired
    private SQLService sqlService;

    @Scheduled(cron = "${notification.cleanup.cron.job.schedule}")
    public void processNotificationCleanupCron() {
        try {
            MDC.put(X_CORRELATION_ID, getCorrelationId());
            MDC.put(INPUT_FILE_NAME, CRON_NOTIFICATION_CLEANUP);
            LOGGER.info("NotificationCleanupCron: Started");
            if (null != notificationCleanupCronEnabled && notificationCleanupCronEnabled.equalsIgnoreCase("yes")) {
                LOGGER.info("NotificationCleanupCron: Processing");
                sqlService.deleteOldNotifications(notificationCleanUpMinutes);
                LOGGER.info("NotificationCleanupCron: Finished");
            }
        } finally {
            MDC.remove(X_CORRELATION_ID);
            MDC.remove(INPUT_FILE_NAME);
        }
    }
}
