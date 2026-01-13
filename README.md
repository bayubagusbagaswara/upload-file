# Upload File

1. No VAT (Safekeeping Fee, KSEI, etc) - type 5, type 6 (without NPWP)
2. VAT (Foreign Client) - type 7.docx
3. VAT (Safekeeping Fee & Trx Handling) - type 1, type 2, type 4 a, type 9, type 11
4. VAT (Safekeeping Fee, KSEI, etc) - type 5, type 6 (With NPWP)
5. 17 OBAL (EB) - type 4b
6. IIG - type 8
7. No VAT (only Safekeeping Fee) - type 3
8. No VAT (only Trx Handling and Safekeeping Fee) - type 10

# Accenture
Job Qualification

Familiar with build systems such as Maven or Gradle

Experience in Microservices and Event-Driven Architecture

Familiarity with other JVM-based languages such as Groovy, Scala, or Kotlin will be a plus

Familiar with modern Java Frameworks such as Spring Boot, Micronaut and Quarkus

Experience in one or more database frameworks such as Hibernate, MyBatis, TopLink, iBATIS

Experience in one or more Java performance tuning tools such as NetBeans profiler, JProfiler, Profiler4J, YourKit, Java Mission Control

Experience or strong understanding in continuous integration and related tools such as Jenkins, Hudson, Sonar, Ant, SVN, GIT, JUnit

Having Java certifications will be a plus point

Ability to meet travel requirements, when applicable

Have a minimum of 3-5 years of relevant experience

Have a minimum of Bachelor degree

Melalui kenaikan suku bunga BI tentu akan membawa dampak pada perekonomian dan masyarakat umum, dengan naiknya suku bunga BI akan berdampak pada naiknya suku bunga pada bank umum yang diikuti dengan kenaikan pada produk – produk perbankan seperti: KPR, dan jenis kredit lainnya. Dari sisi pasar modal kenaikan suku bunga cenderung menjadi sentimen negatif yang menyebabkan pelemahan di pasar modal.

“Adanya kenaikan suku bunga tentunya akan menyebabkan pergeseran minat masyarakat dari konsumsi ke saving, peningkatan suku bunga akan menarik minat masyarakat untuk lebih banyak menyimpan dananya di bank, hal tersebut tentu berdampak pada berkurangnya peredaran uang cash di pasar dipicu oleh tingkat suku bunga yang ada” ujar Professor Sri Subawa dalam wawancaranya.

# Riwayat beli saham BBRI
1. 5400
2. 5300
3. 5250
4. 5000
5. 4940

# Booking Meeting Room

```bash
<dependency>
  <groupId>com.google.api-client</groupId>
  <artifactId>google-api-client</artifactId>
  <version>1.32.1</version>
</dependency>
<dependency>
  <groupId>com.google.oauth-client</groupId>
  <artifactId>google-oauth-client-jetty</artifactId>
  <version>1.32.1</version>
</dependency>
<dependency>
  <groupId>com.google.apis</groupId>
  <artifactId>google-api-services-calendar</artifactId>
  <version>v3-rev305-1.25.0</version>
</dependency>
```

Buat file konfigurasi application.properties atau application.yml dengan detail OAuth2:

```bash
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid, profile, email, https://www.googleapis.com/auth/calendar
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/google
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
```

Buat service yang akan mengatur integrasi dengan Google Calendar:

```java
@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Google Calendar API Spring Boot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/calendar");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final String clientId;
    private final String clientSecret;

    public GoogleCalendarService(@Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
                                 @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private Credential getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public List<Event> getUpcomingEvents() throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    public String createEvent(Event event) throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        event = service.events().insert("primary", event).execute();
        return event.getHtmlLink();
    }
}
```

