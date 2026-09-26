package in.ramesh.zoology.earthwormlab;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public final class ProgressStore {
    private final SharedPreferences prefs;
    public ProgressStore(Context c){ prefs=c.getSharedPreferences("earthworm_native_progress_v2",Context.MODE_PRIVATE); }
    public void setLastSystem(String id){ prefs.edit().putString("last_system",id).apply(); }
    public String getLastSystem(){ return prefs.getString("last_system","external"); }
    public void markVisited(String id){
        Set<String> copy=new HashSet<>(prefs.getStringSet("visited",new HashSet<>()));
        copy.add(id); prefs.edit().putStringSet("visited",copy).apply();
    }
    public int visitedCount(){ return prefs.getStringSet("visited",new HashSet<>()).size(); }
    public void saveScore(int score){ prefs.edit().putInt("assessment_score",score).apply(); }
    public int score(){ return prefs.getInt("assessment_score",0); }
}
