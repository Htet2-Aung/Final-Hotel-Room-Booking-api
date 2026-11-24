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
import com.hostmdy.hotelbooking.domain.Room;
import com.hostmdy.hotelbooking.domain.RoomType;
import com.hostmdy.hotelbooking.service.ActivityLogService;
import com.hostmdy.hotelbooking.service.MapValidationErrorService;
import com.hostmdy.hotelbooking.service.RoomService;
import com.hostmdy.hotelbooking.service.RoomTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
public class RoomController {
	
	private final RoomService roomService;
	private final RoomTypeService roomTypeService;
	private final ActivityLogService activityLogService;
	private final MapValidationErrorService mapErrorService;
	
	@PostMapping("/create/{roomTypeId}")
	public ResponseEntity<?> createRoom(@RequestBody Room room,@PathVariable Long roomTypeId, BindingResult result, Principal principal){
		ResponseEntity<?> responseErrorObj = mapErrorService.validate(result);
		
		if(responseErrorObj != null)
			return responseErrorObj;
		
		RoomType roomType = roomTypeService.findById(roomTypeId).get();
		
		room.setRoomType(roomType);

		Room createRoom = roomService.save(room, roomType);

		String actor = principal != null ? principal.getName() : "unknown";
		activityLogService.log("ROOM_CREATED", "Room #"+createRoom.getId()+" ("+roomType.getName()+") created by "+actor, actor);

		return new ResponseEntity<Room>(createRoom,HttpStatus.CREATED);
	    }
		
	@GetMapping("/all")
	public ResponseEntity<?> getAllRoom(){
		List<Room> roomList = roomService.findAll();
		
		if(roomList.isEmpty())
			return new ResponseEntity<String>("Rooms are not found",HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<List<Room>>(roomList,HttpStatus.OK);
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<?> findRoomById(@PathVariable Long id){
		Optional<Room> roomOptional = roomService.findById(id);
		
		if(roomOptional.isEmpty())
			return new ResponseEntity<String>("Room is not found",HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<Room>(roomOptional.get(),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteRoomById(@PathVariable Long id, Principal principal){
		Optional<Room> roomOpt = roomService.findById(id);
		String details = "Room #"+id+" was deleted";
		if (roomOpt.isPresent()) {
			Room room = roomOpt.get();
			String typeName = room.getRoomType() != null ? room.getRoomType().getName() : "no type";
			details = "Deleted Room #"+id+" ("+typeName+")";
		}

		roomService.deleteById(id);

		String actor = principal != null ? principal.getName() : "admin";
		activityLogService.log("ROOM_DELETED", details+" by "+actor, actor);

		return new ResponseEntity<String>(id+" is deleted!",HttpStatus.OK);
	}
	
	@PostMapping("/update/{roomTypeId}")
	public ResponseEntity<?> updateRoom(@RequestBody Room room,@PathVariable Long roomTypeId, BindingResult result, Principal principal){
		ResponseEntity<?> responseError = mapErrorService.validate(result);
		
		if(responseError != null) 
			return responseError;
		
		RoomType roomType = roomTypeService.findById(roomTypeId).get();
		
		Room updateRoom = roomService.update(room,roomType);
		
		if(updateRoom != null) {
			String actor = principal != null ? principal.getName() : "unknown";
			activityLogService.log("ROOM_UPDATED", "Room #"+updateRoom.getId()+" ("+roomType.getName()+") updated by "+actor, actor);
			return new ResponseEntity<Room>(updateRoom,HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("Room is not found",HttpStatus.NOT_FOUND);
		}
		
	}
	



}