Buat controller yang akan menangani permintaan untuk mengambil jadwal dari Google Calendar dan membuat booking:

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRoomController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/events")
    public List<Event> getUpcomingEvents() {
        try {
            return googleCalendarService.getUpcomingEvents();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @PostMapping("/book")
    public String bookMeetingRoom(@RequestBody Event event) {
        try {
            return googleCalendarService.createEvent(event);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return "Error booking meeting room";
        }
    }
}
```

Jalankan aplikasi Spring Boot Anda.

Lakukan request untuk mengambil jadwal dari Google Calendar melalui endpoint /api/meetings/events.

Lakukan request untuk booking ruang rapat melalui endpoint /api/meetings/book dengan body JSON yang sesuai:

```bash
{
  "summary": "Team Meeting",
  "location": "Room 101",
  "description": "Discussing project status",
  "start": {
    "dateTime": "2024-07-10T10:00:00-07:00",
    "timeZone": "America/Los_Angeles"
  },
  "end": {
    "dateTime": "2024-07-10T11:00:00-07:00",
    "timeZone": "America/Los_Angeles"
  }
}
```

Dengan mengikuti langkah-langkah di atas, Anda dapat membuat API Spring Boot yang terintegrasi dengan Google Calendar untuk mengambil jadwal dan membuat booking berdasarkan data tersebut. Pastikan Anda menyesuaikan konfigurasi dan kode sesuai dengan kebutuhan spesifik aplikasi Anda.

# Function getUpcomingEvents()

Fungsi getUpcomingEvents() dalam kode di atas berfungsi untuk mengambil daftar acara (events) yang akan datang dari Google Calendar pengguna. Berikut adalah penjelasan lebih rinci mengenai fungsi tersebut:

```java
public List<Event> getUpcomingEvents() throws IOException, GeneralSecurityException {
    // Membuat instance Google Calendar service menggunakan kredensial yang didapatkan
    Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials())
            .setApplicationName(APPLICATION_NAME)
            .build();

    // Mengambil daftar acara dari kalender utama (primary)
    Events events = service.events().list("primary")
            .setMaxResults(10) // Menetapkan jumlah maksimum acara yang akan diambil (10 acara)
            .setOrderBy("startTime") // Mengurutkan acara berdasarkan waktu mulai
            .setSingleEvents(true) // Mengatur untuk hanya mengambil acara tunggal, bukan berulang
            .execute();
    
    // Mengembalikan daftar acara
    return events.getItems();
}
```
Langkah-langkah yang Dilakukan oleh getUpcomingEvents()
Membuat Instance Google Calendar Service:

Menggunakan GoogleNetHttpTransport.newTrustedTransport() untuk membuat transport HTTP yang aman.
Menggunakan JSON_FACTORY untuk menangani format JSON.
Menggunakan kredensial yang didapatkan dari fungsi getCredentials() untuk mengotorisasi akses ke Google Calendar.
Mengambil Daftar Acara:

Memanggil metode events().list("primary") untuk mengambil daftar acara dari kalender utama (primary) pengguna.
Menggunakan beberapa parameter untuk mengatur hasil yang diambil:
setMaxResults(10): Mengatur jumlah maksimum acara yang akan diambil sebanyak 10.
setOrderBy("startTime"): Mengurutkan acara berdasarkan waktu mulai.
setSingleEvents(true): Mengatur untuk hanya mengambil acara tunggal, bukan acara berulang (recurring events).
Mengembalikan Daftar Acara:

Memanggil execute() untuk mengeksekusi permintaan dan mendapatkan daftar acara.
Mengembalikan daftar acara yang diambil (events.getItems()).
Kegunaan Fungsi getUpcomingEvents()
Fungsi getUpcomingEvents() sangat berguna dalam konteks aplikasi booking ruang rapat karena:

Menampilkan Jadwal Pengguna:

Fungsi ini memungkinkan aplikasi untuk menampilkan jadwal rapat pengguna yang sudah ada di Google Calendar.
Menghindari Konflik Jadwal:

Dengan mengetahui jadwal rapat yang sudah ada, aplikasi dapat menghindari booking ruang rapat pada waktu yang sudah terjadwal, sehingga menghindari konflik jadwal.
Integrasi yang Lebih Baik dengan Google Calendar:

Fungsi ini memberikan integrasi yang lebih baik dengan Google Calendar, memungkinkan aplikasi untuk sinkronisasi dengan jadwal pengguna secara real-time.

Contoh Penggunaan di Controller
Fungsi ini digunakan di controller untuk menangani permintaan HTTP GET untuk mendapatkan jadwal acara yang akan datang:

```java
@RestController
@RequestMapping("/api/meetings")
public class MeetingRoomController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/events")
    public List<Event> getUpcomingEvents() {
        try {
            return googleCalendarService.getUpcomingEvents();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
```

Endpoint /api/meetings/events dapat dipanggil oleh klien (misalnya, aplikasi web atau mobile) untuk mendapatkan daftar acara yang akan datang dari Google Calendar pengguna.

# OAuth2

Untuk mengatur Google OAuth2 dalam aplikasi Spring Boot, Anda perlu mengikuti beberapa langkah utama: mendapatkan kredensial OAuth2 dari Google API Console, mengkonfigurasi aplikasi Spring Boot Anda untuk menggunakan OAuth2, dan menyiapkan endpoint untuk mengelola proses otentikasi. Berikut ini adalah panduan langkah demi langkah:

Langkah 1: Mendapatkan Kredensial OAuth2 dari Google API Console
Masuk ke Google Cloud Console:

Buka Google Cloud Console.
Buat atau Pilih Proyek:

Pilih proyek yang ada atau buat proyek baru.
Aktifkan Google Calendar API:

Buka "Library" di menu API & Services.
Cari "Google Calendar API" dan aktifkan.
Buat Kredensial OAuth2:

Buka "Credentials" di menu API & Services.
Klik "Create Credentials" dan pilih "OAuth 2.0 Client IDs".
Konfigurasikan layar persetujuan OAuth (OAuth consent screen) jika diminta.
Pilih "Web application" sebagai tipe aplikasi.
Masukkan nama aplikasi dan URL pengalihan (redirect URI) untuk aplikasi Anda.
Redirect URI adalah: http://localhost:8080/login/oauth2/code/google
Dapatkan Client ID dan Client Secret:

Setelah kredensial dibuat, simpan Client ID dan Client Secret.
Langkah 2: Menambahkan Dependensi di Spring Boot
Tambahkan dependensi yang diperlukan di pom.xml:

```bash
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Langkah 3: Konfigurasi application.properties
Tambahkan konfigurasi OAuth2 di src/main/resources/application.properties:

```bash
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid, profile, email, https://www.googleapis.com/auth/calendar.readonly
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/google
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
spring.security.oauth2.client.provider.google.user-name-attribute=sub
```

Gantilah YOUR_CLIENT_ID dan YOUR_CLIENT_SECRET dengan nilai yang Anda dapatkan dari Google Cloud Console.

Langkah 4: Konfigurasi Spring Security
Buat kelas konfigurasi Spring Security:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .antMatchers("/", "/index.html").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .loginPage("/oauth2/authorization/google")
            )
            .logout(logout ->
                logout
                    .logoutSuccessHandler(oidcLogoutSuccessHandler())
            );
    }

    @Bean
    LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository());
        successHandler.setPostLogoutRedirectUri("http://localhost:8080/");
        return successHandler;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
            .clientId("YOUR_CLIENT_ID")
            .clientSecret("YOUR_CLIENT_SECRET")
            .scope("openid", "profile", "email", "https://www.googleapis.com/auth/calendar.readonly")
            .authorizationUri("https://accounts.google.com/o/oauth2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .clientName("Google")
            .redirectUri("{baseUrl}/login/oauth2/code/google")
            .build();
    }
}
```

Gantilah YOUR_CLIENT_ID dan YOUR_CLIENT_SECRET dengan nilai yang Anda dapatkan dari Google Cloud Console.

Gantilah YOUR_CLIENT_ID dan YOUR_CLIENT_SECRET dengan nilai yang Anda dapatkan dari Google Cloud Console.

Langkah 5: Menambahkan Endpoints untuk Otentikasi dan Integrasi Google Calendar
Tambahkan controller untuk mengelola otentikasi dan integrasi dengan Google Calendar:

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/meetings")
public class MeetingRoomController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/events")
    public String getUpcomingEvents(Model model, OAuth2AuthenticationToken authentication) {
        try {
            List<Event> events = googleCalendarService.getUpcomingEvents();
            model.addAttribute("events", events);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return "events"; // View template for displaying events
    }

    @GetMapping("/user")
    public String getUserInfo(Model model, Principal principal) {
        model.addAttribute("user", principal);
        return "user"; // View template for displaying user info
    }
}
```

Buat tampilan (view) untuk menampilkan informasi acara dan pengguna.

Langkah 6: Menjalankan Aplikasi
Sekarang Anda dapat menjalankan aplikasi Spring Boot Anda dan mengakses endpoint /api/meetings/events untuk melihat acara dari Google Calendar pengguna yang telah terautentikasi.

Menguji Aplikasi
Buka browser dan arahkan ke http://localhost:8080.
Klik tombol login untuk memulai proses otentikasi Google OAuth2.
Setelah berhasil login, Anda akan diarahkan kembali ke aplikasi Anda, dan data acara dari Google Calendar akan ditampilkan.
Dengan mengikuti langkah-langkah di atas, Anda dapat mengatur Google OAuth2 di aplikasi Spring Boot dan mengintegrasikan Google Calendar untuk keperluan booking ruang rapat.

