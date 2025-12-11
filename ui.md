# خطة شاملة للـ Design Patterns - خطوة بخطوة

هنستخدم **7 باترن بالضبط** في نظام حجز التذاكر، كل واحد ليه دور محدد.

---

## 1. Pattern: Singleton

**ليه محتاجينه؟**

عشان نضمن إن في instance واحد بس من الكلاسات المهمة زي نظام الحجز وإدارة الجلسة.

**الملفات:**

- `src/core/BookingSystem.java` - نظام الحجز المركزي (singleton)
- `src/core/SessionManager.java` - إدارة جلسة المستخدم الحالي (singleton)

**الخطوات:**

1. انشئ package اسمه `core` جوا `src/`
2. اكتب كلاس `BookingSystem` بكونستركتور private وmethod `getInstance()` thread-safe
3. اكتب كلاس `SessionManager` نفس الفكرة - يحفظ بيانات المستخدم اللي عامل login

---

## 2. Pattern: Factory

**ليه محتاجينه؟**

عشان ننشئ objects (أفلام/مواقع) حسب النوع بدون ما نكتب switch/if كتير في الـ GUI.

**الملفات:**

- `src/factory/MovieFactory.java` - ينشئ أفلام حسب النوع (Action, Comedy, Drama)
- `src/factory/TheaterFactory.java` - ينشئ قاعات (CinemaHall, IMAX, VIP)
- `src/models/Movie.java` - كلاس الفيلم (موجود بالفعل في `gui/Movie.java` نقله لـ models)
- `src/models/Theater.java` - كلاس القاعة

**الخطوات:**

1. انشئ package `factory` و package `models`
2. انقل/أكمل `Movie.java` في `models` (id, title, genre, duration, poster)
3. اكتب `Theater.java` (id, name, type, capacity)
4. اكتب `MovieFactory.createMovie(String genre)` يرجع Movie object
5. اكتب `TheaterFactory.createTheater(String type)` يرجع Theater object

---

## 3. Pattern: Builder

**ليه محتاجينه؟**

طلب الحجز معقد: فيه user, movie, theater, seats, showtime, options - بدل ما نعمل constructor طويل، نبني الطلب خطوة بخطوة.

**الملفات:**

- `src/builder/BookingRequest.java` - الكلاس النهائي (immutable)
- `src/builder/BookingRequestBuilder.java` - الـ builder

**الخطوات:**

1. انشئ package `builder`
2. اكتب `BookingRequest` بحقول: userId, movieId, theaterId, seats, showtime, totalPrice (بدون setters)
3. اكتب `BookingRequestBuilder` بـ methods: setUser(), setMovie(), setTheater(), setSeats(), build()
4. الـ `build()` يتحقق إن الحقول المطلوبة موجودة قبل ما يرجع `BookingRequest`

---

## 4. Pattern: Adapter

**ليه محتاجينه؟**

لو عايزين نستخدم payment API خارجي أو stub، نعمل adapter يحول الـ interface بتاعهم للـ interface بتاعنا.

**الملفات:**

- `src/payment/PaymentService.java` - الواجهة الداخلية
- `src/payment/PaymentAdapter.java` - يلف API خارجي
- `src/payment/ExternalPaymentAPI.java` - stub بسيط للـ API الخارجي

**الخطوات:**

1. انشئ package `payment`
2. اكتب interface `PaymentService` بmethod `processPayment(double amount)`
3. اكتب كلاس `ExternalPaymentAPI` له method مختلف مثلاً `charge(int cents)`
4. اكتب `PaymentAdapter implements PaymentService` ويستخدم `ExternalPaymentAPI` جواه

---

## 5. Pattern: Proxy

**ليه محتاجينه؟**

عشان نحمي الوصول للمخزن (inventory) - نضيف cache أو access control أو logging قبل ما نروح للـ database.

**الملفات:**

- `src/inventory/TicketInventoryService.java` - الواجهة
- `src/inventory/TicketInventoryProxy.java` - الـ proxy (يفحص/يكاش)
- `src/inventory/TicketInventoryStore.java` - التنفيذ الحقيقي (SQLite DAO)

**الخطوات:**

1. انشئ package `inventory`
2. اكتب interface `TicketInventoryService` بmethods: `checkAvailability(int showtimeId)`, `reserveSeats(List<String> seats)`
3. اكتب `TicketInventoryStore implements TicketInventoryService` - يتعامل مع SQLite
4. اكتب `TicketInventoryProxy implements TicketInventoryService` يفحص permissions/cache قبل ما يستدعي `TicketInventoryStore`

---

## 6. Pattern: Decorator

**ليه محتاجينه؟**

السعر بيتغير حسب features (3D, IMAX, Recliner) - بدل ما نعمل subclass لكل تركيبة، نضيف decorators.

**الملفات:**

- `src/pricing/Pricing.java` - الواجهة
- `src/pricing/BasePricing.java` - السعر الأساسي
- `src/pricing/PricingDecorator.java` - decorator مجرد
- `src/pricing/ThreeDDecorator.java` - يضيف رسوم 3D
- `src/pricing/ImaxDecorator.java` - يضيف رسوم IMAX
- `src/pricing/ReclinerDecorator.java` - يضيف رسوم Recliner

**الخطوات:**

1. انشئ package `pricing`
2. اكتب interface `Pricing` بmethod `double getPrice()`
3. اكتب `BasePricing implements Pricing` يرجع سعر ثابت (مثلاً 50)
4. اكتب `PricingDecorator implements Pricing` له حقل `Pricing wrapped` وconstructor
5. اكتب decorators (ThreeD, Imax, Recliner) كل واحد يضيف قيمة على `wrapped.getPrice()`

