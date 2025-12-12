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

---

## 7️⃣ Decorator Pattern (نمط المُزيِّن) - شرح مفصل

### 📖 الشرح النظري الكامل:
Decorator Pattern هو نمط تصميم structural يسمح بإضافة وظائف جديدة لكائنات موجودة ديناميكياً دون تغيير بنيتها. يعتمد على مبدأ "Composition over Inheritance".

### 🏗️ المكونات الأساسية:
1. **Component Interface**: الواجهة الأساسية (`Ticket`)
2. **Concrete Component**: التنفيذ الأساسي (`BaseTicket`)
3. **Decorator**: الكلاس المجرد الذي يغلف الـ Component
4. **Concrete Decorators**: التنفيذات الفعلية (PopcornDrinkDecorator, ThreeDGlassesDecorator, etc.)
5. **Helper Class**: `TicketPriceCalculator` - واجهة عامة لاستخدام الـ Pattern

### 🎯 المشكلة التي يحلها:

#### ❌ المشكلة بدون Decorator:
```java
// لازم نعمل كلاس لكل تركيبة ممكنة!
class BasicTicket { }
class TicketWithPopcorn { }
class TicketWith3DGlasses { }
class TicketWithPopcornAnd3D { }
class TicketWithPopcornAndPremium { }
class TicketWithEverything { }
// ... 2^7 = 128 كلاس محتمل! 😱
```

#### ✅ الحل مع Decorator:
```java
// نبدأ بـ ticket أساسي ونضيف decorators ديناميكياً!
Ticket ticket = new BaseTicket("Inception", "A1", 15.0);
ticket = new PopcornDrinkDecorator(ticket, "Medium");
ticket = new ThreeDGlassesDecorator(ticket);
ticket = new PremiumSeatDecorator(ticket);
// السعر والوصف يتحدثان تلقائياً! ✨
```

### 📂 التطبيق الكامل في المشروع:

#### 1. البنية الأساسية (TicketDecorator.java):

```java
// Component Interface
interface Ticket {
    String getDescription();
    double getCost();
}

// Concrete Component - Base Ticket
class BaseTicket implements Ticket {
    private String movieTitle;
    private String seatNumber;
    private double basePrice;
    
    public BaseTicket(String movieTitle, String seatNumber, double basePrice) {
        this.movieTitle = movieTitle;
        this.seatNumber = seatNumber;
        this.basePrice = basePrice;
    }
    
    @Override
    public String getDescription() {
        return "Movie: " + movieTitle + " | Seat: " + seatNumber;
    }
    
    @Override
    public double getCost() {
        return basePrice;
    }
}

// Abstract Decorator
abstract class TicketDecorator implements Ticket {
    protected Ticket ticket;  // الـ ticket الملفوف
    
    public TicketDecorator(Ticket ticket) {
        this.ticket = ticket;
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription();  // يمرر للـ decorator التالي
    }
    
    @Override
    public double getCost() {
        return ticket.getCost();  // يمرر للـ decorator التالي
    }
}
```

#### 2. Decorators الفعلية:

```java
// Decorator 1: Popcorn & Drink
class PopcornDrinkDecorator extends TicketDecorator {
    private String comboSize;  // Small, Medium, Large
    
    public PopcornDrinkDecorator(Ticket ticket, String comboSize) {
        super(ticket);
        this.comboSize = comboSize;
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Popcorn & Drink Combo (" + comboSize + ")";
    }
    
    @Override
    public double getCost() {
        double comboCost = comboSize.equals("Large") ? 9.99 : 
                          comboSize.equals("Small") ? 5.99 : 7.99;
        return ticket.getCost() + comboCost;  // السعر الأساسي + تكلفة الإضافة
    }
}

// Decorator 2: 3D Glasses
class ThreeDGlassesDecorator extends TicketDecorator {
    public ThreeDGlassesDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + 3D Glasses";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 3.50;
    }
}

// Decorator 3: Premium Seat
class PremiumSeatDecorator extends TicketDecorator {
    public PremiumSeatDecorator(Ticket ticket) {
        super(ticket);
    }
    
    @Override
    public String getDescription() {
        return ticket.getDescription() + " + Premium Reclining Seat";
    }
    
    @Override
    public double getCost() {
        return ticket.getCost() + 5.00;
    }
}

// ... 4 decorators أخرى: VIPLoungeDecorator, ReservedParkingDecorator, 
// MealVoucherDecorator, TicketInsuranceDecorator
```

