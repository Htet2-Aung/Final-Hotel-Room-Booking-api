package com.hostmdy.hotelbooking.service;

import java.util.List;
import java.util.Optional;

import com.hostmdy.hotelbooking.domain.ActivityLog;

public interface ActivityLogService {

	ActivityLog save(ActivityLog activityLog);

	void log(String action, String details, String username);

	List<ActivityLog> getAllLogs();

	List<ActivityLog> getLogsByUsername(String username);

	Optional<ActivityLog> findById(Long id);

	ActivityLog update(Long id, ActivityLog activityLog);

	void deleteById(Long id);
}
