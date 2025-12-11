# شرح تفصيلي للـ Design Patterns المتبقية

## 3️⃣ Builder Pattern (نمط البناء) - شرح مفصل

### 📖 الشرح النظري الكامل:
Builder Pattern هو نمط تصميم creational يفصل بناء كائن معقد عن تمثيله النهائي. يسمح لك ببناء كائن خطوة بخطوة، مع إمكانية اختيار أي خطوات تريدها.

### 🏗️ المكونات الأساسية:
1. **Builder Class**: الكلاس المسؤول عن البناء
2. **Fluent API**: كل setter يرجع `this` للسماح بـ method chaining
3. **Optional Parameters**: معظم الـ setters اختيارية
4. **Validation في build()**: التحقق من صحة البيانات قبل الإنشاء
5. **Immutable Result**: الكائن النهائي غالباً يكون immutable

### 🎯 المشكلة التي يحلها:

#### ❌ المشكلة بدون Builder:
```java
// Constructor بـ 7 parameters - صعب جداً!
Movie movie = new Movie(
    "Inception",
    "Sci-Fi",
    "148 min",
    "PG-13",
    "A mind-bending thriller",
    "inception.jpg",
    null  // ما هذا؟ لا أحد يعرف!
);

// أو استخدام multiple constructors:
Movie(String title)
Movie(String title, String genre)
Movie(String title, String genre, String duration)
Movie(String title, String genre, String duration, String rating)
// ... telescoping constructors hell! 😱
```

#### ✅ الحل مع Builder:
```java
// واضح، مرن، وسهل القراءة!
Movie movie = MovieBuilder.newMovie()
    .setTitle("Inception")           // واضح: هذا العنوان
    .setGenre("Sci-Fi")               // واضح: هذا النوع
    .setDuration("148 min")           // واضح: هذه المدة
    .setTeenRating()                  // convenience method
    .setDescription("A mind-bending thriller")
    .setPosterPath("inception.jpg")
    .build();                         // الإنشاء النهائي
```

### 📂 التطبيق في المشروع:

#### 1. MovieBuilder.java - شرح كامل:

```java
public class MovieBuilder {
    // الحقول المؤقتة للبناء
    private String title;
    private String genre;
    private String duration;
    private String rating;
    private String description;
    private String posterPath;
    
    // Constructor خاص بقيم افتراضية
    public MovieBuilder() {
        this.genre = "General";
        this.duration = "120 min";
        this.rating = "PG-13";
        this.description = "";
        this.posterPath = "";
    }
    
    // كل setter يرجع this للـ chaining
    public MovieBuilder setTitle(String title) {
        this.title = title;
        return this;  // هذا السر! 🔑
    }
    
    public MovieBuilder setGenre(String genre) {
        this.genre = genre;
        return this;
    }
    
    // Convenience methods - تسهل الحياة!
    public MovieBuilder setFamilyFriendly() {
        this.rating = "G";
        return this;
    }
    
    public MovieBuilder setTeenRating() {
        this.rating = "PG-13";
        return this;
    }
    
    public MovieBuilder setMatureRating() {
        this.rating = "R";
        return this;
    }
    
    // build() - الدالة النهائية
    public Movie build() {
        // Validation - تحقق من الشروط
        if (title == null || title.isEmpty()) {
            throw new IllegalStateException("Title is required");
        }
        
        // إنشاء الكائن النهائي
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setPosterPath(posterPath);
        
        return movie;
    }
    
    // Static factory method - بداية جيدة
    public static MovieBuilder newMovie() {
        return new MovieBuilder();
    }
}
```

#### 2. BookingBuilder.java - أكثر تعقيداً:

