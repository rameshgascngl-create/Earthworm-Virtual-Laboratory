package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class PrivacyActivity extends Activity {
    static final String PUBLIC_POLICY_URL = AppPolicy.PUBLIC_POLICY_URL; // privacy.html

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
        app.setPadding(0,dp(8),0,dp(6));body.addView(app);
        body.addView(text("Package in.ramesh.zoology.earthwormlab",14,Color.rgb(185,211,203),false));

        body.addView(section("Privacy Policy",
            "Effective date: 22 September 2026. This screen is the in-app privacy policy required by the store listing. The same policy is published at the public URL below."));
        body.addView(section("Overview",
            "Earthworm Virtual Laboratory is an offline educational application for undergraduate Zoology teaching. The app works without an account, advertising, analytics or remote data collection."));
        body.addView(section("Information collected",
            "The app does not ask for or collect a student's name, email address, phone number, location, contacts, camera data, microphone recordings, photos, files or advertising identifier. The Android application requests no INTERNET permission and includes no advertising or analytics SDK."));
        body.addView(section("Data stored on this device",
            "Language preference, guided-learning progress, assessment progress and scores, review dates and study settings are stored locally so a learner can continue later. This information is not transmitted to the developer or college by the app. Android cloud backup is disabled. Local data remains until the user clears app data or uninstalls the app."));
        body.addView(section("Speech and printing",
            "Narration uses an Android text-to-speech voice installed on the device. The app selects voices that Android reports as not requiring a network connection. Printing opens the Android print service chosen by the user."));
        body.addView(section("External references",
            "Scientific reference links open only after a user tap in the device browser. The Earthworm Virtual Laboratory app itself does not load remote web content inside its laboratory view."));
        body.addView(section("Data sharing",
            "The app does not transmit student progress to the developer, the college, advertisers or analytics providers because it requests no INTERNET permission."));
        body.addView(section("Children's privacy",
            "The app does not create user accounts and does not knowingly collect personal information from children or students. Learning progress remains on the device."));

        TextView urlLabel=text("Public Privacy Policy URL",16,Color.WHITE,true);
        urlLabel.setPadding(0,dp(12),0,dp(4));
        body.addView(urlLabel);
        TextView url=text(PUBLIC_POLICY_URL,14,Color.rgb(56,214,188),false);
        url.setPadding(0,0,0,dp(10));
        body.addView(url);

        Button publicPolicy=new Button(this);
        publicPolicy.setText("View public Privacy Policy");
        publicPolicy.setAllCaps(false);publicPolicy.setTextSize(16);
        publicPolicy.setOnClickListener(v->AppPolicy.openPublicPolicy(this));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(54));lp.setMargins(0,dp(8),0,dp(10));
        body.addView(publicPolicy,lp);

        Button close=new Button(this);
        close.setText("Back to app");close.setAllCaps(false);close.setOnClickListener(v->finish());
        body.addView(close,new LinearLayout.LayoutParams(-1,dp(50)));

        TextView owner=text("Developer / Institution\nDepartment of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu\nContact: rameshgascngl@gmail.com",13,Color.rgb(159,190,181),false);
        owner.setGravity(Gravity.CENTER_HORIZONTAL);owner.setPadding(0,dp(22),0,0);body.addView(owner);
    }

    private TextView section(String title,String body){
        TextView t=text(title+"\n"+body,15,Color.rgb(225,240,235),false);
        t.setLineSpacing(0,1.15f);t.setPadding(0,dp(8),0,dp(10));
        return t;
    }

    private TextView text(String value,int sp,int color,boolean bold){
        TextView t=new TextView(this);t.setText(value);t.setTextSize(sp);t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
