# ADIL CONSTRUCTIONS — Backend Learning Guide
## Spring Boot API, khud banane ke liye (explained, human-written, no AI needed)

Ye guide Part 16-22 ka **human version** hai — same API design, lekin har step
pe *kyun* kar rahe hain wo explain kiya hai, aur real working code diya hai jise
tu directly likh/samajh sakta hai. Tera Hospital Management API aur JPA ka
6-phase roadmap yahan directly kaam aayega — mostly wahi concepts hain, bas
domain badla hai.

**Suggested pace** (tere 32-day project style jaisa hi):
| Section | Topic | Roughly |
|---|---|---|
| 1 | Setup & Tech Stack | Day 1 |
| 2 | Entities & Database Schema | Day 2-3 |
| 3 | Auth (Spring Security + JWT) | Day 4-6 |
| 4 | Dashboard & Project APIs | Day 7-8 |
| 5 | Payments/Documents/Notifications APIs | Day 9 |
| 6 | Public Lead API | Day 10 |
| 7 | Testing everything (Postman) | Day 11 |

---

## SECTION 1 (was Part 16) — Project Setup & Tech Stack

### Kyun ye stack

Layered architecture use karenge — same jo tera Hospital Management API mein
tha: **Controller → Service → Repository → Entity**. Ye pattern Swiggy, Zomato,
Paytm sab industry backends mein standard hai kyunki har layer ka ek hi kaam
hota hai (single responsibility): Controller sirf HTTP handle karta hai,
Service business logic rakhta hai, Repository sirf DB se baat karta hai.

### `pom.xml` mein ye dependencies daal

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <!-- JWT library -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/adil_constructions
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=replace-this-with-a-long-random-secret-string-min-32-chars
jwt.expiration=86400000
```

`ddl-auto=update` matlab Hibernate khud tables bana/update karega entities se —
dev ke liye theek hai, production mein migrations tool (Flyway/Liquibase) use
karte hain, abhi ke liye ye kaafi hai.

### Package structure — aur *kyun* aise

```
com.adilconstructions
 ├─ config       → SecurityConfig, CorsConfig, JwtFilter
 ├─ controller   → sirf @RestController classes, HTTP request/response
 ├─ service      → business logic, "kya karna hai" yahan decide hota hai
 ├─ repository   → JpaRepository interfaces, sirf DB queries
 ├─ entity       → @Entity classes — tables ka Java representation
 ├─ dto          → request/response shapes — entity **kabhi** directly
 │                  controller se return mat karna, warna password hash
 │                  jaisi sensitive field bhi accidentally JSON mein chali
 │                  jaayegi
 └─ exception    → GlobalExceptionHandler
```

### CORS config — React frontend ko allow karne ke liye

Browser by default doosre origin (jaise `localhost:5173`) se `localhost:8080`
pe request block kar deta hai (Same-Origin Policy). Isko explicitly allow
karna padta hai:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Global exception handling — consistent error response

Har API error same JSON shape mein aaye, taaki frontend ek hi jagah handle
kare:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return new ResponseEntity<>(body, status);
    }
}
```

`ResourceNotFoundException` ek simple custom `RuntimeException` class hai jo
tu khud bana lega — jab koi project/payment DB mein na mile, ye throw karna.

---

## SECTION 2 (was Part 17) — Database Schema / Entities

### Relationship map (pehle samajh, phir code likh)

```
User  ──(1:1)──  Project  ──(1:many)──  TimelinePhase
                     │──(1:many)──  Update
                     │──(1:many)──  Payment
                     │──(1:many)──  Document

User  ──(1:many)──  Notification

ConsultationLead   → standalone, koi relation nahi (public form)
```

Tu already `mappedBy` vs `@JoinColumn` ka difference jaanta hai — quick recap:
jo side **foreign key column actually rakhta hai** DB mein, wahan
`@JoinColumn` lagta hai (owning side). Doosri side sirf `mappedBy` se point
karti hai — koi extra column nahi banata, sirf Java object graph ke liye hai.

Yahan **Project** owning side hai `User` ke saath (Project table mein
`owner_id` column hoga), aur **TimelinePhase/Update/Payment/Document** sab
Project ki child hain (unke table mein `project_id` column hoga).

### Enums pehle (alag files mein banao)

```java
public enum Role { CLIENT, ADMIN }
public enum PhaseStatus { COMPLETED, IN_PROGRESS, PENDING }
public enum PaymentMethod { UPI, CARD, CASH }
public enum PaymentStatus { SUCCESS, PENDING }
public enum LeadStatus { NEW, CONTACTED, CLOSED }
```

