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
      deluxe.setImage("https://thumb.wikimedia.org/wikipedia/commons/thumb/6/65/Modern_double_bed_in_a_decorated_hotel_room.jpg/960px-Modern_double_bed_in_a_decorated_hotel_room.jpg");
      deluxe = roomTypeRepository.save(deluxe);

      RoomType standard = new RoomType();
      standard.setName("Standard Room");
      standard.setPrice(60.0);
      standard.setDescription("<p>Cozy room with a queen-size bed, perfect for short city stays.</p>");
      standard.setFacilities("<ul><li>Queen-size bed</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Smart TV</li></ul>");
      standard.setImage("https://thumb.wikimedia.org/wikipedia/commons/thumb/3/3c/Bed_at_hotel_room_at_Hotel_Palace_Tallinn.jpg/960px-Bed_at_hotel_room_at_Hotel_Palace_Tallinn.jpg");
      standard = roomTypeRepository.save(standard);

      // One photo set per room type, matched to the column titles of the admin
      // Room List table: image1 = ROOM'S BED, image2 = ROOM'S BATH, image3 = ROOM'S TOILET.
      // All 4 seeded rooms (2 Deluxe + 2 Standard) reuse their type's set, and every
      // photo is a verified Wikimedia Commons image whose subject matches its column.
      String deluxeBed = "https://thumb.wikimedia.org/wikipedia/commons/thumb/6/65/Modern_double_bed_in_a_decorated_hotel_room.jpg/960px-Modern_double_bed_in_a_decorated_hotel_room.jpg";
      String deluxeBath = "https://thumb.wikimedia.org/wikipedia/commons/thumb/0/09/Futu-Changsheng-Hotel-bathroom-0051.jpg/960px-Futu-Changsheng-Hotel-bathroom-0051.jpg";
      String deluxeToilet = "https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e6/Premier_Inn_bathroom_toilet%2C_Horsham%2C_West_Sussex.jpg/960px-Premier_Inn_bathroom_toilet%2C_Horsham%2C_West_Sussex.jpg";
      String standardBed = "https://thumb.wikimedia.org/wikipedia/commons/thumb/3/3c/Bed_at_hotel_room_at_Hotel_Palace_Tallinn.jpg/960px-Bed_at_hotel_room_at_Hotel_Palace_Tallinn.jpg";
      String standardBath = "https://thumb.wikimedia.org/wikipedia/commons/thumb/5/54/Hotel_Barcelona_Catedral_Bathroom_2010.JPG/960px-Hotel_Barcelona_Catedral_Bathroom_2010.JPG";
      String standardToilet = "https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e5/USVI_IMG_5198_-_Minimalist_white_bathroom_with_light_wood_flooring_a_sleek_vanity_and_a_modern_toilet_featuring_clean_lines_and_soft_lighting.jpg/960px-USVI_IMG_5198_-_Minimalist_white_bathroom_with_light_wood_flooring_a_sleek_vanity_and_a_modern_toilet_featuring_clean_lines_and_soft_lighting.jpg";

      Room deluxeRoom1 = new Room();
      deluxeRoom1.setDescription("<p>Deluxe Room 301 - high floor with panoramic sea view.</p>");
      deluxeRoom1.setImage1(deluxeBed);
      deluxeRoom1.setImage2(deluxeBath);
      deluxeRoom1.setImage3(deluxeToilet);
      deluxeRoom1.setRoomType(deluxe);
      roomRepository.save(deluxeRoom1);

      Room deluxeRoom2 = new Room();
      deluxeRoom2.setDescription("<p>Deluxe Room 302 - corner room with two balconies.</p>");
      deluxeRoom2.setImage1(deluxeBed);
      deluxeRoom2.setImage2(deluxeBath);
      deluxeRoom2.setImage3(deluxeToilet);
      deluxeRoom2.setRoomType(deluxe);
      roomRepository.save(deluxeRoom2);

      Room standardRoom1 = new Room();
      standardRoom1.setDescription("<p>Standard Room 101 - quiet garden-side room.</p>");
      standardRoom1.setImage1(standardBed);
      standardRoom1.setImage2(standardBath);
      standardRoom1.setImage3(standardToilet);
      standardRoom1.setRoomType(standard);
      roomRepository.save(standardRoom1);

      Room standardRoom2 = new Room();
      standardRoom2.setDescription("<p>Standard Room 102 - bright room near the lobby.</p>");
      standardRoom2.setImage1(standardBed);
      standardRoom2.setImage2(standardBath);
      standardRoom2.setImage3(standardToilet);
      standardRoom2.setRoomType(standard);
      roomRepository.save(standardRoom2);

      System.out.println("Seeded 2 room types and 4 sample rooms.");
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