#### 3. TicketPriceCalculator - Helper Class الجديد:

```java
/**
 * Helper class لتسهيل استخدام Decorator Pattern
 * يوفر واجهة بسيطة لحساب السعر النهائي مع الإضافات
 */
public class TicketPriceCalculator {
    
    public static double calculateTotalPrice(
        String movieTitle, 
        double basePricePerSeat, 
        int numberOfSeats, 
        boolean hasPopcorn, 
        boolean has3DGlasses, 
        boolean hasPremiumSeat
    ) {
        if (numberOfSeats == 0) {
            return 0.0;
        }
        
        // إنشاء base ticket (Decorator Pattern)
        String representativeSeat = "A1";
        Ticket ticket = new BaseTicket(movieTitle, representativeSeat, basePricePerSeat);
        
        // تطبيق decorators ديناميكياً
        if (hasPopcorn) {
            ticket = new PopcornDrinkDecorator(ticket, "Medium");
        }
        if (has3DGlasses) {
            ticket = new ThreeDGlassesDecorator(ticket);
        }
        if (hasPremiumSeat) {
            ticket = new PremiumSeatDecorator(ticket);
        }
        
        // حساب السعر النهائي: سعر التذكرة المحسّن × عدد المقاعد
        double enhancedPricePerSeat = ticket.getCost();
        double total = enhancedPricePerSeat * numberOfSeats;
        
        // تعديل للعناصر التي تكون واحدة لكل حجز (مثل Popcorn)
        if (hasPopcorn) {
            double popcornCost = 7.99; // Medium size
            total = total - (popcornCost * (numberOfSeats - 1));
        }
        
        return total;
    }
}
```

### 📍 الاستخدام الفعلي في GUI:

#### ✅ في BookTicket.java - استخدام TicketPriceCalculator:

```java
// في updateTotalPrice() - السطر 412-436
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

**الواجهة**: في شاشة حجز التذاكر، يوجد panel بعنوان "Ticket Extras (Decorator Pattern)" يحتوي على:
- ☑️ 🍿 Popcorn & Drink (+$7.99)
- ☑️ 🕶️ 3D Glasses (+$3.50)
- ☑️ 💺 Premium Seat Upgrade (+$5.00)

عند اختيار أي checkbox، يتم تحديث السعر تلقائياً باستخدام Decorator Pattern!

### 🎬 سيناريو كامل - تجربة المستخدم:

**الموقف**: مستخدم يريد حجز تذاكر لفيلم "Inception"

**الخطوات**:
1. يفتح شاشة BookTicket
2. يختار 3 مقاعد: A1, A2, A3
3. **السعر الأساسي**: $15.00 × 3 = $45.00
4. يختار "Popcorn & Drink" ☑️
   - **السعر الجديد**: $45.00 + $7.99 = $52.99
5. يختار "3D Glasses" ☑️
   - **السعر الجديد**: $52.99 + $3.50 = $56.49
6. يختار "Premium Seat" ☑️
   - **السعر الجديد**: $56.49 + ($5.00 × 3) = $71.49
7. ✨ **السحر**: كل تعديل يحدث تلقائياً عبر Decorator Pattern!

### ✨ الفوائد العملية:

#### 1. **المرونة الديناميكية**:
```java
// يمكن إضافة أو إزالة decorators في runtime
Ticket ticket = new BaseTicket("Movie", "A1", 15.0);

