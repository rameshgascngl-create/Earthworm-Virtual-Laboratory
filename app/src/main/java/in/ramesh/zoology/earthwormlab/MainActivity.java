package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MainActivity extends Activity implements AnatomyCanvas.OnStructureSelected {
    public static final String EXTRA_SYSTEM = "earthworm.native.system";
    private ProgressStore progress;
    private ContentRepository content;
    private TextToSpeech tts;
    private TextView heading,detail,indexTitle;
    private LinearLayout structureList,tabs;
    private Button speakButton;
    private AnatomyCanvas anatomy;
    private String currentSystem = "external";
    private boolean tamil=false;
    private ContentRepository.Structure selectedContent;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        progress=new ProgressStore(this);
        content=new ContentRepository(this);
        if(state!=null){
            tamil=state.getBoolean("tamil",false);
            String restored=state.getString("system","external");
            if(content.systemIds().contains(restored)) currentSystem=restored;
        }else{
            String requested=getIntent()!=null?getIntent().getStringExtra(EXTRA_SYSTEM):null;
            if(requested!=null && content.systemIds().contains(requested)) currentSystem=requested;
        }

        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(Color.rgb(6,21,21));
        LinearLayout body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(18),dp(18),dp(18),dp(26));scroll.addView(body,new ScrollView.LayoutParams(-1,-1));setContentView(scroll);
        if(android.os.Build.VERSION.SDK_INT>=30){getWindow().setDecorFitsSystemWindows(false);scroll.setOnApplyWindowInsetsListener((v,insets)->{android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());body.setPadding(dp(18)+bars.left,dp(18)+bars.top,dp(18)+bars.right,dp(26)+bars.bottom);return insets;});}

        heading=text("",24,Color.WHITE,true);body.addView(heading);
        Button lang=new Button(this);lang.setText("தமிழ் / English");lang.setAllCaps(false);lang.setOnClickListener(v->{tamil=!tamil;refreshLanguage();});body.addView(lang,new LinearLayout.LayoutParams(-1,dp(48)));

        tabs=new LinearLayout(this);
        tabs.setOrientation(LinearLayout.VERTICAL);
        body.addView(tabs);

        anatomy=new AnatomyCanvas(this);anatomy.setListener(this);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(340));cp.setMargins(0,dp(12),0,dp(12));body.addView(anatomy,cp);

        detail=text("",15,Color.rgb(226,242,237),false);detail.setLineSpacing(0,1.16f);body.addView(detail);
        speakButton=new Button(this);
        speakButton.setAllCaps(false);
        speakButton.setOnClickListener(v->speakSelection());
        body.addView(speakButton,new LinearLayout.LayoutParams(-1,dp(50)));

        indexTitle=text("",18,Color.rgb(56,214,188),true);
        indexTitle.setPadding(0,dp(14),0,dp(8));
        body.addView(indexTitle);
        structureList=new LinearLayout(this);structureList.setOrientation(LinearLayout.VERTICAL);body.addView(structureList);

        tts=new TextToSpeech(this,status->{
            if(status==TextToSpeech.SUCCESS) configureOfflineVoice();
        });
        refreshLanguage();
    }

    private void refreshLanguage(){
        tabs.removeAllViews();
        for(String id:content.systemIds()){
            Button b=new Button(this);
            b.setAllCaps(false);
            b.setText(content.systemName(id,tamil));
            b.setOnClickListener(v->showSystem(id));
            tabs.addView(b,new LinearLayout.LayoutParams(-1,dp(46)));
        }
        speakButton.setText(tamil?"தேர்ந்த அமைப்பின் விளக்கத்தை ஒலிக்க":"Speak selected structure");
        indexTitle.setText(tamil?"அமைப்புகளின் பட்டியல்":"Native structure index");
        anatomy.setContentDescription(
            tamil
                ?"மண்புழு உடற்கூறு காட்சி வரைபடம். திரைவாசிப்பான் பயனர்கள் கீழுள்ள அணுகல்திறன் கொண்ட அமைப்புப் பட்டியலைப் பயன்படுத்துக."
                :"Visual earthworm anatomy diagram. Screen-reader users can select the same structures from the accessible list below.");
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

    private void selectContent(ContentRepository.Structure s){
        selectedContent=s;
        detail.setText(s.title(tamil)+"\n\n"+s.detail(tamil));
        anatomy.setSelectedDisplayLabel(s.title(tamil));
        progress.markVisited(s.id);
    }
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
    private boolean configureOfflineVoice(){
        if(tts==null)return false;
        Locale desired=Locale.forLanguageTag(tamil?"ta-IN":"en-IN");
        Set<Voice> voices=tts.getVoices();
        Voice fallback=null;
        if(voices!=null){
            for(Voice voice:voices){
                if(voice==null || voice.isNetworkConnectionRequired())continue;
                Locale locale=voice.getLocale();
                if(locale==null)continue;
                if(locale.getLanguage().equals(desired.getLanguage())){
                    if(locale.toLanguageTag().equalsIgnoreCase(desired.toLanguageTag())){
                        tts.setVoice(voice);
                        return true;
                    }
                    if(fallback==null)fallback=voice;
                }
            }
        }
        if(fallback!=null){
            tts.setVoice(fallback);
            return true;
        }
        return false;
    }

    private void speakSelection(){
        if(tts==null)return;
        if(!configureOfflineVoice()){
            android.widget.Toast.makeText(
                this,
                tamil
                    ?"இந்த மொழிக்கான இணையமில்லா உரை-ஒலி குரல் சாதனத்தில் இல்லை."
                    :"No offline text-to-speech voice is installed for this language.",
                android.widget.Toast.LENGTH_LONG).show();
            return;
        }
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
    @Override protected void onSaveInstanceState(Bundle out){
        out.putBoolean("tamil",tamil);
        out.putString("system",currentSystem);
        super.onSaveInstanceState(out);
    }

    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
    @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
