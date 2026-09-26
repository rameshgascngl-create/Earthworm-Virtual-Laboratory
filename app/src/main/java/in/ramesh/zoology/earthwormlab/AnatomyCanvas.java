package in.ramesh.zoology.earthwormlab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public final class AnatomyCanvas extends View {
    public interface OnStructureSelected {
        void onStructureSelected(NativeData.StructureRecord s);
    }

    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private NativeData.SystemRecord system;
    private NativeData.StructureRecord selected;
    private String selectedDisplayLabel;
    private OnStructureSelected listener;

    public AnatomyCanvas(Context context){
        super(context);
        setFocusable(true);
        setContentDescription("Interactive native earthworm anatomy diagram");
    }

    public void setSystem(NativeData.SystemRecord s){
        system=s;
        selected=null;
        selectedDisplayLabel=null;
        invalidate();
    }

    public void setListener(OnStructureSelected l){listener=l;}
    public NativeData.StructureRecord getSelected(){return selected;}
    public void setSelectedDisplayLabel(String label){
        selectedDisplayLabel=label;
        invalidate();
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float w=getWidth(),h=getHeight();
        c.drawColor(Color.rgb(8,28,28));

        if(system==null){
            drawPreparation(c,w,h);
            return;
        }

        switch(system.id){
            case "external": drawExternal(c,w,h); break;
            case "digestive": drawOpenedBody(c,w,h); drawDigestive(c,w,h); break;
            case "circulatory": drawOpenedBody(c,w,h); drawCirculatory(c,w,h); break;
            case "respiratory": drawRespiration(c,w,h); break;
            case "excretory": drawOpenedBody(c,w,h); drawExcretory(c,w,h); break;
            case "reproductive": drawOpenedBody(c,w,h); drawReproductive(c,w,h); break;
            case "nervous": drawOpenedBody(c,w,h); drawNervous(c,w,h); break;
            case "crosssection": drawCrossSection(c,w,h); break;
            default: drawPreparation(c,w,h); break;
        }

        drawHotspots(c,w,h);
    }

    private void drawPreparation(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(92,60,50));
        RectF worm=new RectF(w*.08f,h*.42f,w*.92f,h*.61f);
        c.drawRoundRect(worm,h*.095f,h*.095f,paint);
        paint.setColor(Color.rgb(56,214,188));
        paint.setTextSize(Math.max(24f,w/25f));
        c.drawText("Preparation / guided dissection",w*.12f,h*.25f,paint);
        paint.setColor(Color.rgb(185,211,203));
        paint.setTextSize(Math.max(20f,w/31f));
        c.drawText("Use the 56-action native guided module for the procedural sequence.",w*.08f,h*.76f,paint);
    }

    private void drawExternal(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(128,80,65));
        RectF worm=new RectF(w*.055f,h*.34f,w*.945f,h*.66f);
        c.drawRoundRect(worm,h*.16f,h*.16f,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(1.5f,w/300f));
        paint.setColor(Color.rgb(77,45,40));
        for(int i=1;i<42;i++){
            float x=worm.left+worm.width()*i/42f;
            c.drawLine(x,worm.top+3,x,worm.bottom-3,paint);
        }

        // Clitellar band. This schematic is not segment-proportional.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(206,143,112));
        RectF clitellum=new RectF(w*.405f,h*.34f,w*.49f,h*.66f);
        c.drawRoundRect(clitellum,10,10,paint);

        // Modern M. posthuma redescription records ventral clitellar setae.
        paint.setColor(Color.rgb(86,55,49));
        for(int i=0;i<6;i++){
            float x=clitellum.left+(i+1)*clitellum.width()/7f;
            c.drawCircle(x,h*.62f,Math.max(1.8f,w*.003f),paint);
        }

        // Separate paired genital markings flanking the male-pore region
        // (XVII and XIX in the species redescription). No scale claim is implied.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(2f,w*.004f));
        paint.setColor(Color.rgb(236,176,126));
        float[] gx={w*.53f,w*.60f};
        for(float x:gx){
            c.drawCircle(x,h*.43f,w*.010f,paint);
            c.drawCircle(x,h*.57f,w*.010f,paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawOpenedBody(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(100,61,54));
        RectF body=new RectF(w*.07f,h*.25f,w*.93f,h*.75f);
        c.drawRoundRect(body,h*.18f,h*.18f,paint);

        paint.setColor(Color.rgb(42,24,24));
        RectF cavity=new RectF(w*.10f,h*.31f,w*.90f,h*.69f);
        c.drawRoundRect(cavity,h*.14f,h*.14f,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(1.5f,w/280f));
        paint.setColor(Color.rgb(134,83,71));
        for(int i=1;i<30;i++){
            float x=cavity.left+cavity.width()*i/30f;
            c.drawLine(x,cavity.top,x,cavity.bottom,paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawDigestive(Canvas c,float w,float h){
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        // Alimentary canal from pharyngeal region posteriorly.
        paint.setStrokeWidth(w*.055f);
        paint.setColor(Color.rgb(192,125,83));
        c.drawLine(w*.12f,h*.50f,w*.86f,h*.50f,paint);

        // Muscular gizzard: shown as a thickened foregut region without
        // assigning a universal segment number.
        paint.setStrokeWidth(w*.085f);
        paint.setColor(Color.rgb(164,95,67));
        c.drawLine(w*.29f,h*.50f,w*.38f,h*.50f,paint);

        // Post-gizzard, pre-intestinal glandular teaching region.
        paint.setStrokeWidth(w*.075f);
        paint.setColor(Color.rgb(208,145,83));
        c.drawLine(w*.39f,h*.50f,w*.49f,h*.50f,paint);

        // Intestine begins posteriorly.
        paint.setStrokeWidth(w*.09f);
        paint.setColor(Color.rgb(137,85,59));
        c.drawLine(w*.51f,h*.50f,w*.87f,h*.50f,paint);

        // Paired anteriorly directed intestinal caeca arising from the
        // XXVII region in the species account; diagram is not segment-scale.
        paint.setStrokeWidth(w*.025f);
        paint.setColor(Color.rgb(225,177,108));
        c.drawLine(w*.56f,h*.43f,w*.62f,h*.36f,paint);
        c.drawLine(w*.56f,h*.57f,w*.62f,h*.64f,paint);

        // Typhlosole: dorsal longitudinal infolding of posterior intestine,
        // drawn inside the intestinal profile rather than as a separate tube.
        paint.setStrokeWidth(w*.012f);
        paint.setColor(Color.rgb(231,162,96));
        Path typh=new Path();
        typh.moveTo(w*.64f,h*.47f);
        typh.cubicTo(w*.70f,h*.44f,w*.76f,h*.44f,w*.82f,h*.47f);
        c.drawPath(typh,paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawCirculatory(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Dorsal vessel.
        paint.setColor(Color.rgb(220,66,72));
        paint.setStrokeWidth(w*.014f);
        c.drawLine(w*.13f,h*.39f,w*.88f,h*.39f,paint);

        // Ventral vessel.
        paint.setColor(Color.rgb(173,42,55));
        c.drawLine(w*.13f,h*.61f,w*.88f,h*.61f,paint);

        // Supra-oesophageal vessel: restricted to the anterior oesophageal region.
        paint.setColor(Color.rgb(244,112,116));
        paint.setStrokeWidth(w*.009f);
        c.drawLine(w*.16f,h*.32f,w*.36f,h*.32f,paint);

        // Paired lateral-oesophageal collecting vessels represented as
        // parallel anterior ventrolateral channels.
        paint.setColor(Color.rgb(189,76,87));
        c.drawLine(w*.16f,h*.67f,w*.36f,h*.67f,paint);
        c.drawLine(w*.16f,h*.70f,w*.36f,h*.70f,paint);

        // Traditional four-pair heart map retained as a teaching convention.
        // The plate itself is explicitly non-segment-proportional.
        paint.setStrokeWidth(w*.018f);
        paint.setColor(Color.rgb(196,49,62));
        for(int i=0;i<4;i++){
            float x=w*(.27f+i*.045f);
            Path p=new Path();
            p.moveTo(x,h*.39f);
            p.cubicTo(x-w*.02f,h*.45f,x-w*.02f,h*.55f,x,h*.61f);
            c.drawPath(p,paint);
        }

        // Segmental capillary networks.
        paint.setStrokeWidth(w*.006f);
        paint.setColor(Color.rgb(236,100,104));
        for(int i=0;i<12;i++){
            float x=w*(.48f+i*.032f);
            c.drawLine(x,h*.39f,x-w*.01f,h*.31f,paint);
            c.drawLine(x,h*.61f,x+w*.01f,h*.69f,paint);
        }

        // Subneural vessel: below the ventral nerve-cord level.
        paint.setColor(Color.rgb(139,44,72));
        paint.setStrokeWidth(w*.008f);
        c.drawLine(w*.45f,h*.72f,w*.86f,h*.72f,paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawRespiration(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.rgb(138,82,70));
        c.drawRoundRect(new RectF(w*.10f,h*.28f,w*.90f,h*.72f),30,30,paint);

        paint.setColor(Color.rgb(99,165,153));
        c.drawRoundRect(new RectF(w*.10f,h*.25f,w*.90f,h*.32f),20,20,paint);

        paint.setColor(Color.rgb(205,126,100));
        c.drawRect(w*.10f,h*.32f,w*.90f,h*.45f,paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w*.012f);
        paint.setColor(Color.rgb(212,54,65));
        Path cap=new Path();
        cap.moveTo(w*.18f,h*.52f);
        for(int i=0;i<7;i++){
            float x=w*(.18f+i*.10f);
            cap.quadTo(x+w*.04f,h*.42f,x+w*.08f,h*.52f);
        }
        c.drawPath(cap,paint);

        paint.setStrokeWidth(w*.006f);
        paint.setColor(Color.rgb(120,200,238));
        for(int i=0;i<5;i++){
            float x=w*(.25f+i*.13f);
            c.drawLine(x,h*.16f,x,h*.32f,paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawExcretory(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(w*.011f);
        paint.setColor(Color.rgb(100,211,204));

        // Pharyngeal nephridial tufts: three paired groups represented
        // in the anterior IV–VI region.
        for(int i=0;i<3;i++){
            float baseX=w*(.20f+i*.055f);
            Path p=new Path();
            p.moveTo(baseX,h*.40f);
            p.cubicTo(baseX-w*.025f,h*.46f,baseX+w*.035f,h*.54f,baseX,h*.61f);
            c.drawPath(p,paint);
        }

        // Septal nephridia: repeated posterior groups associated with septa.
        for(int i=0;i<7;i++){
            float x=w*(.48f+i*.05f);
            Path p=new Path();
            p.moveTo(x,h*.37f);
            p.cubicTo(x-w*.02f,h*.45f,x+w*.025f,h*.54f,x,h*.63f);
            c.drawPath(p,paint);
        }

        // Integumentary nephridia: numerous minute body-wall units.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(137,244,230));
        for(int i=0;i<8;i++){
            float x=w*(.55f+i*.04f);
            c.drawCircle(x,h*.66f,4,paint);
        }

        // Enlarged septal nephridium inset: nephrostomal funnel + coiled tubule.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w*.008f);
        paint.setColor(Color.rgb(180,255,245));
        c.drawCircle(w*.83f,h*.43f,w*.018f,paint);
        Path coil=new Path();
        coil.moveTo(w*.83f,h*.45f);
        coil.cubicTo(w*.80f,h*.48f,w*.88f,h*.50f,w*.84f,h*.54f);
        coil.cubicTo(w*.80f,h*.57f,w*.89f,h*.59f,w*.85f,h*.62f);
        c.drawPath(coil,paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawReproductive(Canvas c,float w,float h){
        // Horizontal anterior-to-posterior schematic; paired organs are mirrored
        // above and below the midline and are not drawn to segment scale.
        paint.setStyle(Paint.Style.FILL);

        // Four pairs of spermathecae (VI–IX).
        paint.setColor(Color.rgb(207,141,244));
        for(int i=0;i<4;i++){
            float x=w*(.20f+i*.045f);
            c.drawOval(new RectF(x-w*.012f,h*.36f,x+w*.012f,h*.43f),paint);
            c.drawOval(new RectF(x-w*.012f,h*.57f,x+w*.012f,h*.64f),paint);
        }

        // Two pairs of testes (X and XI).
        paint.setColor(Color.rgb(238,189,139));
        for(int i=0;i<2;i++){
            float x=w*(.40f+i*.038f);
            c.drawCircle(x,h*.45f,w*.010f,paint);
            c.drawCircle(x,h*.55f,w*.010f,paint);
        }

        // Two pairs of seminal vesicles (XI and XII), larger than testes.
        paint.setColor(Color.rgb(195,109,91));
        for(int i=0;i<2;i++){
            float x=w*(.46f+i*.055f);
            c.drawOval(new RectF(x-w*.024f,h*.34f,x+w*.024f,h*.45f),paint);
            c.drawOval(new RectF(x-w*.024f,h*.55f,x+w*.024f,h*.66f),paint);
        }

        // One pair of ovaries in XIII.
        paint.setColor(Color.rgb(244,198,91));
        c.drawCircle(w*.57f,h*.45f,w*.010f,paint);
        c.drawCircle(w*.57f,h*.55f,w*.010f,paint);

        // Oviducts converge toward the single female-pore pathway.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w*.007f);
        paint.setColor(Color.rgb(244,198,91));
        c.drawLine(w*.57f,h*.45f,w*.62f,h*.50f,paint);
        c.drawLine(w*.57f,h*.55f,w*.62f,h*.50f,paint);

        // Paired vasa deferentia run posteriorly.
        paint.setColor(Color.rgb(238,189,139));
        c.drawLine(w*.42f,h*.45f,w*.77f,h*.45f,paint);
        c.drawLine(w*.42f,h*.55f,w*.77f,h*.55f,paint);

        // Paired racemose prostate fields around the male region.
        paint.setStrokeWidth(w*.030f);
        paint.setColor(Color.rgb(172,95,121));
        c.drawLine(w*.66f,h*.40f,w*.74f,h*.40f,paint);
        c.drawLine(w*.66f,h*.60f,w*.74f,h*.60f,paint);

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawNervous(Canvas c,float w,float h){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(247,200,75));
        c.drawOval(new RectF(w*.15f,h*.33f,w*.21f,h*.40f),paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(w*.012f);
        paint.setColor(Color.rgb(247,200,75));
        Path ring=new Path();
        ring.moveTo(w*.18f,h*.39f);
        ring.cubicTo(w*.20f,h*.45f,w*.22f,h*.51f,w*.27f,h*.57f);
        c.drawPath(ring,paint);

        c.drawLine(w*.27f,h*.61f,w*.88f,h*.61f,paint);

        paint.setStyle(Paint.Style.FILL);
        for(int i=0;i<12;i++){
            float x=w*(.31f+i*.047f);
            c.drawCircle(x,h*.61f,w*.009f,paint);
        }
    }

    private void drawCrossSection(Canvas c,float w,float h){
        float cx=w*.50f,cy=h*.50f;
        float r=Math.min(w,h)*.36f;

        // Body wall from outside inward.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(109,68,58));
        c.drawCircle(cx,cy,r,paint);
        paint.setColor(Color.rgb(206,150,119));
        c.drawCircle(cx,cy,r*.91f,paint);
        paint.setColor(Color.rgb(155,91,73));
        c.drawCircle(cx,cy,r*.82f,paint);
        paint.setColor(Color.rgb(89,52,46));
        c.drawCircle(cx,cy,r*.68f,paint);

        // Coelomic cavity and intestine.
        paint.setColor(Color.rgb(35,28,28));
        c.drawCircle(cx,cy,r*.56f,paint);
        paint.setColor(Color.rgb(149,91,63));
        c.drawCircle(cx,cy,r*.34f,paint);
        paint.setColor(Color.rgb(30,20,20));
        c.drawCircle(cx,cy,r*.20f,paint);

        // Dorsal typhlosole: an inward fold of the intestinal wall, not a separate tube.
        paint.setColor(Color.rgb(205,132,81));
        Path typh=new Path();
        typh.moveTo(cx-r*.14f,cy-r*.18f);
        typh.quadTo(cx,cy+r*.04f,cx+r*.14f,cy-r*.18f);
        typh.quadTo(cx+r*.06f,cy-r*.10f,cx,cy-r*.02f);
        typh.quadTo(cx-r*.06f,cy-r*.10f,cx-r*.14f,cy-r*.18f);
        c.drawPath(typh,paint);

        // Dorsal vessel on the gut.
        paint.setColor(Color.rgb(220,66,72));
        c.drawCircle(cx,cy-r*.46f,r*.055f,paint);

        // Below the gut the vertical order is:
        // ventral vessel -> ventral nerve cord -> subneural vessel.
        paint.setColor(Color.rgb(173,42,55));
        c.drawCircle(cx,cy+r*.46f,r*.052f,paint);

        paint.setColor(Color.rgb(247,200,75));
        c.drawOval(new RectF(
            cx-r*.08f,cy+r*.57f,
            cx+r*.08f,cy+r*.68f),paint);

        paint.setColor(Color.rgb(197,55,76));
        c.drawCircle(cx,cy+r*.76f,r*.042f,paint);

        // Paired setae in section, shown laterally.
        paint.setColor(Color.rgb(205,189,155));
        for(int side=-1;side<=1;side+=2){
            float x=cx+side*r*.72f;
            c.drawOval(new RectF(
                x-r*.025f,cy+r*.15f,
                x+r*.025f,cy+r*.55f),paint);
        }
    }

    private void drawHotspots(Canvas c,float w,float h){
        if(system==null)return;
        paint.setStyle(Paint.Style.FILL);
        for(NativeData.StructureRecord s:system.structures){
            float x=w*s.x,y=h*s.y;
            boolean active=s==selected;
            paint.setColor(active?Color.rgb(255,240,139):Color.rgb(56,214,188));
            c.drawCircle(x,y,active?14f:9f,paint);
        }

        if(selected!=null){
            paint.setColor(Color.WHITE);
            paint.setTextSize(Math.max(22f,w/28f));
            float tx=Math.min(w*.58f,w*selected.x+18);
            float ty=Math.max(h*.11f,h*selected.y-16);
            c.drawText(selectedDisplayLabel!=null?selectedDisplayLabel:selected.name,tx,ty,paint);
        }
    }

    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()!=MotionEvent.ACTION_UP||system==null)return true;

        NativeData.StructureRecord best=null;
        double bestD=Double.MAX_VALUE;
        for(NativeData.StructureRecord s:system.structures){
            double dx=e.getX()-getWidth()*s.x;
            double dy=e.getY()-getHeight()*s.y;
            double d=dx*dx+dy*dy;
            if(d<bestD){bestD=d;best=s;}
        }

        if(best!=null&&bestD<Math.pow(Math.max(64,getWidth()*.09),2)){
            selected=best;
            invalidate();
            performClick();
            if(listener!=null)listener.onStructureSelected(best);
        }
        return true;
    }

    @Override public boolean performClick(){
        super.performClick();
        return true;
    }
}