// إضافة decorators حسب اختيار المستخدم
if (userWantsPopcorn) {
    ticket = new PopcornDrinkDecorator(ticket, "Large");
}
if (userWants3D) {
    ticket = new ThreeDGlassesDecorator(ticket);
}
// السعر والوصف يتحدثان تلقائياً!
```

#### 2. **Open-Closed Principle**:
```java
// ✅ مفتوح للتوسع: إضافة decorator جديد لا يحتاج تعديل الكود القديم
class VRExperienceDecorator extends TicketDecorator {
    // decorator جديد - لا نحتاج تعديل BaseTicket أو decorators أخرى!
}

// ❌ مغلق للتعديل: BaseTicket لا يتغير
```

#### 3. **Composition over Inheritance**:
```java
// ❌ Inheritance: 2^7 = 128 كلاس محتمل!
class TicketWithPopcornAnd3DAndPremium { }

// ✅ Composition: decorators مركبة ديناميكياً
Ticket ticket = new BaseTicket(...);
ticket = new PopcornDrinkDecorator(ticket, "Medium");
ticket = new ThreeDGlassesDecorator(ticket);
ticket = new PremiumSeatDecorator(ticket);
```

#### 4. **سهولة الاستخدام مع TicketPriceCalculator**:
```java
// بدون TicketPriceCalculator: كود معقد
Ticket ticket = new BaseTicket(...);
if (hasPopcorn) ticket = new PopcornDrinkDecorator(ticket, "Medium");
if (has3D) ticket = new ThreeDGlassesDecorator(ticket);
double price = ticket.getCost() * numberOfSeats;
// ... منطق معقد للتعديل

// مع TicketPriceCalculator: سطر واحد!
double total = TicketPriceCalculator.calculateTotalPrice(
    movieTitle, basePrice, numberOfSeats, 
    hasPopcorn, has3D, hasPremium
);
```

### 📊 جميع Decorators المتاحة:

| Decorator | الوصف | السعر الإضافي |
|-----------|-------|---------------|
| `PopcornDrinkDecorator` | وجبة فشار ومشروب | $5.99 (Small), $7.99 (Medium), $9.99 (Large) |
| `ThreeDGlassesDecorator` | نظارات ثلاثية الأبعاد | $3.50 |
| `PremiumSeatDecorator` | ترقية لمقعد فاخر | $5.00 |
| `VIPLoungeDecorator` | دخول صالة VIP | $15.00 |
| `ReservedParkingDecorator` | موقف سيارة محجوز | $5.00 |
| `MealVoucherDecorator` | قسيمة وجبة | $8.99 (Snack), $15.99 (Dinner), $22.99 (Deluxe) |
| `TicketInsuranceDecorator` | تأمين إلغاء/تأجيل | $2.50 |

### 🔄 المقارنة:

| الميزة | بدون Decorator | مع Decorator |
|--------|---------------|--------------|
| **عدد Classes** | 128 كلاس محتمل 😱 | 1 Component + 7 Decorators ✅ |
| **المرونة** | ثابت - كل تركيبة كلاس منفصل | ديناميكي - تركيبات لا نهائية ✅ |
| **الصيانة** | تعديل صعب - كل كلاس منفصل | سهل - تعديل decorator واحد ✅ |
| **التوسع** | إضافة كلاس جديد لكل تركيبة | إضافة decorator واحد فقط ✅ |

### 💡 متى تستخدم Decorator:

**استخدمه عندما**:
- ✅ تريد إضافة وظائف ديناميكياً
- ✅ عدد التركيبات الممكنة كبير جداً
- ✅ تريد تجنب "class explosion"
- ✅ الوظائف الإضافية مستقلة عن بعضها

**لا تستخدمه عندما**:
- ❌ الوظائف الإضافية معقدة جداً ومترابطة
- ❌ عدد التركيبات قليل (2-3 فقط)
- ❌ الـ Component بسيط جداً

---

## 5️⃣ Adapter Pattern (نمط المحول) - شرح مفصل

### 📖 الشرح النظري الكامل:
Adapter Pattern هو نمط تصميم structural يسمح لكائنات غير متوافقة بالعمل معاً. يحول واجهة كلاس إلى واجهة أخرى يتوقعها العميل.

### 🏗️ المكونات الأساسية:
1. **Target Interface**: الواجهة التي يتوقعها النظام (`PaymentProcessor`)
2. **Adaptee**: الأنظمة الموجودة غير المتوافقة (CreditCardPaymentSystem, PayPalPaymentSystem, BankTransferSystem)
3. **Adapter**: الكلاس الذي يحول Adaptee إلى Target (CreditCardAdapter, PayPalAdapter, BankTransferAdapter)
4. **Adapter Factory**: `PaymentAdapterFactory` - لإنشاء الـ Adapter المناسب

### 🎯 المشكلة التي يحلها:

#### ❌ المشكلة بدون Adapter:
```java
// كل نظام دفع له واجهة مختلفة!
CreditCardPaymentSystem.chargeCreditCard(cardNumber, cvv, amount);
PayPalPaymentSystem.makePayment(email, totalAmount);
BankTransferSystem.transferFunds(accountNumber, funds);

