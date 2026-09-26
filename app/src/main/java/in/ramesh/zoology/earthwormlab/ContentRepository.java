package in.ramesh.zoology.earthwormlab;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;

public final class ContentRepository {
    private static final List<String> SYSTEM_ORDER = Arrays.asList(
        "setup","external","digestive","circulatory","respiratory","excretory","reproductive","nervous","crosssection");
    private final JSONObject root;

    public static final class Structure {
        public final String id,system,en,ta,locEn,locTa,fnEn,fnTa,sigEn,sigTa,fixEn,fixTa,deep;
        Structure(String id,JSONObject o){
            this.id=id; system=o.optString("system"); en=o.optString("en"); ta=o.optString("ta");
            locEn=o.optString("locEn"); locTa=o.optString("locTa"); fnEn=o.optString("fnEn"); fnTa=o.optString("fnTa");
            sigEn=o.optString("sigEn"); sigTa=o.optString("sigTa"); fixEn=o.optString("fixEn"); fixTa=o.optString("fixTa");
            deep=o.optString("deep");
        }
        public String title(boolean tamil){ return tamil && !ta.trim().isEmpty()?ta:en; }
        public String detail(boolean tamil){
            String loc=tamil&&!locTa.trim().isEmpty()?locTa:locEn, fn=tamil&&!fnTa.trim().isEmpty()?fnTa:fnEn, sig=tamil&&!sigTa.trim().isEmpty()?sigTa:sigEn, fix=tamil&&!fixTa.trim().isEmpty()?fixTa:fixEn;
            StringBuilder b=new StringBuilder();
            if(!loc.trim().isEmpty()) b.append(tamil?"அமைவிடம்: ":"Location: ").append(loc);
            if(!fn.trim().isEmpty()) b.append("\n\n").append(tamil?"செயல்: ":"Function: ").append(fn);
            if(!sig.trim().isEmpty()) b.append("\n\n").append(tamil?"கற்பித்தல் குறிப்பு: ":"Teaching point: ").append(sig);
            if(!fix.trim().isEmpty()) b.append("\n\n").append(tamil?"திருத்தம்: ":"Common correction: ").append(fix);
            return b.toString();
        }
    }

    public static final class GuidedAction {
        public final String system,tool,target,en,ta;
        GuidedAction(String system,JSONObject o){this.system=system;tool=o.optString("tool");target=o.optString("target");en=o.optString("en");ta=o.optString("ta");}
        public String instruction(boolean tamil){String x=tamil?ta:en;return x.trim().isEmpty()?target:x;}
    }

    public static final class Question {
        public final String system,en,ta; public final List<String> oEn,oTa; public final int answer;
        Question(String system,JSONObject o){
            this.system=system;en=o.optString("en");ta=o.optString("ta");answer=o.optInt("a",-1);
            oEn=list(o.optJSONArray("oEn"));oTa=list(o.optJSONArray("oTa"));
        }
        private static List<String> list(JSONArray a){List<String> out=new ArrayList<>();if(a!=null)for(int i=0;i<a.length();i++)out.add(a.optString(i));return out;}
    }

    public static final class Microscopy {
        public final String id,en,ta,textEn,textTa;
        Microscopy(String id,JSONObject o){this.id=id;en=o.optString("en");ta=o.optString("ta");textEn=o.optString("textEn");textTa=o.optString("textTa");}
    }

    public ContentRepository(Context context){
        try(InputStream in=context.getResources().openRawResource(R.raw.earthworm_content_v138);
            ByteArrayOutputStream out=new ByteArrayOutputStream()){
            byte[] buf=new byte[8192]; int n; while((n=in.read(buf))>0)out.write(buf,0,n);
            root=new JSONObject(new String(out.toByteArray(), StandardCharsets.UTF_8));
        }catch(Exception e){throw new IllegalStateException("Native academic dataset could not be loaded",e);}
    }

    public List<String> systemIds(){
        List<String> out=new ArrayList<>();
        JSONObject o=root.optJSONObject("systems");
        if(o!=null) for(String id:SYSTEM_ORDER) if(o.has(id)) out.add(id);
        return out;
    }
    public String systemName(String id,boolean tamil){
        JSONObject s=root.optJSONObject("systems");JSONObject o=s==null?null:s.optJSONObject(id);if(o==null)return id;
        String x=o.optString(tamil?"ta":"en");return x.trim().isEmpty()?id:x;
    }
    public List<Structure> structures(String system){
        List<Structure> out=new ArrayList<>();JSONObject all=root.optJSONObject("structures");if(all==null)return out;
        Iterator<String> it=all.keys();while(it.hasNext()){String id=it.next();JSONObject o=all.optJSONObject(id);if(o!=null&&system.equals(o.optString("system")))out.add(new Structure(id,o));}return out;
    }
    public List<GuidedAction> guidedActions(){
        List<GuidedAction> out=new ArrayList<>();JSONObject all=root.optJSONObject("guidedModules");if(all==null)return out;
        Iterator<String> it=all.keys();while(it.hasNext()){String system=it.next();JSONArray a=all.optJSONArray(system);if(a!=null)for(int i=0;i<a.length();i++)out.add(new GuidedAction(system,a.optJSONObject(i)));}return out;
    }
    public List<Question> questions(){
        List<Question> out=new ArrayList<>();JSONObject all=root.optJSONObject("questions");if(all==null)return out;
        Iterator<String> it=all.keys();while(it.hasNext()){String system=it.next();JSONArray a=all.optJSONArray(system);if(a!=null)for(int i=0;i<a.length();i++)out.add(new Question(system,a.optJSONObject(i)));}return out;
    }
    public List<Microscopy> microscopy(){
        List<Microscopy> out=new ArrayList<>();JSONObject all=root.optJSONObject("microscopicLessons");if(all==null)return out;
        Iterator<String> it=all.keys();while(it.hasNext()){String id=it.next();JSONObject o=all.optJSONObject(id);if(o!=null)out.add(new Microscopy(id,o));}return out;
    }
    public JSONObject counts(){JSONObject p=root.optJSONObject("provenance");return p==null?new JSONObject():p.optJSONObject("counts");}
}