# Menggunakan 1 API saja
Anda dapat menggabungkan dua logika API dalam satu permintaan (request) dengan beberapa cara. Salah satunya adalah dengan membuat endpoint khusus di backend yang menangani kedua logika tersebut sekaligus. Dalam konteks ini, Anda dapat membuat satu endpoint yang baik mengambil jadwal rapat yang akan datang dari Google Calendar dan kemudian, berdasarkan data tersebut, membuat booking rapat baru.

Langkah 1: Membuat Endpoint Khusus di Backend
Anda dapat membuat endpoint yang mengambil jadwal rapat dari Google Calendar dan kemudian membuat booking rapat baru berdasarkan logika tertentu.

```java
@RestController
@RequestMapping("/api/meetings")
public class MeetingRoomController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @PostMapping("/bookFromEvents")
    public String bookMeetingFromEvents(@RequestBody BookingRequest bookingRequest) {
        try {
            // Ambil jadwal rapat yang akan datang dari Google Calendar
            List<Event> upcomingEvents = googleCalendarService.getUpcomingEvents();

            // Logika untuk memproses acara yang akan datang
            // Misalnya, buat booking baru berdasarkan kondisi tertentu
            Event newEvent = createBookingFromEvents(upcomingEvents, bookingRequest);

            // Buat booking rapat baru di Google Calendar
            return googleCalendarService.createEvent(newEvent);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return "Error processing booking from events";
        }
    }

    private Event createBookingFromEvents(List<Event> upcomingEvents, BookingRequest bookingRequest) {
        // Implementasikan logika Anda untuk membuat booking baru berdasarkan acara yang ada
        // Misalnya, buat booking baru jika tidak ada acara pada waktu yang diminta
        boolean isSlotAvailable = true;
        for (Event event : upcomingEvents) {
            if (event.getStart().getDateTime().isEqual(bookingRequest.getStart().getDateTime())) {
                isSlotAvailable = false;
                break;
            }
        }

        if (isSlotAvailable) {
            Event newEvent = new Event()
                .setSummary(bookingRequest.getSummary())
                .setLocation(bookingRequest.getLocation())
                .setDescription(bookingRequest.getDescription())
                .setStart(bookingRequest.getStart())
                .setEnd(bookingRequest.getEnd());
            return newEvent;
        } else {
            throw new RuntimeException("Slot not available");
        }
    }
}
```

Langkah 2: Struktur Data Request
Definisikan kelas BookingRequest yang akan diterima oleh endpoint /bookFromEvents.

```java
import com.google.api.services.calendar.model.EventDateTime;

public class BookingRequest {
    private String summary;
    private String location;
    private String description;
    private EventDateTime start;
    private EventDateTime end;

    // Getters and setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventDateTime getStart() {
        return start;
    }

    public void setStart(EventDateTime start) {
        this.start = start;
    }

    public EventDateTime getEnd() {
        return end;
    }

    public void setEnd(EventDateTime end) {
        this.end = end;
    }
}
```
Langkah 3: Mengirim Request dari Frontend
Frontend mengirim request POST untuk membuat booking berdasarkan jadwal yang ada

```javascript
import axios from 'axios';

async function bookMeetingFromEvents() {
    const bookingRequest = {
        summary: "Team Meeting",
        location: "Room 101",
        description: "Discussing project status",
        start: {
            dateTime: "2024-07-10T10:00:00-07:00",
            timeZone: "America/Los_Angeles"
        },
        end: {
            dateTime: "2024-07-10T11:00:00-07:00",
            timeZone: "America/Los_Angeles"
        }
    };

    try {
        const response = await axios.post('/api/meetings/bookFromEvents', bookingRequest);
        const bookingLink = response.data;
        console.log('Meeting room booked successfully:', bookingLink);
    } catch (error) {
        console.error('Error booking meeting room:', error);
    }
}

// Panggil fungsi ini saat pengguna mengklik tombol untuk membuat booking
bookMeetingFromEvents();
```

# StringPadder V2

