package in.ramesh.zoology.earthwormlab;
import static org.junit.Assert.*;
import android.content.Intent;
import android.webkit.WebView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OfflineLaunchTest {
    @Test public void bundledLessonLoadsWithRestrictedSettings() throws Exception {
        var instrumentation=InstrumentationRegistry.getInstrumentation();
        Intent intent=new Intent(instrumentation.getTargetContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity activity=(MainActivity)instrumentation.startActivitySync(intent);
        try {
            String result="";
            for(int i=0;i<30;i++) {
                CountDownLatch latch=new CountDownLatch(1);AtomicReference<String> value=new AtomicReference<>();
                instrumentation.runOnMainSync(()->{
                    WebView view=activity.webViewForTest();assertNotNull(view);
                    assertFalse(view.getSettings().getAllowFileAccess());
                    assertFalse(view.getSettings().getAllowContentAccess());
                    view.evaluateJavascript("JSON.stringify({ready:!!window.EarthwormApp,version:window.EarthwormApp?.version,systems:document.querySelectorAll('#systemSelect option').length})",s->{value.set(s);latch.countDown();});
                });
                assertTrue(latch.await(2,TimeUnit.SECONDS));result=value.get();
                if(result!=null&&result.contains(BuildConfig.VERSION_NAME))break;
                Thread.sleep(200);
            }
            assertTrue(result,result.contains(BuildConfig.VERSION_NAME));
            JSONObject payload = new JSONObject((String)new JSONTokener(result).nextValue());
            assertTrue(payload.getBoolean("ready"));
            assertEquals(9,payload.getInt("systems"));
            instrumentation.runOnMainSync(()->assertEquals(MainActivity.START_URL,activity.webViewForTest().getUrl()));
        } finally { instrumentation.runOnMainSync(activity::finish); }
    }
}
