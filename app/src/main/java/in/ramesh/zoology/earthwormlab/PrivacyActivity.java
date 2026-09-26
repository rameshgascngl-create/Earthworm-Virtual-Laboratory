package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class PrivacyActivity extends Activity {
    // Alpha QA intentionally points at the audit branch policy because production
    // main remains untouched. Release candidate promotion must switch this to the
    // final stable public policy URL after the policy is merged/published.
    static final String PUBLIC_POLICY_URL =
        "https://github.com/rameshgascngl-create/Earthworm-Virtual-Laboratory/blob/native/v2.0.0-alpha1/PRIVACY.md";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(6,21,21));
        LinearLayout body=new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(22),dp(28),dp(22),dp(28));
        scroll.addView(body,new ScrollView.LayoutParams(-1,-1));
        setContentView(scroll);

        if(android.os.Build.VERSION.SDK_INT>=30){
            getWindow().setDecorFitsSystemWindows(false);
            scroll.setOnApplyWindowInsetsListener((v,insets)->{
                android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());
                body.setPadding(dp(22)+bars.left,dp(28)+bars.top,dp(22)+bars.right,dp(28)+bars.bottom);
                return insets;
            });
            scroll.requestApplyInsets();
        }

        body.addView(text("About & Privacy",26,Color.WHITE,true));
        TextView app=text("Earthworm Virtual Laboratory v"+BuildConfig.VERSION_NAME,17,Color.rgb(56,214,188),true);
        app.setPadding(0,dp(8),0,dp(14));body.addView(app);

        body.addView(section("Privacy summary",
            "This app works offline. It does not require an account and does not request your name, location, camera, microphone or storage. It contains no advertising or analytics SDK."));
        body.addView(section("Data stored on this device",
            "The last opened anatomy system, visited structure identifiers and the latest assessment score are stored locally on this device. Guided-step position and language mode are not retained after the activity/session ends. Android cloud backup is disabled."));
        body.addView(section("Speech",
            "Narration is enabled only when an installed text-to-speech voice for the selected language reports that it does not require a network connection. If no such offline voice is installed, narration is not started."));
        body.addView(section("External browser",
            "The public Privacy Policy button opens this project's policy page in the device browser after a user tap. The browser and external website apply their own privacy practices."));
        body.addView(section("Data sharing",
            "The app itself does not transmit student progress to the developer, the college, advertisers or analytics providers because the application requests no INTERNET permission."));

        Button publicPolicy=new Button(this);
        publicPolicy.setText("View public Privacy Policy");
        publicPolicy.setAllCaps(false);publicPolicy.setTextSize(16);
        publicPolicy.setOnClickListener(v->openPolicy());
        publicPolicy.setMinHeight(dp(48));publicPolicy.setPadding(dp(10),dp(8),dp(10),dp(8));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(18),0,dp(10));
        body.addView(publicPolicy,lp);

        Button close=new Button(this);
        close.setText("Back to app");close.setAllCaps(false);close.setOnClickListener(v->finish());
        close.setMinHeight(dp(48));close.setPadding(dp(10),dp(8),dp(10),dp(8));body.addView(close,new LinearLayout.LayoutParams(-1,-2));

        TextView owner=text("Developer / Institution\nDepartment of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu\nContact: rameshgascngl@gmail.com",13,Color.rgb(159,190,181),false);
        owner.setGravity(Gravity.CENTER_HORIZONTAL);owner.setPadding(0,dp(22),0,0);body.addView(owner);
    }

    private void openPolicy(){
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PUBLIC_POLICY_URL))
                .addCategory(Intent.CATEGORY_BROWSABLE));
        }catch(ActivityNotFoundException e){
            android.widget.Toast.makeText(this,"No browser is available to open the public policy.",android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private TextView section(String title,String body){
        TextView t=text(title+"\n"+body,15,Color.rgb(225,240,235),false);
        t.setLineSpacing(0,1.15f);t.setPadding(0,dp(8),0,dp(10));
        return t;
    }

    private TextView text(String value,int sp,int color,boolean bold){
        TextView t=new TextView(this);t.setText(value);t.setTextSize(sp);t.setTextColor(color);
        if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);
        return t;
    }
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
