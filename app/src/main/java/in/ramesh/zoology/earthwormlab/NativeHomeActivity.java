package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class NativeHomeActivity extends Activity {
    private LinearLayout body;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(6,21,21));
        body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(22), dp(28), dp(22), dp(28));
        scroll.addView(body, new ScrollView.LayoutParams(-1,-1));
        setContentView(scroll);

        if (android.os.Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            scroll.setOnApplyWindowInsetsListener((v,insets)->{
                android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());
                body.setPadding(dp(22)+bars.left,dp(28)+bars.top,dp(22)+bars.right,dp(28)+bars.bottom);
                return insets;
            });
            scroll.requestApplyInsets();
        }

        TextView eyebrow=text("DEPARTMENT OF ZOOLOGY",12,Color.rgb(56,214,188),true);
        body.addView(eyebrow);
        TextView title=text("Earthworm Virtual Laboratory",28,Color.WHITE,true);
        title.setPadding(0,dp(8),0,0);
        body.addView(title);
        TextView subtitle=text("Metaphire posthuma · Offline bilingual practical learning",16,Color.rgb(185,211,203),false);
        subtitle.setPadding(0,dp(6),0,dp(18));
        body.addView(subtitle);

        addCard("Continue Laboratory","Resume the saved laboratory exactly where you left it.","continue");
        addCard("Guided Study","Follow the structured practical sequence with native narration support.","guided");
        addCard("Systems & Dissection","Explore external morphology and internal organ systems interactively.","explore");
        addCard("Assessment","Open the local assessment module without any account or network connection.","assessment");
        addCard("Progress & Review","Review completed learning and saved progress stored on this device.","review");

        Button privacy=button("About & Privacy");
        privacy.setOnClickListener(v->startActivity(new Intent(this,PrivacyActivity.class)));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));p.setMargins(0,dp(18),0,0);
        body.addView(privacy,p);

        TextView footer=text("Government Arts and Science College, Nagercoil\nVersion "+BuildConfig.VERSION_NAME+" · Offline · No account · No ads",13,Color.rgb(159,190,181),false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0,dp(22),0,0);
        body.addView(footer);
    }

    private void addCard(String title,String detail,String action) {
        LinearLayout card=new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18),dp(16),dp(18),dp(16));
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.rgb(11,35,34));bg.setCornerRadius(dp(16));bg.setStroke(dp(1),Color.rgb(49,93,88));
        card.setBackground(bg);
        card.setClickable(true);card.setFocusable(true);
        TextView t=text(title,19,Color.WHITE,true);card.addView(t);
        TextView d=text(detail,14,Color.rgb(185,211,203),false);d.setPadding(0,dp(5),0,0);card.addView(d);
        card.setOnClickListener(v->openLab(action));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,dp(12));
        body.addView(card,lp);
    }

    private void openLab(String action) {
        Intent i=new Intent(this,MainActivity.class);
        i.putExtra(MainActivity.EXTRA_LAUNCH_ACTION,action);
        startActivity(i);
    }

    private Button button(String label) {
        Button b=new Button(this);
        b.setText(label);b.setTextSize(16);b.setAllCaps(false);
        b.setTextColor(Color.rgb(2,19,16));b.setBackgroundColor(Color.rgb(56,214,188));
        return b;
    }

    private TextView text(String value,int sp,int color,boolean bold) {
        TextView t=new TextView(this);t.setText(value);t.setTextSize(sp);t.setTextColor(color);
        if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);
        return t;
    }

    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
