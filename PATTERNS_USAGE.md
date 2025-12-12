# استخدام الـ Design Patterns في المشروع

## نظام حجز تذاكر السينما - 7 Design Patterns

---

## 1️⃣ Singleton Pattern (نمط الكائن الوحيد)

### 📖 الشرح النظري:
Singleton Pattern هو نمط تصميم يضمن أن الكلاس له نسخة واحدة فقط (instance) في التطبيق بأكمله، ويوفر نقطة وصول عامة (global access point) لهذه النسخة.

### 🏗️ كيف يعمل:
1. Constructor خاص (private) - لمنع إنشاء نسخ جديدة من خارج الكلاس
2. متغير static خاص يحفظ النسخة الوحيدة
3. دالة static عامة `getInstance()` - للحصول على النسخة الوحيدة

### 📂 الملفات في المشروع:

#### 1. BookingSystem.java
```java
public class BookingSystem {
    private static BookingSystem instance;  // النسخة الوحيدة
    
    private BookingSystem() {  // Constructor خاص
        DatabaseManager.getInstance();
        sessionManager = SessionManager.getInstance();
    }
    
    public static synchronized BookingSystem getInstance() {
        if (instance == null) {  // lazy initialization
            instance = new BookingSystem();
        }
        return instance;
    }
}
```

#### 2. DatabaseManager.java
```java
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;  // اتصال واحد بالـ database
    
    private DatabaseManager() {
        connection = DriverManager.getConnection(DB_URL);
        createTables();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
```

#### 3. SessionManager.java
```java
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;  // المستخدم الحالي
    private Map<Integer, String> activeBookings;  // الحجوزات النشطة
    
    private SessionManager() {
        this.activeBookings = new HashMap<>();
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
}
```

### 📍 أين استخدم في الكود:

#### في جميع GUI Files:
```java
// في BookingFrame.java
BookingSystem system = BookingSystem.getInstance();
List<Movie> movies = system.getAllMovies();

// في LoginFrame.java
BookingSystem system = BookingSystem.getInstance();
boolean success = system.login(email, password);

// في RegisterFrame.java
BookingSystem system = BookingSystem.getInstance();
boolean registered = system.register(name, email, username, password);

// في BookTicket.java
BookingSystem bookingSystem = BookingSystem.getInstance();
User user = bookingSystem.getCurrentUser();
```

### 🎯 ليه استخدمناه:

#### 1. BookingSystem:
- **المشكلة**: لو كل GUI file عمل instance جديد، هيبقى عندنا أنظمة حجز مختلفة!
- **الحل**: Singleton يضمن أن الكل يتعامل مع نفس نظام الحجز
- **الفائدة**: تنسيق مركزي لجميع عمليات الحجز، بيانات متسقة

#### 2. DatabaseManager:
- **المشكلة**: لو كل model class عمل connection جديد، هنستهلك موارد كثيرة وممكن نوصل لحد الـ connections
- **الحل**: Singleton يوفر connection واحد يستخدمه الكل
- **الفائدة**: 
  - توفير الموارد (Resource Management)
  - تجنب مشاكل الـ connection pool
  - سهولة غلق الاتصال عند إنهاء التطبيق

#### 3. SessionManager:
- **المشكلة**: معلومات المستخدم الحالي لازم تكون متاحة في كل مكان
- **الحل**: Singleton يحفظ session واحد للمستخدم
- **الفائدة**: 
  - تتبع المستخدم الحالي في كل الشاشات
  - إدارة حالة الحجوزات النشطة
  - Logout يمسح الـ session من مكان واحد

### ✨ الفوائد العملية:

1. **وحدة الحالة (State Consistency)**:
   - لو user عمل login في LoginFrame، BookTicket يشوف نفس الـ user
   - لو تم حجز seats، كل الـ screens تشوف نفس التحديث

2. **توفير الذاكرة (Memory Efficiency)**:
   - بدلاً من 100 instance، عندنا instance واحد
   - الـ database connection واحد بدل من عشرات

