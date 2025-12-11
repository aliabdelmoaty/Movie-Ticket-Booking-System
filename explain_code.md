# شرح تفصيلي لمشروع نظام حجز تذاكر السينما 🎬

## نظرة عامة على المشروع

المشروع ده عبارة عن **نظام حجز تذاكر سينما** مكتوب بـ Java باستخدام Swing للواجهة الرسومية (GUI) و SQLite كقاعدة بيانات. النظام بيسمح للمستخدمين بالتسجيل، تسجيل الدخول، عرض الأفلام، حجز المقاعد، وإدارة الحجوزات.

---

## هيكل المشروع (Project Structure)

المشروع منظم على شكل **packages** (حزم) كل واحدة ليها دور محدد:

```
src/
├── Main.java                    # نقطة البداية - اللي بيبدأ البرنامج
├── model/                       # الـ Models (نماذج البيانات)
│   ├── User.java               # نموذج المستخدم
│   ├── Movie.java              # نموذج الفيلم
│   └── Booking.java            # نموذج الحجز
├── database/                    # إدارة قاعدة البيانات
│   └── DatabaseManager.java    # مدير قاعدة البيانات
├── core/                        # المنطق الأساسي
│   └── BookingSystem.java      # النظام الأساسي للحجز
├── gui/                         # واجهة المستخدم الرسومية
│   ├── LoginFrame.java         # شاشة تسجيل الدخول
│   ├── RegisterFrame.java      # شاشة التسجيل
│   ├── BookingFrame.java       # الشاشة الرئيسية للأفلام
│   ├── MovieDetailsFrame.java  # تفاصيل الفيلم
│   ├── BookTicket.java         # شاشة اختيار المقاعد
│   ├── SeatButton.java         # زر المقعد
│   └── AddMovieDialog.java     # نافذة إضافة فيلم
└── [Test Files]                # ملفات اختبار
    ├── QuickTest.java
    ├── TestDatabase.java
    └── TestSeats.java
```

---

## 1. Main.java - نقطة البداية

```java
import javax.swing.SwingUtilities;
import gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
```

### الشرح التفصيلي:

**السطر 1-2: الـ Imports**
- `SwingUtilities`: كلاس من Swing بيستخدم عشان نضمن إن الـ GUI components بتتحدث في الـ Event Dispatch Thread (EDT). ده مهم جداً عشان Swing مش thread-safe.
- `LoginFrame`: الـ class بتاع شاشة تسجيل الدخول اللي هنفتحها.

**السطر 4: الـ Main Class**
- `public class Main`: الـ class الرئيسي اللي فيه الـ `main` method.

**السطر 5: الـ Main Method**
- `public static void main(String[] args)`: ده الـ entry point بتاع البرنامج. أي Java application لازم يكون فيها `main` method بالشكل ده بالظبط.

**السطر 6: SwingUtilities.invokeLater()**
- `SwingUtilities.invokeLater()`: ده method بيستخدم عشان ينفذ كود في الـ Event Dispatch Thread. الـ lambda expression `() -> { ... }` بتمرر Runnable object.
- ليه بنستخدم ده؟ عشان Swing components لازم تتعمل وتتحدث في الـ EDT عشان ميحصلش مشاكل في الـ threading.

**السطر 7-8: إنشاء LoginFrame**
- `LoginFrame frame = new LoginFrame()`: بنعمل instance جديد من `LoginFrame` (شاشة تسجيل الدخول).
- `frame.setVisible(true)`: بنخلي الشاشة تظهر للمستخدم.

### كيف تشغل المشروع:

**على Linux/Mac:**
```bash
./run.sh
```

**على Windows:**
```cmd
run.bat
```

**أو يدوياً:**
```bash
# Compile
javac -cp ".:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" -d bin -sourcepath src src/database/*.java src/model/*.java src/core/*.java src/gui/*.java src/Main.java

# Run
java -cp "bin:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" Main
```

---

## 2. DatabaseManager.java - مدير قاعدة البيانات

```java
package database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:moviebooking.db";
    private static DatabaseManager instance;
    private Connection connection;
    // ...
}
```

### الشرح التفصيلي:

**السطر 1: Package Declaration**
- `package database;`: ده بيحدد إن الـ class ده موجود في package اسمه `database`. ده بيسمح بتنظيم الكود.

**السطر 3: Imports**
- `java.sql.*`: كل الـ classes المتعلقة بـ SQL و Database operations.

**السطر 5: Class Declaration**
- `public class DatabaseManager`: الـ class بتاع إدارة قاعدة البيانات.

**السطر 6: Database URL**
- `private static final String DB_URL = "jdbc:sqlite:moviebooking.db";`
  - `private`: متغير خاص بالـ class.
  - `static`: يعني مش محتاج instance من الـ class عشان نوصله.
  - `final`: يعني مش ممكن يتغير (constant).
  - `jdbc:sqlite:`: ده الـ protocol بتاع SQLite.
  - `moviebooking.db`: اسم ملف قاعدة البيانات اللي هيتعمل في نفس المجلد.

**السطر 7: Singleton Instance**
- `private static DatabaseManager instance;`: متغير static عشان نخزن الـ instance الوحيد من الـ class (Singleton Pattern).

**السطر 8: Connection**
- `private Connection connection;`: الـ connection object اللي بيوصلنا بقاعدة البيانات.

### Constructor (السطر 10-19):

```java
private DatabaseManager() {
    try {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(DB_URL);
        createTables();
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
    }
}
```

**السطر 10: Private Constructor**
- `private DatabaseManager()`: Constructor خاص (private) عشان منعرفش نعمل instance من برة. ده جزء من Singleton Pattern.

**السطر 12: Load JDBC Driver**
- `Class.forName("org.sqlite.JDBC")`: بنحمل الـ JDBC driver بتاع SQLite. ده ضروري عشان Java تعرف تتعامل مع SQLite.

**السطر 13: Create Connection**
- `DriverManager.getConnection(DB_URL)`: بنعمل connection مع قاعدة البيانات. لو الملف مش موجود، SQLite هيعمله تلقائياً.

**السطر 14: Create Tables**
- `createTables()`: بنستدعي method عشان نعمل الجداول في قاعدة البيانات.

**السطر 15: Exception Handling**
- `catch (ClassNotFoundException | SQLException e)`: لو حصل خطأ (مثلاً الـ driver مش موجود أو مشكلة في الاتصال)، بنطبع الـ error.

### getInstance() Method (السطر 21-26):

```java
public static DatabaseManager getInstance() {
    if (instance == null) {
        instance = new DatabaseManager();
    }
    return instance;
}
```

**ده Singleton Pattern:**
- `public static`: method static يعني ممكن نستدعيه من غير ما نعمل instance.
- `if (instance == null)`: لو الـ instance مش موجود، بنعمله.
- `return instance;`: بنرجع الـ instance الموجود (أو اللي اتعمل للتو).

**ليه بنستخدم Singleton؟**
- عشان نضمن إن عندنا connection واحد بس لقاعدة البيانات في كل البرنامج. ده بيوفر resources وبيمنع مشاكل الـ concurrency.

