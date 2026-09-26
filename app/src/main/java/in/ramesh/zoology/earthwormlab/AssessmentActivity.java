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

public final class AssessmentActivity extends Activity {
    private List<ContentRepository.Question> questions; private int index=0,score=0; private boolean tamil=false;
    private ContentRepository repo;
    private TextView question,meta,feedback; private LinearLayout answers; private Button lang;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        repo=new ContentRepository(this);
        questions=repo.questions();
        if(b==null){
            tamil=getIntent()!=null && getIntent().getBooleanExtra(NativeHomeActivity.EXTRA_TAMIL,false);
        }
        if(b!=null){
            index=Math.max(0,Math.min(b.getInt("index",0),questions.size()));
            score=Math.max(0,b.getInt("score",0));
            tamil=b.getBoolean("tamil",false);
        }
        ScrollView scroll=new ScrollView(this);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(22),dp(28),dp(22),dp(28));root.setBackgroundColor(Color.rgb(6,21,21));scroll.addView(root,new ScrollView.LayoutParams(-1,-1));setContentView(scroll);
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
        meta=text("",14,Color.rgb(56,214,188),true);root.addView(meta);question=text("",21,Color.WHITE,true);question.setPadding(0,dp(10),0,dp(12));root.addView(question);
        answers=new LinearLayout(this);answers.setOrientation(LinearLayout.VERTICAL);root.addView(answers);
        feedback=text("",14,Color.rgb(244,198,91),false);feedback.setPadding(0,dp(12),0,dp(12));root.addView(feedback);
        lang=new Button(this);lang.setText("தமிழ் / English");lang.setAllCaps(false);lang.setOnClickListener(v->{tamil=!tamil;show();});lang.setMinHeight(dp(48));root.addView(lang,new LinearLayout.LayoutParams(-1,-2));show();
    }
    private void show(){
        answers.removeAllViews();feedback.setText("");
        if(index>=questions.size()){new ProgressStore(this).saveScore(score);meta.setText(tamil?"மதிப்பீடு முடிந்தது":"Assessment complete");question.setText(score+" / "+questions.size());return;}
        ContentRepository.Question q=questions.get(index);
        meta.setText((index+1)+" / "+questions.size()+" · "+repo.systemName(q.system,tamil));
        question.setText(tamil&&!q.ta.trim().isEmpty()?q.ta:q.en);
        List<String> opts=tamil&&!q.oTa.isEmpty()?q.oTa:q.oEn;
        for(int i=0;i<opts.size();i++){final int pick=i;Button b=new Button(this);b.setAllCaps(false);b.setText(opts.get(i));b.setOnClickListener(v->answer(pick));b.setMinHeight(dp(48));b.setPadding(dp(10),dp(8),dp(10),dp(8));answers.addView(b,new LinearLayout.LayoutParams(-1,-2));}
    }
    private void answer(int pick){ContentRepository.Question q=questions.get(index);boolean ok=pick==q.answer;if(ok)score++;feedback.setText(ok?(tamil?"சரி":"Correct"):(tamil?"தவறு":"Incorrect"));index++;question.postDelayed(this::show,500);}
    @Override protected void onSaveInstanceState(Bundle out){
        out.putInt("index",index);
        out.putInt("score",score);
        out.putBoolean("tamil",tamil);
        super.onSaveInstanceState(out);
    }

    private TextView text(String v,int sp,int c,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(sp);t.setTextColor(c);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