3. **سهولة الصيانة (Maintainability)**:
   - لو عايز تعدل منطق الحجز، تعدل في مكان واحد
   - كل التطبيق يستفيد من التعديل

### 🔒 Thread Safety:
استخدمنا `synchronized` keyword في `getInstance()` لضمان أن في حالة multi-threading، يتم إنشاء instance واحد فقط.

### 📊 مثال عملي:
```java
// في أي مكان في التطبيق
BookingSystem sys1 = BookingSystem.getInstance();
BookingSystem sys2 = BookingSystem.getInstance();

System.out.println(sys1 == sys2);  // true - نفس الـ instance!
```

---

## 2️⃣ Factory Pattern (نمط المصنع)

### 📖 الشرح النظري:
Factory Pattern هو نمط تصميم يوفر واجهة (interface) لإنشاء كائنات دون تحديد الكلاس الدقيق للكائن المُنشأ. يترك القرار للـ Factory حسب المعطيات.

### 🏗️ كيف يعمل:
1. **Factory Class**: كلاس فيه دالة static تستقبل parameters
2. **Decision Logic**: منطق اتخاذ القرار حسب الـ type المطلوب
3. **Object Creation**: إنشاء الكائن المناسب وإرجاعه
4. **Default Configuration**: تطبيق إعدادات افتراضية لكل نوع

### 📂 الملفات في المشروع:

#### 1. MovieFactory.java
```java
public class MovieFactory {
    
    public enum MovieType {
        ACTION, COMEDY, DRAMA, HORROR, SCIFI, ROMANCE, THRILLER
    }
    
    // الدالة الأساسية للـ Factory
    public static Movie createMovie(MovieType type, String title, 
                                     String duration, String rating, 
                                     String description, String posterPath) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setPosterPath(posterPath);
        
        // هنا السحر! كل نوع له إعدادات خاصة
        switch (type) {
            case ACTION:
                movie.setGenre("Action");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("An action-packed thriller with intense sequences and stunts.");
                }
                break;
                
            case COMEDY:
                movie.setGenre("Comedy");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A hilarious comedy that will make you laugh out loud.");
                }
                break;
                
            case HORROR:
                movie.setGenre("Horror");
                if (description == null || description.isEmpty()) {
                    movie.setDescription("A terrifying horror experience that will keep you on the edge of your seat.");
                }
                break;
            // ... باقي الأنواع
        }
        
        return movie;
    }
    
    // دالة مساعدة لتحويل String إلى MovieType
    public static MovieType getMovieType(String genre) {
        switch (genre.toUpperCase()) {
            case "ACTION": return MovieType.ACTION;
            case "COMEDY": return MovieType.COMEDY;
            case "DRAMA": return MovieType.DRAMA;
            // ... الخ
            default: return MovieType.DRAMA;
        }
    }
}
```