```java
public class BookingBuilder {
    private int userId;
    private int movieId;
    private List<String> seats;
    private double basePrice;
    private double discount;
    private double serviceFee;
    private double tax;
    private double theaterMultiplier;
    
    public BookingBuilder() {
        this.seats = new ArrayList<>();
        this.basePrice = 10.0;
        this.discount = 0.0;
        this.serviceFee = 1.5;
        this.tax = 0.0;
        this.theaterMultiplier = 1.0;
    }
    
    // Basic setters
    public BookingBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }
    
    public BookingBuilder setMovieId(int movieId) {
        this.movieId = movieId;
        return this;
    }
    
    // إضافة مقعد واحد
    public BookingBuilder addSeat(String seat) {
        this.seats.add(seat);
        return this;
    }
    
    // إضافة عدة مقاعد
    public BookingBuilder addSeats(List<String> seats) {
        this.seats.addAll(seats);
        return this;
    }
    
    // Convenience methods للخصومات
    public BookingBuilder applyStudentDiscount() {
        this.discount = 0.15;  // 15%
        return this;
    }
    
    public BookingBuilder applySeniorDiscount() {
        this.discount = 0.20;  // 20%
        return this;
    }
    
    public BookingBuilder applyGroupDiscount(int numberOfSeats) {
        if (numberOfSeats >= 5) {
            this.discount = 0.10;
        }
        if (numberOfSeats >= 10) {
            this.discount = 0.15;
        }
        return this;
    }
    
    // حساب السعر النهائي - منطق معقد!
    private double calculateTotalPrice() {
        int numberOfSeats = seats.size();
        double subtotal = basePrice * numberOfSeats * theaterMultiplier;
        double discountAmount = subtotal * discount;
        double afterDiscount = subtotal - discountAmount;
        double taxAmount = afterDiscount * tax;
        double total = afterDiscount + serviceFee + taxAmount;
        
        return Math.round(total * 100.0) / 100.0;
    }
    
    // build مع validation شامل
    public Booking build() {
        if (userId <= 0) {
            throw new IllegalStateException("Valid user ID is required");
        }
        if (movieId <= 0) {
            throw new IllegalStateException("Valid movie ID is required");
        }
        if (seats.isEmpty()) {
            throw new IllegalStateException("At least one seat must be selected");
        }
        
        String seatsString = String.join(", ", seats);
        double totalPrice = calculateTotalPrice();
        
        return new Booking(userId, movieId, seatsString, totalPrice);
    }
    
    // نقدر نحصل على ملخص قبل الـ build!
    public String getBookingSummary() {
        int numberOfSeats = seats.size();
        double subtotal = basePrice * numberOfSeats * theaterMultiplier;
        double discountAmount = subtotal * discount;
        double total = calculateTotalPrice();
        
        StringBuilder summary = new StringBuilder();
        summary.append("Booking Summary:\n");
        summary.append("Number of Seats: ").append(numberOfSeats).append("\n");
        summary.append("Seats: ").append(String.join(", ", seats)).append("\n");
        summary.append("Base Price per Seat: $").append(basePrice).append("\n");
        summary.append("Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
        
        if (discount > 0) {
            summary.append("Discount (").append((int)(discount * 100)).append("%): -$")
                   .append(String.format("%.2f", discountAmount)).append("\n");
        }
        
        summary.append("Service Fee: $").append(serviceFee).append("\n");
        summary.append("Total: $").append(String.format("%.2f", total));
        
        return summary.toString();
    }
}
```

### 📍 الاستخدام الفعلي في GUI:

#### ✅ في AddMovieDialog.java:
```java
// عند الضغط على زر "Add Movie"
private void addMovie() {
    String title = titleField.getText().trim();
    String genre = genreField.getText().trim();
    String duration = durationField.getText().trim();
    String rating = ratingField.getText().trim();
    
    // استخدام MovieBuilder - واضح ومنظم!
    Movie movie = MovieBuilder.newMovie()
        .setTitle(title)
        .setGenre(genre)
        .setDuration(duration)
        .setRating(rating)
        .setDescription(descriptionArea.getText().trim())
        .setPosterPath(posterPathField.getText().trim())
        .build();  // هنا يتم الـ validation
    
    if (movie.save()) {
        JOptionPane.showMessageDialog(this, "Movie added successfully!");
    }
}
```

