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

    private boolean tamil=false;
    private ScrollView scroll;
    private LinearLayout body;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            tamil=savedInstanceState.getBoolean("tamil",false);
        }else if(getIntent()!=null){
            tamil=getIntent().getBooleanExtra(NativeHomeActivity.EXTRA_TAMIL,false);
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
            tamil?"பயன்பாடு & தனியுரிமை":"About & Privacy",
            26,Color.WHITE,true));

        TextView app=text(
            (tamil?"மண்புழு மெய்நிகர் ஆய்வகம் v":"Earthworm Virtual Laboratory v")
                +BuildConfig.VERSION_NAME,
            17,Color.rgb(56,214,188),true);
        app.setPadding(0,dp(8),0,dp(10));
        body.addView(app);

        Button lang=new Button(this);
        lang.setText("தமிழ் / English");
        lang.setAllCaps(false);
        lang.setMinHeight(dp(48));
        lang.setOnClickListener(v->{tamil=!tamil;render();});
        body.addView(lang,new LinearLayout.LayoutParams(-1,-2));

        body.addView(section(
            tamil?"தனியுரிமைச் சுருக்கம்":"Privacy summary",
            tamil
                ?"இந்தப் பயன்பாடு இணைய இணைப்பு இன்றியும் இயங்குகிறது. பயனர் கணக்கு தேவையில்லை. பெயர், இருப்பிடம், கேமரா, மைக்ரோஃபோன் அல்லது சேமிப்பக அணுகலை இது கோராது. விளம்பர அல்லது பகுப்பாய்வு SDK-கள் இதில் இல்லை."
                :"This app works offline. It does not require an account and does not request your name, location, camera, microphone or storage. It contains no advertising or analytics SDK."));

        body.addView(section(
            tamil?"இந்தச் சாதனத்தில் சேமிக்கப்படும் தகவல்":"Data stored on this device",
            tamil
                ?"கடைசியாகத் திறந்த உடற்கூறு மண்டலம், பார்வையிட்ட அமைப்புகளின் அடையாளங்கள் மற்றும் சமீபத்திய மதிப்பீட்டு மதிப்பெண் மட்டும் சாதனத்தில் உள்ளகமாகச் சேமிக்கப்படுகின்றன. வழிகாட்டும் படியின் நிலையும் மொழித் தேர்வும் அந்தச் செயற்பாடு அல்லது அமர்வு முடிந்தபின் நீண்டகாலக் கற்றல் பதிவாகச் சேமிக்கப்படாது. Android cloud backup முடக்கப்பட்டுள்ளது."
                :"The last opened anatomy system, visited structure identifiers and the latest assessment score are stored locally on this device. Guided-step position and language mode are not retained as long-term learning records after the activity/session ends. Android cloud backup is disabled."));

        body.addView(section(
            tamil?"ஒலி விளக்கம்":"Speech",
            tamil
                ?"தேர்ந்த மொழிக்கான உரை-ஒலி குரல் இணைய இணைப்பு தேவையில்லை என்று நிறுவப்பட்ட Android TTS இயந்திரம் அறிவித்தால் மட்டுமே ஒலி விளக்கம் தொடங்கும். பொருத்தமான இணையமில்லா குரல் இல்லையெனில் ஒலி விளக்கம் தொடங்காது."
                :"Narration is enabled only when an installed text-to-speech voice for the selected language reports that it does not require a network connection. If no such offline voice is installed, narration is not started."));

        body.addView(section(
            tamil?"வெளிப்புற உலாவி":"External browser",
            tamil
                ?"‘பொது தனியுரிமைக் கொள்கையைப் பார்க்க’ என்ற பொத்தானை பயனர் தொடும்போது மட்டும் திட்டத்தின் கொள்கைப் பக்கம் சாதனத்தின் வெளிப்புற இணைய உலாவியில் திறக்கப்படும். அந்த உலாவி மற்றும் இணையதளத்தின் தனியுரிமை நடைமுறைகள் தனியாகப் பொருந்தும்."
                :"The public Privacy Policy button opens this project's policy page in the device browser after a user tap. The browser and external website apply their own privacy practices."));

        body.addView(section(
            tamil?"தகவல் பகிர்வு":"Data sharing",
            tamil
                ?"இந்த Android பயன்பாடு INTERNET அனுமதியை கோராததால் மாணவரின் கற்றல் முன்னேற்றத்தை உருவாக்குநர், கல்லூரி, விளம்பரதாரர் அல்லது பகுப்பாய்வு சேவைக்கு அனுப்பாது."
                :"The app itself does not transmit student progress to the developer, the college, advertisers or analytics providers because the application requests no INTERNET permission."));

        Button publicPolicy=new Button(this);
        publicPolicy.setText(tamil?"பொது தனியுரிமைக் கொள்கையைப் பார்க்க":"View public Privacy Policy");
        publicPolicy.setAllCaps(false);
        publicPolicy.setTextSize(16);
        publicPolicy.setMinHeight(dp(48));
        publicPolicy.setPadding(dp(10),dp(8),dp(10),dp(8));
        publicPolicy.setOnClickListener(v->openPolicy());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(0,dp(18),0,dp(10));
        body.addView(publicPolicy,lp);

        Button close=new Button(this);
        close.setText(tamil?"பயன்பாட்டிற்குத் திரும்புக":"Back to app");
        close.setAllCaps(false);
        close.setMinHeight(dp(48));
        close.setPadding(dp(10),dp(8),dp(10),dp(8));
        close.setOnClickListener(v->finish());
        body.addView(close,new LinearLayout.LayoutParams(-1,-2));

        TextView owner=text(
            tamil
                ?"உருவாக்குநர் / நிறுவனம்\nவிலங்கியல் துறை, அரசு கலை மற்றும் அறிவியல் கல்லூரி, நாகர்கோவில், தமிழ்நாடு\nதொடர்பு: rameshgascngl@gmail.com"
                :"Developer / Institution\nDepartment of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu\nContact: rameshgascngl@gmail.com",
            13,Color.rgb(159,190,181),false);
        owner.setGravity(Gravity.CENTER_HORIZONTAL);
        owner.setPadding(0,dp(22),0,0);
        body.addView(owner);

        scroll.requestApplyInsets();
    }

    private void openPolicy(){
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PUBLIC_POLICY_URL))
                .addCategory(Intent.CATEGORY_BROWSABLE));
        }catch(ActivityNotFoundException e){
            android.widget.Toast.makeText(
                this,
                tamil
                    ?"பொது தனியுரிமைக் கொள்கையைத் திறக்க இணைய உலாவி கிடைக்கவில்லை."
                    :"No browser is available to open the public policy.",
                android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private TextView section(String title,String value){
        TextView t=text(title+"\n"+value,15,Color.rgb(225,240,235),false);
        t.setLineSpacing(0,1.15f);
        t.setPadding(0,dp(8),0,dp(10));
        return t;
    }

    private TextView text(String value,int sp,int color,boolean bold){
        TextView t=new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);
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
