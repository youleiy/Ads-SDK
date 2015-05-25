-injars bin\altamobads-sdk.jar
-outjars 'C:\Users\admin\Desktop'

-libraryjars libs\android-support-v4.jar
-libraryjars libs\AudienceNetwork.jar
-libraryjars 'C:\hyf\SDK\platforms\android-19\android.jar'

-dontshrink
-dontusemixedcaseclassnames
-dontpreverify
-verbose


-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class com.altamob.ads.AltaAdSize

-keep,allowshrinking class com.altamob.ads.AdError {
    <fields>;
    <methods>;
}

-keep,allowshrinking interface  com.altamob.ads.AltaAdListener {
    <methods>;
}

-keep,allowshrinking enum  com.altamob.ads.AltaAdSize {
    <fields>;
    <methods>;
}

-keep,allowshrinking public class com.altamob.ads.AltaAdView {
    <methods>;
}

-keep,allowshrinking class com.altamob.ads.AltaInterstitialAd {
    <methods>;
}

-keep,allowshrinking interface  com.altamob.ads.AltaInterstitialAdListener {
    *** onInterstitialDisplayed(...);
    *** onInterstitialDismissed(...);
}

-keep,allowshrinking class com.altamob.ads.AltaNativeAd {
    <methods>;
}

-keep,allowshrinking class com.altamob.ads.NativeAd {
    public <methods>;
}

-keep,allowshrinking class com.altamob.ads.AltaImage {
    public <methods>;
}

-keep,allowshrinking class com.altamob.ads.AltaNativeAdListener {
    public <methods>;
}

-keep,allowshrinking class com.altamob.ads.x {
    public <methods>;
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Serialization code. Keep all fields and methods that are used for
# serialization.
-keepclassmembers class * extends java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

# Keep names - _class method names. Keep all .class method names. This may be
# useful for libraries that will be obfuscated again with different obfuscators.
-keepclassmembers,allowshrinking class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String,boolean);
}
