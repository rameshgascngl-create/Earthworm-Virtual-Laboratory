package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

/**
 * Native laboratory screen. No WebView, JavaScript, DOM, HTML asset, or browser storage is used.
 */
public final class MainActivity extends Activity implements AnatomyCanvas.OnStructureSelected {
    public static final String EXTRA_SYSTEM = "earthworm.native.system";
    private ProgressStore progress;
    private TextToSpeech tts;
    private TextView heading;
    private TextView detail;
    private AnatomyCanvas anatomy;
    private String currentSystem = "external";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        progress = new ProgressStore(this);
        currentSystem = getIntent() != null ? getIntent().getStringExtra(EXTRA_SYSTEM) : null;
        if (currentSystem == null || NativeData.system(currentSystem) == null) currentSystem = "external";

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(6,21,21));
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(18),dp(18),dp(18),dp(26));
        scroll.addView(body,new ScrollView.LayoutParams(-1,-1));
        setContentView(scroll);

        if (android.os.Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            scroll.setOnApplyWindowInsetsListener((v,insets)->{
                android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());
                body.setPadding(dp(18)+bars.left,dp(18)+bars.top,dp(18)+bars.right,dp(26)+bars.bottom);
                return insets;
            });
        }

        heading = text("",24,Color.WHITE,true);
        body.addView(heading);
        TextView sub = text("Native anatomy laboratory · tap a labelled region",14,Color.rgb(177,205,197),false);
        sub.setPadding(0,dp(4),0,dp(12));
        body.addView(sub);

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        for (NativeData.SystemRecord s : NativeData.SYSTEMS) {
            Button b = new Button(this);
            b.setText(s.shortName);
            b.setAllCaps(false);
            b.setOnClickListener(v->showSystem(s.id));
            tabs.addView(b,new LinearLayout.LayoutParams(0,dp(48),1f));
        }
        body.addView(tabs);

        anatomy = new AnatomyCanvas(this);
        anatomy.setListener(this);
        LinearLayout.LayoutParams canvasLp = new LinearLayout.LayoutParams(-1,dp(360));
        canvasLp.setMargins(0,dp(12),0,dp(12));
        body.addView(anatomy,canvasLp);

        detail = text("",15,Color.rgb(226,242,237),false);
        detail.setLineSpacing(0,1.16f);
        body.addView(detail);

        Button speak = new Button(this);
        speak.setText("Speak selected structure");
        speak.setAllCaps(false);
        speak.setOnClickListener(v->speakSelection());
        body.addView(speak,new LinearLayout.LayoutParams(-1,dp(50)));

        TextView notice = text("Native v2 migration branch: educational parity is gated until every legacy structure, guided step, question and microscopic lesson is mapped and faculty-reviewed.",12,Color.rgb(244,198,91),false);
        notice.setPadding(0,dp(14),0,0);
        body.addView(notice);

        tts = new TextToSpeech(this,status->{ if(status==TextToSpeech.SUCCESS) tts.setLanguage(Locale.forLanguageTag("en-IN")); });
        showSystem(currentSystem);
    }

    private void showSystem(String id) {
        NativeData.SystemRecord system = NativeData.system(id);
        if (system == null) return;
        currentSystem = id;
        heading.setText(system.name);
        anatomy.setSystem(system);
        detail.setText(system.summary);
        progress.setLastSystem(id);
    }

    @Override public void onStructureSelected(NativeData.StructureRecord s) {
        detail.setText(s.name+"\n\nLocation: "+s.location+"\nFunction: "+s.function+"\nTeaching point: "+s.significance);
        progress.markVisited(s.id);
    }

    private void speakSelection() {
        NativeData.StructureRecord s = anatomy.getSelected();
        if (s == null || tts == null) return;
        tts.speak(s.name+". "+s.location+". "+s.function,TextToSpeech.QUEUE_FLUSH,null,"structure-"+s.id);
    }

    private TextView text(String value,int sp,int color,boolean bold){
        TextView t=new TextView(this); t.setText(value); t.setTextSize(sp); t.setTextColor(color);
        if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);
        return t;
    }
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
    @Override protected void onDestroy(){ if(tts!=null){tts.stop();tts.shutdown();} super.onDestroy(); }
}
