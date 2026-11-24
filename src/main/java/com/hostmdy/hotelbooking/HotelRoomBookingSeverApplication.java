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
  
  // Demo photos, matched to the column titles of the admin Room List table:
  // image1 = ROOM'S BED, image2 = ROOM'S BATH, image3 = ROOM'S TOILET.
  // All URLs are Unsplash CDN links that were downloaded and visually verified:
  // every BED photo shows a bedroom, every BATH photo a bath/vanity area and
  // every TOILET photo has a toilet visible. All rooms of one room type share
  // the same photo set, so rows of a type look consistent in the table.
  private static final String STD_BED = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=800&q=80";
  private static final String STD_BATH = "https://images.unsplash.com/photo-1620626011761-996317b8d101?auto=format&fit=crop&w=800&q=80";
  private static final String STD_TOILET = "https://images.unsplash.com/photo-1552321554-5fefe8c9ef14?auto=format&fit=crop&w=800&q=80";
  private static final String SUP_BED = "https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=800&q=80";
  private static final String SUP_BATH = "https://images.unsplash.com/photo-1564540583246-934409427776?auto=format&fit=crop&w=800&q=80";
  private static final String SUP_TOILET = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=800&q=80";
  private static final String DLX_BED = "https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80";
  private static final String DLX_BATH = "https://images.unsplash.com/photo-1604709177225-055f99402ea3?auto=format&fit=crop&w=800&q=80";
  private static final String DLX_TOILET = "https://images.unsplash.com/photo-1631889993959-41b4e9c6e3c5?auto=format&fit=crop&w=800&q=80";
  private static final String STE_BED = "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=800&q=80";
  private static final String STE_BATH = "https://images.unsplash.com/photo-1507652313519-d4e9174996dd?auto=format&fit=crop&w=800&q=80";
  private static final String STE_TOILET = "https://images.unsplash.com/photo-1600566752355-35792bedcfea?auto=format&fit=crop&w=800&q=80";

  // Descriptions of the 6 seeded demo rooms - also used to recognize those rows
  // in an existing database so their photos and room-type link can be repaired.
  private static final String STANDARD_ROOM_101_DESC = "<p>Standard Room 101 - quiet garden-side room.</p>";
  private static final String STANDARD_ROOM_102_DESC = "<p>Standard Room 102 - bright room near the lobby.</p>";
  private static final String SUPERIOR_ROOM_201_DESC = "<p>Superior Room 201 - sea-view balcony room.</p>";
  private static final String DELUXE_ROOM_301_DESC = "<p>Deluxe Room 301 - high floor with panoramic sea view.</p>";
  private static final String DELUXE_ROOM_302_DESC = "<p>Deluxe Room 302 - corner room with two balconies.</p>";
  private static final String SUITE_ROOM_401_DESC = "<p>Suite Room 401 - top-floor suite with a separate living area.</p>";

  public static void main(String[] args) {
    SpringApplication.run(HotelRoomBookingSeverApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // Seeds demo accounts and sample rooms wherever they are MISSING, so both a
    // fresh clone and an older database end up with a working admin login.
    // User rows are only created when absent; the managed demo room/type photos
    // are force-refreshed by seedRoomData() to the verified on-topic set.
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

    // Seeds the 4 demo room types (Standard, Superior, Deluxe, Suite) and the
    // demo rooms wherever they are MISSING, and force-refreshes the photos of
    // the managed demo rows, so a backend restart always leaves the Room List
    // table showing on-topic bed / bath / toilet photos.
    seedRoomData();

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
   * Creates any missing demo room types / rooms and force-refreshes the photos
   * of the managed demo rows. Demo rows are recognized by their seeded name /
   * description; rows created through the admin UI are never touched.
   */
  private void seedRoomData() {
    RoomType standard = upsertRoomType("Standard Room", 60.0,
        "<p>Cozy room with a queen-size bed, perfect for short city stays.</p>",
        "<ul><li>Queen-size bed</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Smart TV</li></ul>",
        STD_BED);
    RoomType superior = upsertRoomType("Superior Room", 90.0,
        "<p>Bright room with a king-size bed and a private sea-view balcony.</p>",
        "<ul><li>King-size bed</li><li>Sea-view balcony</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Rain shower</li></ul>",
        SUP_BED);
    RoomType deluxe = upsertRoomType("Deluxe Room", 120.0,
        "<p>Spacious room with a king-size bed, sea view balcony and rain shower.</p>",
        "<ul><li>King-size bed</li><li>Sea view balcony</li><li>Free Wi-Fi</li><li>Air conditioning</li><li>Mini bar</li></ul>",
        DLX_BED);
    RoomType suite = upsertRoomType("Suite Room", 180.0,
        "<p>Top-floor suite with a separate living area, panoramic sea view and spa bath.</p>",
        "<ul><li>King-size bed</li><li>Separate living area</li><li>Panoramic sea view</li><li>Spa bath</li><li>Mini bar</li><li>Free Wi-Fi</li></ul>",
        STE_BED);

    upsertRoom(STANDARD_ROOM_101_DESC, STD_BED, STD_BATH, STD_TOILET, standard);
    upsertRoom(STANDARD_ROOM_102_DESC, STD_BED, STD_BATH, STD_TOILET, standard);
    upsertRoom(SUPERIOR_ROOM_201_DESC, SUP_BED, SUP_BATH, SUP_TOILET, superior);
    upsertRoom(DELUXE_ROOM_301_DESC, DLX_BED, DLX_BATH, DLX_TOILET, deluxe);
    upsertRoom(DELUXE_ROOM_302_DESC, DLX_BED, DLX_BATH, DLX_TOILET, deluxe);
    upsertRoom(SUITE_ROOM_401_DESC, STE_BED, STE_BATH, STE_TOILET, suite);
  }

  /** Creates the room type when absent; otherwise refreshes only its photo. */
  private RoomType upsertRoomType(String name, Double price, String description, String facilities, String image) {
    for (RoomType existing : roomTypeRepository.findAll()) {
      if (name.equals(existing.getName())) {
        if (!image.equals(existing.getImage())) {
          existing.setImage(image);
          roomTypeRepository.save(existing);
          System.out.println("Refreshed photo of room type -> " + name);
        }
        return existing;
      }
    }
    RoomType roomType = new RoomType();
    roomType.setName(name);
    roomType.setPrice(price);
    roomType.setDescription(description);
    roomType.setFacilities(facilities);
    roomType.setImage(image);
    System.out.println("Seeded room type -> " + name);
    return roomTypeRepository.save(roomType);
  }

  /**
   * Creates the demo room when absent (matched by its HTML-free description);
   * otherwise force-refreshes its bed/bath/toilet photos and room-type link,
   * so a restarted backend always shows the verified photo set for demo rows.
   */
  private void upsertRoom(String description, String bed, String bath, String toilet, RoomType roomType) {
    String key = plainDescription(description);
    for (Room existing : roomRepository.findAll()) {
      if (key.equals(plainDescription(existing.getDescription()))) {
        if (!bed.equals(existing.getImage1()) || !bath.equals(existing.getImage2())
            || !toilet.equals(existing.getImage3()) || existing.getRoomType() == null
            || !roomType.getId().equals(existing.getRoomType().getId())) {
          existing.setImage1(bed);
          existing.setImage2(bath);
          existing.setImage3(toilet);
          existing.setRoomType(roomType);
          roomRepository.save(existing);
          System.out.println("Refreshed demo room -> " + key);
        }
        return;
      }
    }
    Room room = new Room();
    room.setDescription(description);
    room.setImage1(bed);
    room.setImage2(bath);
    room.setImage3(toilet);
    room.setRoomType(roomType);
    roomRepository.save(room);
    System.out.println("Seeded demo room -> " + key);
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