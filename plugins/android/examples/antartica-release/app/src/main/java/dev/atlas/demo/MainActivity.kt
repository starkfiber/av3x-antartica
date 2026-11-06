package dev.atlas.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtlasTheme {
                val model: AtlasViewModel = viewModel()
                val state = model.state
                AtlasScreen(state, model::update)
            }
        }
    }
}

enum class Destination(val label: String, val glyph: String) {
    Learn("Learn", "01"), Lab("Lab", "02"), Evidence("Evidence", "03")
}

data class AtlasState(
    val destination: Destination = Destination.Learn,
    val query: String = "",
    val selected: String? = null,
    val cpuMs: Float = 4f,
    val gpuMs: Float = 12f,
    val refreshHz: Int = 60
)

class AtlasViewModel(private val saved: SavedStateHandle) : ViewModel() {
    var state by mutableStateOf(AtlasState(
        destination = Destination.entries.getOrElse(saved["tab"] ?: 0) { Destination.Learn },
        query = saved["query"] ?: "", selected = saved["selected"],
        cpuMs = saved["cpu"] ?: 4f, gpuMs = saved["gpu"] ?: 12f,
        refreshHz = saved["hz"] ?: 60
    ))
        private set
    fun update(value: AtlasState) {
        state = value
        saved["tab"] = value.destination.ordinal
        saved["query"] = value.query
        saved["selected"] = value.selected
        saved["cpu"] = value.cpuMs
        saved["gpu"] = value.gpuMs
        saved["hz"] = value.refreshHz
    }
}

data class Topic(val id: String, val title: String, val level: String, val summary: String, val detail: String, val url: String)
val topics = listOf(
    Topic("gpu", "From frame to display", "Source inspected", "Follow buffers, fences and composition.",
        "Retained Android 17 r1 BufferQueueProducer source shows backpressure when buffers are unavailable. CompositionEngine Output passes a ready fence into queueBuffer. This is source evidence, not a device timing trace. The selected QPR1 image has not been matched to this source.",
        "https://android.googlesource.com/platform/frameworks/native/+/ae266dcb706d083868578cfedce381ef44488a07/libs/gui/BufferQueueProducer.cpp"),
    Topic("art", "ART and CPU scheduling", "Source + documentation", "Compilation and scheduling are different jobs.",
        "ART can interpret or compile managed methods; the kernel schedules runnable threads. A compilation queue entry does not prove optimized code executed. Task profiles request policy, not a guaranteed core or deadline. No runtime performance measurement is included in this demo.",
        "https://source.android.com/docs/core/runtime/jit-compiler"),
    Topic("boot", "Verified boot and trust", "Artifact inspected", "A valid signature still needs a trusted key.",
        "The case checked a selected vbmeta digest and RSA signature against its embedded key. That is not proof of OEM trust or an entire device boot chain. The selected GSI flags cannot be generalized to every Android phone.",
        "https://android.googlesource.com/platform/external/avb/+/master/README.md"),
    Topic("lifecycle", "An activity's lifetime", "Documentation", "State must survive more than one composition.",
        "This app keeps synchronous Compose snapshot state in a ViewModel and stores small values in SavedStateHandle. These are AndroidX contracts. Durable user records would need a separate storage design.",
        "https://developer.android.com/topic/libraries/architecture/viewmodel"),
    Topic("binder", "Binder between processes", "Source + documentation", "A reply and successful work are different evidence.",
        "Synchronous calls wait for replies; one-way calls do not wait for application work to finish. Threads can block on dependencies even when CPU cores are available. This demo does not trace a live Binder transaction.",
        "https://source.android.com/docs/core/architecture/ipc/binder-threading")
)

private val topicSearchText = topics.associate { it.id to "${it.title} ${it.summary} ${it.detail}" }