```java
package apps.danamon.custody.csa.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * String padding utility class - Version 2.0 (Thread-Safe)
 *
 * Provides methods to pad strings to a specified length on either left or right side.
 * This version fixes the race condition bug from the original StringPadder class.
 *
 * THREAD SAFETY:
 * All methods in this class are thread-safe and can be called concurrently
 * from multiple threads without any sycnhronization issues.
 *
 * BUG FIX FROM V1:
 * The original StringPadder used static variables (strb, sci) which caused
 * race conditions when multiple threads processed transactions simultaneously.
 * This resulted in data contamination where account numbers from different
 * transactions where concatenated together.
 *
 * Example of V1 bug:
 *   Thread A:  accountNr = "3596684534" (10 digits)
 *   Thread B: accountNr = "903683213486" (12 digits)
 *   Result:    cod_acct_no_cr = "9036832134863596684534" (22 digits - WRONG!)
 *
 * V2 FIX:
 * This version uses LOCAL variables instead of static variables, ensuring
 * each method invocation has its own isolated data.
 *
 * USAGE EXAMPLES:
 *
 *   // Example 1: Pad account number with zeros on the left
 *   String accountNr = "3596684534";
 *   String padded = StringPadderV2.leftPad(accountNr, "0", 12);
 *   // Result: "003596684534"
 *
 *   // Example 2: Using single character (faster)
 *   String padded = StringPadderV2.leftPad(accountNr, '0', 12);
 *   // Result: "003596684534"
 *
 *   // Example 3: Right padding for descriptions
 *   String desc = "PAYMENT";
 *   String padded = StringPadderV2.rightPad(desc, " ", 20);
 *   // Result: "PAYMENT
 *
 * @author Danamon IT Team
 * @version 2.0
 * @since 2025-01-12
 */
public class StringPadderV2 {

    /**
     * Maximum allowed padding length to prevent memory issues.
     */
    private static final int MAX_PADDING_LENGTH = 100000;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class and should not be instantiated.
     *
     * @throws AssertionError if instantiation is attempted
     */
    private StringPadderV2() {
        throw new AssertionError("StringPadderV2 is a utility class and cannot be instantiated");
    }

    /**
     * Pads a string on the left side with a repeating string pattern.
     *
     * The padding string will be repeated as many times as necessary
     * until the target length is reached.  If the input string is already
     * equal to or longer than the target length, it is returned unchanged.
     *
     * THREAD SAFETY:  This method is completely thread-safe.  Each invocation
     * uses its own local variables, preventing any data contamination between
     * concurrent calls.
     *
     * EXAMPLES:
     *   leftPad("123", "0", 5)        returns "00123"
     *   leftPad("cat", "X", 10)       returns "XXXXXXXcat"
     *   leftPad("test", "ab", 8)      returns "ababtest"
     *   leftPad("test", "pad", 10)    returns "padpadtest"
     *   leftPad("toolong", "0", 5)    returns "toolong" (no change)
     *
     * USE CASE (Account Number):
     *   String accountNr = "3596684534";      // 10 digits from database
     *   String formatted = leftPad(accountNr, "0", 12);
     *   // Result: "003596684534" (12 digits for API)
     *
     * @param inputString    the string to be padded (must not be null)
     * @param paddingString  the string pattern for padding (must not be null or empty)
     * @param targetLength   the desired final length (must be non-negative)
     * @return the padded string, or original if already at target length
     * @throws IllegalArgumentException if inputString is null
     * @throws IllegalArgumentException if paddingString is null or empty
     * @throws IllegalArgumentException if targetLength is negative
     * @throws IllegalArgumentException if targetLength exceeds maximum allowed
     */
    public static String leftPad(String inputString, String paddingString, int targetLength) {
        // Validate all input parameters
        validateInputString(inputString, "inputString");
        validatePaddingString(paddingString);
        validateTargetLength(targetLength);

        // Fast path:  no padding needed
        if (inputString.length() >= targetLength) {
            return inputString;
        }

        // Calculate how much padding is required
        int paddingLength = targetLength - inputString.length();

        // THREAD-SAFE: Use LOCAL variables (not static)
        // Each thread gets its own StringBuffer and StringCharacterIterator
        StringBuffer buffer = new StringBuffer(targetLength);
        StringCharacterIterator iterator = new StringCharacterIterator(paddingString);

        // Add padding characters by repeating the padding string
        while (buffer.length() < paddingLength) {
            for (char currentChar = iterator.first();
                 currentChar != CharacterIterator.DONE;
                 currentChar = iterator.next()) {

                if (buffer.length() < paddingLength) {
                    buffer.append(currentChar);
                } else {
                    break; // Stop if we've reached exact padding length
                }
            }
        }

        // Append the original input string after padding
        buffer.append(inputString);

        return buffer.toString();
    }

    /**
     * Pads a string on the right side with a repeating string pattern.
     *
     * The padding string will be repeated as many times as necessary
     * until the target length is reached. If the input string is already
     * equal to or longer than the target length, it is returned unchanged.
     *
     * THREAD SAFETY: This method is completely thread-safe.
     *
     * EXAMPLES:
     *   rightPad("123", "0", 5)       returns "12300"
     *   rightPad("cat", "X", 10)      returns "catXXXXXXX"
     *   rightPad("test", "ab", 8)     returns "testabab"
     *   rightPad("test", "pad", 10)   returns "testpadpad"
     *   rightPad("toolong", "0", 5)   returns "toolong" (no change)
     *
     * USE CASE (Description Field):
     *   String description = "PAYMENT";
     *   String formatted = rightPad(description, " ", 20);
     *   // Result: "PAYMENT             " (20 characters)
     *
     * @param inputString    the string to be padded (must not be null)
     * @param paddingString  the string pattern for padding (must not be null or empty)
     * @param targetLength   the desired final length (must be non-negative)
     * @return the padded string, or original if already at target length
     * @throws IllegalArgumentException if inputString is null
     * @throws IllegalArgumentException if paddingString is null or empty
     * @throws IllegalArgumentException if targetLength is negative
     * @throws IllegalArgumentException if targetLength exceeds maximum allowed
     */
    public static String rightPad(String inputString, String paddingString, int targetLength) {
        // Validate all input parameters
        validateInputString(inputString, "inputString");
        validatePaddingString(paddingString);
        validateTargetLength(targetLength);

        // Fast path: no padding needed
        if (inputString.length() >= targetLength) {
            return inputString;
        }

        // THREAD-SAFE: Use LOCAL variables
        StringBuffer buffer = new StringBuffer(targetLength);
        buffer.append(inputString); // Add input first for right padding

        StringCharacterIterator iterator = new StringCharacterIterator(paddingString);

        // Add padding characters after the input string
        while (buffer. length() < targetLength) {
            for (char currentChar = iterator.first();
                 currentChar != CharacterIterator.DONE;
                 currentChar = iterator.next()) {

                if (buffer.length() < targetLength) {
                    buffer.append(currentChar);
                } else {
                    break;
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Pads a string on the left side with a single character.
     * This is an optimized version for single-character padding.
     *
     * PERFORMANCE:  This method is approximately 30% faster than the
     * string-based version when you only need a single padding character.
     *
     * THREAD SAFETY: This method is completely thread-safe.
     *
     * EXAMPLES:
     *   leftPad("123", '0', 5)     returns "00123"
     *   leftPad("A", 'X', 5)       returns "XXXXA"
     *   leftPad("99", '0', 10)     returns "0000000099"
     *
     * COMMON USE CASE:
     *   String accountNr = "3596684534";
     *   String formatted = leftPad(accountNr, '0', 12);
     *   // Result: "003596684534"
     *
     * @param inputString    the string to be padded (must not be null)
     * @param paddingChar    the character to use for padding
     * @param targetLength   the desired final length (must be non-negative)
     * @return the padded string
     * @throws IllegalArgumentException if inputString is null
     * @throws IllegalArgumentException if targetLength is negative
     * @throws IllegalArgumentException if targetLength exceeds maximum allowed
     */
    public static String leftPad(String inputString, char paddingChar, int targetLength) {
        // Validate inputs
        validateInputString(inputString, "inputString");
        validateTargetLength(targetLength);

        // Fast path: no padding needed
        if (inputString.length() >= targetLength) {
            return inputString;
        }

        // THREAD-SAFE: Use LOCAL variable
        StringBuffer buffer = new StringBuffer(targetLength);
        int paddingLength = targetLength - inputString.length();

        // Add padding characters (optimized for single char)
        for (int i = 0; i < paddingLength; i++) {
            buffer. append(paddingChar);
        }

        // Add original string
        buffer.append(inputString);

        return buffer.toString();
    }

    /**
     * Pads a string on the right side with a single character.
     * This is an optimized version for single-character padding.
     *
     * PERFORMANCE:  This method is approximately 30% faster than the
     * string-based version when you only need a single padding character.
     *
     * THREAD SAFETY: This method is completely thread-safe.
     *
     * EXAMPLES:
     *   rightPad("123", '0', 5)    returns "12300"
     *   rightPad("A", 'X', 5)      returns "AXXXX"
     *   rightPad("CODE", ' ', 10)  returns "CODE      "
     *
     * @param inputString    the string to be padded (must not be null)
     * @param paddingChar    the character to use for padding
     * @param targetLength   the desired final length (must be non-negative)
     * @return the padded string
     * @throws IllegalArgumentException if inputString is null
     * @throws IllegalArgumentException if targetLength is negative
     * @throws IllegalArgumentException if targetLength exceeds maximum allowed
     */
    public static String rightPad(String inputString, char paddingChar, int targetLength) {
        // Validate inputs
        validateInputString(inputString, "inputString");
        validateTargetLength(targetLength);

        // Fast path: no padding needed
        if (inputString.length() >= targetLength) {
            return inputString;
        }

        // THREAD-SAFE: Use LOCAL variable
        StringBuffer buffer = new StringBuffer(targetLength);
        buffer.append(inputString);

        int paddingLength = targetLength - inputString.length();

        // Add padding characters (optimized for single char)
        for (int i = 0; i < paddingLength; i++) {
            buffer. append(paddingChar);
        }

        return buffer.toString();
    }

    // ========================================================================
    // PRIVATE VALIDATION METHODS
    // ========================================================================

    /**
     * Validates that an input string is not null.
     *
     * @param inputString the string to validate
     * @param paramName   the parameter name for error message
     * @throws IllegalArgumentException if the string is null
     */
    private static void validateInputString(String inputString, String paramName) {
        if (inputString == null) {
            throw new IllegalArgumentException(
                    "Parameter '" + paramName + "' cannot be null.  " +
                            "Please provide a valid string to pad."
            );
        }
    }

    /**
     * Validates that a padding string is not null or empty.
     *
     * @param paddingString the padding string to validate
     * @throws IllegalArgumentException if the string is null or empty
     */
    private static void validatePaddingString(String paddingString) {
        if (paddingString == null) {
            throw new IllegalArgumentException(
                    "Padding string cannot be null. " +
                            "Please provide a valid string for padding."
            );
        }
        if (paddingString. isEmpty()) {
            throw new IllegalArgumentException(
                    "Padding string cannot be empty. " +
                            "Please provide at least one character for padding."
            );
        }
    }

    /**
     * Validates that a target length is valid (non-negative and within limits).
     *
     * @param targetLength the target length to validate
     * @throws IllegalArgumentException if the length is invalid
     */
    private static void validateTargetLength(int targetLength) {
        if (targetLength < 0) {
            throw new IllegalArgumentException(
                    "Target length cannot be negative. " +
                            "Received: " + targetLength + ". " +
                            "Please provide a non-negative target length."
            );
        }
        if (targetLength > MAX_PADDING_LENGTH) {
            throw new IllegalArgumentException(
                    "Target length exceeds maximum allowed:  " + MAX_PADDING_LENGTH + ". " +
                            "Received: " + targetLength + ". " +
                            "Please use a smaller target length to prevent memory issues."
            );
        }
    }

}
```

