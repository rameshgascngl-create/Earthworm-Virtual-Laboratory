package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class MicroscopyActivity extends Activity {
    private boolean tamil=false; private LinearLayout body; private ContentRepository repo;
    @Override public void onCreate(Bundle b){super.onCreate(b);repo=new ContentRepository(this);render();}
    private void render(){
        ScrollView scroll=new ScrollView(this);body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(22),dp(28),dp(22),dp(28));body.setBackgroundColor(Color.rgb(6,21,21));scroll.addView(body,new ScrollView.LayoutParams(-1,-1));setContentView(scroll);
        body.addView(text(tamil?"நுண்ணோக்கி ஆழ்பார்வை":"Microscopic deep dives",26,Color.WHITE,true));
        Button lang=new Button(this);lang.setText("தமிழ் / English");lang.setAllCaps(false);lang.setOnClickListener(v->{tamil=!tamil;render();});body.addView(lang,new LinearLayout.LayoutParams(-1,dp(50)));
        for(ContentRepository.Microscopy m:repo.microscopy()){TextView card=text((tamil&&!m.ta.isBlank()?m.ta:m.en)+"\n"+(tamil&&!m.textTa.isBlank()?m.textTa:m.textEn),15,Color.rgb(226,242,237),false);card.setLineSpacing(0,1.16f);card.setPadding(dp(14),dp(16),dp(14),dp(18));body.addView(card);}
        TextView gate=text(tamil?"பழைய SVG படம் இங்கே நோக்கமுடன் பயன்படுத்தப்படவில்லை. ஒவ்வொரு நுண்ணமைப்பிற்கும் சொந்த Android வரைபடம் மறுஉருவாக்கப்பட்ட பின்னரே படத் தரச் சமத்துவம் ஏற்கப்படும்.":"Legacy SVG markup is intentionally not rendered here. Visual parity remains blocked until each microscopic plate is reconstructed as a native Android diagram.",12,Color.rgb(244,198,91),false);body.addView(gate);
    }
    private TextView text(String v,int sp,int c,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(sp);t.setTextColor(c);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
