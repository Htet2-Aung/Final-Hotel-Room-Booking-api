package com.hostmdy.hotelbooking.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.hostmdy.hotelbooking.domain.ActivityLog;

public interface ActivityLogRepository extends CrudRepository<ActivityLog, Long> {

	List<ActivityLog> findByUsernameOrderByCreatedAtDesc(String username);
}