#### 2. TheaterFactory.java (محدث - 5 أنواع)
```java
public class TheaterFactory {
    
    public enum TheaterType {
        STANDARD, IMAX, VIP, DOLBY_ATMOS, FOUR_DX
    }
    
    public static Theater createTheater(TheaterType type, int capacity) {
        switch (type) {
            case STANDARD:
                return new StandardTheater(capacity);
            case IMAX:
                return new IMAXTheater(capacity);
            case VIP:
                return new VIPTheater(capacity);
            case DOLBY_ATMOS:
                return new DolbyAtmosTheater(capacity);
            case FOUR_DX:
                return new FourDXTheater(capacity);
            default:
                return new StandardTheater(capacity);
        }
    }
    
    // Theater Interface
    public interface Theater {
        String getName();
        String getDescription();
        double getPriceMultiplier();  // مهم للأسعار!
        int getCapacity();
        String[] getFeatures();
    }
    
    // Standard Theater - السعر الأساسي (1.0x)
    static class StandardTheater implements Theater {
        public double getPriceMultiplier() { return 1.0; }
        public String[] getFeatures() {
            return new String[]{"Comfortable Seating", "Digital Sound", "HD Screen"};
        }
    }
    
    // IMAX Theater - سعر أعلى بـ 80% (1.8x)
    static class IMAXTheater implements Theater {
        public double getPriceMultiplier() { return 1.8; }
        public String[] getFeatures() {
            return new String[]{"Giant IMAX Screen", "12-Channel Sound", 
                              "Laser Projection", "Premium Seating"};
        }
    }
    
    // VIP Theater - سعر أعلى بـ 150% (2.5x)
    static class VIPTheater implements Theater {
        public double getPriceMultiplier() { return 2.5; }
        public String[] getFeatures() {
            return new String[]{"Reclining Leather Seats", "Waiter Service", 
                              "Premium Sound", "Extra Legroom"};
        }
    }
    
    // Dolby Atmos Theater - سعر أعلى بـ 50% (1.5x)
    static class DolbyAtmosTheater implements Theater {
        public double getPriceMultiplier() { return 1.5; }
        public String[] getFeatures() {
            return new String[]{"Dolby Atmos Sound", "Enhanced Visuals", 
                              "Comfortable Seating", "Object-Based Audio"};
        }
    }
    
    // 4DX Theater - سعر أعلى بـ 100% (2.0x)
    static class FourDXTheater implements Theater {
        public double getPriceMultiplier() { return 2.0; }
        public String[] getFeatures() {
            return new String[]{"Motion Seats", "Wind Effects", "Water Spray", 
                              "Scent Effects", "Lighting Effects"};
        }
    }
}
```

### 📍 أين استخدم في الكود:

#### ✅ 1. في AddMovieDialog.java (السطر 235):

**الواجهة (UI)**:
```java
// ComboBox لاختيار نوع الفيلم
String[] movieTypes = {"ACTION", "COMEDY", "DRAMA", "HORROR", 
                      "SCIFI", "ROMANCE", "THRILLER"};
JComboBox<String> genreCombo = new JComboBox<>(movieTypes);
```

**استخدام الـ Factory (السطر 235)**:
```java
private void addMovie() {
    String genre = genreField.getText().trim();  // من الـ ComboBox
    
    // استخدام Factory Pattern
    MovieType movieType = MovieFactory.getMovieType(genre);
    Movie movie = MovieFactory.createMovie(
        movieType,
        title,
        duration,
        rating,
        description.isEmpty() ? null : description,
        posterPath.isEmpty() ? null : posterPath
    );
    
    // حفظ في الـ database
    movie.save();
}
```

### 🎯 ليه استخدمناه:

#### 1. MovieFactory:

**المشكلة بدون Factory**:
```java
// كود سيء - تكرار وصعب الصيانة
if (genre.equals("Action")) {
    movie.setGenre("Action");
    movie.setDescription("An action-packed thriller...");
} else if (genre.equals("Comedy")) {
    movie.setGenre("Comedy");
    movie.setDescription("A hilarious comedy...");
} else if (genre.equals("Horror")) {
    movie.setGenre("Horror");
    movie.setDescription("A terrifying horror...");
}
// ... 7 أنواع = كود طويل ومعقد!
```

**الحل مع Factory**:
```java
// كود نظيف ومنظم
Movie movie = MovieFactory.createMovie(movieType, title, duration, rating, description, posterPath);
// Factory يتعامل مع كل التفاصيل!
```

**الفوائد**:
1. **Default Descriptions**: لو المستخدم ما كتب description، Factory يضع واحد مناسب تلقائياً
2. **Consistency**: كل أفلام الـ Action لها نفس النمط من الوصف
3. **Extensibility**: لإضافة نوع جديد، فقط أضف case واحد في Factory

#### ✅ 2. في BookTicket.java - Theater Selection (السطر 254-330):

