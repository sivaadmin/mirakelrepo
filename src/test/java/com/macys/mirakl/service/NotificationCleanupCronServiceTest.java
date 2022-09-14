package com.macys.mirakl.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationCleanupCronServiceTest {

    @InjectMocks
    private NotificationCleanupCronService notificationCleanupCronService;

    @Mock
    private SQLService sqlService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(notificationCleanupCronService,"notificationCleanUpMinutes",15);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testProcessNotificationCleanupCron(){
        //Given
        ReflectionTestUtils.setField(notificationCleanupCronService,"notificationCleanupCronEnabled","yes");
        doNothing().when(sqlService).deleteOldNotifications(15);

        //When
        notificationCleanupCronService.processNotificationCleanupCron();
        //Then
        verify(sqlService,times(1)).deleteOldNotifications( anyInt());

    }

    @Test
    void testProcessNotificationCleanupCronNotEnabled(){
        //Given
        ReflectionTestUtils.setField(notificationCleanupCronService,"notificationCleanupCronEnabled","no");

        //When
        notificationCleanupCronService.processNotificationCleanupCron();
        //Then
        verify(sqlService,times(0)).deleteOldNotifications( anyInt());

    }
}