#### ✅ في BookTicket.java - استخدام أكثر تعقيداً:
```java
private void confirmBooking() {
    BookingSystem bookingSystem = BookingSystem.getInstance();
    
    // بناء الحجز خطوة بخطوة
    BookingBuilder builder = BookingBuilder.newBooking()
        .setUserId(bookingSystem.getCurrentUser().getId())
        .setMovieId(movieId)
        .setBasePrice(SEAT_PRICE);  // 15.00
    
    // إضافة كل المقاعد المختارة
    for (SeatButton seat : selectedSeats) {
        builder.addSeat(seat.getSeatLabel());
    }
    
    // تطبيق خصم إذا كان عدد المقاعد كبير
    if (selectedSeats.size() >= 5) {
        builder.applyGroupDiscount(selectedSeats.size());
    }
    
    // بناء الحجز النهائي
    Booking booking = builder.build();
    
    // الآن الحجز جاهز مع السعر المحسوب تلقائياً!
    System.out.println("Total: $" + booking.getTotalPrice());
}
```

### ✨ الفوائد العملية:

#### 1. **Readability (قابلية القراءة)**:
```java
// بدون Builder - صعب الفهم
new Booking(1, 5, "A1, A2, A3", 45.99);  // ما معنى هذه الأرقام؟

// مع Builder - واضح تماماً
BookingBuilder.newBooking()
    .setUserId(1)        // معرف المستخدم
    .setMovieId(5)       // معرف الفيلم
    .addSeat("A1")       // المقعد الأول
    .addSeat("A2")       // المقعد الثاني
    .addSeat("A3")       // المقعد الثالث
    .build();            // إنشاء!
```

#### 2. **Flexibility (المرونة)**:
```java
// يمكنك اختيار ما تريد فقط
Movie quickMovie = MovieBuilder.newMovie()
    .setTitle("Quick Movie")
    .build();  // باقي الحقول ستأخذ القيم الافتراضية

// أو إضافة كل شيء
Movie fullMovie = MovieBuilder.newMovie()
    .setTitle("Full Movie")
    .setGenre("Action")
    .setDuration("120 min")
    .setMatureRating()
    .setDescription("Complete description")
    .setPosterPath("poster.jpg")
    .build();
```

#### 3. **Validation المركزي**:
```java
// كل الـ validation في مكان واحد (build)
public Booking build() {
    if (userId <= 0) {
        throw new IllegalStateException("Valid user ID is required");
    }
    if (seats.isEmpty()) {
        throw new IllegalStateException("At least one seat must be selected");
    }
    // ... المزيد من الفحوصات
    
    return new Booking(...);
}
```

#### 4. **Complex Calculations**:
```java
// Builder يتعامل مع الحسابات المعقدة
BookingBuilder builder = BookingBuilder.newBooking()
    .setBasePrice(12.50)
    .setTheaterType("IMAX", 1.8)  // IMAX multiplier
    .addSeat("A1")
    .addSeat("A2")
    .applyStudentDiscount()  // 15% off
    .build();

// السعر النهائي محسوب تلقائياً:
// (12.50 * 2 seats * 1.8 IMAX) = 45.00
// minus 15% discount = 38.25
// plus service fee = 39.75
```

### 🎬 سيناريو كامل - رحلة مستخدم:

```java
// 1. المستخدم يفتح شاشة إضافة فيلم
AddMovieDialog dialog = new AddMovieDialog(parent);

// 2. يملأ الحقول:
//    - Title: "Avatar 2"
//    - Genre: "SCIFI" (من ComboBox)
//    - Duration: "192 min"
//    - Rating: "PG-13"
//    - Description: (فاضي)

// 3. يضغط "Add Movie"

// 4. الكود يستخدم Builder:
Movie movie = MovieBuilder.newMovie()
    .setTitle("Avatar 2")
    .setGenre("Sci-Fi")  // من Factory
    .setDuration("192 min")
    .setTeenRating()
    .setDescription("")  // فاضي!
    .build();

// 5. build() يتحقق: ✅ Title موجود
// 6. build() ينشئ Movie بكل الحقول

// 7. النتيجة: فيلم كامل مع description افتراضي من Factory!
```

### 🔄 المقارنة الشاملة:

