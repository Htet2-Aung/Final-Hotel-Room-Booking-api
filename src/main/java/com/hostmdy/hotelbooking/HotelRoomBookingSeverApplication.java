package com.hostmdy.hotelbooking;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
  
  // Demo photos, matched to the column titles of the admin Room List table:
  // image1 = ROOM'S BED, image2 = ROOM'S BATH, image3 = FRONT VIEW.
  // These mirror the images currently used in the app: the Deluxe set and the
  // Standard FRONT VIEW are Wikimedia Commons photos; the Standard type photo
  // and the two Standard rooms' bed/bath photos were uploaded through the admin
  // UI and are served from the backend's uploads/ folder (keep those files, or
  // the seeded URLs will stop loading).
  private static final String DELUXE_BED = "https://thumb.wikimedia.org/wikipedia/commons/thumb/5/5d/Hotel_Room_Chicago_Hilton2.jpg/960px-Hotel_Room_Chicago_Hilton2.jpg";
  private static final String DELUXE_BATH = "https://thumb.wikimedia.org/wikipedia/commons/thumb/a/a8/USVI_IMG_5256_-_Modern_white_bathroom_with_a_built-in_bathtub_and_shower_featuring_neatly_stacked_pillows_on_the_side.jpg/960px-USVI_IMG_5256_-_Modern_white_bathroom_with_a_built-in_bathtub_and_shower_featuring_neatly_stacked_pillows_on_the_side.jpg";
  private static final String DELUXE_FRONT_VIEW = "https://upload.wikimedia.org/wikipedia/commons/9/9b/Superior_Bed_Room_at_Shaza_Al_Madina.jpg";
  private static final String STANDARD_BED = "http://localhost:8181/uploads/155a2ebc-ff37-42f2-9eab-09cd2c6d13aa.jpg";
  private static final String STANDARD_FRONT_VIEW = "https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e3/DSC_6622_Cozy_modern_hotel_room_with_a_large_king_bed_colorful_wall_art_and_a_minimalist_seating_area.jpg/960px-DSC_6622_Cozy_modern_hotel_room_with_a_large_king_bed_colorful_wall_art_and_a_minimalist_seating_area.jpg";
  private static final String STANDARD_101_BED = "http://localhost:8181/uploads/9cdc12dd-9e7d-4298-8b04-03ad37ed2858.jpg";
  private static final String STANDARD_101_BATH = "http://localhost:8181/uploads/fad96bd1-1c94-4bfb-afdd-ef5470616f36.jpg";
  private static final String STANDARD_102_BED = "http://localhost:8181/uploads/b741c2e2-5cb0-477d-9e6d-1b597bd91279.jpg";
  private static final String STANDARD_102_BATH = "http://localhost:8181/uploads/ef11bd71-b17e-4dd9-9338-2112562930a9.jpg";

  // Descriptions of the 4 seeded demo rooms - also used to recognize those rows in an
  // existing database so their photos can be repaired (see refreshDemoImages()).
  private static final String DELUXE_ROOM_301_DESC = "<p>Deluxe Room 301 - high floor with panoramic sea view.</p>";
  private static final String DELUXE_ROOM_302_DESC = "<p>Deluxe Room 302 - corner room with two balconies.</p>";
  private static final String STANDARD_ROOM_101_DESC = "<p>Standard Room 101 - quiet garden-side room.</p>";
  private static final String STANDARD_ROOM_102_DESC = "<p>Standard Room 102 - bright room near the lobby.</p>";

  public static void main(String[] args) {
    SpringApplication.run(HotelRoomBookingSeverApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // Seeds demo accounts and sample rooms wherever they are MISSING, so both a
    // fresh clone and an older database end up with a working admin login.
    // Each item is only created when absent - existing rows are never touched,
    // except the demo photos, which refreshDemoImages() repairs to the verified set.
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
      deluxe.setDescription("Spacious room with a king-size bed, sea view balcony and rain shower.");
      deluxe.setFacilities("<ul><li>King-size bed</li><li>Sea view balcony</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Mini bar</li></ul>");
      deluxe.setImage(DELUXE_BED);
      deluxe = roomTypeRepository.save(deluxe);

      RoomType standard = new RoomType();
      standard.setName("Standard Room");
      standard.setPrice(60.0);
      standard.setDescription("<p>Cozy room with a queen-size bed, perfect for short city stays.</p>");
      standard.setFacilities("<ul><li>Queen-size bed</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Smart TV</li></ul>");
      standard.setImage(STANDARD_BED);
      standard = roomTypeRepository.save(standard);

      // Photo sets are the class-level constants above (shared with refreshDemoImages()).

      Room deluxeRoom1 = new Room();
      deluxeRoom1.setDescription(DELUXE_ROOM_301_DESC);
      deluxeRoom1.setImage1(DELUXE_BED);
      deluxeRoom1.setImage2(DELUXE_BATH);
      deluxeRoom1.setImage3(DELUXE_FRONT_VIEW);
      deluxeRoom1.setRoomType(deluxe);
      roomRepository.save(deluxeRoom1);

      Room deluxeRoom2 = new Room();
      deluxeRoom2.setDescription(DELUXE_ROOM_302_DESC);
      deluxeRoom2.setImage1(DELUXE_BED);
      deluxeRoom2.setImage2(DELUXE_BATH);
      deluxeRoom2.setImage3(DELUXE_FRONT_VIEW);
      deluxeRoom2.setRoomType(deluxe);
      roomRepository.save(deluxeRoom2);

      Room standardRoom1 = new Room();
      standardRoom1.setDescription(STANDARD_ROOM_101_DESC);
      standardRoom1.setImage1(STANDARD_101_BED);
      standardRoom1.setImage2(STANDARD_101_BATH);
      standardRoom1.setImage3(STANDARD_FRONT_VIEW);
      standardRoom1.setRoomType(standard);
      roomRepository.save(standardRoom1);

      Room standardRoom2 = new Room();
      standardRoom2.setDescription(STANDARD_ROOM_102_DESC);
      standardRoom2.setImage1(STANDARD_102_BED);
      standardRoom2.setImage2(STANDARD_102_BATH);
      standardRoom2.setImage3(STANDARD_FRONT_VIEW);
      standardRoom2.setRoomType(standard);
      roomRepository.save(standardRoom2);

      System.out.println("Seeded 2 room types and 4 sample rooms.");
    }

    // Fills in any MISSING demo photos in an existing database (photos already
    // set through the admin UI are never overwritten), so a backend restart is
    // enough to fix blank image fields from older seeds.
    refreshDemoImages();

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

  /**
   * Fills in MISSING photos of the seeded demo room types and rooms on startup.
   * Rows are recognized by their seeded name / description; only blank image
   * fields are repaired, so photos set through the admin UI are never overwritten.
   */
  private void refreshDemoImages() {
    Map<String, String[]> demoRoomImages = new LinkedHashMap<>();
    // Keys are HTML-free so rows match whether their stored description is the
    // seeded "<p>...</p>" text or a plain/variant version saved by ReactQuill.
    demoRoomImages.put(plainDescription(DELUXE_ROOM_301_DESC), new String[] { DELUXE_BED, DELUXE_BATH, DELUXE_FRONT_VIEW });
    demoRoomImages.put(plainDescription(DELUXE_ROOM_302_DESC), new String[] { DELUXE_BED, DELUXE_BATH, DELUXE_FRONT_VIEW });
    demoRoomImages.put(plainDescription(STANDARD_ROOM_101_DESC), new String[] { STANDARD_101_BED, STANDARD_101_BATH, STANDARD_FRONT_VIEW });
    demoRoomImages.put(plainDescription(STANDARD_ROOM_102_DESC), new String[] { STANDARD_102_BED, STANDARD_102_BATH, STANDARD_FRONT_VIEW });

    int fixedRooms = 0;
    for (Room room : roomRepository.findAll()) {
      // Rooms saved through the admin UI carry rich-text markup in the
      // description ("<p>...</p>" from ReactQuill), so compare HTML-free text.
      String[] expected = demoRoomImages.get(plainDescription(room.getDescription()));
      // Only fill in missing photos - never overwrite images set via the admin UI.
      if (expected != null
          && (isBlank(room.getImage1()) || isBlank(room.getImage2()) || isBlank(room.getImage3()))) {
        room.setImage1(expected[0]);
        room.setImage2(expected[1]);
        room.setImage3(expected[2]);
        roomRepository.save(room);
        fixedRooms++;
      }
    }

    int fixedRoomTypes = 0;
    for (RoomType roomType : roomTypeRepository.findAll()) {
      if ("Deluxe Room".equals(roomType.getName()) && isBlank(roomType.getImage())) {
        roomType.setImage(DELUXE_BED);
        roomTypeRepository.save(roomType);
        fixedRoomTypes++;
      } else if ("Standard Room".equals(roomType.getName()) && isBlank(roomType.getImage())) {
        roomType.setImage(STANDARD_BED);
        roomTypeRepository.save(roomType);
        fixedRoomTypes++;
      }
    }

    if (fixedRooms > 0 || fixedRoomTypes > 0) {
      System.out.println("Repaired demo photos -> room types: " + fixedRoomTypes + ", rooms: " + fixedRooms);
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  /**
   * Strips HTML tags/entities and collapses whitespace so the seeded plain-text
   * descriptions still match after a room was edited with the ReactQuill editor.
   */
  private static String plainDescription(String description) {
    if (description == null) {
      return null;
    }
    return description
        .replaceAll("<[^>]*>", "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replaceAll("\\s+", " ")
        .trim();
  }

}