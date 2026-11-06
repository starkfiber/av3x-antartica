# Keep the SavedStateHandle constructor used by the lifecycle factory.
-keepclassmembers class dev.atlas.demo.AtlasViewModel { public <init>(androidx.lifecycle.SavedStateHandle); }
