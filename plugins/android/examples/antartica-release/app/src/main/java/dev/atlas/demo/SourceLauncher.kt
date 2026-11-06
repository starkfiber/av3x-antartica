package dev.atlas.demo

import java.net.URI

/** Returns visible feedback on failure; the injected launcher keeps tests offline. */
internal fun openSourceSafely(url: String, launch: (String) -> Unit): String? {
    val source = try { URI(url) } catch (_: Exception) { null }
    if (source == null || source.scheme !in setOf("https", "http") || source.host.isNullOrBlank()) {
        return "This source address is invalid."
    }
    return try {
        launch(url)
        null
    } catch (_: RuntimeException) {
        "No browser could open this source. Please try again after enabling a browser."
    }
}
