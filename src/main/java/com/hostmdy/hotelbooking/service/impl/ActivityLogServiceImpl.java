package com.hostmdy.hotelbooking.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hostmdy.hotelbooking.domain.ActivityLog;
import com.hostmdy.hotelbooking.repository.ActivityLogRepository;
import com.hostmdy.hotelbooking.service.ActivityLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

	private final ActivityLogRepository activityLogRepository;

	@Override
	public ActivityLog save(ActivityLog activityLog) {
		// TODO Auto-generated method stub
		return activityLogRepository.save(activityLog);
	}

	@Override
	public void log(String action, String details, String username) {
		// TODO Auto-generated method stub
		ActivityLog activityLog = new ActivityLog();
		activityLog.setAction(action);
		activityLog.setDetails(details);
		activityLog.setUsername(username);
		activityLogRepository.save(activityLog);
	}

	@Override
	public List<ActivityLog> getAllLogs() {
		// TODO Auto-generated method stub
		return (List<ActivityLog>) activityLogRepository.findAll();
	}

	@Override
	public List<ActivityLog> getLogsByUsername(String username) {
		// only the actions that belong to the given login name
		return activityLogRepository.findByUsernameOrderByCreatedAtDesc(username);
	}

	@Override
	public Optional<ActivityLog> findById(Long id) {
		return activityLogRepository.findById(id);
	}

	@Override
	public ActivityLog update(Long id, ActivityLog activityLog) {
		// username and createdAt are preserved; only the text fields change
		ActivityLog existing = activityLogRepository.findById(id).orElse(null);
		if (existing == null) {
			return null;
		}
		existing.setAction(activityLog.getAction());
		existing.setDetails(activityLog.getDetails());
		return activityLogRepository.save(existing);
	}

	@Override
	public void deleteById(Long id) {
		activityLogRepository.deleteById(id);
	}

}