| الميزة | بدون Builder | مع Builder |
|--------|--------------|-----------|
| **عدد Parameters** | 7+ في constructor | صفر! كل شيء بالـ methods |
| **Readability** | `new Movie(a,b,c,d,e,f,g)` ❌ | `.setTitle("x").setGenre("y")` ✅ |
| **Optional Fields** | multiple constructors 😫 | اختر ما تريد ✅ |
| **Validation** | في constructor أو خارجه | مركزي في `build()` ✅ |
| **Complex Calculations** | صعب جداً | سهل وواضح ✅ |
| **Maintenance** | تغيير صعب | إضافة method جديد فقط ✅ |
| **Testability** | صعب test كل الحالات | سهل test كل scenario ✅ |

### 💡 نصائح الاستخدام:

1. **استخدم Builder عندما**:
   - الكائن له 4+ parameters
   - بعض parameters اختيارية
   - تحتاج validation معقد
   - الكائن له حالات مختلفة (student booking, VIP booking, etc.)

2. **لا تستخدم Builder عندما**:
   - الكائن بسيط (2-3 fields فقط)
   - كل الـ fields مطلوبة دائماً
   - لا يوجد منطق معقد

---

## 4️⃣ Prototype Pattern (نمط النسخ) - شرح مفصل

### 📖 الشرح النظري الكامل:
Prototype Pattern هو نمط تصميم creational يسمح لك بنسخ (clone) كائنات موجودة دون أن يعتمد الكود على classes الخاصة بها.

### 🏗️ المكونات الأساسية:
1. **Cloneable Interface**: علامة أن الكائن قابل للنسخ
2. **clone() Method**: الدالة التي تنسخ الكائن
3. **Deep Copy**: نسخ الكائن وكل محتوياته
4. **Prototype Registry**: (اختياري) مخزن للـ prototypes

### 🎯 المشكلة التي يحلها:

#### ❌ المشكلة بدون Prototype:
```java
// عايز تنشئ "Avatar 2" بناءً على "Avatar"
Movie avatar1 = Movie.findById(1);

// لازم تكتب كل شيء من جديد!
Movie avatar2 = new Movie();
avatar2.setTitle("Avatar: The Way of Water");  // نسخ يدوي
avatar2.setGenre(avatar1.getGenre());           // نسخ يدوي
avatar2.setDuration(avatar1.getDuration());     // نسخ يدوي
avatar2.setRating(avatar1.getRating());         // نسخ يدوي
avatar2.setDescription(avatar1.getDescription()); // نسخ يدوي
avatar2.setPosterPath("avatar2.jpg");           // تعديل
// ... متعب جداً! 😫
```

#### ✅ الحل مع Prototype:
```java
// بساطة ونظافة!
Movie avatar1 = Movie.findById(1);
MoviePrototype prototype = new MoviePrototype(avatar1);
MoviePrototype sequel = prototype.cloneAsSequel("The Way of Water");
Movie avatar2 = sequel.getMovie();
avatar2.save();  // Done! ✅
```

### 📂 التطبيق الكامل:

```java
public class MoviePrototype implements Cloneable {
    private Movie movie;
    
    public MoviePrototype(Movie movie) {
        this.movie = movie;
    }
    
    public Movie getMovie() {
        return movie;
    }
    
    // الدالة الأساسية للنسخ
    @Override
    public MoviePrototype clone() {
        try {
            // نسخ الـ MoviePrototype نفسه
            MoviePrototype cloned = (MoviePrototype) super.clone();
            
            // Deep copy للـ movie - مهم جداً!
            Movie clonedMovie = new Movie();
            clonedMovie.setTitle(this.movie.getTitle());
            clonedMovie.setGenre(this.movie.getGenre());
            clonedMovie.setDuration(this.movie.getDuration());
            clonedMovie.setRating(this.movie.getRating());
            clonedMovie.setDescription(this.movie.getDescription());
            clonedMovie.setPosterPath(this.movie.getPosterPath());
            
            cloned.setMovie(clonedMovie);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Convenience methods - تسهل الاستخدام
    
    // نسخ مع تغيير العنوان فقط
    public MoviePrototype cloneWithTitle(String newTitle) {
        MoviePrototype cloned = this.clone();
        cloned.getMovie().setTitle(newTitle);
        return cloned;
    }
    
    // نسخ مع تغيير النوع
    public MoviePrototype cloneWithGenre(String newGenre) {
        MoviePrototype cloned = this.clone();
        cloned.getMovie().setGenre(newGenre);
        return cloned;
    }
    
    // نسخ كـ sequel - الأهم!
    public MoviePrototype cloneAsSequel(String sequelNumber) {
        MoviePrototype cloned = this.clone();
        String originalTitle = this.movie.getTitle();
        cloned.getMovie().setTitle(originalTitle + " " + sequelNumber);
        return cloned;
    }
    
    // نسخ لعرض مختلف (نفس الفيلم، صالة أخرى)
    public MoviePrototype cloneForDifferentShowing() {
        return this.clone();
    }
}
```

