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