### createTables() Method (السطر 32-91):

```java
private void createTables() {
    try {
        Statement stmt = connection.createStatement();
        
        // Users table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        stmt.execute(createUsersTable);
        // ... باقي الجداول
    }
}
```

**السطر 33: Create Statement**
- `Statement stmt = connection.createStatement()`: بنعمل Statement object عشان ننفذ SQL queries.

**السطر 36-44: Users Table Schema**
- `CREATE TABLE IF NOT EXISTS`: بنعمل الجدول لو مش موجود.
- `id INTEGER PRIMARY KEY AUTOINCREMENT`: عمود ID كـ primary key وبيزيد تلقائياً.
- `name TEXT NOT NULL`: اسم المستخدم، مش ممكن يكون null.
- `email TEXT UNIQUE NOT NULL`: الإيميل لازم يكون unique ومش null.
- `username TEXT UNIQUE NOT NULL`: اسم المستخدم لازم يكون unique.
- `password TEXT NOT NULL`: كلمة المرور.
- `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`: تاريخ الإنشاء تلقائياً.

**السطر 48-58: Movies Table**
- نفس الفكرة، بس للسينما:
  - `title`: عنوان الفيلم
  - `genre`: النوع
  - `duration`: المدة
  - `rating`: التقييم
  - `description`: الوصف
  - `poster_path`: مسار صورة البوستر

**السطر 61-71: Bookings Table**
- `user_id`: ID المستخدم (Foreign Key)
- `movie_id`: ID الفيلم (Foreign Key)
- `seats`: المقاعد المحجوزة (مخزنة كـ string مثل "A1, A2, A3")
- `total_price`: السعر الإجمالي
- `booking_date`: تاريخ الحجز

**السطر 74-84: Seats Table**
- `movie_id`: ID الفيلم
- `seat_label`: تسمية المقعد (مثل "A1")
- `is_occupied`: هل المقعد محجوز (boolean)
- `booking_id`: ID الحجز المرتبط
- `UNIQUE(movie_id, seat_label)`: كل مقعد في فيلم معين لازم يكون unique

### closeConnection() Method (السطر 93-101):

```java
public void closeConnection() {
    try {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

- بنقفل الـ connection لو كان مفتوح. ده مهم عشان نحرر الـ resources.

---

## 3. User.java - نموذج المستخدم

```java
package model;

import database.DatabaseManager;
import java.sql.*;