### `User.java`

```java
@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;   // BCrypt hash, kabhi plain text nahi

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String avatarUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Project project;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
```

### `Project.java`

```java
@Entity
@Table(name = "projects")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private Integer builtUpAreaSqft;
    private Integer bedrooms;
    private Integer durationMonths;
    private Double totalBudget;
    private Double paidAmount;
    private String currentStage;
    private Integer overallProgress;
    private String nextMilestoneName;
    private LocalDate nextMilestoneDate;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<TimelinePhase> timelinePhases = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Update> updates = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();
}
```

`orphanRemoval = true` yahan **meticulous** (bareek) reason se lagaya hai — tu
already padh chuka hai ye vs `CascadeType.REMOVE`: agar kisi `TimelinePhase`
ko list se hata diya (`project.getTimelinePhases().remove(phase)`), Hibernate
apne aap DB se bhi delete kar dega, bina explicit `repository.delete()` call
kiye. Ye tab useful hai jab child entity ka **koi independent existence nahi**
hai apne parent ke bina — yahan bilkul fit baithta hai.

### Child entities (compact, same pattern)

```java
@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TimelinePhase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private PhaseStatus status;
    private Integer percent;
    private Integer sortOrder;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Update {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private LocalDate dueDate;
    private LocalDateTime paidAt;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
    @PrePersist
    protected void onCreate() { this.uploadedAt = LocalDateTime.now(); }
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    @ManyToOne @JoinColumn(name = "user_id")
    private User user;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConsultationLead {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private String message;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private LeadStatus status;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = LeadStatus.NEW;
    }
}
```

### Repositories — ek line ka pattern, 7 baar repeat

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByOwnerId(Long ownerId);
}

public interface TimelinePhaseRepository extends JpaRepository<TimelinePhase, Long> {
    List<TimelinePhase> findByProjectIdOrderBySortOrderAsc(Long projectId);
}

public interface UpdateRepository extends JpaRepository<Update, Long> {
    Page<Update> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);
}

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByProjectIdOrderByPaidAtDesc(Long projectId);
}

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByProjectId(Long projectId);
}

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}

public interface ConsultationLeadRepository extends JpaRepository<ConsultationLead, Long> {}
```

---

## SECTION 3 (was Part 18) — Authentication (Spring Security + JWT)

### Concept: Stateless auth kya hota hai

Tu already 401 vs 403 ka difference jaanta hai. JWT flow samajh:

1. User `/api/auth/login` pe email+password bhejta hai.
2. Server verify karta hai, agar sahi hai to ek **signed token** (JWT) return
   karta hai — ye token mein user ki email encoded hoti hai, aur ek signature
   jo prove karta hai ki token server ne hi banaya hai (koi fake nahi bana
   sakta bina secret key ke).
3. Har agli request mein client ye token `Authorization: Bearer <token>`
   header mein bhejta hai.
4. Server **koi session store nahi karta** (isliye "stateless") — bas har
   request pe token verify karta hai. Ye horizontally scalable hai — multiple
   backend servers ke beech load balance karne mein session-based auth se
   zyada **robust** (mazboot) approach hai.

### `JwtUtil.java` — token banane/verify karne ka logic

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### `JwtFilter.java` — har request pe token check karta hai

```java
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }
}
```

Ye filter Spring Security ke chain mein `UsernamePasswordAuthenticationFilter`
se **pehle** lagega (jaisa tune context-path issue debug karte waqt filter
chain ka order dekha tha — order yahan bhi utna hi critical hai).

### `SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/leads/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

Ye wahi `SecurityFilterChain` pattern hai jo tune Hospital Management project
mein debug kiya tha — yahan `permitAll()` sirf `auth` aur `leads` endpoints
pe hai, baaki sab `authenticated()` maangta hai.

### DTOs

```java
public record RegisterRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain a number")
    String password,
    String phone
) {}

public record LoginRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {}

public record AuthResponse(String token, Long userId, String name, String email, String role) {}
```

(Java `record` use kiya — DTOs ke liye perfect hai, immutable + auto
getters/constructor, boilerplate kam.)

### `AuthService.java`

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(Role.CLIENT)
                .build();
        userRepository.save(user); 
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
```

### `AuthController.java`

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetails> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userDetails);
    }
}
```