// في BookTicket.java - كود معقد ومكرر!
if (paymentMethod.equals("Credit Card")) {
    CreditCardPaymentSystem cc = new CreditCardPaymentSystem();
    cc.chargeCreditCard(cardNumber, cvv, amount);
} else if (paymentMethod.equals("PayPal")) {
    PayPalPaymentSystem pp = new PayPalPaymentSystem();
    pp.makePayment(email, amount);
} else if (paymentMethod.equals("Bank Transfer")) {
    BankTransferSystem bt = new BankTransferSystem();
    bt.transferFunds(accountNumber, amount);
}
// ... كود مكرر ومعقد! 😫
```

#### ✅ الحل مع Adapter:
```java
// واجهة موحدة لجميع أنظمة الدفع!
PaymentProcessor processor = PaymentAdapterFactory.createPaymentProcessor(method);
processor.processPayment(amount, customerInfo);
// نفس الكود لكل طريقة دفع! ✨
```

### 📂 التطبيق الكامل في المشروع:

#### 1. Target Interface (PaymentProcessor.java):

```java
/**
 * الواجهة الموحدة التي يتوقعها النظام
 */
public interface PaymentProcessor {
    boolean processPayment(double amount, String customerInfo);
    String getPaymentStatus();
    String getTransactionId();
}
```

#### 2. Adaptees - الأنظمة الموجودة (PaymentAdapter.java):

```java
// Adaptee 1: Credit Card System
class CreditCardPaymentSystem {
    private String transactionId;
    
    public boolean chargeCreditCard(String cardNumber, String cvv, double amount) {
        // منطق معالجة البطاقة الائتمانية
        this.transactionId = "CC-" + System.currentTimeMillis();
        return true;
    }
    
    public String getLastTransactionId() {
        return transactionId;
    }
}

// Adaptee 2: PayPal System
class PayPalPaymentSystem {
    private String orderId;
    
    public boolean makePayment(String email, double totalAmount) {
        // منطق معالجة PayPal
        this.orderId = "PP-" + System.currentTimeMillis();
        return true;
    }
    
    public String getOrderId() {
        return orderId;
    }
}

// Adaptee 3: Bank Transfer System
class BankTransferSystem {
    private String referenceNumber;
    
    public boolean transferFunds(String accountNumber, double funds) {
        // منطق التحويل البنكي
        this.referenceNumber = "BT-" + System.currentTimeMillis();
        return true;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
}
```

#### 3. Adapters - المحولات (PaymentAdapter.java):

```java
// Adapter 1: Credit Card Adapter
class CreditCardAdapter implements PaymentProcessor {
    private CreditCardPaymentSystem creditCardSystem;
    private boolean paymentSuccessful;
    
    public CreditCardAdapter() {
        this.creditCardSystem = new CreditCardPaymentSystem();
    }
    