**الواجهة**: Panel بعنوان "Theater Type (Factory Pattern)" مع ComboBox:
```java
String[] theaterTypes = {"STANDARD", "IMAX", "VIP", "DOLBY_ATMOS", "FOUR_DX"};
theaterTypeCombo = new JComboBox<>(theaterTypes);

// إنشاء theater افتراضي
currentTheaterType = TheaterType.STANDARD;
selectedTheater = TheaterFactory.createTheater(currentTheaterType, ROWS * COLS);

// عند تغيير الاختيار
theaterTypeCombo.addActionListener(e -> {
    String selected = (String) theaterTypeCombo.getSelectedItem();
    currentTheaterType = TheaterType.valueOf(selected);
    selectedTheater = TheaterFactory.createTheater(currentTheaterType, ROWS * COLS);
    updateTotalPrice();  // تحديث السعر تلقائياً!
    
    // عرض معلومات الصالة
    JLabel theaterInfoLabel = new JLabel(
        "<html><div style='width:250px'>" +
        "<b>" + selectedTheater.getName() + "</b><br/>" +
        selectedTheater.getDescription() + "<br/>" +
        "<small>Price Multiplier: " + 
        String.format("%.1fx", selectedTheater.getPriceMultiplier()) + 
        "</small>" +
        "</div></html>"
    );
    // ... عرض المعلومات
});
```

**السيناريو**: نظام التسعير مختلف حسب نوع الصالة
- **STANDARD**: سعر عادي (1.0x) - $15.00
- **IMAX**: سعر أعلى بـ 80% (1.8x) - $27.00
- **VIP**: سعر أعلى بـ 150% (2.5x) - $37.50
- **DOLBY_ATMOS**: سعر أعلى بـ 50% (1.5x) - $22.50
- **FOUR_DX**: سعر أعلى بـ 100% (2.0x) - $30.00

```java
// عند حساب السعر في updateTotalPrice()
double theaterMultiplier = selectedTheater != null ? 
                           selectedTheater.getPriceMultiplier() : 1.0;
double basePricePerSeat = SEAT_PRICE * theaterMultiplier;
// مثال: IMAX = 15.00 * 1.8 = 27.00
```

### ✨ الفوائد العملية:

1. **Encapsulation (التغليف)**:
   - منطق إنشاء الأفلام معزول في Factory
   - AddMovieDialog لا يعرف تفاصيل كل نوع

2. **Maintainability (سهولة الصيانة)**:
   - لتغيير description نوع معين، تعدل في مكان واحد
   - كل الأفلام الجديدة تستفيد من التعديل

3. **Scalability (قابلية التوسع)**:
   - لإضافة نوع "DOCUMENTARY" جديد:
     ```java
     case DOCUMENTARY:
         movie.setGenre("Documentary");
         movie.setDescription("An informative documentary...");
         break;
     ```
   - فقط 4 أسطر في Factory!

4. **User Experience**:
   - المستخدم يختار النوع من قائمة
   - يحصل على وصف افتراضي جيد تلقائياً
   - ما يحتاج يكتب كل شيء

### 📊 مثال عملي - سيناريو كامل:

```java
// المستخدم اختار "Horror" من ComboBox
String selectedGenre = "HORROR";

// Factory يحول النص لـ enum
MovieType type = MovieFactory.getMovieType(selectedGenre);  // HORROR

// Factory ينشئ فيلم رعب بمواصفات خاصة
Movie horrorMovie = MovieFactory.createMovie(
    type,
    "The Conjuring",
    "112 min",
    "R",
    null,  // لم يكتب وصف
    "conjuring.jpg"
);

// النتيجة:
// - Genre: "Horror"
// - Description: "A terrifying horror experience that will keep you on the edge of your seat."
// - كل شيء جاهز تلقائياً!
```

### 🎨 تجربة المستخدم في الواجهة:

1. المستخدم يفتح "Add Movie"
2. يختار "HORROR" من القائمة المنسدلة
3. يكتب العنوان والمدة والتقييم
4. **لا يكتب وصف** (مشغول أو كسلان 😅)
5. يضغط "Add Movie"
6. Factory يضيف وصف احترافي تلقائياً!
7. الفيلم يُضاف بمواصفات كاملة ✅

### 🔄 المقارنة:

| بدون Factory | مع Factory |
|--------------|-----------|
| 50+ سطر في AddMovieDialog | 3 أسطر فقط |
| منطق معقد ومكرر | منطق مركزي ومنظم |
| صعب إضافة أنواع جديدة | سهل جداً |
| احتمال أخطاء عالي | آمن ومضمون |