Tujhe ek `UserDetailsServiceImpl` bhi banana hoga jo `User` entity ko Spring
Security ke `UserDetails` interface mein wrap kare — standard boilerplate hai,
Spring Security docs mein "Custom UserDetailsService" search kar, ya mujhse
alag se pooch lena jab is point pe pahunche.

---

## SECTION 4 (was Part 19) — Dashboard & Project APIs

### Core rule: har client sirf apna data dekhe

Har endpoint pe current logged-in user nikaal ke check karna ki wo project
**usi ka hai** — nahi to koi bhi apna JWT use karke doosre client ka data
dekh sakta hai. Ye galti bahut common hai naye backend developers mein.

```java
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<OverviewResponse> overview(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(dashboardService.getOverview(user.getUsername()));
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<TimelinePhaseResponse>> timeline(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(dashboardService.getTimeline(user.getUsername()));
    }

    @GetMapping("/updates")
    public ResponseEntity<Page<UpdateResponse>> updates(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dashboardService.getUpdates(user.getUsername(), page, size));
    }

    @GetMapping("/project")
    public ResponseEntity<ProjectResponse> project(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(dashboardService.getProject(user.getUsername()));
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TimelinePhaseRepository timelinePhaseRepository;
    private final UpdateRepository updateRepository;

    private Project getProjectForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No project found for this user"));
    }

    public OverviewResponse getOverview(String email) {
        Project p = getProjectForUser(email);
        return new OverviewResponse(
                p.getOverallProgress(), p.getCurrentStage(),
                p.getNextMilestoneName(), p.getNextMilestoneDate()
        );
    }

    public List<TimelinePhaseResponse> getTimeline(String email) {
        Project p = getProjectForUser(email);
        return timelinePhaseRepository.findByProjectIdOrderBySortOrderAsc(p.getId())
                .stream()
                .map(ph -> new TimelinePhaseResponse(ph.getName(), ph.getStatus().name(), ph.getPercent()))
                .toList();
    }

    public Page<UpdateResponse> getUpdates(String email, int page, int size) {
        Project p = getProjectForUser(email);
        return updateRepository.findByProjectIdOrderByCreatedAtDesc(p.getId(), PageRequest.of(page, size))
                .map(u -> new UpdateResponse(u.getTitle(), u.getDescription(), u.getThumbnailUrl(), u.getCreatedAt()));
    }

    public ProjectResponse getProject(String email) {
        Project p = getProjectForUser(email);
        return new ProjectResponse(p.getTitle(), p.getLocation(), p.getBuiltUpAreaSqft(),
                p.getBedrooms(), p.getDurationMonths(), p.getTotalBudget());
    }
}
```

