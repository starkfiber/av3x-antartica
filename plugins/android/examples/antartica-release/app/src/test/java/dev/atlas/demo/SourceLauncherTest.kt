package dev.atlas.demo

import org.junit.Assert.*
import org.junit.Test

class SourceLauncherTest {
    @Test fun validSourceIsForwarded() {
        var opened: String? = null
        val source = "https://developer.android.com/guide"
        assertNull(openSourceSafely(source) { opened = it })
        assertEquals(source, opened)
    }
    @Test fun invalidSourcesNeverLaunch() {
        listOf("bad address", "file:///etc/passwd", "https://").forEach {
            assertEquals("This source address is invalid.", openSourceSafely(it) { fail("Must not launch") })
        }
    }
    @Test fun missingHandlerHasVisibleFeedback() {
        val result = openSourceSafely("https://developer.android.com") { throw IllegalStateException("No activity") }
        assertTrue(result!!.contains("No browser could open"))
    }
}