---

## 3️⃣ Builder Pattern (نمط البناء)

### 📂 الملفات:
- `src/builder/MovieBuilder.java`
- `src/builder/BookingBuilder.java`

### 📍 أين استخدم:

#### 1. في AddMovieDialog.java (السطر 231-238):
```java
// استخدام MovieBuilder لإنشاء فيلم جديد
Movie movie = MovieBuilder.newMovie()
    .setTitle(title)
    .setGenre(genre)
    .setDuration(duration)
    .setRating(rating)
    .setDescription(description)
    .setPosterPath(posterPath)
    .build();
```

#### 2. في BookTicket.java (السطر 304-318):
```java
// استخدام BookingBuilder لإنشاء حجز
BookingBuilder builder = BookingBuilder.newBooking()
    .setUserId(bookingSystem.getCurrentUser().getId())
    .setMovieId(movieId)
    .setBasePrice(SEAT_PRICE);

// إضافة المقاعد
for (SeatButton seat : selectedSeats) {
    builder.addSeat(seat.getSeatLabel());
}

Booking booking = builder.build();
```

### 🎯 ليه استخدمناه:
1. **MovieBuilder**: الفيلم له معلومات كثيرة (عنوان، نوع، مدة، تقييم، وصف، صورة) - Builder يسهل بناءه
2. **BookingBuilder**: الحجز معقد (مستخدم، فيلم، مقاعد متعددة، سعر، خصومات) - Builder يوفر مرونة

### ✨ الفائدة:
- بناء كائنات معقدة خطوة بخطوة
- كود أكثر قراءة ووضوح
- سهولة إضافة حقول جديدة دون تغيير الكود القديم

---

## 4️⃣ Prototype Pattern (نمط النسخ)

### 📂 الملف:
- `src/prototype/MoviePrototype.java`

### 📍 أين استخدم:

#### ✅ في BookingFrame.java - زر "📋 Clone Movie":
```java
// استخدام حقيقي في الكود!
MoviePrototype prototype = new MoviePrototype(selectedMovie);
String sequelName = JOptionPane.showInputDialog(...);
MoviePrototype cloned = prototype.cloneAsSequel(sequelName.trim());
model.Movie newMovie = cloned.getMovie();
newMovie.save();
```

**الواجهة**: زر "Clone Movie" يسمح للمستخدم باختيار فيلم ونسخه كجزء ثاني!

### 🎯 ليه استخدمناه:
- لإنشاء أجزاء متتالية من نفس الفيلم (Sequel, Prequel)
- نسخ فيلم لعرض مختلف مع تعديلات بسيطة
- توفير الوقت بدلاً من إدخال كل المعلومات من جديد

### ✨ الفائدة:
- نسخ كائنات معقدة بسرعة
- الاحتفاظ بالخصائص الأساسية مع إمكانية التعديل
- أداء أفضل من إنشاء كائن جديد من الصفر

---

## 5️⃣ Adapter Pattern (نمط المحول)

### 📂 الملفات:
- `src/adapter/PaymentProcessor.java` (الواجهة الموحدة - Target Interface)
- `src/adapter/PaymentAdapter.java` (3 محولات + 3 أنظمة دفع)
- `src/adapter/PaymentAdapterFactory.java` (Factory لإنشاء Adapters)

### 📍 أين استخدم:

