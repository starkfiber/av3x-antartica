# Instrumentation runner uses this shared library, which target-only R8 otherwise removes.
# Benchmark-only harness accommodation; production release rules are unchanged.
-keep class androidx.tracing.** { *; }
