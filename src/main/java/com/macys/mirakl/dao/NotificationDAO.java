package com.macys.mirakl.dao;

public interface NotificationDAO {

	int findNotificationDetails(String subName, String fileName, String bucketName);

	void insertNotificationDetails(String subName, String fileName, String bucketName);

	void deleteOldNotifications(int beforeMinutes);

}