public class User {
    private int id;
    private String name;
    private String email;
    private String username;
    private String password;
    private Timestamp createdAt;
    // ...
}
```

### الشرح التفصيلي:

**السطر 1: Package**
- `package model;`: الـ class موجود في package `model`.

**السطر 3-4: Imports**
- `DatabaseManager`: عشان نستخدم قاعدة البيانات.
- `java.sql.*`: للـ SQL operations.

**السطر 6-12: Fields (الخصائص)**
- `private int id`: ID المستخدم في قاعدة البيانات.
- `private String name`: الاسم الكامل.
- `private String email`: الإيميل.
- `private String username`: اسم المستخدم.
- `private String password`: كلمة المرور (ملاحظة: في الواقع لازم تتشفير، لكن هنا مخزنة plain text).
- `private Timestamp createdAt`: تاريخ الإنشاء.

### Constructors (السطر 14-31):

```java
public User() {}
```

**Default Constructor:**
- Constructor فاضي عشان نعمل User object من غير قيم.

```java
public User(String name, String email, String username, String password) {
    this.name = name;
    this.email = email;
    this.username = username;
    this.password = password;
}
```

**Constructor مع البيانات الأساسية:**
- بنستخدم `this.` عشان نفرق بين الـ parameter والـ field.

```java
public User(int id, String name, String email, String username, String password, Timestamp createdAt) {
    // ... نفس الفكرة مع ID و createdAt
}
```

**Constructor كامل:**
- بنستخدمه لما نجيب بيانات من قاعدة البيانات.

### Getters and Setters (السطر 33-50):

```java
public int getId() { return id; }
public void setId(int id) { this.id = id; }
```

- **Getter**: method بيرجع قيمة الـ field.
- **Setter**: method بيحدد قيمة الـ field.
- ده جزء من **Encapsulation** في OOP.

### save() Method (السطر 53-82):

```java
public boolean save() {
    String sql = "INSERT INTO users (name, email, username, password) VALUES (?, ?, ?, ?)";
    
    try {
        PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.setString(3, username);
        pstmt.setString(4, password);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            // Get the last inserted ID
            Statement stmt = DatabaseManager.getInstance().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            pstmt.close();
            return true;
        }
        pstmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
```

**السطر 54: SQL Query**
- `INSERT INTO users ...`: query عشان نحفظ المستخدم في قاعدة البيانات.
- `?`: placeholders عشان نمنع SQL Injection.

**السطر 57: PreparedStatement**
- `PreparedStatement`: نوع من الـ Statement بيستخدم parameters. ده آمن أكتر من String concatenation.

**السطر 58-61: Set Parameters**
- `pstmt.setString(1, name)`: بنحط القيمة في الـ placeholder الأول.
- الأرقام (1, 2, 3, 4) بتشير لترتيب الـ placeholders.

**السطر 63: Execute Update**
- `executeUpdate()`: بننفذ الـ query وبنرجع عدد الصفوف المتأثرة.

**السطر 65-73: Get Last Inserted ID**
- `SELECT last_insert_rowid()`: بنجيب الـ ID اللي اتعمل تلقائياً.
- `rs.next()`: بنتحرك للصف الأول في الـ ResultSet.
- `this.id = rs.getInt(1)`: بنحط الـ ID في الـ object.

**السطر 74-76: Close Resources**
- مهم جداً نغلق الـ resources عشان نحرر الذاكرة.

### findByEmailAndPassword() Method (السطر 84-107):

```java
public static User findByEmailAndPassword(String email, String password) {
    String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
    
    try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        pstmt.setString(1, email);
        pstmt.setString(2, password);
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getTimestamp("created_at")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
```

**السطر 84: Static Method**
- `static`: يعني مش محتاج instance من User عشان نستدعيه.

**السطر 85: SELECT Query**
- `SELECT * FROM users WHERE ...`: بنبحث عن مستخدم بالإيميل والباسورد.

**السطر 87: Try-with-Resources**
- `try (PreparedStatement ...)`: الـ try-with-resources بيغلق الـ resource تلقائياً.

**السطر 91: Execute Query**
- `executeQuery()`: بنستخدمه مع SELECT (مش UPDATE/INSERT).

**السطر 93-101: Create User Object**
- لو لقينا صف، بنعمل User object جديد من البيانات.

**السطر 106: Return Null**
- لو مفيش مستخدم، بنرجع `null`.

### findByUsername() و findByEmail() Methods:

- نفس الفكرة، بس بتبحث عن username أو email بس.

---

## 4. Movie.java - نموذج الفيلم

```java
package model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private String duration;
    private String rating;
    private String description;
    private String posterPath;
    private Timestamp createdAt;
    // ...
}
```

### الشرح:

**الـ Fields:**
- `id`: ID الفيلم
- `title`: العنوان
- `genre`: النوع (Action, Drama، إلخ)
- `duration`: المدة (مثل "2h 30m")
- `rating`: التقييم (مثل "8.5")
- `description`: الوصف
- `posterPath`: مسار صورة البوستر
- `createdAt`: تاريخ الإضافة

### save() Method:

- نفس فكرة `User.save()`، بس بنحفظ بيانات الفيلم.

### getAllMovies() Method (السطر 100-123):

```java
public static List<Movie> getAllMovies() {
    List<Movie> movies = new ArrayList<>();
    String sql = "SELECT * FROM movies";
    
    try (Statement stmt = DatabaseManager.getInstance().getConnection().createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            movies.add(new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("duration"),
                rs.getString("rating"),
                rs.getString("description"),
                rs.getString("poster_path"),
                rs.getTimestamp("created_at")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return movies;
}
```

**السطر 101: List Declaration**
- `List<Movie>`: قائمة من نوع Movie.
- `ArrayList<>()`: implementation من List.

**السطر 102: SELECT Query**
- `SELECT * FROM movies`: بنجيب كل الأفلام.

**السطر 104-105: Try-with-Resources**
- بنستخدم Statement و ResultSet في نفس الـ try.

**السطر 107: While Loop**
- `while (rs.next())`: بنلف على كل صف في النتيجة.

**السطر 108-117: Create Movie Objects**
- بنعمل Movie object لكل صف ونضيفه في الـ List.

### searchByTitle() Method (السطر 150-174):

```java
public static List<Movie> searchByTitle(String searchTerm) {
    List<Movie> movies = new ArrayList<>();
    String sql = "SELECT * FROM movies WHERE title LIKE ?";
    
    try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        pstmt.setString(1, "%" + searchTerm + "%");
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            // ... نفس الفكرة
        }
    }
    return movies;
}
```

**السطر 152: LIKE Query**
- `WHERE title LIKE ?`: بنبحث في العنوان.
- `LIKE`: بيستخدم pattern matching.

**السطر 155: Wildcard Pattern**
- `"%" + searchTerm + "%"`: الـ `%` يعني أي حروف قبل أو بعد. يعني لو بحثنا "dark" هنلاقي "The Dark Knight".

---

## 5. Booking.java - نموذج الحجز

```java
package model;

public class Booking {
    private int id;
    private int userId;
    private int movieId;
    private String seats;
    private double totalPrice;
    private Timestamp bookingDate;
    
    // For display purposes
    private String userName;
    private String movieTitle;
    // ...
}
```

### الشرح:

**الـ Fields:**
- `id`: ID الحجز
- `userId`: ID المستخدم اللي عمل الحجز
- `movieId`: ID الفيلم
- `seats`: المقاعد (مخزنة كـ string مثل "A1, A2, A3")
- `totalPrice`: السعر الإجمالي
- `bookingDate`: تاريخ الحجز
- `userName` و `movieTitle`: للعرض فقط (مش في قاعدة البيانات)

### save() Method (السطر 65-98):

```java
public boolean save() {
    String sql = "INSERT INTO bookings (user_id, movie_id, seats, total_price) VALUES (?, ?, ?, ?)";
    
    try {
        PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.setInt(2, movieId);
        pstmt.setString(3, seats);
        pstmt.setDouble(4, totalPrice);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            // Get last inserted ID
            // ...
            this.id = rs.getInt(1);
            
            // Mark seats as occupied
            markSeatsAsOccupied();
            
            return true;
        }
    }
    return false;
}
```

**السطر 89: markSeatsAsOccupied()**
- بعد ما نحفظ الحجز، بنستدعي method عشان نحدد إن المقاعد دي محجوزة.

### markSeatsAsOccupied() Method (السطر 100-114):

```java
private void markSeatsAsOccupied() {
    String[] seatArray = seats.split(", ");
    String sql = "INSERT OR REPLACE INTO seats (movie_id, seat_label, is_occupied, booking_id) VALUES (?, ?, 1, ?)";
    
    try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        for (String seat : seatArray) {
            pstmt.setInt(1, movieId);
            pstmt.setString(2, seat.trim());
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }
}
```

**السطر 101: Split Seats String**
- `seats.split(", ")`: بنقسم الـ string "A1, A2, A3" لـ array ["A1", "A2", "A3"].

**السطر 102: INSERT OR REPLACE**
- `INSERT OR REPLACE`: لو المقعد موجود، بنحدثه. لو مش موجود، بنضيفه.

**السطر 105: For Loop**
- بنلف على كل مقعد ونحفظه في قاعدة البيانات.

### getBookingsByUser() Method (السطر 116-146):

```java
public static List<Booking> getBookingsByUser(int userId) {
    List<Booking> bookings = new ArrayList<>();
    String sql = "SELECT b.*, u.name as user_name, m.title as movie_title " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN movies m ON b.movie_id = m.id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.booking_date DESC";
    
    try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Booking booking = new Booking(/* ... */);
            booking.setUserName(rs.getString("user_name"));
            booking.setMovieTitle(rs.getString("movie_title"));
            bookings.add(booking);
        }
    }
    return bookings;
}
```

**السطر 118-122: JOIN Query**
- `JOIN users u ON b.user_id = u.id`: بنربط جدول الحجوزات بجدول المستخدمين.
- `JOIN movies m ON b.movie_id = m.id`: بنربط جدول الحجوزات بجدول الأفلام.
- `ORDER BY b.booking_date DESC`: بنرتب الحجوزات من الأحدث للأقدم.

### isSeatOccupied() Method (السطر 178-193):

```java
public static boolean isSeatOccupied(int movieId, String seatLabel) {
    String sql = "SELECT is_occupied FROM seats WHERE movie_id = ? AND seat_label = ?";
    
    try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        pstmt.setInt(1, movieId);
        pstmt.setString(2, seatLabel);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return rs.getBoolean("is_occupied");
        }
    }
    return false;
}
```

- بنتحقق لو مقعد معين محجوز لفيلم معين.

### getOccupiedSeats() Method (السطر 195-210):

```java
public static List<String> getOccupiedSeats(int movieId) {
    List<String> occupiedSeats = new ArrayList<>();
    String sql = "SELECT seat_label FROM seats WHERE movie_id = ? AND is_occupied = 1";
    
    try (PreparedStatement pstmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
        pstmt.setInt(1, movieId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            occupiedSeats.add(rs.getString("seat_label"));
        }
    }
    return occupiedSeats;
}
```

- بنرجع قائمة بكل المقاعد المحجوزة لفيلم معين.

---

## 6. BookingSystem.java - النظام الأساسي

```java
package core;

import model.User;
import model.Movie;
import model.Booking;
import database.DatabaseManager;

public class BookingSystem {
    private static BookingSystem instance;
    private User currentUser;
    
