package in.ramesh.zoology.earthwormlab;

import static org.junit.Assert.*;
import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OfflineLaunchTest {
    @Test public void nativeLaboratoryLaunchesWithoutWebRuntime() {
        var instrumentation = InstrumentationRegistry.getInstrumentation();
        var context = instrumentation.getTargetContext();

        assertEquals("2.0.0-alpha1", BuildConfig.VERSION_NAME);
        assertTrue(NativeData.SYSTEMS.length >= 6);
        assertNull(NativeData.system("not-a-system"));
        assertNotNull(NativeData.system("external"));

        Intent intent = new Intent(context, MainActivity.class)
            .putExtra(MainActivity.EXTRA_SYSTEM, "digestive")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(intent);
        try {
            assertNotNull(activity);
            assertEquals("in.ramesh.zoology.earthwormlab", context.getPackageName());
        } finally {
            instrumentation.runOnMainSync(activity::finish);
        }
    }
}
