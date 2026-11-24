package com.hostmdy.hotelbooking;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hostmdy.hotelbooking.domain.Room;
import com.hostmdy.hotelbooking.domain.RoomType;
import com.hostmdy.hotelbooking.domain.User;
import com.hostmdy.hotelbooking.domain.security.Role;
import com.hostmdy.hotelbooking.domain.security.UserRoles;
import com.hostmdy.hotelbooking.repository.RoomRepository;
import com.hostmdy.hotelbooking.repository.RoomTypeRepository;
import com.hostmdy.hotelbooking.service.RoleService;
import com.hostmdy.hotelbooking.service.UserService;

@SpringBootApplication
public class HotelRoomBookingSeverApplication implements CommandLineRunner {
  
  @Autowired
  UserService userService;
  
  @Autowired
  RoleService roleService;
  
  @Autowired
  BCryptPasswordEncoder passwordEncoder;

  @Autowired
  RoomTypeRepository roomTypeRepository;

  @Autowired
  RoomRepository roomRepository;
  
  public static void main(String[] args) {
    SpringApplication.run(HotelRoomBookingSeverApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // Seeds demo accounts and sample rooms wherever they are MISSING, so both a
    // fresh clone and an older database end up with a working admin login.
    // Each item is only created when absent - existing rows are never touched.
    if (userService.findByUsername("admin@gmail.com").isEmpty()) {
      Role adminRole = new Role();
      adminRole.setName("ROLE_ADMIN");

      User admin = new User();
      admin.setFirstname("Hotel");
      admin.setLastname("Admin");
      admin.setPhone("09-987654321");
      admin.setUsername("admin@gmail.com");
      admin.setPassword("5678"); // createUser() encodes it with BCrypt

      Set<UserRoles> adminRoles = new HashSet<>();
      adminRoles.add(new UserRoles(admin, adminRole));
      userService.createUser(admin, adminRoles);
      System.out.println("Seeded admin account -> admin@gmail.com / 5678");
    }

    if (userService.findByUsername("user@gmail.com").isEmpty()) {
      Role userRole = new Role();
      userRole.setName("ROLE_USER");

      User user = new User();
      user.setFirstname("Mg");
      user.setLastname("Mg");
      user.setPhone("09-123456789");
      user.setUsername("user@gmail.com");
      user.setPassword("1234"); // createUser() encodes it with BCrypt

      Set<UserRoles> userRoles = new HashSet<>();
      userRoles.add(new UserRoles(user, userRole));
      userService.createUser(user, userRoles);
      System.out.println("Seeded demo user -> user@gmail.com / 1234");
    }

    if (roomTypeRepository.count() == 0) {
      RoomType deluxe = new RoomType();
      deluxe.setName("Deluxe Room");
      deluxe.setPrice(120.0);
      deluxe.setDescription("<p>Spacious room with a king-size bed, sea view balcony and rain shower.</p>");
      deluxe.setFacilities("<ul><li>King-size bed</li><li>Sea view balcony</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Mini bar</li></ul>");
      deluxe.setImage("https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80");
      deluxe = roomTypeRepository.save(deluxe);

      RoomType standard = new RoomType();
      standard.setName("Standard Room");
      standard.setPrice(60.0);
      standard.setDescription("<p>Cozy room with a queen-size bed, perfect for short city stays.</p>");
      standard.setFacilities("<ul><li>Queen-size bed</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Smart TV</li></ul>");
      standard.setImage("https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=800&q=80");
      standard = roomTypeRepository.save(standard);

      Room deluxeRoom1 = new Room();
      deluxeRoom1.setDescription("<p>Deluxe Room 301 - high floor with panoramic sea view.</p>");
      deluxeRoom1.setImage1("https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80");
      deluxeRoom1.setImage2("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80");
      deluxeRoom1.setImage3("https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=800&q=80");
      deluxeRoom1.setRoomType(deluxe);
      roomRepository.save(deluxeRoom1);

      Room deluxeRoom2 = new Room();
      deluxeRoom2.setDescription("<p>Deluxe Room 302 - corner room with two balconies.</p>");
      deluxeRoom2.setImage1("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80");
      deluxeRoom2.setImage2("https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80");
      deluxeRoom2.setImage3("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=800&q=80");
      deluxeRoom2.setRoomType(deluxe);
      roomRepository.save(deluxeRoom2);

      Room standardRoom1 = new Room();
      standardRoom1.setDescription("<p>Standard Room 101 - quiet garden-side room.</p>");
      standardRoom1.setImage1("https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=800&q=80");
      standardRoom1.setImage2("https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=800&q=80");
      standardRoom1.setImage3("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=800&q=80");
      standardRoom1.setRoomType(standard);
      roomRepository.save(standardRoom1);

      System.out.println("Seeded 2 room types and 3 sample rooms.");
    }

    // Always prints the current data state on startup, so you can see in the
    // console whether seeding already happened or was skipped.
    System.out.println("Startup data check -> users: " + userService.findAllUser().size()
        + ", room types: " + roomTypeRepository.count()
        + ", rooms: " + roomRepository.count());
    // (Original one-time seed snippet kept below for reference.)
    
//    Role role1 = new Role();
//    role1.setId(1L);
//    role1.setName("ROLE_USER");
//    
//    User user1 = new User();
//    user1.setFirstname("Mg");
//    user1.setLastname("Mg");
//    user1.setPhone("09-123456789");
//    user1.setUsername("mgmg@gmail.com");
//    user1.setPassword(passwordEncoder.encode("1234"));
//    
//    Set<UserRoles> userRoles1 = new HashSet<>();
//    userRoles1.add(new UserRoles(user1, role1));
//    userService.createUser(user1, userRoles1);
//    
//    Role role2 = new Role();
//    role2.setId(2L);
//    role2.setName("ROLE_ADMIN");
//    
//    User user2 = new User();
//    user2.setFirstname("Hotel");
//    user2.setLastname("Admin");
//    user2.setPhone("09-987654321");
//    user2.setUsername("admin@gmail.com");
//    user2.setPassword(passwordEncoder.encode("5678"));
//    
//    Set<UserRoles> userRoles2 = new HashSet<>();
//    userRoles2.add(new UserRoles(user2, role2));
//    
//    userService.createUser(user2, userRoles2);
    
  }

}