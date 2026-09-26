package in.ramesh.zoology.earthwormlab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/**
 * Native schematic renderer for the nine reviewed microscopic/deep-dive lessons.
 * No SVG, HTML, WebView, or JavaScript rendering is used.
 */
public final class MicroscopyCanvas extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private String lessonId="gizzard";

    public MicroscopyCanvas(Context context){
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    public void setLessonId(String id){
        lessonId=id==null?"gizzard":id;
        setContentDescription("Native microscopy diagram: "+lessonId);
        invalidate();
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        c.drawColor(Color.rgb(12,35,34));
        switch(lessonId){
            case "gizzard": drawGizzard(c,w,h); break;
            case "typhlosole": drawTyphlosole(c,w,h); break;
            case "nephridium": drawNephridium(c,w,h); break;
            case "spermatheca": drawSpermatheca(c,w,h); break;
            case "skin": drawSkin(c,w,h); break;
            case "heart": drawHeart(c,w,h); break;
            case "ganglion": drawGanglion(c,w,h); break;
            case "gonads": drawGonads(c,w,h); break;
            case "bodywall": drawBodyWall(c,w,h); break;
            default: drawPlaceholder(c,w,h);
        }
    }

    private void drawGizzard(Canvas c,float w,float h){
        float cx=w*.5f,cy=h*.52f,r=Math.min(w,h)*.34f;
        fill(c,Color.rgb(87,43,46),cx,cy,r);
        fill(c,Color.rgb(186,99,76),cx,cy,r*.74f);
        fill(c,Color.rgb(42,27,29),cx,cy,r*.30f);
        strokeCircle(c,Color.rgb(255,218,174),cx,cy,r*.45f,w*.018f);
        label(c,"muscular wall",w*.08f,h*.15f);
        label(c,"lumen",w*.43f,h*.55f);
    }

    private void drawTyphlosole(Canvas c,float w,float h){
        float cx=w*.5f,cy=h*.52f,r=Math.min(w,h)*.35f;
        fill(c,Color.rgb(116,66,57),cx,cy,r);
        fill(c,Color.rgb(38,27,28),cx,cy,r*.72f);
        p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(218,139,81));
        Path fold=new Path();
        fold.moveTo(cx-r*.58f,cy-r*.43f);
        fold.quadTo(cx,cy+r*.70f,cx+r*.58f,cy-r*.43f);
        fold.quadTo(cx+r*.23f,cy-r*.24f,cx,cy+r*.22f);
        fold.quadTo(cx-r*.23f,cy-r*.24f,cx-r*.58f,cy-r*.43f);
        c.drawPath(fold,p);
        label(c,"dorsal typhlosolar fold",w*.08f,h*.15f);
        label(c,"intestinal lumen",w*.36f,h*.84f);
    }

    private void drawNephridium(Canvas c,float w,float h){
        p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(w*.018f);p.setColor(Color.rgb(109,219,210));
        Path coil=new Path();coil.moveTo(w*.18f,h*.52f);
        coil.cubicTo(w*.25f,h*.18f,w*.42f,h*.86f,w*.52f,h*.48f);
        coil.cubicTo(w*.61f,h*.16f,w*.72f,h*.82f,w*.82f,h*.46f);
        c.drawPath(coil,p);
        p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(201,255,249));
        c.drawCircle(w*.16f,h*.51f,w*.035f,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(w*.008f);
        for(int i=0;i<8;i++){
            double a=Math.PI*2*i/8.0;
            float x=(float)(w*.16+Math.cos(a)*w*.055);
            float y=(float)(h*.51+Math.sin(a)*h*.09);
            c.drawLine(w*.16f,h*.51f,x,y,p);
        }
        label(c,"nephrostome",w*.06f,h*.22f);
        label(c,"coiled tubule",w*.55f,h*.18f);
    }

    private void drawSpermatheca(Canvas c,float w,float h){
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(190,130,220));
        c.drawOval(new RectF(w*.31f,h*.23f,w*.69f,h*.68f),p);
        p.setColor(Color.rgb(79,44,83));
        c.drawOval(new RectF(w*.39f,h*.31f,w*.61f,h*.58f),p);
        p.setColor(Color.rgb(218,173,237));
        c.drawRoundRect(new RectF(w*.47f,h*.64f,w*.54f,h*.88f),18,18,p);
        label(c,"ampulla",w*.09f,h*.21f);
        label(c,"duct",w*.58f,h*.83f);
    }

    private void drawSkin(Canvas c,float w,float h){
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(102,180,165));c.drawRect(w*.08f,h*.18f,w*.92f,h*.27f,p);
        p.setColor(Color.rgb(210,137,104));c.drawRect(w*.08f,h*.27f,w*.92f,h*.43f,p);
        p.setColor(Color.rgb(138,75,66));c.drawRect(w*.08f,h*.43f,w*.92f,h*.78f,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(w*.012f);p.setColor(Color.rgb(221,61,69));
        Path cap=new Path();cap.moveTo(w*.17f,h*.52f);
        for(int i=0;i<6;i++){float x=w*(.17f+i*.115f);cap.quadTo(x+w*.05f,h*.42f,x+w*.10f,h*.52f);}c.drawPath(cap,p);
        p.setStrokeWidth(w*.007f);p.setColor(Color.rgb(128,205,240));
        for(int i=0;i<5;i++){float x=w*(.24f+i*.13f);c.drawLine(x,h*.08f,x,h*.29f,p);}
        label(c,"mucus film",w*.08f,h*.12f);
        label(c,"subepidermal capillaries",w*.38f,h*.72f);
    }

    private void drawHeart(Canvas c,float w,float h){
        p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(w*.035f);p.setColor(Color.rgb(214,57,69));
        c.drawLine(w*.20f,h*.18f,w*.20f,h*.82f,p);
        c.drawLine(w*.80f,h*.18f,w*.80f,h*.82f,p);
        Path arch=new Path();arch.moveTo(w*.20f,h*.33f);
        arch.cubicTo(w*.38f,h*.20f,w*.42f,h*.72f,w*.58f,h*.61f);
        arch.cubicTo(w*.72f,h*.52f,w*.67f,h*.34f,w*.80f,h*.30f);
        c.drawPath(arch,p);
        p.setStrokeWidth(w*.012f);p.setColor(Color.rgb(255,222,224));
        c.drawLine(w*.43f,h*.42f,w*.49f,h*.48f,p);
        c.drawLine(w*.43f,h*.54f,w*.49f,h*.48f,p);
        label(c,"contractile vascular arch",w*.08f,h*.12f);
    }

    private void drawGanglion(Canvas c,float w,float h){
        p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(247,200,75));
        c.drawOval(new RectF(w*.30f,h*.20f,w*.70f,h*.52f),p);
        p.setColor(Color.rgb(126,92,34));
        c.drawOval(new RectF(w*.41f,h*.29f,w*.59f,h*.43f),p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(w*.022f);p.setColor(Color.rgb(247,200,75));
        c.drawLine(w*.46f,h*.50f,w*.43f,h*.86f,p);
        c.drawLine(w*.54f,h*.50f,w*.57f,h*.86f,p);
        p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(255,231,135));
        for(int i=0;i<7;i++)c.drawCircle(w*(.36f+i*.045f),h*.27f,w*.009f,p);
        label(c,"ganglion",w*.08f,h*.18f);
        label(c,"paired longitudinal cords",w*.48f,h*.91f);
    }

    private void drawGonads(Canvas c,float w,float h){
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(237,190,142));
        c.drawOval(new RectF(w*.18f,h*.28f,w*.34f,h*.48f),p);
        c.drawOval(new RectF(w*.36f,h*.28f,w*.52f,h*.48f),p);
        p.setColor(Color.rgb(240,204,94));
        c.drawCircle(w*.63f,h*.39f,w*.045f,p);
        c.drawCircle(w*.74f,h*.39f,w*.045f,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(w*.010f);p.setColor(Color.rgb(229,161,116));
        c.drawLine(w*.26f,h*.48f,w*.60f,h*.76f,p);
        c.drawLine(w*.44f,h*.48f,w*.60f,h*.76f,p);
        p.setColor(Color.rgb(244,198,91));
        c.drawLine(w*.63f,h*.43f,w*.68f,h*.72f,p);
        c.drawLine(w*.74f,h*.43f,w*.68f,h*.72f,p);
        label(c,"testicular region",w*.07f,h*.22f);
        label(c,"ovarian region",w*.59f,h*.22f);
    }

    private void drawBodyWall(Canvas c,float w,float h){
        float left=w*.15f,right=w*.85f,top=h*.12f;
        float[] heights={.07f,.09f,.13f,.18f,.08f};
        int[] colors={
            Color.rgb(218,181,145),
            Color.rgb(205,135,104),
            Color.rgb(170,94,79),
            Color.rgb(121,67,61),
            Color.rgb(213,176,131)
        };
        float y=top;
        for(int i=0;i<heights.length;i++){
            float hh=h*heights[i];
            p.setStyle(Paint.Style.FILL);p.setColor(colors[i]);
            c.drawRect(left,y,right,y+hh,p);y+=hh;
        }
        p.setColor(Color.rgb(28,25,25));
        c.drawRect(left,y,right,h*.86f,p);
        label(c,"cuticle",w*.04f,h*.17f);
        label(c,"epidermis",w*.04f,h*.27f);
        label(c,"circular muscle",w*.04f,h*.39f);
        label(c,"longitudinal muscle",w*.04f,h*.55f);
        label(c,"peritoneum",w*.04f,h*.67f);
        label(c,"coelom",w*.58f,h*.82f);
    }

    private void drawPlaceholder(Canvas c,float w,float h){
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(w*.01f);p.setColor(Color.rgb(56,214,188));
        c.drawRect(w*.20f,h*.20f,w*.80f,h*.80f,p);
        label(c,"Native diagram",w*.33f,h*.52f);
    }

    private void fill(Canvas c,int color,float x,float y,float r){
        p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawCircle(x,y,r,p);
    }

    private void strokeCircle(Canvas c,int color,float x,float y,float r,float width){
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(width);p.setColor(color);c.drawCircle(x,y,r,p);
    }

    private void label(Canvas c,String value,float x,float y){
        p.setStyle(Paint.Style.FILL);p.setColor(Color.WHITE);
        p.setTextSize(Math.max(20f,getWidth()/31f));
        c.drawText(value,x,y,p);
    }
}