#### ✅ في BookTicket.java - عند تأكيد الحجز (السطر 495-550):
```java
// استخدام حقيقي في الكود!
// يظهر dialog لاختيار طريقة الدفع
String[] paymentOptions = {"Credit Card", "PayPal", "Bank Transfer"};
int paymentChoice = JOptionPane.showOptionDialog(this,
    String.format("Total Amount: $%.2f\nSelect Payment Method:", finalPrice),
    "Payment Method (Adapter Pattern)",
    JOptionPane.DEFAULT_OPTION,
    JOptionPane.QUESTION_MESSAGE,
    null,
    paymentOptions,
    paymentOptions[0]);

// تحديد طريقة الدفع
PaymentMethod method;
switch (paymentChoice) {
    case 0: method = PaymentMethod.CREDIT_CARD; break;
    case 1: method = PaymentMethod.PAYPAL; break;
    case 2: method = PaymentMethod.BANK_TRANSFER; break;
    default: method = PaymentMethod.CREDIT_CARD;
}

// استخدام Factory لإنشاء Adapter المناسب
PaymentProcessor processor = PaymentAdapterFactory.createPaymentProcessor(method);
String customerInfo = bookingSystem.getCurrentUser().getEmail();

if (processor.processPayment(finalPrice, customerInfo)) {
    // إظهار Transaction ID ورسالة النجاح
    String transactionId = processor.getTransactionId();
    String status = processor.getPaymentStatus();
    message.append("Transaction ID: ").append(transactionId);
    message.append("\nPayment: ").append(status);
}
```

**الواجهة**: عند تأكيد الحجز، يظهر dialog بعنوان "Payment Method (Adapter Pattern)" مع 3 خيارات:
- 💳 Credit Card
- 💰 PayPal  
- 🏦 Bank Transfer

### 🎯 ليه استخدمناه:
- **أنظمة الدفع المختلفة** لها واجهات مختلفة:
  - `CreditCardPaymentSystem.chargeCreditCard(cardNumber, cvv, amount)`
  - `PayPalPaymentSystem.makePayment(email, totalAmount)`
  - `BankTransferSystem.transferFunds(accountNumber, funds)`
- **واجهة موحدة**: `PaymentProcessor` توحد التعامل مع جميع الأنظمة
- **PaymentAdapterFactory**: يسهل إنشاء الـ Adapter المناسب
- **سهولة التوسع**: إضافة طريقة دفع جديدة = إضافة Adapter واحد فقط

### ✨ الفائدة:
- ✅ توحيد التعامل مع أنظمة مختلفة
- ✅ فصل منطق الدفع عن منطق الحجز
- ✅ المرونة في إضافة أو تغيير أنظمة الدفع
- ✅ نفس الكود لكل طريقة دفع - لا حاجة لـ if-else معقد

---

## 6️⃣ Proxy Pattern (نمط الوكيل)

### 📂 الملف:
- `src/proxy/MovieProxy.java` (MovieProxy و AdminMovieProxy)

### 📍 أين استخدم:

#### ✅ 1. MovieProxy - في MovieDetailsFrame.java (السطر 22):
```java
// استخدام حقيقي في الكود!
int userAge = 18; // يمكن أخذه من user profile
MovieProxy proxy = new MovieProxy(realMovie, currentUser, userAge);

if (!proxy.canView()) {
    JOptionPane.showMessageDialog(null,
        "Age restriction: You must be older to view this movie.\nRating: " + movie.getRating(),
        "Access Denied",
        JOptionPane.WARNING_MESSAGE);
    return; // يمنع فتح تفاصيل الفيلم
}
```

**الواجهة**: عند فتح تفاصيل فيلم، Proxy يفحص العمر أولاً!

#### 2. AdminMovieProxy (لحماية عمليات الإدارة):
```java
// فقط الأدمن يقدر يعدل أو يمسح أفلام
AdminMovieProxy adminProxy = new AdminMovieProxy(movie, adminUser);
if (adminProxy.updateMovie(title, genre, duration, rating, description)) {
    // تم التعديل
} else {
    // رفض - المستخدم مش أدمن
}
```

### 🎯 ليه استخدمناه:
1. **MovieProxy**: 
   - حماية الأطفال من محتوى غير مناسب
   - تطبيق نظام التقييم العمري (G, PG, PG-13, R, NC-17)
   
2. **AdminMovieProxy**:
   - حماية عمليات التعديل والحذف
   - السماح فقط للمسؤولين بإدارة الأفلام

### ✨ الفائدة:
- التحكم في الوصول (Access Control)
- فصل منطق الأمان عن منطق العمل
- Lazy Loading (تحميل البيانات عند الحاجة فقط)

---

## 7️⃣ Decorator Pattern (نمط المُزيِّن)