    @Override
    public boolean processPayment(double amount, String customerInfo) {
        // تحويل customerInfo إلى cardNumber و CVV
        String[] parts = customerInfo.split(",");
        String cardNumber = parts.length > 0 ? parts[0] : "XXXX";
        String cvv = parts.length > 1 ? parts[1] : "XXX";
        
        // استخدام Adaptee
        paymentSuccessful = creditCardSystem.chargeCreditCard(cardNumber, cvv, amount);
        return paymentSuccessful;
    }
    
    @Override
    public String getPaymentStatus() {
        return paymentSuccessful ? "Payment Successful via Credit Card" : "Payment Failed";
    }
    
    @Override
    public String getTransactionId() {
        return creditCardSystem.getLastTransactionId();
    }
}

// Adapter 2: PayPal Adapter
class PayPalAdapter implements PaymentProcessor {
    private PayPalPaymentSystem paypalSystem;
    private boolean paymentSuccessful;
    
    public PayPalAdapter() {
        this.paypalSystem = new PayPalPaymentSystem();
    }
    
    @Override
    public boolean processPayment(double amount, String customerInfo) {
        // customerInfo هو email في حالة PayPal
        paymentSuccessful = paypalSystem.makePayment(customerInfo, amount);
        return paymentSuccessful;
    }
    
    @Override
    public String getPaymentStatus() {
        return paymentSuccessful ? "Payment Successful via PayPal" : "Payment Failed";
    }
    
    @Override
    public String getTransactionId() {
        return paypalSystem.getOrderId();
    }
}

// Adapter 3: Bank Transfer Adapter
class BankTransferAdapter implements PaymentProcessor {
    private BankTransferSystem bankSystem;
    private boolean paymentSuccessful;
    
    public BankTransferAdapter() {
        this.bankSystem = new BankTransferSystem();
    }
    
    @Override
    public boolean processPayment(double amount, String customerInfo) {
        // customerInfo هو accountNumber في حالة Bank Transfer
        paymentSuccessful = bankSystem.transferFunds(customerInfo, amount);
        return paymentSuccessful;
    }
    
    @Override
    public String getPaymentStatus() {
        return paymentSuccessful ? "Payment Successful via Bank Transfer" : "Payment Failed";
    }
    
    @Override
    public String getTransactionId() {
        return bankSystem.getReferenceNumber();
    }
}
```

#### 4. PaymentAdapterFactory - Factory للـ Adapters:

```java
/**
 * Factory Pattern + Adapter Pattern
 * يسهل إنشاء الـ Adapter المناسب حسب طريقة الدفع
 */
public class PaymentAdapterFactory {
    public enum PaymentMethod {
        CREDIT_CARD, PAYPAL, BANK_TRANSFER
    }
    
