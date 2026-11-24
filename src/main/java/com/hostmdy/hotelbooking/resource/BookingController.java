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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hostmdy.hotelbooking.domain.Booking;
import com.hostmdy.hotelbooking.domain.BookingRoom;
import com.hostmdy.hotelbooking.domain.Room;
import com.hostmdy.hotelbooking.domain.SendEmail;
import com.hostmdy.hotelbooking.domain.User;
import com.hostmdy.hotelbooking.service.ActivityLogService;
import com.hostmdy.hotelbooking.service.BookingService;
import com.hostmdy.hotelbooking.service.MapValidationErrorService;
import com.hostmdy.hotelbooking.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = {"http://localhost:3000"})
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;
	private final UserService userService;
	private final ActivityLogService activityLogService;
	private final MapValidationErrorService mapErrorService;

	

	@GetMapping("/allbooking")
	public List<Booking> allbookinglist(Principal principal) {
		
		User user = userService.findByUsername(principal.getName()).get();
		
		
		return bookingService.findByUserId(user.getId());
	}
	
	@GetMapping("/all")
	public List<Booking> findAll(){
		
		return bookingService.allBookings();
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getBookingById(@PathVariable Long id) {
		Optional<Booking> bookingOpt = bookingService.findById(id);
		if (bookingOpt.isEmpty()) {
			return new ResponseEntity<String>("Booking Not Found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Booking>(bookingOpt.get(), HttpStatus.OK);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createBooking(@RequestBody Booking booking,BindingResult result,Principal principal) {
		ResponseEntity<?> responseErrorObject = mapErrorService.validate(result);
		
		if(responseErrorObject != null)
			return responseErrorObject;
		
//		Optional<Room> roomOpt = roomService.findById();
//		List<BookingRoom> bkList = new ArrayList<>();
		
		booking.setStatuss(SendEmail.Pending);
		
		Booking createBooking = bookingService.save(booking,principal.getName());

		activityLogService.log("BOOKING_CREATED", "Booking #"+createBooking.getId()+" created for guest "+createBooking.getGuestName(), principal.getName());

		return new ResponseEntity<Booking>(createBooking,HttpStatus.CREATED);
		
	}
		
	@PostMapping("/update/{bookingId}")
	public ResponseEntity<?> updateBooking(@RequestBody Booking booking, @PathVariable Long bookingId, BindingResult result, Long userId){
		ResponseEntity<?> responseErrorObject = mapErrorService.validate(result);
		Booking bookingforUser = bookingService.findById(bookingId).get();
		System.out.println("Booking controller with booking:"+bookingforUser);
		System.out.println("Booking controller with booking:"+bookingforUser.getUsername());
		User user = null;
		if (bookingforUser.getUsername() != null) {
			Optional<User> userOpt = userService.findByUsername(bookingforUser.getUsername());
			if (userOpt.isPresent()) {
				user = userOpt.get();
			}
		}
		System.out.println("Booking controller with user:"+user);
	
		
		if (responseErrorObject != null)
			return responseErrorObject;
		
		
		Booking updatedBooking=bookingService.updateBooking(bookingId,booking,user);

		if(updatedBooking != null) {
			activityLogService.log("BOOKING_CONFIRMED", "Booking #"+bookingId+" ("+updatedBooking.getGuestName()+") confirmed", updatedBooking.getUsername() != null ? updatedBooking.getUsername() : "unknown");
		      return new ResponseEntity<Booking>(updatedBooking,HttpStatus.OK);
		    }else {
		      return new ResponseEntity<String>("Booking type is not found",HttpStatus.NOT_FOUND);
		    }
	}


    @PostMapping("/create/{bookingId}/{roomId}")
    public BookingRoom createBookingRoom(@PathVariable Long bookingId, @PathVariable Long roomId) {
        return bookingService.createBookingRoom(bookingId, roomId);
    }

  
  
//    @PutMapping("/{bookingId}")
//    public Booking updateBooking(@PathVariable Long bookingId, @RequestBody Booking updatedBooking) {
//        return bookingService.updateBooking(bookingId, updatedBooking);
//    }

  

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId, Principal principal) {
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        String guestName = bookingOpt.map(Booking::getGuestName).orElse("unknown guest");
        String owner = bookingOpt.map(Booking::getUsername).orElse(null);

        bookingService.deleteBooking(bookingId);

        String actor = principal != null ? principal.getName() : (owner != null ? owner : "guest");
        activityLogService.log("BOOKING_CANCELLED", "Booking #"+bookingId+" ("+guestName+") cancelled by "+actor, actor);
    }

    @DeleteMapping("/{bookingId}/rooms/{roomId}")
    public void deleteBookingRoom(@PathVariable Long bookingId, @PathVariable Long roomId) {
        bookingService.deleteBookingRoom(bookingId, roomId);
    }
}
