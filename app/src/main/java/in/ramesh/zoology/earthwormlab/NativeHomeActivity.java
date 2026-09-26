package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class NativeHomeActivity extends Activity {
    public static final String EXTRA_TAMIL="earthworm.language.tamil";

    private boolean tamil=false;
    private ScrollView scroll;
    private LinearLayout body;
    private ContentRepository content;
    private ProgressStore store;

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        content=new ContentRepository(this);
        store=new ProgressStore(this);

        if(savedInstanceState!=null){
            tamil=savedInstanceState.getBoolean("tamil",false);
        }else if(getIntent()!=null){
            tamil=getIntent().getBooleanExtra(EXTRA_TAMIL,false);
        }

        scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(6,21,21));
        setContentView(scroll);

        if(android.os.Build.VERSION.SDK_INT>=30){
            getWindow().setDecorFitsSystemWindows(false);
            scroll.setOnApplyWindowInsetsListener((v,insets)->{
                android.graphics.Insets bars=insets.getInsets(
                    WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());
                if(body!=null){
                    body.setPadding(
                        dp(22)+bars.left,
                        dp(28)+bars.top,
                        dp(22)+bars.right,
                        dp(28)+bars.bottom);
                }
                return insets;
            });
        }

        render();
    }

    private void render(){
        body=new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(22),dp(28),dp(22),dp(28));
        scroll.removeAllViews();
        scroll.addView(body,new ScrollView.LayoutParams(-1,-1));

        body.addView(text(
            tamil?"விலங்கியல் துறை":"DEPARTMENT OF ZOOLOGY",
            12,Color.rgb(56,214,188),true));

        TextView title=text(
            tamil?"மண்புழு மெய்நிகர் ஆய்வகம்":"Earthworm Virtual Laboratory",
            28,Color.WHITE,true);
        title.setPadding(0,dp(8),0,0);
        body.addView(title);

        TextView sub=text(
            tamil?"சொந்த Android பதிப்பு · Metaphire posthuma":"Native Android generation · Metaphire posthuma",
            16,Color.rgb(185,211,203),false);
        sub.setPadding(0,dp(5),0,dp(12));
        body.addView(sub);

        Button lang=button("தமிழ் / English");
        lang.setOnClickListener(v->{tamil=!tamil;render();});
        body.addView(lang,new LinearLayout.LayoutParams(-1,-2));

        addCard(
            tamil?"ஆய்வகத்தைத் தொடர்க":"Continue native laboratory",
            tamil?"கடைசியாகப் பார்த்த மண்டலத்தை மீண்டும் திறக்கவும். WebView பயன்படுத்தப்படவில்லை.":"Resume the last native system. No WebView is involved.",
            store.getLastSystem());

        for(String systemId:content.systemIds()){
            addCard(
                content.systemName(systemId,tamil),
                systemSummary(systemId),
                systemId);
        }

        Button guided=button(tamil?"வழிகாட்டும் செய்முறை · 56 செயல்கள்":"Guided practical · 56 actions");
        guided.setOnClickListener(v->startActivity(withLanguage(new Intent(this,GuidedActivity.class))));
        body.addView(guided,new LinearLayout.LayoutParams(-1,-2));

        Button microscopy=button(tamil?"நுண்ணோக்கி ஆழ்பார்வை · 9 பாடங்கள்":"Microscopy · 9 lessons");
        microscopy.setOnClickListener(v->startActivity(withLanguage(new Intent(this,MicroscopyActivity.class))));
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,-2);
        mp.setMargins(0,dp(10),0,0);
        body.addView(microscopy,mp);

        Button assessment=button(tamil?"மதிப்பீடு · 72 வினாக்கள்":"Native assessment · 72 questions");
        assessment.setOnClickListener(v->startActivity(withLanguage(new Intent(this,AssessmentActivity.class))));
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,-2);
        ap.setMargins(0,dp(10),0,0);
        body.addView(assessment,ap);

        Button privacy=button(tamil?"பயன்பாடு & தனியுரிமை":"About & Privacy");
        privacy.setOnClickListener(v->startActivity(new Intent(this,PrivacyActivity.class)));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,-2);
        pp.setMargins(0,dp(10),0,0);
        body.addView(privacy,pp);

        TextView footer=text(
            tamil
                ?"பதிப்பு "+BuildConfig.VERSION_NAME+" · சொந்த Views/Canvas · இணையமில்லா · கணக்கு இல்லை · விளம்பரம் இல்லை\n"+
                 "பார்வையிட்ட அமைப்புகள்: "+store.visitedCount()+" · மதிப்பீட்டு மதிப்பெண்: "+store.score()
                :"Version "+BuildConfig.VERSION_NAME+" · native Views/Canvas · offline · no account · no ads\n"+
                 "Visited structures: "+store.visitedCount()+" · assessment score: "+store.score(),
            13,Color.rgb(159,190,181),false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0,dp(22),0,0);
        body.addView(footer);

        scroll.requestApplyInsets();
    }

    private Intent withLanguage(Intent i){
        return i.putExtra(EXTRA_TAMIL,tamil);
    }

    private String systemSummary(String id){
        if(tamil){
            switch(id){
                case "setup": return "மாதிரி அமைப்பு, ஊசி நிலைநிறுத்தல், வெட்டு மற்றும் பாதுகாப்பான திறப்பு வரிசை.";
                case "external": return "புறத்தோற்றமும் கண்ட அடையாளங்களும்.";
                case "digestive": return "உணவுக்குழாய் மண்டலம், பகுதிச் சிறப்பாக்கம் மற்றும் உறிஞ்சுதல்.";
                case "circulatory": return "மூடிய இரத்த ஓட்ட மண்டலமும் சுருங்கும் நாள வளைவுகளும்.";
                case "respiratory": return "ஈரமான உடற்புறத் தோலும் தோல் வழி வாயுப் பரிமாற்றமும்.";
                case "excretory": return "தொண்டை, இடைத்திரை மற்றும் உடற்சுவர் நெஃப்ரிடியா அமைப்புகள்.";
                case "reproductive": return "இருபாலுறுப்பு இனப்பெருக்க உறுப்புகளும் நாளங்களும்.";
                case "nervous": return "மூளை நரம்புத் திரள்கள், இணைப்புகள் மற்றும் வயிற்றுப்புற நரம்புக் கயிறு.";
                case "crosssection": return "குறுக்குவெட்டின் திசு மற்றும் உறுப்பு அமைவுறவுகள்.";
                default: return "சொந்த Android செய்முறைப் பயிற்சி.";
            }
        }
        switch(id){
            case "setup": return "Specimen orientation, pinning, incision and safe opening sequence.";
            case "external": return "External morphology and segmental landmarks.";
            case "digestive": return "Alimentary canal, regional specialisation and absorption.";
            case "circulatory": return "Closed vascular system and contractile vascular arches.";
            case "respiratory": return "Moist integument and cutaneous gas exchange.";
            case "excretory": return "Pharyngeal, septal and integumentary nephridial systems.";
            case "reproductive": return "Hermaphrodite reproductive organs and ducts.";
            case "nervous": return "Cerebral ganglia, connectives and ventral nerve cord.";
            case "crosssection": return "Native transverse-section orientation and tissue relationships.";
            default: return "Native practical-learning module.";
        }
    }

    private void addCard(String title,String detail,String system){
        LinearLayout card=new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18),dp(16),dp(18),dp(16));

        android.graphics.drawable.GradientDrawable bg=
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.rgb(11,35,34));
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1),Color.rgb(49,93,88));
        card.setBackground(bg);
        card.setClickable(true);
        card.setFocusable(true);

        card.addView(text(title,18,Color.WHITE,true));
        TextView d=text(detail,14,Color.rgb(185,211,203),false);
        d.setPadding(0,dp(5),0,0);
        card.addView(d);

        card.setOnClickListener(v->{
            Intent i=new Intent(this,MainActivity.class);
            i.putExtra(MainActivity.EXTRA_SYSTEM,system);
            i.putExtra(EXTRA_TAMIL,tamil);
            startActivity(i);
        });

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(0,0,0,dp(10));
        body.addView(card,lp);
    }

    private Button button(String label){
        Button b=new Button(this);
        b.setText(label);
        b.setTextSize(16);
        b.setAllCaps(false);
        b.setMinHeight(dp(48));
        b.setPadding(dp(12),dp(10),dp(12),dp(10));
        return b;
    }

    private TextView text(String v,int sp,int c,boolean bold){
        TextView t=new TextView(this);
        t.setText(v);
        t.setTextSize(sp);
        t.setTextColor(c);
        if(bold)t.setTypeface(
            android.graphics.Typeface.DEFAULT,
            android.graphics.Typeface.BOLD);
        return t;
    }

    @Override protected void onSaveInstanceState(Bundle out){
        out.putBoolean("tamil",tamil);
        super.onSaveInstanceState(out);
    }

    private int dp(int v){
        return Math.round(v*getResources().getDisplayMetrics().density);
    }
}