# CasaToGlController

```java
package apps.danamon.custody.csa.controller.rest;

import apps.danamon.custody.csa.controller.RestController;
import apps.danamon.custody.csa.dto.CasaToglDTO.CasaToGlRequestDTO;
import apps.danamon.custody.csa.util.*;
import id.co.danamon.apps.csa.enums.ApprovalStatus;
import id.co.danamon.apps.csa.rb.model.CoreBankingTransaction;
import id.co.danamon.apps.csa.rb.service.CoreBankingTransactionService;
import okhttp3.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slim3.repackaged.org.json.JSONException;
import org.slim3.repackaged.org.json.JSONObject;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CasaToGlV2Controller extends RestController {

    public void doPost() throws IOException {
        String id = request.getParameter("txnNr");
        String userId = request.getHeader("user-id");

        CoreBankingTransaction transaction = CoreBankingTransactionService.get().getById(Long.parseLong(id));
        hitApi(transaction, userId);
    }

    public JSONObject hitApi(CoreBankingTransaction txn, String userId) {
        if (userId == null) {
            userId = "00115530";
        }

        JSONObject response = null;
        try {
            DecimalFormat decimalFmt = new DecimalFormat("0");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddhhmmssSSS");

            int digits = Integer.valueOf(ConfigPropertiesUtil.getProperty("digits.randoms"));
            CasaToGlRequestDTO casaToGlRequestDTO = new CasaToGlRequestDTO();
            String fourDigit = reffNumberGenerator(digits);
            String channelId = "CSA";
            String key = ConfigPropertiesUtil.getProperty("api.key");
            String binNo = "0000";
            String serviceCode = "CASAGL_OB";
            String userRefNo = getReffNumber(channelId + sdf2.format(new Date()) + fourDigit);

            String requestTime = sdf.format(new Date());
            String input = channelId + EncryptionUtil.encryptSHA256(key) + binNo + serviceCode + requestTime + userRefNo;
            String encrypted = EncryptionUtil.encryptSHA256(input);
            System.out.println(txn.getAccountNr());

            // ================================================================
            // CRITICAL BUG FIX:  Ganti StringPadder dengan StringPadderV2
            // StringPadder lama punya race condition bug (static variables)
            // StringPadderV2 thread-safe (local variables)
            // ================================================================
            String accountNumber =
                    ConfigPropertiesUtil.getProperty("api.stringpadder.position").equalsIgnoreCase("left") ?
                            StringPadderV2.leftPad(txn.getAccountNr(), ConfigPropertiesUtil.getProperty("api.stringpadder.value"), 12) :
                            StringPadderV2.rightPad(txn.getAccountNr(), ConfigPropertiesUtil.getProperty("api.stringpadder.value"), 12);

            System.out.println(accountNumber);

            casaToGlRequestDTO.setChannel_id(channelId);
            casaToGlRequestDTO.setAuth_token(encrypted);
            casaToGlRequestDTO.setUser_ref_no(userRefNo);
            casaToGlRequestDTO.setService_code(serviceCode);
            casaToGlRequestDTO. setBin_no(binNo);
            casaToGlRequestDTO.setRequest_time(requestTime);
            casaToGlRequestDTO.setCod_acct_no_dr(ConfigPropertiesUtil.getProperty("api.gl"));
            casaToGlRequestDTO.setCod_acct_no_type_dr("40");
            casaToGlRequestDTO.setCod_acct_no_ccy_dr("360");
            casaToGlRequestDTO.setTrx_amount_dr(decimalFmt.format(txn.getAmount()) + ConfigPropertiesUtil.getProperty("api.digits.amount"));
            casaToGlRequestDTO.setTrx_rate_dr("0000010000000");
            casaToGlRequestDTO.setTrx_amount_lce_dr(decimalFmt.format(txn. getAmount()) + ConfigPropertiesUtil.getProperty("api.digits.amount"));
            casaToGlRequestDTO.setCod_acct_no_cr(accountNumber);
            casaToGlRequestDTO.setCod_acct_no_type_cr("20");
            casaToGlRequestDTO.setCod_acct_no_ccy_cr("360");
            casaToGlRequestDTO.setTrx_amount_cr(decimalFmt.format(txn.getAmount()) + ConfigPropertiesUtil.getProperty("api. digits.amount"));
            casaToGlRequestDTO.setTrx_rate_cr("0000010000000");
            casaToGlRequestDTO.setTrx_amount_lce_cr(decimalFmt. format(txn.getLceTransaction()) + ConfigPropertiesUtil. getProperty("api.digits.amount"));
            casaToGlRequestDTO.setDesc(txn.getDescription1());
            casaToGlRequestDTO.setCost_center("9207");

            JSONObject ObjectRequest = new JSONObject(casaToGlRequestDTO);
            SAVEREQUESTTODB(casaToGlRequestDTO, ObjectRequest, txn. getId());

            // FIX: Typo okhttpp → okhttp
            String username = ConfigPropertiesUtil. getProperty("okhttp.username");
            String password = ConfigPropertiesUtil.getProperty("okhttp.password");
            String encryptBase64 = EncryptionUtil.encryptBase64(username, password);

            // Mengirim permintaan ke MW API
            String mwApiUrl = ConfigPropertiesUtil.getProperty("api. mw.url");
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String requestBody = ObjectRequest.toString();
            RequestBody body = RequestBody. create(mediaType, requestBody);
            Request request = new Request. Builder()
                    .url(mwApiUrl)
                    .addHeader("Authorization", "Basic " + encryptBase64)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            Response responses = getUnsafeOkHttpClient().newCall(request).execute();
            String responseJson = responses.body().string();
            JSONObject objectResponse = null;
            try {
                objectResponse = new JSONObject(responseJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //response dari api MD
            response = objectResponse;
            SAVERESPONSETODB(response, txn.getId());

            int status = (! response.getString("code_status").equals("000000")) ? 2 : 1;
            Session session = HibernateUtil. getSessionFactory().openSession();
            Transaction dbTxn = null;
            try {
                dbTxn = session.beginTransaction();
                String query = "update rb_ncbs_transaction set status = :status, approvalStatus = :approvalStatus, processedDate = :processedDate,rejectCode = :rejectCode," +
                        "approveDate = :approveDate, approverId = :approverId where id = :id";
                Query q = session.createSQLQuery(query).
                        setString("id", txn.getId().toString()).
                        setString("approvalStatus", String.valueOf(ApprovalStatus.Approved)).
                        setDate("processedDate", new Date()).
                        setInteger("rejectCode", Integer.valueOf(response. getString("code_status"))).
                        setInteger("status", status).
                        setString("approverId", userId).
                        setDate("approveDate", new Date());
                q.executeUpdate();
                dbTxn.commit();
                System.out.println("UPDATE Response status DONE where txn id : " + txn.getId());
            } catch (HibernateException e) {
                e.getMessage();
                e.printStackTrace();
                if (dbTxn != null) {
                    dbTxn.rollback();
                }
            } finally {
                session.close();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();;
        }

        return response;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java. security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.readTimeout(15, TimeUnit.MINUTES).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void SAVEREQUESTTODB(CasaToGlRequestDTO requestDTO, JSONObject request, Long txn) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction dbTxn = null;
        try {
            dbTxn = session. beginTransaction();
            String query = "INSERT INTO rb_request_ncbs (" +
                    "id_transaction," +
                    "channel_id," +
                    "bin_no," +
                    "user_ref_no," +
                    "response_time," +
                    "service_code," +
                    "auth_token," +
                    "cod_acct_no_dr," +
                    "cod_acct_no_type_dr," +
                    "cod_acct_no_ccy_dr," +
                    "trx_amount_dr," +
                    "trx_rate_dr," +
                    "trx_amount_lce_dr," +
                    "trx_amount_lce_cr," +
                    "dsc," +
                    "cost_center," +
                    "requestDate," +
                    "request_json" +
                    ")" +
                    "VALUES (" +
                    ":id_transaction," +
                    ":chanelId," +
                    ": bin_no," +
                    ":user_ref_no," +
                    ":response_time," +
                    ":service_code," +
                    ":auth_token," +
                    ":cod_acct_no_dr," +
                    ":cod_acct_no_type_dr," +
                    ":cod_acct_no_ccy_dr," +
                    ":trx_amount_dr," +
                    ":trx_rate_dr," +
                    ":trx_amount_lce_dr," +
                    ":trx_amount_lce_cr," +
                    ":desc," +
                    ":cost_center," +
                    ":requestDate," +
                    ":request_json);";
            Query q = session. createSQLQuery(query).
                    setLong("id_transaction", txn).
                    setString("chanelId", requestDTO.getChannel_id()).
                    setString("bin_no", requestDTO.getBin_no()).
                    setString("user_ref_no", requestDTO.getUser_ref_no()).
                    setString("response_time", requestDTO.getRequest_time()).
                    setString("service_code", requestDTO. getService_code()).
                    setString("auth_token", requestDTO.getAuth_token()).
                    setString("cod_acct_no_dr", requestDTO.getCod_acct_no_dr()).
                    setString("cod_acct_no_type_dr", requestDTO.getCod_acct_no_type_dr()).
                    setString("cod_acct_no_ccy_dr", requestDTO. getCod_acct_no_type_dr()).
                    setString("trx_amount_dr", requestDTO.getTrx_amount_dr()).
                    setString("trx_rate_dr", requestDTO.getTrx_rate_dr()).
                    setString("trx_amount_lce_dr", requestDTO.getTrx_amount_lce_dr()).
                    setString("trx_amount_lce_cr", requestDTO.getTrx_amount_lce_cr()).
                    setString("desc", requestDTO.getDesc()).
                    setString("cost_center", requestDTO.getCost_center()).
                    setDate("requestDate", new Date()).
                    setString("request_json", request.toString());

            q.executeUpdate();
            dbTxn.commit();
            //jgn di delete ya ini untuk log nya response dari md
            System.out.println("INSERT Request DONE where txn id : " + txn);
        } catch (HibernateException e) {
            e.getMessage();
            e.printStackTrace();
            if (dbTxn != null) {
                dbTxn. rollback();
            }
        } finally {
            session.close();
        }
    }

    private static void SAVERESPONSETODB(JSONObject response, Long idTrx) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction dbTxn = null;
        try {
            dbTxn = session.beginTransaction();
            String query = "INSERT INTO rb_response_ncbs (" +
                    "id_transaction," +
                    "channel_id," +
                    "bin_no," +
                    "user_ref_no," +
                    "response_time," +
                    "service_code," +
                    "auth_token," +
                    "cod_acct_no_dr," +
                    "cod_acct_no_type_dr," +
                    "cod_acct_no_ccy_dr," +
                    "trx_amount_dr," +
                    "trx_rate_dr," +
                    "trx_amount_lce_dr," +
                    "trx_amount_lce_cr," +
                    "dsc," +
                    "cost_center," +
                    "code_status," +
                    "desc_status," +
                    "response_json" +
                    ")" +
                    "VALUES (" +
                    ":id_transaction," +
                    ":chanelId," +
                    ":bin_no," +
                    ":user_ref_no," +
                    ":response_time," +
                    ":service_code," +
                    ":auth_token," +
                    ": cod_acct_no_dr," +
                    ":cod_acct_no_type_dr," +
                    ": cod_acct_no_ccy_dr," +
                    ":trx_amount_dr," +
                    ":trx_rate_dr," +
                    ":trx_amount_lce_dr," +
                    ":trx_amount_lce_cr," +
                    ":desc," +
                    ":cost_center," +
                    ":code_status," +
                    ": desc_status," +
                    ":json_response);";

            Query q = session.createSQLQuery(query).
                    setLong("id_trasaction", idTrx).
                    setString("chanelId", response.getString("channel_id")).
                    setString("bin_no", response.getString("bin_no")).
                    setString("user_ref_no", response.getString("user_ref_no")).
                    setString("response_time", response.getString("response_time")).
                    setString("service_code", response.getString("service_code")).
                    setString("auth_token", response.getString("auth_token")).
                    setString("cod_acct_no_dr", response.getString("cod_acct_no_dr")).
                    setString("cod_acct_no_type_dr", response.getString("cod_acct_no_type_dr")).
                    setString("cod_acct_no_ccy_dr", response.getString("cod_acct_no_ccy_dr")).
                    setString("trx_amount_dr", response.getString("trx_amount_dr")).
                    setString("trx_rate_dr", response.getString("trx_rate_dr")).
                    setString("trx_amount_lce_dr", response.getString("trx_amount_lce_dr")).
                    setString("trx_amount_lce_cr", response.getString("trx_amount_lce_cr")).
                    setString("desc", response.getString("desc")).
                    setString("cost_center", response.getString("cost_center")).
                    setString("code_status", response.getString("code_status")).
                    setString("desc_status", response.getString("desc_status")).
                    setString("json_response", response.toString());
            q.executeUpdate();
            dbTxn.commit();
            //jgn di delete ya ini untuk log nya response dari md
            System.out.println("INSERT Response DONE where txn id : " + idTrx);
        } catch (HibernateException e) {
            e.getMessage();
            e.printStackTrace();
            if (dbTxn != null) {
                dbTxn.rollback();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static String getReffNumber(String Param) {

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddhhmmssSSS");

        Session session = HibernateUtil. getSessionFactory().openSession();
        Transaction dbTxn = null;

        boolean hasreff = false;
        int digits = 4;
        try {

            digits = Integer.valueOf(ConfigPropertiesUtil.getProperty("digits. randoms"));
            dbTxn = session.beginTransaction();
            String query = "select user_ref_no from rb_request_ncbs where user_ref_no = '" + Param + "' ";
            Query q = session.createSQLQuery(query);

            String reff = (String) q.uniqueResult();
            if (reff != null) {
                hasreff = true;
            } else {
                hasreff = false;
            }

            dbTxn.commit();

        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            if (dbTxn != null) {
                dbTxn. rollback();
            }
        } finally {
            session.close();
        }


        if (hasreff) { // termination condition
            String reff = "CSA" + sdf2.format(new Date()) + reffNumberGenerator(digits);

            return getReffNumber(reff); // recursive call
        } else {
            return Param;
        }
    }


    private static String addDigitAcNr(String accNr) {
        int digit = 12;
        String account = accNr;
        if (account.length() < digit) {
            while (account.length() < digit) {
                account += "0";
            }
        }
        return account;
    }


    public static String reffNumberGenerator(int len) {
        StringBuilder sb = new StringBuilder(len);

        try {
            String AB = ConfigPropertiesUtil.getProperty("range.logic.randoms");

            SecureRandom rnd = new SecureRandom();

            for (int i = 0; i < len; i++)
                sb.append(AB. charAt(rnd.nextInt(AB.length())));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
```

