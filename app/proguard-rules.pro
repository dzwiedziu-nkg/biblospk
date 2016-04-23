# Workaround of following FIXMEs
#-dontobfuscate
#-optimizationpasses 5
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontpreverify
#-verbose
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

# EventBus
-keepattributes *Annotation*
#-keepattributes *Subscribe*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# FIXME: so... works? must be testing

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# ActiveAndroid
-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer
#-keepattributes Column
#-keepattributes Table
#-keepclasseswithmembers class * { @com.activeandroid.annotation.Column <fields>; }
# FIXME: java.lang.NoSuchFieldException: No field mId in class La/a/g but so... works? must be testing