---

## 7. Pattern: Observer

**ليه محتاجينه؟**

لما حجز يحصل، نبلغ UI/notifications/logs - بدل ما كل واحد يسأل، نبلغهم تلقائياً.

**الملفات:**

- `src/events/BookingEventBus.java` - الـ subject
- `src/events/BookingEventListener.java` - الواجهة للمستمعين
- `src/events/UINotificationListener.java` - مستمع يظهر رسالة
- `src/events/LoggingListener.java` - مستمع يسجل في console

**الخطوات:**

1. انشئ package `events`
2. اكتب interface `BookingEventListener` بmethod `onBookingEvent(String eventType, Object data)`
3. اكتب `BookingEventBus` بlist من listeners ومethods: `subscribe(listener)`, `publish(eventType, data)`
4. اكتب مستمعين (UI, Logging) ينفذوا الـ interface

---

## هيكل الملفات النهائي

```
src/
├── Main.java
├── core/
│   ├── BookingSystem.java (Singleton)
│   └── SessionManager.java (Singleton)
├── factory/
│   ├── MovieFactory.java
│   └── TheaterFactory.java
├── models/
│   ├── Movie.java
│   ├── Theater.java
│   └── User.java
├── builder/
│   ├── BookingRequest.java
│   └── BookingRequestBuilder.java
├── payment/
│   ├── PaymentService.java (interface)
│   ├── PaymentAdapter.java
│   └── ExternalPaymentAPI.java (stub)
├── inventory/
│   ├── TicketInventoryService.java (interface)
│   ├── TicketInventoryProxy.java (Proxy)
│   └── TicketInventoryStore.java (real DAO)
├── pricing/
│   ├── Pricing.java (interface)
│   ├── BasePricing.java
│   ├── PricingDecorator.java (abstract)
│   ├── ThreeDDecorator.java
│   ├── ImaxDecorator.java
│   └── ReclinerDecorator.java
├── events/
│   ├── BookingEventBus.java (Observer subject)
│   ├── BookingEventListener.java (interface)
│   ├── UINotificationListener.java
│   └── LoggingListener.java
└── gui/
    ├── LoginFrame.java (existing)
    ├── RegisterFrame.java (existing)
    └── BookingFrame.java (جديد - شاشة الحجز)
```

---

## خطوات التنفيذ (بالترتيب)

### المرحلة 1: إعداد الـ Models والـ Core (Singleton)

1. أنشئ الـ packages: `core`, `models`
2. اكتب `User.java`, `Movie.java`, `Theater.java` بحقولهم الأساسية
3. اكتب `SessionManager.java` (singleton) - يحفظ الـ User الحالي
4. اكتب `BookingSystem.java` (singleton) - له method `createBooking(BookingRequest req)`

### المرحلة 2: Factory Pattern

1. أنشئ package `factory`
2. اكتب `MovieFactory` و `TheaterFactory`
3. اختبرهم من `Main.java` (طبع objects)

### المرحلة 3: Builder Pattern

1. أنشئ package `builder`
2. اكتب `BookingRequest` و `BookingRequestBuilder`
3. اختبرهم: ابني طلب وطبعه

### المرحلة 4: Adapter Pattern

1. أنشئ package `payment`
2. اكتب الـ interface والـ stub والـ adapter
3. اختبر: استدعي `PaymentAdapter.processPayment(100)`

### المرحلة 5: Proxy Pattern

1. أنشئ package `inventory`
2. اكتب الـ interface والـ proxy والـ store (stub بسيط بدون SQLite أول حاجة)
3. اختبر: استدعي `proxy.checkAvailability(...)`

### المرحلة 6: Decorator Pattern

1. أنشئ package `pricing`
2. اكتب الـ interface والـ base والـ decorators
3. اختبر: `new ImaxDecorator(new ThreeDDecorator(new BasePricing())).getPrice()`

### المرحلة 7: Observer Pattern

1. أنشئ package `events`
2. اكتب الـ event bus والـ listeners
3. اختبر: سجّل listeners وانشر event

### المرحلة 8: ربط الـ GUI

1. اكتب `BookingFrame.java` - شاشة اختيار فيلم/قاعة/مقاعد
2. من `LoginFrame` بعد login ناجح، افتح `BookingFrame`
3. في `BookingFrame`:

   - استخدم `MovieFactory` لعرض الأفلام
   - استخدم `TheaterFactory` لعرض القاعات
   - استخدم `BookingRequestBuilder` لبناء الطلب
   - استخدم `BookingSystem.getInstance().createBooking(...)`
   - استخدم `PaymentAdapter` للدفع
   - استخدم `TicketInventoryProxy` للتحقق من المقاعد
   - استخدم decorators للسعر النهائي
   - انشر events عبر `BookingEventBus`

### المرحلة 9: إضافة SQLite

1. أضف JDBC driver لـ SQLite
2. أكمل `TicketInventoryStore` ليتعامل مع database حقيقي
3. أنشئ جداول: users, movies, theaters, showtimes, bookings

---

## ملاحظات مهمة للمبتدئين

- **ابدأ بسيط:** كل pattern اكتبه واختبره لوحده في `Main.java` قبل ما تربطه بالـ GUI
- **استخدم comments:** اشرح كل method ليه موجود
- **لا تستعجل:** افهم كل pattern قبل ما تنتقل للي بعده
- **اختبر أول بأول:** متستناش لحد النهاية، كل ما تخلص pattern اطبع output تتأكد إنه شغال
- **SQLite آخر حاجة:** ابدأ بـ stub data (ArrayList) وبعدين حول للـ database