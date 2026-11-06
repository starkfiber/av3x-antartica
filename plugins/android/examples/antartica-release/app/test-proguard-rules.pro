# Error Prone metadata references a javac-only enum, never used by Android tests.
# Scoped to the test APK; app R8 missing-class diagnostics remain intact.
-dontwarn javax.lang.model.element.Modifier