    private BookingSystem() {
        DatabaseManager.getInstance();
    }
    
    public static BookingSystem getInstance() {
        if (instance == null) {
            instance = new BookingSystem();
        }
        return instance;
    }
    // ...
}
```

### الشرح:

**Singleton Pattern:**
- نفس فكرة `DatabaseManager`، بنستخدم Singleton عشان عندنا instance واحد بس من النظام.

**currentUser:**
- `private User currentUser`: المستخدم اللي مسجل دخول دلوقتي.

### register() Method (السطر 25-37):

```java
public boolean register(String name, String email, String username, String password) {
    // Check if user already exists
    if (User.findByEmail(email) != null) {
        return false; // Email already exists
    }
    
    if (User.findByUsername(username) != null) {
        return false; // Username already exists
    }
    
    User user = new User(name, email, username, password);
    return user.save();
}
```

**الخطوات:**
1. بنتحقق لو الإيميل موجود.
2. بنتحقق لو اسم المستخدم موجود.
3. لو كل حاجة تمام، بنعمل User جديد ونحفظه.

### login() Method (السطر 39-46):

```java
public boolean login(String email, String password) {
    User user = User.findByEmailAndPassword(email, password);
    if (user != null) {
        currentUser = user;
        return true;
    }
    return false;
}
```

- بنبحث عن المستخدم بالإيميل والباسورد.
- لو لقيناه، بنحطوه في `currentUser` وبنرجع `true`.

### logout() Method (السطر 48-50):

```java
public void logout() {
    currentUser = null;
}
```

- بنحط `currentUser` على `null`.

### addMovie() Method (السطر 61-64):

```java
public boolean addMovie(String title, String genre, String duration, String rating, String description, String posterPath) {
    Movie movie = new Movie(title, genre, duration, rating, description, posterPath);
    return movie.save();
}
```

- بنعمل Movie جديد ونحفظه.

### getAllMovies() و searchMovies() Methods:

- بنرجع كل الأفلام أو بنبحث عن أفلام معينة.

### createBooking() Method (السطر 79-86):

```java
public boolean createBooking(int movieId, String seats, double totalPrice) {
    if (!isLoggedIn()) {
        return false;
    }
    
    Booking booking = new Booking(currentUser.getId(), movieId, seats, totalPrice);
    return booking.save();
}
```

- بنتحقق لو المستخدم مسجل دخول.
- لو آه، بنعمل Booking جديد ونحفظه.

---

## 7. LoginFrame.java - شاشة تسجيل الدخول

```java
package gui;

import javax.swing.*;
import java.awt.*;
import core.BookingSystem;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    // ...
}
```

### الشرح:

**السطر 1: Package**
- `package gui;`: الـ class في package `gui`.

**السطر 3-5: Imports**
- `javax.swing.*`: كل الـ Swing components (JFrame, JButton، إلخ).
- `java.awt.*`: الـ AWT components (Color, Font، إلخ).
- `core.BookingSystem`: عشان نستخدم النظام.

**السطر 7: Class Declaration**
- `extends JFrame`: الـ class بيورث من JFrame (نافذة Swing).

**السطر 8-11: Fields**
- `emailField`: حقل الإيميل.
- `passwordField`: حقل الباسورد (JPasswordField بيخفي النص).

### Constructor (السطر 14-116):

```java
public LoginFrame() {
    setTitle("Login");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);
    setResizable(false);
    // ...
}
```

**السطر 15: Set Title**
- `setTitle("Login")`: بنحط عنوان النافذة.

**السطر 16: Default Close Operation**
- `JFrame.EXIT_ON_CLOSE`: لما المستخدم يقفل النافذة، البرنامج كله يغلق.

**السطر 17: Set Size**
- `setSize(800, 600)`: حجم النافذة (عرض × ارتفاع).

**السطر 18: Center Window**
- `setLocationRelativeTo(null)`: بنحط النافذة في وسط الشاشة.

**السطر 19: Non-Resizable**
- `setResizable(false)`: النافذة مش هتتقدر تتغير في الحجم.

### Background Panel (السطر 21-25):

```java
JPanel backgroundPanel = new JPanel();
backgroundPanel.setBackground(new Color(16, 22, 34)); // Dark Mode
backgroundPanel.setLayout(new GridBagLayout());
add(backgroundPanel);
```

**السطر 22: Dark Background**
- `new Color(16, 22, 34)`: لون داكن (RGB values).

**السطر 23: GridBagLayout**
- `GridBagLayout`: layout manager قوي بيسمح بترتيب مرن للـ components.

### Form Panel (السطر 27-31):

```java
JPanel form = new JPanel(new GridBagLayout());
form.setBackground(new Color(28, 31, 39)); // Dark card
form.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 8, true));
form.setPreferredSize(new Dimension(400, 400));
```

**السطر 31: Border**
- `createLineBorder(..., 8, true)`: border بسمك 8 pixels و rounded corners.

### Email Field (السطر 51-65):

```java
JLabel emailLabel = new JLabel("Email");
emailLabel.setForeground(Color.WHITE);
c.gridy++;
form.add(emailLabel, c);

emailField = new JTextField(20);
emailField.setFont(new Font("Spline Sans", Font.PLAIN, 14));
emailField.setForeground(Color.WHITE);
emailField.setBackground(new Color(28, 31, 39));
emailField.setCaretColor(Color.WHITE);
emailField.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 5, true));
```

**السطر 57: JTextField**
- `new JTextField(20)`: حقل نص بعرض 20 characters.

**السطر 58: Font**
- `new Font("Spline Sans", Font.PLAIN, 14)`: خط "Spline Sans" بحجم 14.

**السطر 61: Caret Color**
- `setCaretColor(Color.WHITE)`: لون المؤشر (cursor) أبيض.

### Login Button (السطر 83-91):

```java
JButton loginBtn = new JButton("Login");
loginBtn.setFont(new Font("Spline Sans", Font.BOLD, 14));
loginBtn.setBackground(new Color(19, 91, 236)); // primary color
loginBtn.setForeground(Color.WHITE);
loginBtn.setFocusPainted(false);

