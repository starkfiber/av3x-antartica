package dev.atlas.demo

/** Ideal serial CPU/GPU frame; not a measurement or a real presentation scheduler. */
internal fun nextPresentationMs(cpuMs: Double, gpuMs: Double, refreshHz: Int): Double {
    require(cpuMs.isFinite() && gpuMs.isFinite() && cpuMs >= 0 && gpuMs >= 0 && refreshHz > 0)
    val interval = 1000.0 / refreshHz
    return kotlin.math.ceil((cpuMs + gpuMs) / interval) * interval
}
