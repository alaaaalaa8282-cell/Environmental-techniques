# الأدوات البيئية (EcoTools)

تطبيق Android بسيط بيغلف 3 أدوات ويب (كل واحدة ملف HTML مستقل) داخل WebView:

1. مولد تقارير الأثر البيئي (EIS Generator)
2. التدقيق البيئي (Environmental Audit)
3. حلال المشكلات البيئية (Problem Solver Expert)

## طريقة الرفع والبناء على GitHub

1. اعمل Repository جديد على GitHub (أو استخدم واحد موجود).
2. ارفع كل محتويات هذا الملف المضغوط كما هي (حافظ على البنية والمجلدات).
3. روح لتبويب **Actions** في الريبو — الـ workflow `Build APK` هيشتغل تلقائيًا مع أول push على فرع `main` (أو تقدر تشغّله يدويًا من "Run workflow").
4. لما يخلص البناء (تقريبًا 2-4 دقايق)، هتلاقي ملف APK جاهز تحت **Artifacts** في نفس الـ run باسم `EcoTools-debug-apk` — نزّله وثبّته على أي جهاز أندرويد.

## تعديل أو إضافة أدوات جديدة

- ملفات HTML الأدوات موجودة في: `app/src/main/assets/`
- الأزرار في الشاشة الرئيسية في: `app/src/main/res/layout/activity_main.xml` و `app/src/main/java/com/abumohamed/ecotools/MainActivity.java`
- لإضافة أداة جديدة: حط ملف الـ HTML في مجلد `assets`، وضيف زرار جديد في `activity_main.xml` وربطه بـ `openTool("filename.html")` في `MainActivity.java`.

## توقيع نسخة Release (اختياري)

الـ workflow الحالي بيبني نسخة Debug بس عشان تشتغل من غير أي إعدادات إضافية. لو احتجت نسخة Release موقّعة تُنشر على المتجر، قولي وأجهزلك خطوة التوقيع بالـ Keystore عبر GitHub Secrets زي باقي تطبيقاتك.. 