loginBtn.addActionListener(e -> handleLogin());
```

**السطر 89: Action Listener**
- `addActionListener(e -> handleLogin())`: لما المستخدم يضغط الزر، بنستدعي `handleLogin()`.

### handleLogin() Method (السطر 118-145):

```java
private void handleLogin() {
    String email = emailField.getText().trim();
    String password = new String(passwordField.getPassword());
    
    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please fill in all fields!",
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    BookingSystem bookingSystem = BookingSystem.getInstance();
    
    if (bookingSystem.login(email, password)) {
        JOptionPane.showMessageDialog(this,
            "Welcome back, " + bookingSystem.getCurrentUser().getName() + "!",
            "Login Successful",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new BookingFrame().setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this,
            "Invalid email or password!",
            "Login Failed",
            JOptionPane.ERROR_MESSAGE);
    }
}
```

**السطر 119-120: Get Input**
- `getText().trim()`: بنجيب النص ونشيل المسافات من الأول والآخر.
- `getPassword()`: بنجيب الباسورد (يرجع char array).

**السطر 122-128: Validation**
- بنتحقق لو الحقول فاضية.

**السطر 130: Get BookingSystem**
- `BookingSystem.getInstance()`: بنجيب الـ instance.

**السطر 132-138: Successful Login**
- لو الدخول نجح، بنعرض رسالة ترحيب.
- `dispose()`: بنقفل النافذة الحالية.
- `new BookingFrame().setVisible(true)`: بنفتح الشاشة الرئيسية.

**السطر 139-144: Failed Login**
- لو الدخول فشل، بنعرض رسالة خطأ.

---

## 8. RegisterFrame.java - شاشة التسجيل

- نفس فكرة `LoginFrame`، بس فيها حقول أكتر (name, email, username, password).

### handleRegister() Method (السطر 152-198):

```java
private void handleRegister() {
    String name = nameField.getText().trim();
    String email = emailField.getText().trim();
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());
    
    // Validation
    if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please fill in all fields!",
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (!email.contains("@")) {
        JOptionPane.showMessageDialog(this,
            "Please enter a valid email address!",
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (password.length() < 6) {
        JOptionPane.showMessageDialog(this,
            "Password must be at least 6 characters long!",
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    BookingSystem bookingSystem = BookingSystem.getInstance();
    
    if (bookingSystem.register(name, email, username, password)) {
        JOptionPane.showMessageDialog(this,
            "Registration successful! Please login to continue.",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginFrame().setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this,
            "Registration failed! Email or username already exists.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
```

**الـ Validations:**
1. كل الحقول لازم تكون مملوءة.
2. الإيميل لازم يكون فيه `@`.
3. الباسورد لازم يكون 6 أحرف على الأقل.

---

## 9. BookingFrame.java - الشاشة الرئيسية

```java
package gui;

public class BookingFrame extends JFrame {
    private JTextField searchField;
    private List<model.Movie> movies;
    // ...
}
```

### الشرح:

**الـ Fields:**
- `searchField`: حقل البحث.
- `movies`: قائمة الأفلام.

### Constructor (السطر 14-145):

```java
public BookingFrame() {
    setTitle("Movie Booking");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1000, 700);
    setLocationRelativeTo(null);
    setResizable(false);
    
    loadMovies();
    // ...
}
```

**السطر 22: loadMovies()**
- بنحمل الأفلام من قاعدة البيانات.

### loadMovies() Method (السطر 147-166):

```java
private void loadMovies() {
    // First, import movies from posters folder to database if not already imported
    importMoviesFromPostersFolder();
    
    // Load all movies from database
    BookingSystem system = BookingSystem.getInstance();
    movies = system.getAllMovies();
    
    // If still empty, add default movies to database
    if (movies.isEmpty()) {
        system.addMovie("The Shawshank Redemption", "Drama", "2h 22m", "9.3", 
            "Two imprisoned men bond over a number of years.", null);
        system.addMovie("The Godfather", "Crime, Drama", "2h 55m", "9.2", 
            "The aging patriarch of an organized crime dynasty transfers control.", null);
        system.addMovie("The Dark Knight", "Action, Crime, Drama", "2h 32m", "9.0", 
            "When the menace known as the Joker wreaks havoc on Gotham.", null);
        
        movies = system.getAllMovies();
    }
}
```

**الخطوات:**
1. بنستورد أفلام من مجلد `assets/posters`.
2. بنحمل كل الأفلام من قاعدة البيانات.
3. لو مفيش أفلام، بنضيف أفلام افتراضية.

### importMoviesFromPostersFolder() Method (السطر 168-218):

```java
private void importMoviesFromPostersFolder() {
    BookingSystem system = BookingSystem.getInstance();
    File postersDir = new File("assets/posters");
    
    if (postersDir.exists() && postersDir.isDirectory()) {
        File[] posterFiles = postersDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".png")
        );
        
        if (posterFiles != null) {
            String[] genres = {"Action", "Drama", "Sci-Fi", "Thriller", "Comedy", 
                             "Adventure", "Crime", "Mystery", "Romance"};
            String[] descriptions = {
                "An epic tale of adventure and excitement.",
                // ... باقي الأوصاف
            };
            
            for (int i = 0; i < posterFiles.length; i++) {
                String title = posterFiles[i].getName()
                    .replace(".jpeg", "").replace(".jpg", "").replace(".png", "");
                title = title.substring(0, 1).toUpperCase() + title.substring(1);
                
                String genre = genres[i % genres.length];
                int hours = 2 + (i % 2);
                int minutes = (i % 3) * 15;
                String duration = hours + "h " + minutes + "m";
                
                double rating = 7.5 + (i % 15) * 0.1;
                String ratingStr = String.format("%.1f", rating);
                
                String description = descriptions[i % descriptions.length];
                String posterPath = posterFiles[i].getPath();
                
                // Check if movie with same title already exists
                List<model.Movie> existingMovies = system.searchMovies(title);
                if (existingMovies.isEmpty()) {
                    system.addMovie(title, genre, duration, ratingStr, description, posterPath);
                }
            }
        }
    }
}
```

**الشرح:**
- بنفتح مجلد `assets/posters`.
- بنجيب كل ملفات الصور (.jpeg, .jpg, .png).
- لكل صورة، بنعمل فيلم جديد:
  - العنوان من اسم الملف.
  - النوع من array (باستخدام modulo عشان نكرر).
  - المدة والتقييم عشوائيين.
- لو الفيلم موجود، مش بنضيفه تاني.

### createMovieCard() Method (السطر 385-473):

```java
private JPanel createMovieCard(model.Movie movie) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout());
    card.setPreferredSize(new Dimension(200, 350));
    card.setBackground(new Color(28, 31, 39));
    card.setBorder(BorderFactory.createLineBorder(new Color(59, 67, 84), 1, true));
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Movie poster
    JPanel posterPanel = new JPanel();
    posterPanel.setPreferredSize(new Dimension(200, 280));
    posterPanel.setBackground(new Color(40, 45, 55));
    posterPanel.setLayout(new BorderLayout());
    
    if (movie.getPosterPath() != null) {
        try {
            ImageIcon icon = new ImageIcon(movie.getPosterPath());
            Image image = icon.getImage().getScaledInstance(200, 280, Image.SCALE_SMOOTH);
            JLabel posterLabel = new JLabel(new ImageIcon(image));
            posterPanel.add(posterLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            addPosterPlaceholder(posterPanel);
        }
    } else {
        addPosterPlaceholder(posterPanel);
    }
    
    // Movie info panel
    // ... (rating, title, genre)
    
    // Hover effect and click event
    card.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            card.setBackground(new Color(35, 38, 46));
            infoPanel.setBackground(new Color(35, 38, 46));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            card.setBackground(new Color(28, 31, 39));
            infoPanel.setBackground(new Color(28, 31, 39));
        }
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            MovieDetailsFrame detailsFrame = new MovieDetailsFrame(guiMovie, movie.getId());
            detailsFrame.setVisible(true);
        }
    });
    
    return card;
}
```

**الشرح:**
- بنعمل card (بطاقة) لكل فيلم.
- بنحمل صورة البوستر لو موجودة.
- لو الصورة مش موجودة، بنعرض placeholder (🎬).
- بنضيف hover effect (لما الماوس يعدي على الكارد، اللون يتغير).
- لما المستخدم يضغط على الكارد، بنفتح `MovieDetailsFrame`.

---

## 10. MovieDetailsFrame.java - تفاصيل الفيلم

```java
package gui;

