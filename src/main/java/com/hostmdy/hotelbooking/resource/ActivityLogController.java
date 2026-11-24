package com.hostmdy.hotelbooking.resource;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostmdy.hotelbooking.domain.ActivityLog;
import com.hostmdy.hotelbooking.service.ActivityLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/activitylog")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ActivityLogController {

	private final ActivityLogService activityLogService;

	// Activity logs are private: whoever is logged in (admin or normal user)
	// only ever sees the actions recorded under their own login name.
	// The old "/all" endpoint was removed so nobody can read other users' activity.
	@GetMapping("/my")
	public ResponseEntity<?> getMyLogs(Principal principal) {
		if (principal == null) {
			return new ResponseEntity<String>("Login required to view activity logs.", HttpStatus.UNAUTHORIZED);
		}
		List<ActivityLog> logList = activityLogService.getLogsByUsername(principal.getName());
		return new ResponseEntity<List<ActivityLog>>(logList, HttpStatus.OK);
	}

	// Anyone logged in can add a manual note to their own activity history.
	@PostMapping("/create")
	public ResponseEntity<?> createLog(@RequestBody ActivityLog activityLog, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<String>("Login required to add activity logs.", HttpStatus.UNAUTHORIZED);
		}
		ActivityLog newLog = new ActivityLog();
		newLog.setAction(activityLog.getAction());
		newLog.setDetails(activityLog.getDetails());
		newLog.setUsername(principal.getName());
		return new ResponseEntity<ActivityLog>(activityLogService.save(newLog), HttpStatus.CREATED);
	}

	// Only the owner of a log entry may change it.
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateLog(@PathVariable Long id, @RequestBody ActivityLog activityLog, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<String>("Login required to update activity logs.", HttpStatus.UNAUTHORIZED);
		}
		Optional<ActivityLog> logOpt = activityLogService.findById(id);
		if (logOpt.isEmpty() || !logOpt.get().getUsername().equals(principal.getName())) {
			return new ResponseEntity<String>("Activity Log Not Found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ActivityLog>(activityLogService.update(id, activityLog), HttpStatus.OK);
	}

	// Only the owner of a log entry may delete it.
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteLog(@PathVariable Long id, Principal principal) {
		if (principal == null) {
			return new ResponseEntity<String>("Login required to delete activity logs.", HttpStatus.UNAUTHORIZED);
		}
		Optional<ActivityLog> logOpt = activityLogService.findById(id);
		if (logOpt.isEmpty() || !logOpt.get().getUsername().equals(principal.getName())) {
			return new ResponseEntity<String>("Activity Log Not Found", HttpStatus.NOT_FOUND);
		}
		activityLogService.deleteById(id);
		return new ResponseEntity<String>("Id:" + id + " is deleted!", HttpStatus.OK);
	}
}