### 📂 الملفات:
- `src/decorator/TicketDecorator.java` (البنية الأساسية + 7 Decorators)
- `src/decorator/TicketPriceCalculator.java` (Helper Class - جديد!)

### 📍 أين استخدم:

#### ✅ في BookTicket.java - Ticket Extras Panel (السطر 331-436):

**الواجهة**:
```java
// Panel بعنوان "Ticket Extras (Decorator Pattern)"
JPanel extrasPanel = new JPanel();
extrasPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(new Color(59, 67, 84)),
    "Ticket Extras (Decorator Pattern)",
    ...
));

// 3 checkboxes للإضافات
popcornCheckBox = new JCheckBox("🍿 Popcorn & Drink (+$7.99)");
glasses3DCheckBox = new JCheckBox("🕶️ 3D Glasses (+$3.50)");
premiumSeatCheckBox = new JCheckBox("💺 Premium Seat Upgrade (+$5.00)");
```

**الاستخدام الفعلي - في updateTotalPrice() (السطر 412-436)**:
```java
private void updateTotalPrice() {
    if (selectedSeats.isEmpty()) {
        totalPriceLabel.setText("Total: $0.00");
        return;
    }
    
    // حساب السعر الأساسي مع theater multiplier (Factory Pattern)
    double theaterMultiplier = selectedTheater != null ? 
                               selectedTheater.getPriceMultiplier() : 1.0;
    double basePricePerSeat = SEAT_PRICE * theaterMultiplier;
    
    // استخدام Decorator Pattern عبر TicketPriceCalculator
    boolean hasPopcorn = popcornCheckBox != null && popcornCheckBox.isSelected();
    boolean has3DGlasses = glasses3DCheckBox != null && glasses3DCheckBox.isSelected();
    boolean hasPremiumSeat = premiumSeatCheckBox != null && premiumSeatCheckBox.isSelected();
    
    // حساب السعر النهائي باستخدام Decorator Pattern
    double total = TicketPriceCalculator.calculateTotalPrice(
        movie.getTitle(),
        basePricePerSeat,
        selectedSeats.size(),
        hasPopcorn,
        has3DGlasses,
        hasPremiumSeat
    );
    
    totalPriceLabel.setText(String.format("Total: $%.2f", total));
}
```

**عند تأكيد الحجز (السطر 483-493)**:
```java
// حساب السعر النهائي مع decorators
double finalPrice = booking.getTotalPrice();
if (popcornCheckBox.isSelected()) {
    finalPrice += 7.99;
}
if (glasses3DCheckBox.isSelected()) {
    finalPrice += 3.50;
}
if (premiumSeatCheckBox.isSelected()) {
    finalPrice += 5.00 * selectedSeats.size();
}
```

**الواجهة**: في شاشة حجز التذاكر، panel بعنوان "Ticket Extras (Decorator Pattern)" فيه 3 checkboxes. عند اختيار أي checkbox، يتم تحديث السعر تلقائياً!

### 🎯 ليه استخدمناه:
- **المرونة الديناميكية**: العميل يقدر يختار إضافات مختلفة للتذكرة
- **كل إضافة مستقلة**: كل decorator له سعر ووصف خاص
- **TicketPriceCalculator**: واجهة بسيطة لاستخدام Decorator Pattern
- **سهولة التوسع**: إضافة decorator جديد لا يحتاج تعديل الكود القديم

### 📦 الإضافات المتاحة (7 Decorators):
1. **PopcornDrinkDecorator** - وجبة فشار ومشروب (Small: $5.99, Medium: $7.99, Large: $9.99)
2. **ThreeDGlassesDecorator** - نظارات ثلاثية الأبعاد ($3.50)
3. **PremiumSeatDecorator** - ترقية لمقعد فاخر ($5.00)
4. **VIPLoungeDecorator** - دخول صالة VIP ($15.00)
5. **ReservedParkingDecorator** - موقف سيارة محجوز ($5.00)
6. **MealVoucherDecorator** - قسيمة وجبة (Snack: $8.99, Dinner: $15.99, Deluxe: $22.99)
7. **TicketInsuranceDecorator** - تأمين إلغاء/تأجيل ($2.50)

