package in.ramesh.zoology.earthwormlab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public final class AnatomyCanvas extends View {
    public interface OnStructureSelected { void onStructureSelected(NativeData.StructureRecord s); }
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private NativeData.SystemRecord system;
    private NativeData.StructureRecord selected;
    private OnStructureSelected listener;

    public AnatomyCanvas(Context context){ super(context); setFocusable(true); setContentDescription("Interactive native earthworm anatomy diagram"); }
    public void setSystem(NativeData.SystemRecord s){ system=s; selected=null; invalidate(); }
    public void setListener(OnStructureSelected l){ listener=l; }
    public NativeData.StructureRecord getSelected(){ return selected; }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        c.drawColor(Color.rgb(10,32,31));
        paint.setColor(Color.rgb(120,73,61));
        RectF worm=new RectF(w*.06f,h*.35f,w*.94f,h*.66f);
        c.drawRoundRect(worm,h*.15f,h*.15f,paint);
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(Math.max(2f,w/220f)); paint.setColor(Color.rgb(87,47,42));
        for(int i=1;i<35;i++){float x=worm.left+worm.width()*i/35f;c.drawLine(x,worm.top+3,x,worm.bottom-3,paint);}
        paint.setStyle(Paint.Style.FILL);
        if(system==null)return;
        for(NativeData.StructureRecord s:system.structures){
            float x=w*s.x,y=h*s.y;
            paint.setColor(s==selected?Color.rgb(255,240,139):Color.rgb(56,214,188));
            c.drawCircle(x,y,s==selected?15f:11f,paint);
            paint.setColor(Color.WHITE); paint.setTextSize(Math.max(24f,w/26f));
            c.drawText(s.name,x+16,y-10,paint);
        }
    }

    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()!=MotionEvent.ACTION_UP||system==null)return true;
        NativeData.StructureRecord best=null; double bestD=Double.MAX_VALUE;
        for(NativeData.StructureRecord s:system.structures){
            double dx=e.getX()-getWidth()*s.x,dy=e.getY()-getHeight()*s.y,d=dx*dx+dy*dy;
            if(d<bestD){bestD=d;best=s;}
        }
        if(best!=null && bestD<Math.pow(Math.max(72,getWidth()*.11),2)){
            selected=best; invalidate(); performClick(); if(listener!=null)listener.onStructureSelected(best);
        }
        return true;
    }
    @Override public boolean performClick(){ super.performClick(); return true; }
}
