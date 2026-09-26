package in.ramesh.zoology.earthwormlab;

import static org.junit.Assert.*;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class OfflineLaunchTest {

    private Context context(){
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test public void nativeRuntimeHasExpectedIdentityAndNoInternetPermission() throws Exception {
        Context context=context();

        assertEquals("2.0.0-alpha2",BuildConfig.VERSION_NAME);
        assertEquals("in.ramesh.zoology.earthwormlab",context.getPackageName());

        PackageInfo info=context.getPackageManager().getPackageInfo(
            context.getPackageName(),
            PackageManager.GET_PERMISSIONS);

        if(info.requestedPermissions!=null){
            for(String permission:info.requestedPermissions){
                assertNotEquals(
                    "Native offline app must not request INTERNET",
                    Manifest.permission.INTERNET,
                    permission);
            }
        }
    }

    @Test public void authoritativeAcademicInventoryLoadsAtRuntime(){
        ContentRepository repo=new ContentRepository(context());

        assertEquals(9,repo.systemIds().size());

        int structures=0;
        Set<String> ids=new HashSet<>();
        for(String system:repo.systemIds()){
            for(ContentRepository.Structure s:repo.structures(system)){
                structures++;
                assertTrue("Duplicate structure id: "+s.id,ids.add(s.id));
                assertFalse("Missing English structure label: "+s.id,s.en.trim().isEmpty());
                assertFalse("Missing Tamil structure label: "+s.id,s.ta.trim().isEmpty());
            }
        }

        assertEquals(55,structures);
        assertEquals(55,ids.size());
        assertEquals(56,repo.guidedActions().size());
        assertEquals(72,repo.questions().size());
        assertEquals(9,repo.microscopy().size());

        JSONObject counts=repo.counts();
        assertEquals(55,counts.optInt("structures"));
        assertEquals(56,counts.optInt("guidedActions"));
        assertEquals(72,counts.optInt("questions"));
        assertEquals(9,counts.optInt("microscopicLessons"));
    }

    @Test public void nativeVisualHotspotsExactlyMatchAcademicStructures(){
        ContentRepository repo=new ContentRepository(context());
        Set<String> academic=new HashSet<>();
        for(String system:repo.systemIds()){
            for(ContentRepository.Structure s:repo.structures(system)) academic.add(s.id);
        }

        Set<String> visual=new HashSet<>();
        int hotspotCount=0;
        for(NativeData.SystemRecord system:NativeData.SYSTEMS){
            for(NativeData.StructureRecord s:system.structures){
                hotspotCount++;
                assertTrue("Duplicate native hotspot id: "+s.id,visual.add(s.id));
            }
        }

        assertEquals(55,hotspotCount);
        assertEquals(academic,visual);
    }

    @Test public void progressStorePersistsNativeProgress(){
        Context context=context();
        context.getSharedPreferences("earthworm_native_progress_v2",Context.MODE_PRIVATE)
            .edit().clear().commit();

        ProgressStore first=new ProgressStore(context);
        first.setLastSystem("reproductive");
        first.markVisited("ovaries");
        first.saveScore(17);

        ProgressStore second=new ProgressStore(context);
        assertEquals("reproductive",second.getLastSystem());
        assertEquals(1,second.visitedCount());
        assertEquals(17,second.score());
    }

    @Test public void statefulNativeActivitiesSurviveRecreation(){
        Context context=context();

        Intent main=new Intent(context,MainActivity.class)
            .putExtra(MainActivity.EXTRA_SYSTEM,"digestive");
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(main)){
            scenario.recreate();
        }

        try(ActivityScenario<GuidedActivity> scenario=ActivityScenario.launch(GuidedActivity.class)){
            scenario.recreate();
        }

        try(ActivityScenario<AssessmentActivity> scenario=ActivityScenario.launch(AssessmentActivity.class)){
            scenario.recreate();
        }

        try(ActivityScenario<MicroscopyActivity> scenario=ActivityScenario.launch(MicroscopyActivity.class)){
            scenario.recreate();
        }
    }
}
