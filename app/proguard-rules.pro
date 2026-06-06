# Keep data classes for Gson
-keepclassmembers class com.tomplayer.app.data.model.** { *; }
-keepclassmembers class com.tomplayer.app.data.parser.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Media3
-dontwarn androidx.media3.**

# Keep Coil
-dontwarn coil.**

# Keep Room
-keep class * extends androidx.room.RoomDatabase
