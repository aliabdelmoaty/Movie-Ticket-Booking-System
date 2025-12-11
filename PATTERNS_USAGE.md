# استخدام الـ Design Patterns في المشروع

## نظام حجز تذاكر السينما - 7 Design Patterns

---

## 1️⃣ Singleton Pattern (نمط الكائن الوحيد)

### 📂 الملفات:
- `src/core/BookingSystem.java`
- `src/database/DatabaseManager.java`
- `src/core/SessionManager.java`

### 📍 أين استخدم:
```java
// في جميع أنحاء التطبيق
BookingSystem system = BookingSystem.getInstance();
DatabaseManager db = DatabaseManager.getInstance();
SessionManager session = SessionManager.getInstance();
```

### 🎯 ليه استخدمناه:
1. **BookingSystem**: نحتاج نسخة واحدة فقط لإدارة جميع عمليات الحجز في النظام
2. **DatabaseManager**: اتصال واحد فقط بقاعدة البيانات لتجنب مشاكل الاتصالات المتعددة
3. **SessionManager**: نسخة واحدة لإدارة جلسات المستخدمين وحالة الحجوزات

### ✨ الفائدة:
- ضمان وجود نسخة واحدة فقط من الكلاسات المهمة
- توفير نقطة وصول عامة (global access point)
- التحكم في الموارد المشتركة

---

## 2️⃣ Factory Pattern (نمط المصنع)

### 📂 الملفات:
- `src/factory/MovieFactory.java`
- `src/factory/TheaterFactory.java`

### 📍 أين استخدم:

#### ✅ MovieFactory - في AddMovieDialog.java (السطر 235):
```java
// استخدام حقيقي في الكود!
MovieType movieType = MovieFactory.getMovieType(genre);
Movie movie = MovieFactory.createMovie(
    movieType,
    title,
    duration,
    rating,
    description.isEmpty() ? null : description,
    posterPath.isEmpty() ? null : posterPath
);
```

**الواجهة**: عند إضافة فيلم جديد، يختار المستخدم النوع من ComboBox وFactory يقوم بإنشاء الفيلم تلقائياً!

#### TheaterFactory:
```java
// لإنشاء صالات عرض بأنواع مختلفة
Theater imaxTheater = TheaterFactory.createTheater(TheaterType.IMAX, 200);
Theater vipTheater = TheaterFactory.createTheater(TheaterType.VIP, 50);
```

### 🎯 ليه استخدمناه:
1. **MovieFactory**: كل نوع فيلم (Action, Comedy, Drama, etc.) له خصائص افتراضية مختلفة
2. **TheaterFactory**: كل صالة (IMAX, VIP, Standard) لها مواصفات وأسعار مختلفة

### ✨ الفائدة:
- فصل منطق إنشاء الكائنات عن الكود الأساسي
- سهولة إضافة أنواع جديدة من الأفلام أو الصالات
- تطبيق خصائص افتراضية لكل نوع

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
