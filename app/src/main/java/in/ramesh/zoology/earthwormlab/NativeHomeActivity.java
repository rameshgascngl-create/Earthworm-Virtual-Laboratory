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
    private LinearLayout body;
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(Color.rgb(6,21,21));
        body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(22),dp(28),dp(22),dp(28));scroll.addView(body,new ScrollView.LayoutParams(-1,-1));setContentView(scroll);
        if(android.os.Build.VERSION.SDK_INT>=30){getWindow().setDecorFitsSystemWindows(false);scroll.setOnApplyWindowInsetsListener((v,insets)->{android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());body.setPadding(dp(22)+bars.left,dp(28)+bars.top,dp(22)+bars.right,dp(28)+bars.bottom);return insets;});}
        body.addView(text("DEPARTMENT OF ZOOLOGY",12,Color.rgb(56,214,188),true));
        TextView title=text("Earthworm Virtual Laboratory",28,Color.WHITE,true);title.setPadding(0,dp(8),0,0);body.addView(title);
        TextView sub=text("Native Android generation · Metaphire posthuma",16,Color.rgb(185,211,203),false);sub.setPadding(0,dp(5),0,dp(18));body.addView(sub);
        ProgressStore store=new ProgressStore(this);
        addCard("Continue native laboratory","Resume the last native system. No WebView is involved.",store.getLastSystem());
        for(NativeData.SystemRecord s:NativeData.SYSTEMS)addCard(s.name,s.summary,s.id);
        Button guided=button("Guided practical · 56 actions");guided.setOnClickListener(v->startActivity(new Intent(this,GuidedActivity.class)));body.addView(guided,new LinearLayout.LayoutParams(-1,dp(52)));\n        Button microscopy=button("Microscopy · 9 lessons");microscopy.setOnClickListener(v->startActivity(new Intent(this,MicroscopyActivity.class)));LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,dp(52));mp.setMargins(0,dp(10),0,0);body.addView(microscopy,mp);\n        Button assessment=button("Native assessment · 72 questions");assessment.setOnClickListener(v->startActivity(new Intent(this,AssessmentActivity.class)));LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(52));ap.setMargins(0,dp(10),0,0);body.addView(assessment,ap);
        Button privacy=button("About & Privacy");privacy.setOnClickListener(v->startActivity(new Intent(this,PrivacyActivity.class)));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));p.setMargins(0,dp(10),0,0);body.addView(privacy,p);
        TextView footer=text("Version "+BuildConfig.VERSION_NAME+" · native Views/Canvas · offline · no account · no ads\nVisited structures: "+store.visitedCount()+" · assessment score: "+store.score(),13,Color.rgb(159,190,181),false);footer.setGravity(Gravity.CENTER);footer.setPadding(0,dp(22),0,0);body.addView(footer);
    }
    private void addCard(String title,String detail,String system){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(18),dp(16),dp(18),dp(16));android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();bg.setColor(Color.rgb(11,35,34));bg.setCornerRadius(dp(16));bg.setStroke(dp(1),Color.rgb(49,93,88));card.setBackground(bg);card.setClickable(true);card.setFocusable(true);card.addView(text(title,18,Color.WHITE,true));TextView d=text(detail,14,Color.rgb(185,211,203),false);d.setPadding(0,dp(5),0,0);card.addView(d);card.setOnClickListener(v->{Intent i=new Intent(this,MainActivity.class);i.putExtra(MainActivity.EXTRA_SYSTEM,system);startActivity(i);});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,dp(10));body.addView(card,lp);}
    private Button button(String label){Button b=new Button(this);b.setText(label);b.setTextSize(16);b.setAllCaps(false);return b;}
    private TextView text(String v,int sp,int c,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(sp);t.setTextColor(c);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
