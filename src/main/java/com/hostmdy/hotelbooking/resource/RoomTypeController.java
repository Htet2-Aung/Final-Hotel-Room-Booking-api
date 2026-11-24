package com.hostmdy.hotelbooking.resource;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.hostmdy.hotelbooking.domain.RoomType;
import com.hostmdy.hotelbooking.service.ActivityLogService;
import com.hostmdy.hotelbooking.service.MapValidationErrorService;
import com.hostmdy.hotelbooking.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roomType")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
public class RoomTypeController {
	private final RoomTypeService roomTypeService;
	private final ActivityLogService activityLogService;
	private final MapValidationErrorService mapErrorService;
	
	@PostMapping("/create")
	public ResponseEntity<?> createRoomType(@RequestBody RoomType roomType,BindingResult result, Principal principal) {
		ResponseEntity<?> responseErrorObject = mapErrorService.validate(result);
		
		if(responseErrorObject != null)
			return responseErrorObject;
		
		RoomType createRoomType = roomTypeService.save(roomType);
		
		String actor = principal != null ? principal.getName() : "unknown";
		activityLogService.log("ROOMTYPE_CREATED", "Room Type '"+createRoomType.getName()+"' (#"+createRoomType.getId()+", "+createRoomType.getPrice()+" MMK) created by "+actor, actor);
		
		return new ResponseEntity<RoomType>(createRoomType,HttpStatus.CREATED);
		
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<?> findRoomTypeById(@PathVariable Long id){
		Optional<RoomType> roomTypeOpt = roomTypeService.findById(id);
		
		if(roomTypeOpt.isEmpty())
			return new ResponseEntity<String>("Room Type is not found",HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<RoomType>(roomTypeOpt.get(),HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllRoomType(){
		List<RoomType> roomTypeList = roomTypeService.findAll();
		
		if(roomTypeList.isEmpty())
			return new ResponseEntity<String>("Room type is not found",HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<List<RoomType>>(roomTypeList,HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteRoomTypeById(@PathVariable Long id, Principal principal){
		Optional<RoomType> roomTypeOpt = roomTypeService.findById(id);
		String name = roomTypeOpt.map(RoomType::getName).orElse("unknown");
		
		roomTypeService.deleteById(id);
		
		String actor = principal != null ? principal.getName() : "admin";
		
		if(roomTypeService.findById(id).isPresent()) {
			activityLogService.log("ROOMTYPE_DELETE_FAILED", "Could not delete Room Type '"+name+"' (#"+id+") - rooms may still be linked (by "+actor+")", actor);
			return new ResponseEntity<String>("Can't delete",HttpStatus.NOT_FOUND);
		}
		
		activityLogService.log("ROOMTYPE_DELETED", "Deleted Room Type '"+name+"' (#"+id+") by "+actor, actor);
		
		return new ResponseEntity<String>("Id:"+id+" is deleted!",HttpStatus.OK);
	}
	
	@PostMapping("/update")
	public ResponseEntity<?> updateRoomType(@Valid @RequestBody RoomType roomType, BindingResult result, Principal principal){
		ResponseEntity<?> responseError = mapErrorService.validate(result);
		
		if(responseError != null)
			return responseError;
		
		RoomType updateRoomType = roomTypeService.updateRoomType(roomType);
		
		if(updateRoomType != null) {
			String actor = principal != null ? principal.getName() : "unknown";
			activityLogService.log("ROOMTYPE_UPDATED", "Room Type '"+updateRoomType.getName()+"' (#"+updateRoomType.getId()+") updated by "+actor, actor);
			return new ResponseEntity<RoomType>(updateRoomType,HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Room type is not found",HttpStatus.NOT_FOUND);
		}
		
		
	}
}
