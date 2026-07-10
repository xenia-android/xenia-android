# Keep Xenia JNI bridge classes - native code references these by name
-keep class com.xenia.android.XeniaRuntimeException { *; }
-keep class com.xenia.android.emulator.WindowedAppActivity { *; }
-keep class com.xenia.android.emulator.WindowedAppActivity$* { *; }
-keep class com.xenia.android.emulator.WindowSurfaceView { *; }
-keep class com.xenia.android.emulator.EmulatorActivity { *; }
-keep class com.xenia.android.emulator.GpuTraceViewerActivity { *; }

# Keep native method signatures
-keepclasseswithmembernames class * {
    native <methods>;
}

# AndroidX
-dontwarn androidx.**

# General Android rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
