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

#### 2. TheaterFactory.java
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
            // ... باقي الأنواع
        }
    }
    
    // كل Theater له مواصفات مختلفة
    static class IMAXTheater implements Theater {
        public String getName() { return "IMAX Theater"; }
        public double getPriceMultiplier() { return 1.8; }  // سعر أعلى
        public String[] getFeatures() {
            return new String[]{"Giant IMAX Screen", "12-Channel Sound", 
                              "Laser Projection", "Premium Seating"};
        }
    }
    
    static class VIPTheater implements Theater {
        public String getName() { return "VIP Luxury Theater"; }
        public double getPriceMultiplier() { return 2.5; }  // أغلى سعر
        public String[] getFeatures() {
            return new String[]{"Reclining Leather Seats", "Waiter Service", 
                              "Premium Sound", "Extra Legroom"};
        }
    }
}
```

### 📍 أين استخدم في الكود:

#### ✅ في AddMovieDialog.java (السطر 50-65):

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

#### 2. TheaterFactory:

**السيناريو**: نظام التسعير مختلف حسب نوع الصالة
- **Standard**: سعر عادي (1.0x)
- **IMAX**: سعر أعلى بـ 80% (1.8x)
- **VIP**: سعر أعلى بـ 150% (2.5x)

```java
// عند حساب السعر
Theater theater = TheaterFactory.createTheater(TheaterType.IMAX, 200);
double basePrice = 10.0;
double finalPrice = basePrice * theater.getPriceMultiplier();  // 10 * 1.8 = 18
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
- `src/adapter/PaymentProcessor.java` (الواجهة الموحدة)
- `src/adapter/PaymentAdapter.java` (3 محولات)
- `src/adapter/PaymentAdapterFactory.java`

### 📍 أين استخدم:

#### ✅ في BookTicket.java - عند تأكيد الحجز (السطر 388):
```java
// استخدام حقيقي في الكود!
// يظهر dialog لاختيار طريقة الدفع
String[] paymentOptions = {"Credit Card", "PayPal", "Bank Transfer"};
int paymentChoice = JOptionPane.showOptionDialog(...);

PaymentProcessor processor = PaymentAdapterFactory.createPaymentProcessor(method);
String customerInfo = bookingSystem.getCurrentUser().getEmail();

if (processor.processPayment(finalPrice, customerInfo)) {
    // إظهار Transaction ID ورسالة النجاح
    message.append("Transaction ID: ").append(processor.getTransactionId());
}
```

**الواجهة**: عند تأكيد الحجز، يختار المستخدم طريقة الدفع من 3 خيارات!

### 🎯 ليه استخدمناه:
- أنظمة الدفع المختلفة (Credit Card, PayPal, Bank) لها واجهات مختلفة
- نحتاج واجهة موحدة للتعامل مع جميع أنظمة الدفع
- سهولة إضافة طرق دفع جديدة دون تغيير الكود الأساسي

### ✨ الفائدة:
- توحيد التعامل مع أنظمة مختلفة
- فصل منطق الدفع عن منطق الحجز
- المرونة في إضافة أو تغيير أنظمة الدفع

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

### 📂 الملف:
- `src/decorator/TicketDecorator.java`

### 📍 أين استخدم:

#### ✅ في BookTicket.java - Ticket Extras Panel (السطر 248):
```java
// استخدام حقيقي في الكود!
// 3 checkboxes للإضافات
popcornCheckBox = new JCheckBox("🍿 Popcorn & Drink (+$7.99)");
glasses3DCheckBox = new JCheckBox("🕶️ 3D Glasses (+$3.50)");
premiumSeatCheckBox = new JCheckBox("💺 Premium Seat Upgrade (+$5.00)");

// عند الحساب
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

**الواجهة**: في شاشة حجز التذاكر، panel بعنوان "Ticket Extras (Decorator Pattern)" فيه 3 checkboxes!

### 🎯 ليه استخدمناه:
- العميل يقدر يختار إضافات مختلفة للتذكرة
- كل إضافة لها سعر مختلف
- المرونة في إضافة أو إزالة المزايا

### 📦 الإضافات المتاحة:
1. **PopcornDrinkDecorator** - وجبة فشار ومشروب (صغير/وسط/كبير)
2. **ThreeDGlassesDecorator** - نظارات ثلاثية الأبعاد
3. **PremiumSeatDecorator** - ترقية لمقعد فاخر
4. **VIPLoungeDecorator** - دخول صالة VIP
5. **ReservedParkingDecorator** - موقف سيارة محجوز
6. **MealVoucherDecorator** - قسيمة وجبة (خفيفة/عشاء/فاخرة)
7. **TicketInsuranceDecorator** - تأمين إلغاء/تأجيل

### ✨ الفائدة:
- إضافة وظائف جديدة دون تعديل الكلاس الأساسي
- مرونة في اختيار المزايا
- سهولة إضافة مزايا جديدة
- كل عميل يحصل على تذكرة حسب احتياجاته

---

## 📊 ملخص الاستخدام

| Pattern | عدد التطبيقات | أين استخدم فعلياً | الأهمية |
|---------|---------------|-------------------|----------|
| Singleton | 3 | ✅ في جميع أنحاء التطبيق | ⭐⭐⭐⭐⭐ |
| Factory | 2 | ✅ AddMovieDialog.java (السطر 235) | ⭐⭐⭐⭐⭐ |
| Builder | 2 | ✅ AddMovieDialog, BookTicket | ⭐⭐⭐⭐⭐ |
| Prototype | 1 | ✅ BookingFrame.java - زر Clone Movie | ⭐⭐⭐⭐⭐ |
| Adapter | 3 | ✅ BookTicket.java - نظام الدفع | ⭐⭐⭐⭐⭐ |
| Proxy | 2 | ✅ MovieDetailsFrame.java - فحص العمر | ⭐⭐⭐⭐⭐ |
| Decorator | 7 | ✅ BookTicket.java - Ticket Extras | ⭐⭐⭐⭐⭐ |

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