    public static PaymentProcessor createPaymentProcessor(PaymentMethod method) {
        switch (method) {
            case CREDIT_CARD:
                return new CreditCardAdapter();
            case PAYPAL:
                return new PayPalAdapter();
            case BANK_TRANSFER:
                return new BankTransferAdapter();
            default:
                return new CreditCardAdapter(); // Default
        }
    }
}
```

### 📍 الاستخدام الفعلي في GUI:

#### ✅ في BookTicket.java - استخدام PaymentAdapterFactory:

```java
// في confirmBooking() - السطر 495-550
private void confirmBooking() {
    // ... بناء الحجز
    
    // استخدام Adapter Pattern للدفع
    String[] paymentOptions = {"Credit Card", "PayPal", "Bank Transfer"};
    int paymentChoice = JOptionPane.showOptionDialog(this,
        String.format("Total Amount: $%.2f\nSelect Payment Method:", finalPrice),
        "Payment Method (Adapter Pattern)",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        paymentOptions,
        paymentOptions[0]);
    
    if (paymentChoice == -1) {
        return; // المستخدم ألغى
    }
    
    // تحديد طريقة الدفع
    PaymentMethod method;
    switch (paymentChoice) {
        case 0:
            method = PaymentMethod.CREDIT_CARD;
            break;
        case 1:
            method = PaymentMethod.PAYPAL;
            break;
        case 2:
            method = PaymentMethod.BANK_TRANSFER;
            break;
        default:
            method = PaymentMethod.CREDIT_CARD;
    }
    
    // استخدام Factory لإنشاء Adapter المناسب
    PaymentProcessor processor = PaymentAdapterFactory.createPaymentProcessor(method);
    String customerInfo = bookingSystem.getCurrentUser().getEmail();
    
    // معالجة الدفع - نفس الكود لكل طريقة!
    if (processor.processPayment(finalPrice, customerInfo)) {
        // نجح الدفع
        String transactionId = processor.getTransactionId();
        String status = processor.getPaymentStatus();
        // ... عرض رسالة النجاح
    }
}
```

**الواجهة**: عند تأكيد الحجز، يظهر dialog بعنوان "Payment Method (Adapter Pattern)" مع 3 خيارات:
- 💳 Credit Card
- 💰 PayPal
- 🏦 Bank Transfer

بغض النظر عن الاختيار، نفس الكود يتعامل مع جميع الطرق!

### 🎬 سيناريو كامل - تجربة المستخدم:

**الموقف**: مستخدم يريد دفع $71.49 لحجز التذاكر

**الخطوات**:
1. يضغط "Confirm Booking"
2. يظهر dialog بطرق الدفع
3. يختار "PayPal" 💰
4. ✨ **السحر يحدث**:
   ```java
   PaymentMethod method = PaymentMethod.PAYPAL;
   PaymentProcessor processor = PaymentAdapterFactory.createPaymentProcessor(method);
   // يتم إنشاء PayPalAdapter تلقائياً
   
