package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class MicroscopyActivity extends Activity {
    private boolean tamil=false;
    private ContentRepository repo;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        repo=new ContentRepository(this);
        if(b!=null)tamil=b.getBoolean("tamil",false);
        else if(getIntent()!=null)tamil=getIntent().getBooleanExtra(NativeHomeActivity.EXTRA_TAMIL,false);
        render();
    }

    private void render(){
        ScrollView scroll=new ScrollView(this);
        LinearLayout body=new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(22),dp(28),dp(22),dp(28));
        body.setBackgroundColor(Color.rgb(6,21,21));
        scroll.addView(body,new ScrollView.LayoutParams(-1,-1));
        setContentView(scroll);

        body.addView(text(
            tamil?"நுண்ணோக்கி ஆழ்பார்வை":"Microscopic deep dives",
            26,Color.WHITE,true));

        Button lang=new Button(this);
        lang.setText("தமிழ் / English");
        lang.setAllCaps(false);
        lang.setOnClickListener(v->{tamil=!tamil;render();});
        lang.setMinHeight(dp(48));body.addView(lang,new LinearLayout.LayoutParams(-1,-2));

        for(ContentRepository.Microscopy m:repo.microscopy()){
            TextView title=text(
                tamil&&!m.ta.trim().isEmpty()?m.ta:m.en,
                19,Color.rgb(56,214,188),true);
            title.setPadding(0,dp(20),0,dp(8));
            body.addView(title);

            MicroscopyCanvas plate=new MicroscopyCanvas(this);
            plate.setLessonId(m.id);
            plate.setTamil(tamil);
            plate.setContentDescription(
                (tamil&&!m.ta.trim().isEmpty()?m.ta:m.en)
                +(tamil?" — சொந்த Android நுண்ணமைப்பு விளக்கப்படம்":" — native Android microscopy diagram"));
            LinearLayout.LayoutParams plateParams=
                new LinearLayout.LayoutParams(-1,dp(260));
            plateParams.setMargins(0,0,0,dp(10));
            body.addView(plate,plateParams);

            TextView description=text(
                tamil&&!m.textTa.trim().isEmpty()?m.textTa:m.textEn,
                15,Color.rgb(226,242,237),false);
            description.setLineSpacing(0,1.16f);
            description.setPadding(dp(10),0,dp(10),dp(12));
            body.addView(description);
        }

        TextView gate=text(
            tamil
                ?"ஒன்பது ஆழ்பார்வைப் படங்களும் தற்போது Android Canvas-ல் சொந்தமாக வரையப்படுகின்றன. உற்பத்தி வெளியீட்டிற்கு முன் ஒவ்வொரு படமும் கல்வியியல் மற்றும் கருவி QA-வில் தனித்தனியாக ஏற்கப்பட வேண்டும்."
                :"All nine deep-dive plates are now rendered natively with Android Canvas. Each plate still requires individual academic and physical-device QA before production acceptance.",
            12,Color.rgb(244,198,91),false);
        gate.setPadding(0,dp(18),0,0);
        body.addView(gate);
    }

    @Override protected void onSaveInstanceState(Bundle out){
        out.putBoolean("tamil",tamil);
        super.onSaveInstanceState(out);
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

    private int dp(int v){
        return Math.round(v*getResources().getDisplayMetrics().density);
    }
}