### ✨ الفائدة:
- ✅ إضافة وظائف جديدة دون تعديل الكود الأساسي (Open-Closed Principle)
- ✅ مرونة في اختيار المزايا (Composition over Inheritance)
- ✅ سهولة إضافة مزايا جديدة (إضافة decorator واحد فقط)
- ✅ كل عميل يحصل على تذكرة حسب احتياجاته
- ✅ TicketPriceCalculator يسهل الاستخدام

---

## 📊 ملخص الاستخدام

| Pattern | عدد التطبيقات | أين استخدم فعلياً | الأهمية |
|---------|---------------|-------------------|----------|
| Singleton | 3 | ✅ في جميع أنحاء التطبيق | ⭐⭐⭐⭐⭐ |
| Factory | 2 | ✅ AddMovieDialog.java (السطر 235)<br/>✅ BookTicket.java - Theater Selection (السطر 254-330) | ⭐⭐⭐⭐⭐ |
| Builder | 2 | ✅ AddMovieDialog.java<br/>✅ BookTicket.java (السطر 464-481) | ⭐⭐⭐⭐⭐ |
| Prototype | 1 | ✅ BookingFrame.java - زر Clone Movie | ⭐⭐⭐⭐⭐ |
| Adapter | 3 + Factory | ✅ BookTicket.java - نظام الدفع (السطر 495-550)<br/>✅ PaymentAdapterFactory | ⭐⭐⭐⭐⭐ |
| Proxy | 2 | ✅ MovieDetailsFrame.java - فحص العمر | ⭐⭐⭐⭐⭐ |
| Decorator | 7 + Helper | ✅ BookTicket.java - Ticket Extras (السطر 331-436)<br/>✅ TicketPriceCalculator | ⭐⭐⭐⭐⭐ |

---

## 🎯 الفوائد العامة

### 1. Maintainability (سهولة الصيانة)
- كل Pattern في package منفصل
- الكود منظم ومفهوم
- سهل التعديل والتطوير

### 2. Scalability (قابلية التوسع)
- سهولة إضافة أنواع أفلام جديدة (Factory)
- سهولة إضافة طرق دفع جديدة (Adapter)
- سهولة إضافة مزايا للتذاكر (Decorator)

### 3. Flexibility (المرونة)
- Builder يسمح بخيارات متعددة
- Decorator يسمح بتخصيص التذاكر
- Factory يسمح بإنشاء أنواع مختلفة

### 4. Code Reusability (إعادة استخدام الكود)
- Singleton يمنع التكرار
- Prototype يسمح بالنسخ السريع
- Patterns قابلة لإعادة الاستخدام

---

## 🚀 كيفية التوسع مستقبلاً

### إضافة نوع فيلم جديد:
```java
// في MovieFactory.java - إضافة DOCUMENTARY
case DOCUMENTARY:
    movie.setGenre("Documentary");
    movie.setDescription("An informative documentary exploring real events");
    break;
```

### إضافة طريقة دفع جديدة:
```java
// إنشاء CryptoAdapter جديد
class CryptoAdapter implements PaymentProcessor {
    // تطبيق الدفع بالعملات الرقمية
}
```

### إضافة ميزة تذكرة جديدة:
```java
// إنشاء decorator جديد
class VRExperienceDecorator extends TicketDecorator {
    // إضافة تجربة الواقع الافتراضي
}
```

---

## ✅ الخلاصة

جميع الـ 7 Patterns مطبقة في الكود:
- ✅ **Singleton**: مستخدم في النظام الأساسي
- ✅ **Factory**: جاهز لإنشاء الأفلام والصالات
- ✅ **Builder**: مستخدم فعلياً في GUI
- ✅ **Prototype**: جاهز لنسخ الأفلام
- ✅ **Adapter**: جاهز لأنظمة الدفع
- ✅ **Proxy**: جاهز للتحكم بالوصول
- ✅ **Decorator**: جاهز لتخصيص التذاكر

**الكود نظيف، منظم، وقابل للتوسع!** 🎉