   processor.processPayment(71.49, "user@email.com");
   // PayPalAdapter يحول هذا إلى:
   // paypalSystem.makePayment("user@email.com", 71.49);
   ```
5. يظهر Transaction ID: "PP-1234567890"
6. رسالة نجاح: "Payment Successful via PayPal"

### ✨ الفوائد العملية:

#### 1. **واجهة موحدة**:
```java
// نفس الكود لكل طريقة دفع!
PaymentProcessor processor = PaymentAdapterFactory.createPaymentProcessor(method);
processor.processPayment(amount, customerInfo);
String transactionId = processor.getTransactionId();
```

#### 2. **سهولة إضافة طرق دفع جديدة**:
```java
// لإضافة Crypto Payment:
// 1. إنشاء CryptoPaymentSystem (Adaptee)
// 2. إنشاء CryptoAdapter (Adapter)
// 3. إضافة case في PaymentAdapterFactory
case CRYPTO:
    return new CryptoAdapter();

// الكود القديم لا يتغير! ✅
```

#### 3. **فصل الاهتمامات (Separation of Concerns)**:
```java
// BookTicket.java لا يعرف تفاصيل PayPal أو Credit Card
// كل شيء معزول في Adapters
```

#### 4. **سهولة الاختبار (Testability)**:
```java
// يمكن عمل Mock Adapter للاختبار
class MockPaymentAdapter implements PaymentProcessor {
    public boolean processPayment(double amount, String info) {
        return true; // للاختبار فقط
    }
}
```

### 🔄 المقارنة:

| الميزة | بدون Adapter | مع Adapter |
|--------|-------------|------------|
| **الكود** | if-else معقد ومكرر | كود واحد موحد ✅ |
| **إضافة طريقة دفع** | تعديل في كل مكان | إضافة Adapter واحد ✅ |
| **الصيانة** | صعب - منطق مبعثر | سهل - منطق مركزي ✅ |
| **الاختبار** | صعب - كل طريقة منفصلة | سهل - Mock Adapter ✅ |

### 💡 متى تستخدم Adapter:

**استخدمه عندما**:
- ✅ تريد استخدام كلاس موجود بواجهة غير متوافقة
- ✅ تريد توحيد واجهات أنظمة مختلفة
- ✅ تريد إضافة أنظمة جديدة دون تعديل الكود القديم

**لا تستخدمه عندما**:
- ❌ يمكن تعديل الـ Adaptee مباشرة
- ❌ الواجهات متوافقة بالفعل
- ❌ التعقيد لا يستحق

---

## 2️⃣ Factory Pattern - TheaterFactory (تحديث)

### 📖 التحديثات الجديدة:
تم تحديث `TheaterFactory` ليشمل 5 أنواع من الصالات مع price multipliers مختلفة.

### 📂 التطبيق المحدث:

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
    
    // Standard Theater - السعر الأساسي
    static class StandardTheater implements Theater {
        public double getPriceMultiplier() { return 1.0; }
        public String[] getFeatures() {
            return new String[]{"Comfortable Seating", "Digital Sound", "HD Screen"};
        }
    }
    
    // IMAX Theater - سعر أعلى بـ 80%
    static class IMAXTheater implements Theater {
        public double getPriceMultiplier() { return 1.8; }
        public String[] getFeatures() {
            return new String[]{"Giant IMAX Screen", "12-Channel Sound", 
                              "Laser Projection", "Premium Seating"};
        }
    }
    
    // VIP Theater - سعر أعلى بـ 150%
    static class VIPTheater implements Theater {
        public double getPriceMultiplier() { return 2.5; }
        public String[] getFeatures() {
            return new String[]{"Reclining Leather Seats", "Waiter Service", 
                              "Premium Sound", "Extra Legroom"};
        }
    }
    
    // Dolby Atmos Theater - سعر أعلى بـ 50%
    static class DolbyAtmosTheater implements Theater {
        public double getPriceMultiplier() { return 1.5; }
        public String[] getFeatures() {
            return new String[]{"Dolby Atmos Sound", "Enhanced Visuals", 
                              "Comfortable Seating", "Object-Based Audio"};
        }
    }
    
    // 4DX Theater - سعر أعلى بـ 100%
    static class FourDXTheater implements Theater {
        public double getPriceMultiplier() { return 2.0; }
        public String[] getFeatures() {
            return new String[]{"Motion Seats", "Wind Effects", "Water Spray", 
                              "Scent Effects", "Lighting Effects"};
        }
    }
}
```

### 📍 الاستخدام في BookTicket.java:

```java
// في BookTicket.java - السطر 254-330
// Factory Pattern - Theater Type Selection
JPanel theaterPanel = new JPanel();
theaterPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(new Color(59, 67, 84)),
    "Theater Type (Factory Pattern)",
    ...
));

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

### 🎬 مثال عملي:

```java
// المستخدم يختار IMAX
TheaterType type = TheaterType.IMAX;
Theater theater = TheaterFactory.createTheater(type, 200);

// حساب السعر
double basePrice = 15.00;
double finalPrice = basePrice * theater.getPriceMultiplier();
// finalPrice = 15.00 * 1.8 = 27.00

// عرض المميزات
String[] features = theater.getFeatures();
// ["Giant IMAX Screen", "12-Channel Sound", "Laser Projection", "Premium Seating"]
```

### 📊 جدول المقارنة:

| Theater Type | Price Multiplier | السعر (من $15) | المميزات |
|--------------|------------------|-----------------|----------|
| **STANDARD** | 1.0x | $15.00 | Comfortable Seating, Digital Sound, HD Screen |
| **IMAX** | 1.8x | $27.00 | Giant Screen, 12-Channel Sound, Laser Projection |
| **VIP** | 2.5x | $37.50 | Reclining Seats, Waiter Service, Premium Sound |
| **DOLBY_ATMOS** | 1.5x | $22.50 | Dolby Atmos Sound, Enhanced Visuals, Object-Based Audio |
| **FOUR_DX** | 2.0x | $30.00 | Motion Seats, Wind Effects, Water Spray, Scent Effects |

---

هذا الشرح يغطي جميع التحديثات الجديدة! 🎉
