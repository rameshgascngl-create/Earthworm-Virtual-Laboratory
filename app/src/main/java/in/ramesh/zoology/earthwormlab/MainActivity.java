package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity implements AnatomyCanvas.OnStructureSelected {
    public static final String EXTRA_SYSTEM = "earthworm.native.system";
    private ProgressStore progress;
    private ContentRepository content;
    private TextToSpeech tts;
    private TextView heading,detail;
    private LinearLayout structureList;
    private AnatomyCanvas anatomy;
    private String currentSystem = "external";
    private boolean tamil=false;
    private ContentRepository.Structure selectedContent;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        progress=new ProgressStore(this);
        content=new ContentRepository(this);
        String requested=getIntent()!=null?getIntent().getStringExtra(EXTRA_SYSTEM):null;
        if(requested!=null && content.systemIds().contains(requested)) currentSystem=requested;

        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(Color.rgb(6,21,21));
        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(18),dp(18),dp(18),dp(26));scroll.addView(body,new ScrollView.LayoutParams(-1,-1));setContentView(scroll);
        if(android.os.Build.VERSION.SDK_INT>=30){getWindow().setDecorFitsSystemWindows(false);scroll.setOnApplyWindowInsetsListener((v,insets)->{android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());body.setPadding(dp(18)+bars.left,dp(18)+bars.top,dp(18)+bars.right,dp(26)+bars.bottom);return insets;});}

        heading=text("",24,Color.WHITE,true);body.addView(heading);
        Button lang=new Button(this);lang.setText("தமிழ் / English");lang.setAllCaps(false);lang.setOnClickListener(v->{tamil=!tamil;showSystem(currentSystem);});body.addView(lang,new LinearLayout.LayoutParams(-1,dp(48)));

        LinearLayout tabs=new LinearLayout(this);tabs.setOrientation(LinearLayout.VERTICAL);
        for(String id:content.systemIds()){Button b=new Button(this);b.setAllCaps(false);b.setText(content.systemName(id,false));b.setOnClickListener(v->showSystem(id));tabs.addView(b,new LinearLayout.LayoutParams(-1,dp(46)));}
        body.addView(tabs);

        anatomy=new AnatomyCanvas(this);anatomy.setListener(this);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(340));cp.setMargins(0,dp(12),0,dp(12));body.addView(anatomy,cp);

        detail=text("",15,Color.rgb(226,242,237),false);detail.setLineSpacing(0,1.16f);body.addView(detail);
        Button speak=new Button(this);speak.setText("Speak selected structure");speak.setAllCaps(false);speak.setOnClickListener(v->speakSelection());body.addView(speak,new LinearLayout.LayoutParams(-1,dp(50)));

        TextView indexTitle=text("Native structure index",18,Color.rgb(56,214,188),true);indexTitle.setPadding(0,dp(14),0,dp(8));body.addView(indexTitle);
        structureList=new LinearLayout(this);structureList.setOrientation(LinearLayout.VERTICAL);body.addView(structureList);

        tts=new TextToSpeech(this,status->{if(status==TextToSpeech.SUCCESS)tts.setLanguage(Locale.forLanguageTag(tamil?"ta-IN":"en-IN"));});
        showSystem(currentSystem);
    }

    private void showSystem(String id){
        if(!content.systemIds().contains(id))return;
        currentSystem=id;selectedContent=null;heading.setText(content.systemName(id,tamil));detail.setText(tamil?"அமைப்பைத் தேர்ந்து கட்டமைப்பைத் தொடவும்.":"Select a structure below or use the native diagram.");
        NativeData.SystemRecord visual=NativeData.system(id);anatomy.setSystem(visual);
        structureList.removeAllViews();
        List<ContentRepository.Structure> records=content.structures(id);
        if(records.isEmpty()){TextView none=text(tamil?"இந்தப் பிரிவு செய்முறை/குறுக்குவெட்டு வழிகாட்டுதலால் கற்பிக்கப்படுகிறது.":"This section is taught through the guided procedure/cross-section workflow.",14,Color.rgb(185,211,203),false);structureList.addView(none);}
        for(ContentRepository.Structure s:records){Button b=new Button(this);b.setAllCaps(false);b.setText(s.title(tamil));b.setOnClickListener(v->selectContent(s));structureList.addView(b,new LinearLayout.LayoutParams(-1,dp(52)));}
        progress.setLastSystem(id);
    }

    private void selectContent(ContentRepository.Structure s){selectedContent=s;detail.setText(s.title(tamil)+"\n\n"+s.detail(tamil));progress.markVisited(s.id);}
    @Override public void onStructureSelected(NativeData.StructureRecord s){
        ContentRepository.Structure authoritative=content.structure(s.id);
        if(authoritative!=null){
            selectContent(authoritative);
        }else{
            selectedContent=null;
            detail.setText(s.name);
            progress.markVisited(s.id);
        }
    }
    private void speakSelection(){
        if(tts==null)return;
        tts.setLanguage(Locale.forLanguageTag(tamil?"ta-IN":"en-IN"));
        String speech;
        if(selectedContent!=null)speech=selectedContent.title(tamil)+". "+selectedContent.detail(tamil);
        else {
            NativeData.StructureRecord s=anatomy.getSelected();
            if(s==null)return;
            ContentRepository.Structure authoritative=content.structure(s.id);
            if(authoritative==null)return;
            speech=authoritative.title(tamil)+". "+authoritative.detail(tamil);
        }
        tts.speak(speech,TextToSpeech.QUEUE_FLUSH,null,"native-structure");
    }
    private TextView text(String v,int sp,int c,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(sp);t.setTextColor(c);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
    @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