@Composable
fun AtlasTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(
        primary = Color(0xFF225C45), onPrimary = Color.White,
        primaryContainer = Color(0xFFD8EBDD), onPrimaryContainer = Color(0xFF102F23),
        background = Color(0xFFF8F8F2), surface = Color(0xFFF8F8F2)
    ), content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtlasScreen(state: AtlasState, update: (AtlasState) -> Unit) {
    val uri = LocalUriHandler.current
    val feedback = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val learnList = rememberLazyListState()
    val labList = rememberLazyListState()
    val evidenceList = rememberLazyListState()
    val listState = when (state.destination) {
        Destination.Learn -> learnList
        Destination.Lab -> labList
        Destination.Evidence -> evidenceList
    }
    val normalizedQuery = state.query.trim()
    val matches = remember(normalizedQuery) {
        topics.filter { topicSearchText.getValue(it.id).contains(normalizedQuery, ignoreCase = true) }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(feedback) },
        topBar = { TopAppBar(title = { Text("Android Atlas") }) },
        bottomBar = {
            NavigationBar {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(selected = state.destination == destination,
                        onClick = { update(state.copy(destination = destination)) },
                        icon = { Text(destination.glyph, modifier = Modifier.clearAndSetSemantics {}) },
                        label = { Text(destination.label) })
                }
            }
        }
    ) { padding ->
        LazyColumn(state = listState, modifier = Modifier.testTag("atlas-list").fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(),
            contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item(key = "${state.destination.name}-header") {
                Text("A LITTLE DEEPER, EVERY DAY", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text(when (state.destination) {
                    Destination.Learn -> "Understand the layers."
                    Destination.Lab -> "Explore a frame."
                    Destination.Evidence -> "Know what we know."
                }, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.semantics { heading() })
                Text("Native AndroidX demo · five curated lessons", style = MaterialTheme.typography.bodyMedium)
            }
            when (state.destination) {
                Destination.Learn -> {
                    item(key = "learn-search") {
                        OutlinedTextField(value = state.query, onValueChange = { update(state.copy(query = it)) },
                            label = { Text("Search lessons") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { if (state.query.isNotEmpty()) TextButton(onClick = { update(state.copy(query = "")) }) { Text("Clear") } })
                    }
                    item(key = "learn-count") { Text("${matches.size} lessons", style = MaterialTheme.typography.labelLarge) }
                    if (matches.isEmpty()) item(key = "learn-empty") {
                        Text("No matching lesson in this small demo. Try “frame”, “boot”, or “ART”.")
                    }
                    items(matches, key = { it.id }) { topic ->
                        ElevatedCard(onClick = { update(state.copy(selected = if (state.selected == topic.id) null else topic.id)) }, modifier = Modifier.fillMaxWidth().semantics {
                            stateDescription = if (state.selected == topic.id) "Expanded" else "Collapsed"
                        }) {
                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(topic.level, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(topic.title, style = MaterialTheme.typography.titleLarge)
                                Text(topic.summary)
                                Text(if (state.selected == topic.id) "Hide lesson" else "Read lesson", style = MaterialTheme.typography.labelLarge)
                                if (state.selected == topic.id) {
                                    Text(topic.detail)
                                    TextButton(onClick = {
                                        openSourceSafely(topic.url, uri::openUri)?.let { message ->
                                            scope.launch { feedback.showSnackbar(message) }
                                        }
                                    }) { Text("Open source in browser") }
                                }
                            }
                        }
                    }
                }
                Destination.Lab -> {
                    item { Text("ILLUSTRATIVE · NOT MEASURED", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
                    item { Text("One frame starts at time zero. CPU work finishes before GPU work begins. We then wait for the next refresh boundary. This simplified model omits compositor cost, scanout, input sampling and real queue limits.") }
                    item { TimingSlider("CPU work", state.cpuMs) { update(state.copy(cpuMs = it)) } }
                    item { TimingSlider("GPU work", state.gpuMs) { update(state.copy(gpuMs = it)) } }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf(60, 120).forEach { hz -> FilterChip(selected = state.refreshHz == hz,
                                onClick = { update(state.copy(refreshHz = hz)) }, label = { Text("$hz Hz") }) }
                        }
                    }
                    item {
                        val interval = 1000.0 / state.refreshHz
                        val ready = (state.cpuMs + state.gpuMs).toDouble()
                        val present = nextPresentationMs(state.cpuMs.toDouble(), state.gpuMs.toDouble(), state.refreshHz)
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Model result", style = MaterialTheme.typography.titleLarge)
                                Text("GPU ready: ${decimal(ready)} ms")
                                Text("Next presentation boundary: ${decimal(present)} ms")
                                Text("Refresh interval: ${decimal(interval)} ms")
                                Text("These numbers cannot rank Android against iOS or predict your phone's speed.")
                            }
                        }
                    }
                }
                Destination.Evidence -> {
                    item { Text("A source observation is not a benchmark. This app embeds a small teaching dataset; it is not connected to the full Atlas plugin or REA.") }
                    items(listOf(
                        "Inspected" to "Selected pinned source and static artifacts in the research case. Android source is 17 r1; equivalence to the QPR1 image is unverified.",
                        "Documented" to "Official API and architecture references. Opening a source link leaves this app and uses your browser.",
                        "Derived" to "The Lab's arithmetic uses your chosen values. It does not sample the CPU, GPU or display.",
                        "Still unknown" to "Device performance, vendor driver behavior, runtime frame pacing and matched Android/iOS workloads."
                    ), key = { it.first }) { (title, body) ->
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                                Text(body)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun decimal(value: Double) = String.format(Locale.getDefault(), "%.2f", value)

@Composable
private fun TimingSlider(label: String, value: Float, change: (Float) -> Unit) {
    Column {
        Text("$label: ${value.roundToInt()} ms", style = MaterialTheme.typography.titleMedium)
        Slider(value = value, onValueChange = { change(it.roundToInt().toFloat()) },
            valueRange = 1f..24f, steps = 22,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "$label in milliseconds" })
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LearnPreview() { AtlasTheme { AtlasScreen(AtlasState(), {}) } }