public class MovieDetailsFrame extends JFrame {
    private BookingFrame.Movie movie;
    private int movieId;
    // ...
}
```

### الشرح:

- نافذة بتعرض تفاصيل الفيلم (البوستر، العنوان، التقييم، الوصف).
- فيها زر "Book Tickets" اللي بيفتح `BookTicket` frame.

---

## 11. BookTicket.java - شاشة اختيار المقاعد

```java
package gui;

public class BookTicket extends JFrame {
    private BookingFrame.Movie movie;
    private int movieId;
    private JPanel seatsPanel;
    private JLabel selectedSeatsLabel;
    private JLabel totalPriceLabel;
    private List<SeatButton> selectedSeats;
    private static final double SEAT_PRICE = 15.00;
    private static final int ROWS = 8;
    private static final int COLS = 12;
    // ...
}
```

### الشرح:

**الـ Constants:**
- `SEAT_PRICE = 15.00`: سعر المقعد الواحد.
- `ROWS = 8`: عدد الصفوف.
- `COLS = 12`: عدد الأعمدة.

### createSeatsPanel() Method (السطر 180-207):

```java
private JPanel createSeatsPanel() {
    JPanel container = new JPanel();
    container.setBackground(new Color(16, 22, 34));
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    
    // Create seats grid
    JPanel seatsGrid = new JPanel(new GridLayout(ROWS, COLS, 8, 8));
    seatsGrid.setBackground(new Color(16, 22, 34));
    seatsGrid.setMaximumSize(new Dimension(800, 400));
    
    // Get occupied seats from database
    List<String> occupiedSeats = Booking.getOccupiedSeats(movieId);
    
    // Generate seats with real occupied status from database
    for (int row = 0; row < ROWS; row++) {
        for (int col = 0; col < COLS; col++) {
            String seatLabel = (char)('A' + row) + String.valueOf(col + 1);
            boolean isOccupied = occupiedSeats.contains(seatLabel);
            SeatButton seatButton = new SeatButton(seatLabel, isOccupied, this);
            seatsGrid.add(seatButton);
        }
    }
    
    return container;
}
```

**السطر 186: GridLayout**
- `new GridLayout(ROWS, COLS, 8, 8)`: grid بـ 8 صفوف و 12 عمود، مع مسافة 8 pixels بين كل مقعد.

**السطر 191: Get Occupied Seats**
- `Booking.getOccupiedSeats(movieId)`: بنجيب المقاعد المحجوزة من قاعدة البيانات.

**السطر 194-200: Generate Seats**
- `for (int row = 0; row < ROWS; row++)`: بنلف على الصفوف.
- `for (int col = 0; col < COLS; col++)`: بنلف على الأعمدة.
- `(char)('A' + row)`: بنحول رقم الصف لحرف (A, B, C، إلخ).
- `String.valueOf(col + 1)`: رقم العمود (1, 2, 3، إلخ).
- `seatLabel`: بيكون "A1", "A2", "B1"، إلخ.
- `occupiedSeats.contains(seatLabel)`: بنتحقق لو المقعد محجوز.
- `new SeatButton(...)`: بنعمل زر للمقعد.

### confirmBooking() Method (السطر 280-336):

```java
private void confirmBooking() {
    if (selectedSeats.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please select at least one seat!",
            "No Seats Selected",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    BookingSystem bookingSystem = BookingSystem.getInstance();
    
    // Check if user is logged in
    if (!bookingSystem.isLoggedIn()) {
        int choice = JOptionPane.showConfirmDialog(this,
            "You need to login to book tickets. Do you want to login now?",
            "Login Required",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
        return;
    }
    
    // Build seats string
    StringBuilder seatsBuilder = new StringBuilder();
    for (int i = 0; i < selectedSeats.size(); i++) {
        seatsBuilder.append(selectedSeats.get(i).getSeatLabel());
        if (i < selectedSeats.size() - 1) {
            seatsBuilder.append(", ");
        }
    }
    String seats = seatsBuilder.toString();
    double totalPrice = selectedSeats.size() * SEAT_PRICE;
    
    // Save booking to database
    if (bookingSystem.createBooking(movieId, seats, totalPrice)) {
        // Show success message
        JOptionPane.showMessageDialog(this,
            message.toString(),
            "Booking Successful",
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    } else {
        JOptionPane.showMessageDialog(this,
            "Failed to create booking. Please try again.",
            "Booking Failed",
            JOptionPane.ERROR_MESSAGE);
    }
}
```

**الخطوات:**
1. بنتحقق لو في مقاعد مختارة.
2. بنتحقق لو المستخدم مسجل دخول.
3. بنبني string من المقاعد المختارة (مثل "A1, A2, A3").
4. بنحسب السعر الإجمالي.
5. بنحفظ الحجز في قاعدة البيانات.

---

## 12. SeatButton.java - زر المقعد

```java
package gui;

import javax.swing.*;
import java.awt.*;

public class SeatButton extends JButton {
    private String seatLabel;
    private boolean isOccupied;
    private boolean isSelected;
    private BookTicket parentFrame;
    
    public SeatButton(String seatLabel, boolean isOccupied, BookTicket parentFrame) {
        super(seatLabel);
        this.seatLabel = seatLabel;
        this.isOccupied = isOccupied;
        this.isSelected = false;
        this.parentFrame = parentFrame;
        
        setFont(new Font("Spline Sans", Font.BOLD, 11));
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(50, 50));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isOccupied) {
            setBackground(new Color(220, 53, 69)); // Red
            setForeground(Color.WHITE);
            setEnabled(false);
        } else {
            setBackground(new Color(59, 67, 84)); // Gray
            setForeground(Color.WHITE);
        }
        
        addActionListener(e -> toggleSeat());
    }
    
    private void toggleSeat() {
        if (isOccupied) return;
        
        isSelected = !isSelected;
        
        if (isSelected) {
            setBackground(new Color(19, 91, 236)); // Blue
            parentFrame.addSelectedSeat(this);
        } else {
            setBackground(new Color(59, 67, 84)); // Gray
            parentFrame.removeSelectedSeat(this);
        }
        
        parentFrame.updateBookingInfo();
    }
    
    public String getSeatLabel() {
        return seatLabel;
    }
}
```

### الشرح:

**السطر 7: Extends JButton**
- `extends JButton`: الـ class بيورث من JButton.

**السطر 8-11: Fields**
- `seatLabel`: تسمية المقعد (مثل "A1").
- `isOccupied`: هل المقعد محجوز.
- `isSelected`: هل المقعد مختار من المستخدم.
- `parentFrame`: الـ frame اللي فيه الزر (BookTicket).

**السطر 25-29: If Occupied**
- لو المقعد محجوز:
  - لون أحمر.
  - `setEnabled(false)`: مش ممكن يضغط عليه.

**السطر 30-33: If Available**
- لو المقعد متاح:
  - لون رمادي.

**السطر 37-50: toggleSeat() Method**
- لما المستخدم يضغط على المقعد:
  - لو محجوز، مش بنعمل حاجة.
  - لو متاح، بنغير حالته (مختار/مش مختار).
  - لو اختاره، لونه بقي أزرق.
  - بنضيفه أو نشيله من قائمة المقاعد المختارة.
  - بنحدث معلومات الحجز.

---

## 13. AddMovieDialog.java - نافذة إضافة فيلم

```java
package gui;

public class AddMovieDialog extends JDialog {
    private JTextField titleField;
    private JTextField genreField;
    private JTextField durationField;
    private JTextField ratingField;
    private JTextArea descriptionArea;
    private JTextField posterPathField;
    private JButton browseButton;
    private boolean movieAdded = false;
    // ...
}
```

### الشرح:

- `extends JDialog`: نافذة dialog (مش frame كامل).
- `modal = true`: يعني لما تفتح، المستخدم مش يقدر يضغط على النوافذ التانية.

### browsePosterImage() Method (السطر 177-193):

```java
private void browsePosterImage() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Poster Image");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
    
    // Set initial directory to assets/posters
    File postersDir = new File("assets/posters");
    if (postersDir.exists() && postersDir.isDirectory()) {
        fileChooser.setCurrentDirectory(postersDir);
    }
    
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        posterPathField.setText(selectedFile.getAbsolutePath());
    }
}
```

**الشرح:**
- `JFileChooser`: نافذة اختيار ملف.
- `setFileFilter(...)`: بنحدد إننا عايزين صور بس.
- `showOpenDialog(...)`: بنفتح النافذة.
- لو المستخدم اختار ملف، بنحط مساره في `posterPathField`.

### addMovie() Method (السطر 195-249):

```java
private void addMovie() {
    // Validate inputs
    String title = titleField.getText().trim();
    String genre = genreField.getText().trim();
    String duration = durationField.getText().trim();
    String rating = ratingField.getText().trim();
    String description = descriptionArea.getText().trim();
    String posterPath = posterPathField.getText().trim();
    
    if (title.isEmpty() || genre.isEmpty() || duration.isEmpty() || rating.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please fill in all required fields (*)",
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Validate rating
    try {
        double ratingValue = Double.parseDouble(rating);
        if (ratingValue < 0 || ratingValue > 10) {
            JOptionPane.showMessageDialog(this,
                "Rating must be between 0.0 and 10.0",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Rating must be a valid number",
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Add movie to database
    BookingSystem system = BookingSystem.getInstance();
    boolean added = system.addMovie(title, genre, duration, rating, 
        description.isEmpty() ? "No description available." : description,
        posterPath.isEmpty() ? null : posterPath);
    
    if (added) {
        JOptionPane.showMessageDialog(this,
            "Movie added successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
        movieAdded = true;
        dispose();
    } else {
        JOptionPane.showMessageDialog(this,
            "Failed to add movie. Please try again.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
```

**الـ Validations:**
1. كل الحقول المطلوبة لازم تكون مملوءة.
2. التقييم لازم يكون رقم بين 0 و 10.

---

## 14. ImportMovies.java - استيراد الأفلام

```java
import core.BookingSystem;
import java.io.File;

public class ImportMovies {
    public static void main(String[] args) {
        System.out.println("=== Importing Movies from Posters Folder ===\n");
        
        BookingSystem system = BookingSystem.getInstance();
        File postersDir = new File("assets/posters");
        
        // ... نفس فكرة importMoviesFromPostersFolder() في BookingFrame
    }
}
```

### الشرح:

- ملف منفصل عشان يستورد الأفلام من مجلد `assets/posters`.
- ممكن تشغله منفصل عشان تملأ قاعدة البيانات.

**كيف تشغله:**
```bash
java -cp "bin:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" ImportMovies
```

---

## 15. ملفات الاختبار (Test Files)

### QuickTest.java

- ملف اختبار سريع بيختبر:
  - التسجيل
  - تسجيل الدخول
  - إضافة فيلم
  - عمل حجز
  - التحقق من حالة المقاعد

### TestDatabase.java

- بيختبر كل العمليات على قاعدة البيانات:
  - User operations
  - Movie operations
  - Booking operations

### TestSeats.java

- بيختبر المقاعد المحجوزة لفيلم معين.

---

## 16. ملفات التشغيل (Run Scripts)

### run.sh (Linux/Mac)

```bash
#!/bin/bash
# Script to run Movie Ticket Booking System

echo "🎬 Starting Movie Ticket Booking System..."
echo ""

# Check if compiled
if [ ! -d "bin" ] || [ ! -f "bin/Main.class" ]; then
    echo "⚙️ Compiling project..."
    javac -cp ".:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" -d bin -sourcepath src src/database/*.java src/model/*.java src/core/*.java src/gui/*.java src/Main.java
    echo "✅ Compilation complete!"
    echo ""
fi

# Run the application
echo "🚀 Launching application..."
java -cp "bin:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" Main

echo ""
echo "👋 Application closed."
```

**الشرح:**
- `#!/bin/bash`: shebang عشان النظام يعرف إنه bash script.
- `if [ ! -d "bin" ]`: بنتحقق لو مجلد `bin` مش موجود.
- `javac ...`: بنكمل المشروع.
- `java ...`: بنشغل البرنامج.

### run.bat (Windows)

- نفس الفكرة، بس بـ Windows batch syntax.

---

## 17. Dependencies (المكتبات المطلوبة)

المشروع بيستخدم 3 مكتبات:

1. **sqlite-jdbc.jar**: JDBC driver لـ SQLite.
2. **slf4j-api.jar**: Simple Logging Facade for Java (API).
3. **slf4j-simple.jar**: Implementation بسيط لـ SLF4J.

**كيف تحملهم:**
- ممكن تحملهم من Maven Central أو من مواقع المكتبات.

---

## 18. Assets (الموارد)

### assets/posters/

- مجلد فيه صور البوسترات للأفلام (.jpeg, .jpg, .png).

### assets/login_background.png و register_background.png

- صور خلفية (لو كانت مستخدمة في الكود).

### assets/SplineSans-Regular.ttf

- ملف الخط المستخدم في الواجهة.

---

## 19. ملاحظات مهمة وتحسينات مقترحة

### الأمان (Security):

1. **كلمات المرور**: حالياً مخزنة plain text. لازم تتشفير (مثلاً باستخدام BCrypt أو SHA-256).
2. **SQL Injection**: الكود محمي باستخدام PreparedStatement، لكن لازم نتأكد إن كل الـ queries بتستخدم parameters.
3. **Input Validation**: في بعض الأماكن ممكن نضيف validation أكتر (مثلاً email format validation أقوى).

### الأداء (Performance):

1. **Connection Pooling**: ممكن نستخدم connection pooling عشان نتحكم في عدد الـ connections.
2. **Image Caching**: ممكن نخزن الصور في memory عشان ما نحملش نفس الصورة أكتر من مرة.
3. **Lazy Loading**: ممكن نحمل الأفلام على batches عشان ما نحملش كل حاجة مرة واحدة.

### UX/UI:

1. **Loading Indicators**: ممكن نضيف loading spinners لما البيانات تتحمل.
2. **Error Messages**: ممكن نحسن رسائل الخطأ عشان تكون أوضح.
3. **Responsive Design**: الواجهة حالياً fixed size. ممكن نعملها responsive.

### الكود:

1. **Exception Handling**: في بعض الأماكن بنستخدم `e.printStackTrace()` بس. ممكن نستخدم logging library (مثل Log4j).
2. **Code Duplication**: في بعض الأماكن في كود مكرر (مثلاً في `BookingFrame.refreshMoviesDisplay()`). ممكن نعمل helper methods.
3. **Constants**: بعض القيم hardcoded (مثل الألوان). ممكن نعمل constants class.

---

## 20. كيفية تشغيل المشروع

### المتطلبات (Prerequisites):

- **JDK 8 أو أحدث**: عشان نستخدم lambda expressions و try-with-resources.
- **SQLite JDBC Driver**: موجود في `sqlite-jdbc.jar`.
- **SLF4J Libraries**: موجودة في `slf4j-api.jar` و `slf4j-simple.jar`.

### خطوات التشغيل:

1. **تأكد إن كل المكتبات موجودة:**
   - `sqlite-jdbc.jar`
   - `slf4j-api.jar`
   - `slf4j-simple.jar`

2. **شغل الـ script:**
   ```bash
   # Linux/Mac
   chmod +x run.sh
   ./run.sh
   
   # Windows
   run.bat
   ```

3. **أو يدوياً:**
   ```bash
   # Compile
   javac -cp ".:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" -d bin -sourcepath src src/database/*.java src/model/*.java src/core/*.java src/gui/*.java src/Main.java
   
   # Run
   java -cp "bin:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" Main
   ```

---

## 21. خريطة التدفق (Flow Chart)

```
1. Main.main()
   └──> LoginFrame (شاشة تسجيل الدخول)
        ├──> RegisterFrame (لو المستخدم ضغط "Create account")
        │    └──> LoginFrame (بعد التسجيل الناجح)
        └──> BookingFrame (بعد تسجيل الدخول الناجح)
             ├──> MovieDetailsFrame (لو المستخدم ضغط على فيلم)
             │    └──> BookTicket (لو المستخدم ضغط "Book Tickets")
             │         └──> Booking Confirmed (بعد تأكيد الحجز)
             └──> AddMovieDialog (لو المستخدم ضغط "+ Add Movie")
                  └──> BookingFrame (بعد إضافة الفيلم)
```

---

## 22. قاعدة البيانات (Database Schema)

### جدول users:
- `id` (INTEGER, PRIMARY KEY, AUTOINCREMENT)
- `name` (TEXT, NOT NULL)
- `email` (TEXT, UNIQUE, NOT NULL)
- `username` (TEXT, UNIQUE, NOT NULL)
- `password` (TEXT, NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

### جدول movies:
- `id` (INTEGER, PRIMARY KEY, AUTOINCREMENT)
- `title` (TEXT, NOT NULL)
- `genre` (TEXT, NOT NULL)
- `duration` (TEXT, NOT NULL)
- `rating` (TEXT, NOT NULL)
- `description` (TEXT)
- `poster_path` (TEXT)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

### جدول bookings:
- `id` (INTEGER, PRIMARY KEY, AUTOINCREMENT)
- `user_id` (INTEGER, NOT NULL, FOREIGN KEY → users.id)
- `movie_id` (INTEGER, NOT NULL, FOREIGN KEY → movies.id)
- `seats` (TEXT, NOT NULL)
- `total_price` (REAL, NOT NULL)
- `booking_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

### جدول seats:
- `id` (INTEGER, PRIMARY KEY, AUTOINCREMENT)
- `movie_id` (INTEGER, NOT NULL, FOREIGN KEY → movies.id)
- `seat_label` (TEXT, NOT NULL)
- `is_occupied` (BOOLEAN, DEFAULT 0)
- `booking_id` (INTEGER, FOREIGN KEY → bookings.id)
- `UNIQUE(movie_id, seat_label)`

---

## 23. Design Patterns المستخدمة

### 1. Singleton Pattern:
- `DatabaseManager`: بنستخدم instance واحد بس من قاعدة البيانات.
- `BookingSystem`: بنستخدم instance واحد بس من النظام.

**ليه بنستخدمه؟**
- عشان نضمن إن عندنا connection واحد بس لقاعدة البيانات.
- عشان نمنع مشاكل الـ concurrency.

### 2. Model-View-Controller (MVC):
- **Model**: `User`, `Movie`, `Booking` (البيانات).
- **View**: `LoginFrame`, `BookingFrame`، إلخ (الواجهة).
- **Controller**: `BookingSystem` (المنطق).

### 3. DAO Pattern (Data Access Object):
- الـ Models (`User`, `Movie`, `Booking`) فيها methods للتعامل مع قاعدة البيانات (مثل `save()`, `findByEmail()`).

---

## 24. الخلاصة

المشروع ده نظام حجز تذاكر سينما كامل بـ Java و Swing. بيستخدم:
- **SQLite** كقاعدة بيانات.
- **Swing** للواجهة الرسومية.
- **Singleton Pattern** لإدارة الموارد.
- **MVC Architecture** لتنظيم الكود.

الكود منظم وواضح، لكن في حاجات ممكن تتحسن (مثل تشفير كلمات المرور، تحسين الأداء، إلخ).

---

## 25. أسئلة شائعة (FAQ)

### س: إيه اللي يحدث لو قاعدة البيانات مش موجودة؟
**ج:** SQLite هيعمل الملف تلقائياً لما نحاول نعمل connection.

### س: إيه اللي يحدث لو المستخدم حاول يحجز مقعد محجوز؟
**ج:** المقاعد المحجوزة `disabled` (مش ممكن يضغط عليها).

### س: إزاي أقدر أضيف أفلام جديدة؟
**ج:** ممكن تضيفهم من الواجهة (زر "+ Add Movie") أو تضع صور في `assets/posters` و تشغل `ImportMovies`.

### س: إيه اللي يحدث لو المستخدم حاول يحجز من غير ما يسجل دخول؟
**ج:** النظام هيطلب منه يسجل دخول أولاً.

---

**تم الشرح! 🎉**

لو عندك أي أسئلة أو استفسارات، أنا جاهز! 😊
