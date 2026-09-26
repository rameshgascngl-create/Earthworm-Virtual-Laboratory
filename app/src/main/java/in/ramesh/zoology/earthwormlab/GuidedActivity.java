package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;

public final class GuidedActivity extends Activity {
    private List<ContentRepository.GuidedAction> actions; private int index=0; private boolean tamil=false;
    private TextView title,meta,instruction; private Button prev,next,lang;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);ContentRepository repo=new ContentRepository(this);actions=repo.guidedActions();
        ScrollView scroll=new ScrollView(this);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(22),dp(28),dp(22),dp(28));root.setBackgroundColor(Color.rgb(6,21,21));scroll.addView(root,new ScrollView.LayoutParams(-1,-1));setContentView(scroll);
        title=text("",24,Color.WHITE,true);root.addView(title);meta=text("",14,Color.rgb(56,214,188),true);meta.setPadding(0,dp(10),0,dp(12));root.addView(meta);
        instruction=text("",17,Color.rgb(226,242,237),false);instruction.setLineSpacing(0,1.18f);root.addView(instruction);
        lang=new Button(this);lang.setAllCaps(false);lang.setText("தமிழ் / English");lang.setOnClickListener(v->{tamil=!tamil;show();});root.addView(lang,new LinearLayout.LayoutParams(-1,dp(50)));
        LinearLayout nav=new LinearLayout(this);prev=new Button(this);next=new Button(this);prev.setText("Previous");next.setText("Next");prev.setOnClickListener(v->{if(index>0){index--;show();}});next.setOnClickListener(v->{if(index<actions.size()-1){index++;show();}});nav.addView(prev,new LinearLayout.LayoutParams(0,dp(54),1));nav.addView(next,new LinearLayout.LayoutParams(0,dp(54),1));root.addView(nav);
        show();
    }
    private void show(){ContentRepository.GuidedAction a=actions.get(index);title.setText((tamil?"வழிகாட்டும் செய்முறை ":"Guided practical ")+(index+1)+"/"+actions.size());meta.setText((tamil?"மண்டலம்: ":"System: ")+a.system+"   ·   "+(tamil?"கருவி: ":"Tool: ")+a.tool+"   ·   "+(tamil?"இலக்கு: ":"Target: ")+a.target);instruction.setText(a.instruction(tamil));prev.setEnabled(index>0);next.setEnabled(index<actions.size()-1);}
    private TextView text(String v,int sp,int c,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(sp);t.setTextColor(c);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
