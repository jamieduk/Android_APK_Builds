# Add project specific ProGuard rules here.
# Keep Room entities
-keep class com.fitrack.app.data.** { *; }

# Keep Retrofit models
-keep class com.fitrack.app.model.** { *; }

# Keep generic signature of Room
-keepattributes Signature
-keepattributes *Annotation*