Notice: `getProjectForUser()` ek jagah likha, sabhi methods reuse karte hain —
**DRY** (Don't Repeat Yourself) principle, aur ownership-check logic sirf ek
jagah maintain karni padti hai.

---

## SECTION 5 (was Part 20) — Payments, Documents & Notifications APIs

### Payment endpoint — `@Transactional` zaroori kyun

Jab client payment karta hai, do cheezein ek saath honi chahiye: (1) naya
`Payment` row create ho, (2) `Project.paidAmount` update ho. Agar beech mein
kuch fail ho gaya (server crash, DB error), to dono ek saath fail hone chahiye
— warna data inconsistent ho jayega (payment record ban gaya lekin paidAmount
nahi badla). Isi ke liye `@Transactional` lagate hain — tune EntityManager ke
Transient→Managed→Detached lifecycle mein dirty checking padha hoga; yahan
`@Transactional` method ke andar entity ko modify karna dirty checking
trigger karta hai, aur method end pe automatically DB mein flush ho jata hai.

```java
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProjectRepository projectRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentSummaryResponse pay(String email, PayRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        double remaining = project.getTotalBudget() - project.getPaidAmount();
        if (req.amount() > remaining) {
            throw new IllegalArgumentException("Amount exceeds remaining balance");
        }

        Payment payment = Payment.builder()
                .amount(req.amount())
                .method(req.method())
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .project(project)
                .build();
        paymentRepository.save(payment);

        // Dirty checking: entity managed session ke andar hai, ye change
        // apne aap DB mein flush ho jayega — explicit save() call ki zaroorat
        // nahi (kyunki 'project' already a managed entity hai is transaction mein).
        project.setPaidAmount(project.getPaidAmount() + req.amount());

        return buildSummary(project);
    }

    public PaymentSummaryResponse getSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return buildSummary(project);
    }

    private PaymentSummaryResponse buildSummary(Project project) {
        double remaining = project.getTotalBudget() - project.getPaidAmount();
        return new PaymentSummaryResponse(project.getTotalBudget(), project.getPaidAmount(), remaining);
    }
}
```

```java
public record PayRequest(@Positive Double amount, @NotNull PaymentMethod method) {}
public record PaymentSummaryResponse(Double totalBudget, Double paidAmount, Double remainingAmount) {}
```

```java
@RestController
@RequestMapping("/api/dashboard/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PaymentSummaryResponse> summary(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(paymentService.getSummary(user.getUsername()));
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentSummaryResponse> pay(
            @AuthenticationPrincipal UserDetails user, @Valid @RequestBody PayRequest req) {
        return ResponseEntity.ok(paymentService.pay(user.getUsername(), req));
    }
}
```

Documents aur Notifications controllers same simple GET-list pattern follow
karte hain jaisa DashboardController mein tha — ownership-check + repository
call + DTO map. Notifications mein bas ek extra `PATCH` endpoint hai:

```java
@RestController
@RequestMapping("/api/dashboard/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(notificationService.getAll(user.getUsername()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal UserDetails user) {
        notificationService.markAllRead(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
```

---

## SECTION 6 (was Part 21) — Public Consultation Lead API

Ye endpoint auth ke bina hai (landing page ka form), isliye validation aur
halka rate-limiting zaroori hai taaki spam bots isse abuse na kar sakein.

```java
public record LeadRequest(
    @NotBlank String name,
    @NotBlank @Pattern(regexp = "^(\\+91)?[6-9]\\d{9}$", message = "Enter a valid Indian phone number")
    String phone,
    String message
) {}
```

```java
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final ConsultationLeadRepository leadRepository;

    // Simple in-memory rate limit: IP → last-request-timestamps.
    // Production mein Redis ya Bucket4j jaisi library use karna behtar hai.
    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Map<String, String>> submit(
            @Valid @RequestBody LeadRequest req, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        List<Long> timestamps = requestLog.computeIfAbsent(ip, k -> new ArrayList<>());
        timestamps.removeIf(t -> now - t > 3600_000); // purane 1hr se zyada entries hata do

        if (timestamps.size() >= 5) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many requests. Please try again later."));
        }
        timestamps.add(now);

        ConsultationLead lead = ConsultationLead.builder()
                .name(req.name()).phone(req.phone()).message(req.message())
                .build();
        leadRepository.save(lead);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Thank you! We'll contact you within 24 hours."));
    }
}
```

Admin endpoint (baad ke liye scaffold, abhi bas bana ke chhod do):

```java
@RestController
@RequestMapping("/api/admin/leads")
@RequiredArgsConstructor
public class AdminLeadController {

    private final ConsultationLeadRepository leadRepository;

    @GetMapping
    public ResponseEntity<Page<ConsultationLead>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(leadRepository.findAll(PageRequest.of(page, size)));
    }
}
```

---

## SECTION 7 (was Part 22) — Ab test kaise kare (bina frontend ke)

Frontend abhi mat jodo — pehle Postman ya `curl` se **har endpoint independently
verify** kar, taaki pata chale backend akela hi sahi kaam kar raha hai. Yahi
professional workflow hai — backend ko frontend se pehle "battle-test" karna.

**1. Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Kaif","email":"kaif@test.com","password":"pass1234","phone":"9876543210"}'
```

**2. Login → token milega:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"kaif@test.com","password":"pass1234"}'
```

**3. Us token se protected endpoint call kar:**
```bash
curl http://localhost:8080/api/dashboard/overview \
  -H "Authorization: Bearer <paste-token-here>"
```

**4. Postman mein karna ho to:** ek Collection banao, "Bearer Token" ek
Collection-level Authorization variable rakho (login ke response se copy karo)
— taaki har request mein manually header na daalna pade.

**Checklist — har endpoint ke liye test karo:**
- Sahi data pe 200/201 milta hai?
- Galat token/no token pe 401 milta hai (403 nahi — ye distinction tune
  pehle bhi debug kiya tha, yahan bhi sahi rakhna)?
- Doosre user ka data access karne pe 403/404 milta hai (ownership check
  kaam kar raha hai)?
- Invalid input (jaise negative payment amount) pe 400 aur clear error
  message milta hai?

Jab ye sab manually verify ho jaye, tabhi React frontend ko backend se jodna
— us waqt sirf `axios` client banana, JWT ko localStorage mein store karna,
aur `mockData.ts` ki jagah real API calls lagana hoga (ye pure frontend-side
kaam hai, backend yahan tak already complete hai).

---

## SECTION 8 — Maven, Requirements, aur How to Run

### Prerequisites (kya kya chahiye)

| Tool | Version | Check karo |
|---|---|---|
| Java JDK | 17 ya upar | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| PostgreSQL | koi bhi recent | `psql --version` |

> **Note:** Java 17 minimum hai — Spring Boot 4.x Java 17 se neeche support nahi karta.

---

### Database banao (pehli baar sirf)

```sql
psql -U postgres
CREATE DATABASE adil_construction;
\q
```

Agar tera Postgres username `postgres` nahi hai (jaise `kaifkhan` hai is project mein):

```bash
psql -U kaifkhan -c "CREATE DATABASE adil_construction;"
```

---

### Maven — Important Commands

```bash
# Sirf compile karo (bina run ke)
mvn compile

# Tests run karo
mvn test

# JAR file banao (tests skip)
mvn clean package -DskipTests

# App locally run karo (CORRECT command)
mvn spring-boot:run

# Clean build — puri target/ directory delete karke fresh build
mvn clean install
```

> ❌ `mvn springboot:run` ya `mvn springBoot:run` kaam NAHI karta.
> ✅ Sirf `mvn spring-boot:run` (hyphen ke saath) sahi hai.

---

### App locally run karna

```bash
cd constructerApp
mvn spring-boot:run
```

App start hogi **http://localhost:8080** pe.

Startup pe ye log dikhe toh sahi hai:
```
Tomcat started on port 8080 (http)
Started ConstructerAppApplication in X.XXX seconds
```

---

### Common Errors aur Fix

**Error: Port 8080 already in use**
```bash
# Port 8080 pe kaun chal raha hai dekho
lsof -i :8080

# Us process ko band karo
lsof -ti:8080 | xargs kill -9

# Phir dobara run karo
mvn spring-boot:run
```

**Error: No plugin found for prefix 'springboot'**
```
Cause: Galat command likha — springboot (bina hyphen) ya springBoot
Fix:   mvn spring-boot:run  ← hyphen required
```

**Error: Connection to localhost:5432 refused**
```bash
# PostgreSQL start karo (macOS Homebrew)
brew services start postgresql@14

# Ya directly
pg_ctl -D /usr/local/var/postgresql@14 start
```

**Error: Database "adil_construction" does not exist**
```bash
psql -U kaifkhan -c "CREATE DATABASE adil_construction;"
```

---

### Environment Variables (default values)

Locally kuch set karne ki zaroorat nahi — `application.properties` mein sab defaults hain.
Agar override karna ho:

```bash
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your_strong_secret_key
```

| Variable | Default (local) |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/adil_construction` |
| `SPRING_DATASOURCE_USERNAME` | `kaifkhan` |
| `SPRING_DATASOURCE_PASSWORD` | *(empty)* |
| `JWT_SECRET` | dev default (change in prod!) |
| `JWT_EXPIRATION` | `86400000` (24 hours, ms mein) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:5174` |
| `ADMIN_BOOTSTRAP_EMAIL` | `admin@constructer.com` |
| `ADMIN_BOOTSTRAP_PASSWORD` | `Admin@1234` |

---

### Default Admin (auto-seed hota hai pehli baar)

```
Email:    admin@constructer.com
Password: Admin@1234
```

Login karo, JWT token lo, phir protected APIs call karo.

---

### JAR se run karna (production style)

```bash
# JAR build karo
mvn clean package -DskipTests

# Run karo
java -jar target/constructerApp-0.0.1-SNAPSHOT.jar
```

Production mein env vars pass karna:
```bash
java -jar target/constructerApp-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://prod-host:5432/adil_construction \
  --spring.datasource.username=prod_user \
  --spring.datasource.password=prod_pass \
  --jwt.secret=super_strong_secret
```

---

### Docker se run karna

```bash
# Image build karo
docker build -t constructer-api .

# Run karo (local Postgres ke saath)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/adil_construction \
  -e SPRING_DATASOURCE_USERNAME=kaifkhan \
  -e SPRING_DATASOURCE_PASSWORD= \
  constructer-api
```

> `host.docker.internal` — Docker container se tere Mac ka localhost access karne ke liye.
