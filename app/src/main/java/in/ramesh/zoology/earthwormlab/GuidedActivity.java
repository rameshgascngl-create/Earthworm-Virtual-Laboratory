package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;

public final class GuidedActivity extends Activity {
    private List<ContentRepository.GuidedAction> actions;
    private ContentRepository repo;
    private int index=0;
    private boolean tamil=false;
    private TextView title,meta,instruction;
    private Button prev,next,lang;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        repo=new ContentRepository(this);
        actions=repo.guidedActions();
        if(b==null){
            tamil=getIntent()!=null && getIntent().getBooleanExtra(NativeHomeActivity.EXTRA_TAMIL,false);
        }
        if(b!=null){
            index=Math.max(0,Math.min(b.getInt("index",0),Math.max(0,actions.size()-1)));
            tamil=b.getBoolean("tamil",false);
        }

        ScrollView scroll=new ScrollView(this);
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22),dp(28),dp(22),dp(28));
        root.setBackgroundColor(Color.rgb(6,21,21));
        scroll.addView(root,new ScrollView.LayoutParams(-1,-1));
        setContentView(scroll);
        if(android.os.Build.VERSION.SDK_INT>=30){
            getWindow().setDecorFitsSystemWindows(false);
            scroll.setOnApplyWindowInsetsListener((v,insets)->{
                android.graphics.Insets bars=insets.getInsets(
                    WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());
                root.setPadding(
                    dp(22)+bars.left,
                    dp(28)+bars.top,
                    dp(22)+bars.right,
                    dp(28)+bars.bottom);
                return insets;
            });
            scroll.requestApplyInsets();
        }

        title=text("",24,Color.WHITE,true);
        root.addView(title);

        meta=text("",14,Color.rgb(56,214,188),true);
        meta.setPadding(0,dp(10),0,dp(12));
        root.addView(meta);

        instruction=text("",17,Color.rgb(226,242,237),false);
        instruction.setLineSpacing(0,1.18f);
        root.addView(instruction);

        lang=new Button(this);
        lang.setAllCaps(false);
        lang.setText("தமிழ் / English");
        lang.setOnClickListener(v->{tamil=!tamil;show();});
        lang.setMinHeight(dp(48));root.addView(lang,new LinearLayout.LayoutParams(-1,-2));

        LinearLayout nav=new LinearLayout(this);
        prev=new Button(this);
        next=new Button(this);
        prev.setAllCaps(false);
        next.setAllCaps(false);
        prev.setOnClickListener(v->{if(index>0){index--;show();}});
        next.setOnClickListener(v->{if(index<actions.size()-1){index++;show();}});
        prev.setMinHeight(dp(48));prev.setPadding(dp(8),dp(8),dp(8),dp(8));nav.addView(prev,new LinearLayout.LayoutParams(0,-2,1));
        next.setMinHeight(dp(48));next.setPadding(dp(8),dp(8),dp(8),dp(8));nav.addView(next,new LinearLayout.LayoutParams(0,-2,1));
        root.addView(nav);

        show();
    }

    private void show(){
        ContentRepository.GuidedAction a=actions.get(index);
        ContentRepository.Structure structure=repo.structure(a.target);

        String systemName=repo.systemName(a.system,tamil);
        String tool=toolName(a.tool,tamil);
        String target=structure==null?"":structure.title(tamil);

        title.setText((tamil?"வழிகாட்டும் செய்முறை ":"Guided practical ")+(index+1)+"/"+actions.size());

        StringBuilder m=new StringBuilder();
        m.append(tamil?"மண்டலம்: ":"System: ").append(systemName);
        m.append("   ·   ").append(tamil?"கருவி/முறை: ":"Tool/method: ").append(tool);
        if(!target.isEmpty()){
            m.append("   ·   ").append(tamil?"அமைப்பு: ":"Structure: ").append(target);
        }
        meta.setText(m.toString());

        instruction.setText(instructionFor(a,structure));
        prev.setText(tamil?"முந்தையது":"Previous");
        next.setText(tamil?"அடுத்தது":"Next");
        prev.setEnabled(index>0);
        next.setEnabled(index<actions.size()-1);
    }

    private String instructionFor(ContentRepository.GuidedAction a,ContentRepository.Structure s){
        String explicit=a.instruction(tamil);
        if(explicit!=null && !explicit.trim().isEmpty()) return explicit;

        if(s==null){
            return tamil
                ?"இந்த செய்முறைப் படியை கவனமாக நிறைவேற்றுக."
                :"Complete this practical step carefully.";
        }

        String target=s.title(tamil);
        switch(a.tool){
            case "magnifier":
                return tamil
                    ? target+" அமைப்பை உருப்பெருக்கத்தில் உற்றுநோக்கி, அதன் அடையாளப் பண்பை உறுதிப்படுத்துக."
                    : "Examine "+target+" under magnification and confirm its diagnostic feature.";
            case "probe":
                return tamil
                    ? target+" அமைப்பை மழுங்கிய ஆய்வூசியால் மெதுவாகத் தடமறியவும்; அருகிலுள்ள திசுக்களை இழுக்கவோ கிழிக்கவோ வேண்டாம்."
                    : "Trace "+target+" gently with a blunt probe; do not pull or tear adjacent tissue.";
            case "forceps":
                return tamil
                    ? target+" அமைப்பை இடுக்கியால் மெதுவாக வெளிப்படுத்துக; நசுக்கவோ கிழிக்கவோ வேண்டாம்."
                    : "Expose "+target+" gently with forceps; avoid crushing or tearing it.";
            case "dropper":
                return tamil
                    ? "தேவையான அளவு திரவத்தை மட்டும் துளிசொட்டியால் சேர்த்து "+target+" அமைப்பை உற்றுநோக்குக."
                    : "Add only the required amount of fluid with the dropper, then observe "+target+".";
            case "inspect":
            default:
                return tamil
                    ? target+" அமைப்பை உற்றுநோக்கி அதன் அமைவிடத்தையும் வேறுபடுத்தும் பண்பையும் உறுதிப்படுத்துக."
                    : "Observe "+target+" and confirm its position and distinguishing feature.";
        }
    }

    private String toolName(String tool,boolean ta){
        switch(tool){
            case "pin": return ta?"நிலைநிறுத்தும் ஊசி":"Dissecting pin";
            case "scissors": return ta?"நுண் கத்தரிக்கோல்":"Fine scissors";
            case "forceps": return ta?"இடுக்கி":"Forceps";
            case "probe": return ta?"மழுங்கிய ஆய்வூசி":"Blunt probe";
            case "dropper": return ta?"துளிசொட்டி":"Dropper";
            case "magnifier": return ta?"உருப்பெருக்கி":"Magnifier";
            case "inspect":
            default: return ta?"உற்றுநோக்குதல்":"Inspection";
        }
    }

    @Override protected void onSaveInstanceState(Bundle out){
        out.putInt("index",index);
        out.putBoolean("tamil",tamil);
        super.onSaveInstanceState(out);
    }

    private TextView text(String v,int sp,int c,boolean bold){
        TextView t=new TextView(this);
        t.setText(v);
        t.setTextSize(sp);
        t.setTextColor(c);
        if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);
        return t;
    }

    private int dp(int v){
        return Math.round(v*getResources().getDisplayMetrics().density);
    }
}
