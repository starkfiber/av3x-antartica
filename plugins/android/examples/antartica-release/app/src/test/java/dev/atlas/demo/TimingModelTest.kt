package dev.atlas.demo

import org.junit.Assert.assertEquals
import org.junit.Test

class TimingModelTest {
    @Test fun exactRefreshBoundaryDoesNotWaitAnExtraFrame() {
        assertEquals(20.0, nextPresentationMs(8.0, 12.0, 50), 0.000001)
    }
    @Test fun missingBoundaryWaitsForFollowingRefresh() {
        assertEquals(40.0, nextPresentationMs(8.0, 12.01, 50), 0.000001)
    }
    @Test fun zeroWorkIsReadyAtTimeZero() {
        assertEquals(0.0, nextPresentationMs(0.0, 0.0, 120), 0.000001)
    }
    @Test(expected = IllegalArgumentException::class) fun nonFiniteInputIsRejected() {
        nextPresentationMs(Double.NaN, 1.0, 60)
    }
}