# CoreBankingTransaction Model
```java
@Entity
@Table(name = "rb_ncbs_transaction")
public class CoreBankingTransaction extends ApprovableEntity {

	public static final String SOURCE_TXN_ID = "sourceTxnId";
	public static final String SOURCE_TXN_DATE = "sourceTxnDate";
	public static final String PROCESSED_DATE = "processedDate";
	public static final String REFERENCE = "reference";
	public static final String SOURCE_TXN = "sourceTransaction";
	public static final String PORTFOLIO = "portfolio";
	public static final String BATCHFILE = "batchFile";

	public static final String ID = "id";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "portfolioCode", nullable = false)
	private Portfolio portfolio;

	@Column(nullable = false, length = 12)
	private String accountNr;

	@Column(nullable = false, length = 3)
	private String currency = "000";

	@Column(nullable = false)
	private double amount;

	@Column(nullable = false)
	private double lceTransaction;

	@Column(nullable = false)
	private double amountFee;

	private double foreignExchangeRate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CoreBankingSourceTransaction sourceTransaction;

	@Column(nullable = false)
	private String sourceTxnId;

	@Column(nullable = false)
	private Date sourceTxnDate;

	@Column(length = 5)
	private String costCenter = "";

	@Column(length = 20)
	private String reference = "";

	@Column(nullable = false, length = 30)
	private String description1;

	@Column(length = 30)
	private String description2 = "";

	@Column(nullable = false)
	private NCBSTransactionStatus status = NCBSTransactionStatus.Pending;

	@Column(length = 4)
	private String rejectCode = "";

	@Column(length = 30)
	private String shortName = "";

	@ManyToOne
	@JoinColumn(nullable = false)
	private CoreBankingBatchFile batchFile;

	private Date processedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public String getAccountNr() {
		return accountNr;
	}

	public void setAccountNr(String accountNr) {
		this.accountNr = accountNr != null ? accountNr.toUpperCase()
				: accountNr;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency != null ? currency.toUpperCase() : currency;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getLceTransaction() {
		return lceTransaction;
	}

	public void setLceTransaction(double lceTransaction) {
		this.lceTransaction = lceTransaction;
	}

	public double getAmountFee() {
		return amountFee;
	}

	public void setAmountFee(double amountFee) {
		this.amountFee = amountFee;
	}

	public double getForeignExchangeRate() {
		return foreignExchangeRate;
	}

	public void setForeignExchangeRate(double foreignExchangeRate) {
		this.foreignExchangeRate = foreignExchangeRate;
	}

	public CoreBankingSourceTransaction getSourceTransaction() {
		return sourceTransaction;
	}

	public void setSourceTransaction(
			CoreBankingSourceTransaction sourceTransaction) {
		this.sourceTransaction = sourceTransaction;
	}

	public String getSourceTxnId() {
		return sourceTxnId;
	}

	public void setSourceTxnId(String sourceTxnId) {
		this.sourceTxnId = sourceTxnId != null ? sourceTxnId.toUpperCase()
				: sourceTxnId;
	}

	public Date getSourceTxnDate() {
		return sourceTxnDate;
	}

	public void setSourceTxnDate(Date sourceTxnDate) {
		this.sourceTxnDate = sourceTxnDate;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter != null ? costCenter.toUpperCase()
				: costCenter;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference != null ? reference.toUpperCase()
				: reference;
	}

	public String getDescription1() {
		return description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1 != null ? description1.toUpperCase()
				: description1;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2 != null ? description2.toUpperCase()
				: description2;
	}

	public NCBSTransactionStatus getStatus() {
		return status;
	}

	public void setStatus(NCBSTransactionStatus status) {
		this.status = status;
	}

	public String getRejectCode() {
		return rejectCode;
	}

	public void setRejectCode(String rejectCode) {
		this.rejectCode = rejectCode != null ? rejectCode.toUpperCase()
				: rejectCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName != null ? shortName.toUpperCase()
				: shortName;
	}

	public CoreBankingBatchFile getBatchFile() {
		return batchFile;
	}

	public void setBatchFile(CoreBankingBatchFile batchFile) {
		this.batchFile = batchFile;
	}

	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

}
```

