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

Buat file konfigurasi application.properties atau application.yml dengan detail OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid, profile, email, https://www.googleapis.com/auth/calendar
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/google
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo

Buat service yang akan mengatur integrasi dengan Google Calendar:
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


