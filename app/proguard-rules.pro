# 基础设置 不混淆，方便复现
-dontobfuscate
-dontoptimize
-keepattributes SourceFile,LineNumberTable,*Annotation*

# Compose / Kotlin / 协程
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Room / Lifecycle / DataStore / CameraX
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-dontwarn androidx.room.**

-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# 其他基础库
-dontwarn javax.annotation.**
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn sun.security.**

# Retrofit OkHttp Gson
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.google.gson.**

# 模块
-keep class com.hfur.schedule.** { *; }
-keep class com.xah.shared.** { *; }
-keep class com.xah.transition.** { *; }
-keep class com.xah.uicommon.** { *; }