### 📍 الاستخدام الفعلي في GUI:

#### ✅ في BookingFrame.java - زر Clone Movie:

```java
// الكود الكامل لزر Clone Movie
private void cloneSelectedMovie() {
    if (movies.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No movies available to clone!",
            "Clone Movie",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // 1. عرض قائمة بالأفلام المتاحة
    String[] movieTitles = new String[movies.size()];
    for (int i = 0; i < movies.size(); i++) {
        movieTitles[i] = movies.get(i).getTitle();
    }
    
    // 2. المستخدم يختار فيلم
    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Select movie to clone (Prototype Pattern):",
        "Clone Movie",
        JOptionPane.QUESTION_MESSAGE,
        null,
        movieTitles,
        movieTitles[0]
    );
    
    if (selected != null) {
        // 3. إيجاد الفيلم المختار
        model.Movie selectedMovie = null;
        for (model.Movie m : movies) {
            if (m.getTitle().equals(selected)) {
                selectedMovie = m;
                break;
            }
        }
        
        if (selectedMovie != null) {
            // 4. استخدام Prototype Pattern للنسخ
            MoviePrototype prototype = new MoviePrototype(selectedMovie);
            
            // 5. المستخدم يدخل اسم الجزء الجديد
            String sequelName = JOptionPane.showInputDialog(
                this,
                "Enter sequel name (e.g., 'Part 2', 'II', 'Reloaded'):",
                "Clone Movie",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (sequelName != null && !sequelName.trim().isEmpty()) {
                // 6. نسخ الفيلم كـ sequel
                MoviePrototype cloned = prototype.cloneAsSequel(sequelName.trim());
                model.Movie newMovie = cloned.getMovie();
                
                // 7. حفظ في الـ database
                if (newMovie.save()) {
                    JOptionPane.showMessageDialog(this,
                        "Movie cloned successfully!\n" +
                        "Original: " + selectedMovie.getTitle() + "\n" +
                        "Clone: " + newMovie.getTitle(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // 8. تحديث العرض
                    loadMovies();
                    refreshMoviesDisplay();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to save cloned movie!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
```

### 🎬 سيناريو كامل - تجربة المستخدم:

**الموقف**: صاحب السينما عنده فيلم "Avengers" ناجح جداً

**الخطوات**:
1. يفتح البرنامج ويشوف قائمة الأفلام
2. يضغط على زر "📋 Clone Movie" (البنفسجي)
3. يظهر له dialog بكل الأفلام
4. يختار "Avengers"
5. يظهر له input field
6. يكتب "Endgame"
7. يضغط OK
8. ✨ **السحر يحدث**:
   ```java
   MoviePrototype prototype = new MoviePrototype(avengers);
   MoviePrototype cloned = prototype.cloneAsSequel("Endgame");
   // النتيجة:
   // Title: "Avengers Endgame"
   // Genre: "Action" (من الأصل)
   // Duration: "143 min" (من الأصل)
   // Rating: "PG-13" (من الأصل)
   // Description: نفس وصف الأصل
   // Poster: نفس الأصل (يمكن تغييره لاحقاً)
   ```
9. رسالة نجاح تظهر
10. الفيلم الجديد يظهر في القائمة!

### ✨ الفوائد العملية:

