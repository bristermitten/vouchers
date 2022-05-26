-libraryjars <java.home>/jmods/java.sql.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.logging.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.naming.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.management.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.compiler.jmod(!**.jar;!module-info.class)


-dontwarn com.mojang.**
-dontwarn io.netty.**
-dontwarn com.destroystokyo.paper.**
-dontwarn org.bukkit.**
-dontwarn co.aikar.timings.**
-dontwarn org.jetbrains.annotations.**
-dontwarn org.intellij.lang.annotations.**
-dontwarn me.clip.placeholderapi.**
-dontwarn com.viaversion.viaversion.**
-dontwarn com.google.gson.**
-dontwarn **hikari.metrics**
-dontwarn io.micrometer.core.instrument.MeterRegistry
-dontwarn com.codahale.metrics.**
-dontwarn org.aopalliance.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn **slf4j**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.MethodHandle

-dontnote com.google.**
-dontnote org.checkerframework.**

-keep class com.codahale.metrics.**
-keep class sun.misc.Unsafe { *; }

# Guice
-keep class me.bristermitten.vouchers.com.google.inject.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }
-keep class me.bristermitten.vouchers.co.aikar.commands.annotation.** { *; }
-keepclassmembers class * {
    @me.bristermitten.vouchers.com.google.inject.Provides *;
    @me.bristermitten.vouchers.com.google.inject.Inject *;
    @me.bristermitten.vouchers.co.aikar.commands.annotation.* *;
    @javax.inject.* *;
}

-keepclassmembers class * implements java.lang.annotation.Annotation {
    ** *();
}

-keep public enum me.bristermitten.vouchers.**{
    *;
}

-dontobfuscate
-dontoptimize

-keep,allowoptimization class * extends org.bukkit.plugin.java.JavaPlugin { *; }

-keep class * extends org.bukkit.event.Listener {
    @org.bukkit.event.EventHandler <methods>;
}


-adaptresourcefilecontents **.yml,META-INF/MANIFEST.MF

-keepattributes *