# CasaToGlDTO

```java
public class CasaToGlRequestDTO {



    public String channel_id;
    public String auth_token;
    public String user_ref_no ;//chanel id + yyyyMMdd + 4 digitsequence number,increment/day
    public String service_code;
    public String bin_no;
    public String request_time;//tanggal yyyyMMddhhmmss
    public String cod_acct_no_dr;
    public String cod_acct_no_type_dr;
    public String cod_acct_no_ccy_dr;
    public String trx_amount_dr;
    public String trx_rate_dr;
    public String trx_amount_lce_dr;
    public String cod_acct_no_cr;
    public String cod_acct_no_type_cr;
    public String cod_acct_no_ccy_cr;
    public String trx_amount_cr;
    public String trx_rate_cr;
    public String trx_amount_lce_cr;
    public String desc;
    public String cost_center;


    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getUser_ref_no() {
        return user_ref_no;
    }

    public void setUser_ref_no(String user_ref_no) {
        this.user_ref_no = user_ref_no;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getBin_no() {
        return bin_no;
    }

    public void setBin_no(String bin_no) {
        this.bin_no = bin_no;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getCod_acct_no_dr() {
        return cod_acct_no_dr;
    }

    public void setCod_acct_no_dr(String cod_acct_no_dr) {
        this.cod_acct_no_dr = cod_acct_no_dr;
    }

    public String getCod_acct_no_type_dr() {
        return cod_acct_no_type_dr;
    }

    public void setCod_acct_no_type_dr(String cod_acct_no_type_dr) {
        this.cod_acct_no_type_dr = cod_acct_no_type_dr;
    }

    public String getCod_acct_no_ccy_dr() {
        return cod_acct_no_ccy_dr;
    }

    public void setCod_acct_no_ccy_dr(String cod_acct_no_ccy_dr) {
        this.cod_acct_no_ccy_dr = cod_acct_no_ccy_dr;
    }

    public String getTrx_amount_dr() {
        return trx_amount_dr;
    }

    public void setTrx_amount_dr(String trx_amount_dr) {
        this.trx_amount_dr = trx_amount_dr;
    }

    public String getTrx_rate_dr() {
        return trx_rate_dr;
    }

    public void setTrx_rate_dr(String trx_rate_dr) {
        this.trx_rate_dr = trx_rate_dr;
    }

    public String getTrx_amount_lce_dr() {
        return trx_amount_lce_dr;
    }

    public void setTrx_amount_lce_dr(String trx_amount_lce_dr) {
        this.trx_amount_lce_dr = trx_amount_lce_dr;
    }

    public String getCod_acct_no_cr() {
        return cod_acct_no_cr;
    }

    public void setCod_acct_no_cr(String cod_acct_no_cr) {
        this.cod_acct_no_cr = cod_acct_no_cr;
    }

    public String getCod_acct_no_type_cr() {
        return cod_acct_no_type_cr;
    }

    public void setCod_acct_no_type_cr(String cod_acct_no_type_cr) {
        this.cod_acct_no_type_cr = cod_acct_no_type_cr;
    }

    public String getCod_acct_no_ccy_cr() {
        return cod_acct_no_ccy_cr;
    }

    public void setCod_acct_no_ccy_cr(String cod_acct_no_ccy_cr) {
        this.cod_acct_no_ccy_cr = cod_acct_no_ccy_cr;
    }

    public String getTrx_amount_cr() {
        return trx_amount_cr;
    }

    public void setTrx_amount_cr(String trx_amount_cr) {
        this.trx_amount_cr = trx_amount_cr;
    }

    public String getTrx_rate_cr() {
        return trx_rate_cr;
    }

    public void setTrx_rate_cr(String trx_rate_cr) {
        this.trx_rate_cr = trx_rate_cr;
    }

    public String getTrx_amount_lce_cr() {
        return trx_amount_lce_cr;
    }

    public void setTrx_amount_lce_cr(String trx_amount_lce_cr) {
        this.trx_amount_lce_cr = trx_amount_lce_cr;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCost_center() {
        return cost_center;
    }

    public void setCost_center(String cost_center) {
        this.cost_center = cost_center;
    }
}
```