#### 1. **توفير الوقت**:
```java
// بدون Prototype: 2-3 دقائق
// - فتح "Add Movie"
// - كتابة كل المعلومات من جديد
// - نسخ ولصق الوصف
// - اختيار نفس النوع
// - اختيار نفس التقييم
// - حفظ

// مع Prototype: 10 ثوانية!
// - اختر الفيلم
// - اكتب "Part 2"
// - Done! ✅
```

#### 2. **Consistency (الاتساق)**:
```java
// كل أفلام السلسلة لها نفس المواصفات
MoviePrototype prototype = new MoviePrototype(matrix);
MoviePrototype m2 = prototype.cloneAsSequel("Reloaded");
MoviePrototype m3 = prototype.cloneAsSequel("Revolutions");

// الثلاثة لهم:
// - نفس النوع: "Sci-Fi"
// - نفس التقييم: "R"
// - نفس المدة تقريباً
```

#### 3. **Flexibility مع Deep Copy**:
```java
// النسخة مستقلة تماماً
MoviePrototype cloned = prototype.clone();
cloned.getMovie().setRating("PG-13");  // تعديل النسخة
// الأصل ما يتأثر! ✅
```

#### 4. **حالات استخدام متعددة**:
```java
// Sequel
prototype.cloneAsSequel("Part 2");

// Remake
prototype.cloneWithTitle("The Thing (2011 Remake)");

// Different genre
prototype.cloneWithGenre("Horror");

// Same movie, different showing
prototype.cloneForDifferentShowing();  // نفس الفيلم في صالة أخرى
```

### 🔍 Deep Copy vs Shallow Copy:

```java
// ❌ Shallow Copy - خطر!
public MoviePrototype clone() {
    MoviePrototype cloned = (MoviePrototype) super.clone();
    // cloned.movie يشير لنفس الـ movie object!
    // تعديل واحد يؤثر على الاثنين! ⚠️
    return cloned;
}

// ✅ Deep Copy - آمن
public MoviePrototype clone() {
    MoviePrototype cloned = (MoviePrototype) super.clone();
    
    // إنشاء movie object جديد
    Movie clonedMovie = new Movie();
    clonedMovie.setTitle(this.movie.getTitle());
    // ... نسخ كل الحقول
    
    cloned.setMovie(clonedMovie);
    // الآن كل واحد مستقل ✅
    return cloned;
}
```

### 💡 متى تستخدم Prototype:

**استخدمه عندما**:
- ✅ تريد نسخ كائنات موجودة
- ✅ الكائن معقد ومكلف إنشاءه من الصفر
- ✅ تريد إنشاء variations من كائن أساسي
- ✅ النظام يحتاج templates للكائنات

**لا تستخدمه عندما**:
- ❌ الكائنات بسيطة جداً
- ❌ Deep copy معقد جداً (objects متداخلة كثيرة)
- ❌ ما في حاجة فعلية للنسخ

### 🎭 أمثلة واقعية أخرى:

```java
// مثال 1: نسخ فيلم لصالة مختلفة
Movie inception = Movie.findByTitle("Inception");
MoviePrototype proto = new MoviePrototype(inception);

// نفس الفيلم في IMAX
Movie inceptionIMAX = proto.cloneForDifferentShowing();
// تغيير الصالة فقط، كل شيء آخر نفسه

// مثال 2: إنشاء سلسلة كاملة
MoviePrototype harryPotter1 = new MoviePrototype(movie);
List<Movie> series = new ArrayList<>();

for (int i = 2; i <= 8; i++) {
    MoviePrototype part = harryPotter1.cloneAsSequel("Part " + i);
    series.add(part.getMovie());
}
// 7 أفلام في ثوان! 🚀

// مثال 3: Prototype Registry (متقدم)
Map<String, MoviePrototype> registry = new HashMap<>();
registry.put("action", new MoviePrototype(actionTemplate));
registry.put("comedy", new MoviePrototype(comedyTemplate));

// استخدام
MoviePrototype newAction = registry.get("action").clone();
```

هذا الشرح يغطي Prototype Pattern بالتفصيل الكامل! 🎉
