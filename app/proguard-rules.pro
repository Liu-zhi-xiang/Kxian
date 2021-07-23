# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wanglei/DevTools/Android/SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#
-keep class com.gyf.barlibrary.* {*;}
#talkingdata
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**
#清除编译打包时的警告
-ignorewarnings
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
#日历
-keep class com.gjmetal.app.widget.calendarview.**{*;}
#视频播放
-keep class com.gjmetal.app.widget.MyVideoPlay.**{*;}
#eventBus

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#webview
-keep class android.webkit.JavascriptInterface {*;}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keep public class com.gjmetal.app.ui.ball.**{*;}

#xutils
-dontwarn com.lidroid.xutils.**
-keep class com.lidroid.xutils.**{*;}
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#友盟
-dontwarn com.umeng.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*
-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
-keep public class **.R$*{
   public static final int *;
}
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.gjmetal.app.R$*{
public static final int *;
}
#ShareSdk
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-keep class m.framework.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**
-dontwarn **.R$*
# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontoptimize
#反射需要加入
-keepattributes EnclosingMethod
-keepattributes Signature
#显示错误行数
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-dontwarn android.support.v4.**
-dontwarn me.add1.**
-dontwarn okio.**
-keep class com.squareup.okhttp.**{*;}
-keep class okio.**{*;}

#日期控件
-dontwarn cn.qqtheme.framework.**
-keep class cn.qqtheme.framework.entity.** { *;}
-keep class cn.qqtheme.framework.picker.AddressPicker$* { *;}
#httpclient
#不混淆android-async-http(这里的与你用的httpClient框架决定)
-keep class com.loopj.android.http.**{*;}
#不混淆org.apache.http.legacy.jar
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}
#视频
-keep class cn.jzvd.**{*;}
#拍照
-keep class cn.finalteam.galleryfinal.widget.*{*;}
-keep class cn.finalteam.galleryfinal.widget.crop.*{*;}
-keep class cn.finalteam.galleryfinal.widget.zoonview.*{*;}
#图片预览
-keep class com.stfalcon.frescoimageviewer.*{*;}
-keep class com.facebook.**{*;}

#工具类
-keep class com.blankj.utilcode.** { *; }
-keepclassmembers class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**

#类
-keep class com.gjmetal.app.adapter.**{*;}
-keep class com.gjmetal.app.base.**{*;}
-keep class com.gjmetal.app.model.**{*;}
-keep class com.gjmetal.app.util.GjUtil.**{*;}
-keep class com.gjmetal.app.util.MediaUtils.**{*;}

#butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
#
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.v4.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends BaseFragment
-keep public class * extends Fragment
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }
#Talkingdata
-dontwarn com.tendcloud.tenddata.**
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
#qiniu
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.**
-keep public class java.nio.**
#EventBus3.0
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent{*;}
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
 public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**
-keep class com.ultrapower.** {*;}
-keep class sun.misc.Unsafe{*;}
