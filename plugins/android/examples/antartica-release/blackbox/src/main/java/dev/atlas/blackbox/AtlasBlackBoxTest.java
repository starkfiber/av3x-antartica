package dev.atlas.blackbox;

import android.content.Intent;
import android.graphics.Rect;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.*;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AtlasBlackBoxTest {
    private UiDevice device;
    /** Search only the app's scrollable content; never swipe system bars or loop indefinitely. */
    private UiObject2 visible(BySelector selector, String description) {
        UiObject2 node = device.wait(Until.findObject(selector), 1500);
        if (node != null) return node;
        // A previous assertion can leave the list below the search field.
        for (Direction direction : new Direction[] { Direction.UP, Direction.DOWN }) {
            for (int attempt = 0; attempt < 12; attempt++) {
                UiObject2 content = device.findObject(By.pkg("dev.atlas.demo").scrollable(true));
                if (content == null) break;
                nativeScroll(content, direction, 0.70f);
                device.waitForIdle();
                node = device.findObject(selector);
                if (node != null) return node;
            }
        }
        fail("Not reachable within bounded content scroll: " + description);
        return null;
    }
    private void nativeScroll(UiObject2 content, Direction direction, float fraction) {
        Rect viewport = content.getVisibleBounds();
        int margin = Math.max(16, viewport.height() / 8);
        int center = viewport.centerY();
        int distance = Math.min((int) (viewport.height() * fraction), viewport.height() - margin * 2);
        int top = center - distance / 2;
        int bottom = center + distance / 2;
        assertTrue("Swipe must stay inside content", distance > 0 && top > viewport.top && bottom < viewport.bottom);
        int from = direction == Direction.DOWN ? bottom : top;
        int to = direction == Direction.DOWN ? top : bottom;
        try {
            device.executeShellCommand("input swipe " + viewport.centerX() + " " + from + " "
                + viewport.centerX() + " " + to + " 350");
        } catch (java.io.IOException failure) {
            throw new AssertionError("Owned emulator content swipe failed", failure);
        }
        device.waitForIdle();
    }
    private UiObject2 text(String value) {
        return visible(By.pkg("dev.atlas.demo").textContains(value), value);
    }
    private void tapDiagnostic(String stage, Rect viewport, Rect bounds) {
        android.os.Bundle report = new android.os.Bundle();
        String summary = stage + " display=" + device.getDisplayWidth() + "x" + device.getDisplayHeight()
            + " rotation=" + device.getDisplayRotation() + " viewport=" + viewport.toShortString()
            + " title=" + bounds.toShortString() + " tap=" + bounds.centerX() + "," + bounds.centerY();
        try {
            java.io.File folder = InstrumentationRegistry.getInstrumentation().getContext().getExternalFilesDir("tap-diagnostics");
            if (folder != null) {
                folder.mkdirs();
                java.io.File screenshot = new java.io.File(folder, System.currentTimeMillis() + "-" + stage + ".png");
                boolean captured = device.takeScreenshot(screenshot);
                summary += " screenshot=" + screenshot.getAbsolutePath() + " captured=" + captured;
            } else {
                summary += " screenshot-error=no-external-files-directory";
            }
        } catch (RuntimeException failure) { summary += " screenshot-error=" + failure; }
        try {
            java.io.ByteArrayOutputStream hierarchy = new java.io.ByteArrayOutputStream();
            device.dumpWindowHierarchy(hierarchy);
            summary += " hierarchy=" + hierarchy.toString("UTF-8");
        } catch (java.io.IOException failure) { summary += " hierarchy-error=" + failure; }
        report.putString("stream", "\nATLAS_TAP " + summary + "\n");
        InstrumentationRegistry.getInstrumentation().sendStatus(2, report);
    }
    private void clickTopic(boolean expanding) {
        for (int attempt = 0; attempt < 10; attempt++) {
            UiObject2 title = text("From frame to display");
            UiObject2 content = device.findObject(By.pkg("dev.atlas.demo").scrollable(true));
            assertNotNull("Topic must be inside scrollable content", content);
            Rect viewport = content.getVisibleBounds();
            int margin = Math.max(16, viewport.height() / 8);
            Rect safe = new Rect(viewport);
            safe.inset(margin, margin);
            Rect bounds = title.getVisibleBounds();
            if (bounds.width() > 0 && bounds.height() > 0 && safe.contains(bounds)) {
                tapDiagnostic("before", viewport, bounds);
                assertTrue("Tap inside content viewport", device.click(bounds.centerX(), bounds.centerY()));
                device.waitForIdle();
                device.wait(Until.hasObject(By.pkg("dev.atlas.demo").textContains("Hide lesson")), 1000);
                tapDiagnostic("after-before-any-scroll", viewport, bounds);
                return;
            }
            Direction direction = bounds.centerY() >= safe.centerY() ? Direction.DOWN : Direction.UP;
            nativeScroll(content, direction, 0.25f);
            device.waitForIdle();
        }
        fail("Topic title could not be positioned safely inside content viewport");
    }
    private UiObject2 searchField() {
        return visible(By.pkg("dev.atlas.demo").clazz("android.widget.EditText"), "Search input");
    }
    @Before public void launchFresh() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        assertEquals("This harness is restricted to an emulator", "1",
            device.executeShellCommand("getprop ro.kernel.qemu").trim());
        assertEquals("This harness is restricted to the explicitly named lab AVD", "AV3X_Antartica_QA",
            device.executeShellCommand("getprop ro.boot.qemu.avd_name").trim());
        device.executeShellCommand("am force-stop dev.atlas.demo");
        Intent launch = new Intent().setClassName("dev.atlas.demo", "dev.atlas.demo.MainActivity")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InstrumentationRegistry.getInstrumentation().getContext().startActivity(launch);
        text("5 lessons");
    }
    @Test public void searchEmptyAndClear() {
        searchField().setText("frame");
        text("1 lessons");
        text("From frame to display");
        searchField().setText("zz-no-match-913");
        text("0 lessons");
        text("No matching lesson");
        text("Clear").click();
        text("5 lessons");
        assertEquals("", searchField().getText());
    }
    @Test public void expansionAndCollapse() {
        searchField().setText("frame");
        text("1 lessons");
        clickTopic(true);
        text("Hide lesson");
        clickTopic(false);
        // Only one lesson is present, so this cannot match another collapsed card.
        text("Read lesson");
    }
    @Test public void destinationsExposeUnmeasuredModelAndEvidence() {
        text("Lab").click();
        text("ILLUSTRATIVE");
        text("Evidence").click();
        text("Know what we know.");
        text("Learn").click();
        text("5 lessons");
    }
    @Test public void backgroundProcessDeathRestoresSavedState() throws Exception {
        searchField().setText("frame");
        text("1 lessons");
        clickTopic(true);
        text("Hide lesson");
        device.pressHome();
        assertTrue("Target must leave foreground before kill",
            device.wait(Until.gone(By.pkg("dev.atlas.demo")), 5000));
        device.waitForIdle();
        String before = device.executeShellCommand("pidof dev.atlas.demo").trim();
        assertFalse("A live target process must exist before this test kills it", before.isEmpty());
        device.executeShellCommand("am kill dev.atlas.demo");
        String after = before;
        for (int attempt = 0; attempt < 25 && !after.isEmpty(); attempt++) {
            android.os.SystemClock.sleep(200);
            after = device.executeShellCommand("pidof dev.atlas.demo").trim();
        }
        assertEquals("Background process did not terminate; do not claim process-death coverage", "", after);
        // Preserve the existing task and its saved state. Do not force-stop or clear task here.
        Intent reopen = new Intent().setClassName("dev.atlas.demo", "dev.atlas.demo.MainActivity")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InstrumentationRegistry.getInstrumentation().getContext().startActivity(reopen);
        assertEquals("frame", searchField().getText());
        text("1 lessons");
        text("Hide lesson");
    }

}
