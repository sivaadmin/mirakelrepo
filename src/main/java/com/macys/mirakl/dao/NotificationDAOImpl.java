package com.macys.mirakl.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class NotificationDAOImpl implements NotificationDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDAOImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public int findNotificationDetails(String subName, String fileName, String bucketName) {
		try {
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("select COUNT(1) from ").append("MP_NOTIFICATION_TRACKER")
					.append(" where SUB_NAME=? and FILE_NAME=? and BUCKET_NAME=?");
			return (int)jdbcTemplate.queryForObject(queryBuilder.toString(), Integer.class,
					new Object[] { subName, fileName, bucketName});
		} catch(EmptyResultDataAccessException erdae) {
			LOGGER.error("Exception in findNotificationDetails:",erdae);
			return 0;			
		}
	}

	@Override
	public void insertNotificationDetails(String subName, String fileName, String bucketName) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append("MP_NOTIFICATION_TRACKER")
				.append("(CREATED_TS,SUB_NAME,FILE_NAME,BUCKET_NAME)").append("values(?,?,?,?)");

		jdbcTemplate.update(queryBuilder.toString(), timestamp, subName, fileName, bucketName);
	}

	@Override
	public void deleteOldNotifications(int beforeMinutes) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM MP_NOTIFICATION_TRACKER WHERE CREATED_TS< NOW() - INTERVAL ? MINUTE");
		jdbcTemplate.update(queryBuilder.toString(),beforeMinutes);
	}

}