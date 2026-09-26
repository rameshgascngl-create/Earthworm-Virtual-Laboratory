package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class AssessmentActivity extends Activity {
    private final String[][] questions = {
        {"Which structure increases intestinal absorptive surface area?","Typhlosole","Gizzard","Clitellum","Typhlosole"},
        {"The female genital pore is classically described on which segment?","14","18","10","14"},
        {"Which organs receive sperm from the mating partner?","Spermathecae","Seminal vesicles","Ovaries","Spermathecae"},
        {"Which vessel is the principal dorsal longitudinal vessel?","Dorsal blood vessel","Ventral nerve cord","Vas deferens","Dorsal blood vessel"}
    };
    private int index=0,score=0;
    private TextView q,feedback;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(22),dp(28),dp(22),dp(28));root.setBackgroundColor(Color.rgb(6,21,21));
        q=text("",22,Color.WHITE,true);root.addView(q);
        feedback=text("",15,Color.rgb(244,198,91),false);feedback.setPadding(0,dp(12),0,dp(12));root.addView(feedback);
        for(int i=1;i<=3;i++){ final int answerIndex=i; Button btn=new Button(this); btn.setAllCaps(false); btn.setOnClickListener(v->answer(answerIndex)); root.addView(btn,new LinearLayout.LayoutParams(-1,dp(56))); btn.setTag("answer"+i); }
        setContentView(root);showQuestion();
    }
    private void showQuestion(){
        if(index>=questions.length){new ProgressStore(this).saveScore(score);q.setText("Assessment complete: "+score+"/"+questions.length);feedback.setText("This alpha contains only a native smoke-test question set. Full 72-question parity is a release blocker.");return;}
        String[] row=questions[index];q.setText(row[0]);feedback.setText("");
        LinearLayout root=(LinearLayout)q.getParent();
        for(int i=1;i<=3;i++){Button b=(Button)root.findViewWithTag("answer"+i);b.setText(row[i]);b.setEnabled(true);}
    }
    private void answer(int i){
        if(index>=questions.length)return;String[] row=questions[index];if(row[i].equals(row[4])){score++;feedback.setText("Correct");}else feedback.setText("Correct answer: "+row[4]);
        index++; q.postDelayed(this::showQuestion,450);
    }
    private TextView text(String v,int sp,int c,boolean bold){TextView t=new TextView(this);t.setText(v);t.setTextSize(sp);t.setTextColor(c);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,android.graphics.Typeface.BOLD);return t;}
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
}
