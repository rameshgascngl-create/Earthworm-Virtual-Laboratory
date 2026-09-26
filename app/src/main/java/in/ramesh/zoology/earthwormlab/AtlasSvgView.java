package in.ramesh.zoology.earthwormlab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

public final class AtlasSvgView extends View {
    private SVG svg;
    private int resourceId=0;
    private final RectF viewport=new RectF();

    public AtlasSvgView(Context context){
        super(context);
        init();
    }

    public AtlasSvgView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    private void init(){
        setBackgroundColor(Color.rgb(8,28,28));
        setFocusable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void setSvgResource(int resId){
        resourceId=resId;
        svg=null;
        if(resId!=0){
            try{
                svg=SVG.getFromResource(getResources(),resId);
            }catch(SVGParseException ex){
                android.util.Log.e("EarthwormAtlas","Unable to parse atlas SVG resource "+resId,ex);
            }
        }
        requestLayout();
        invalidate();
    }

    public int getSvgResource(){return resourceId;}

    @Override protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int width=MeasureSpec.getSize(widthMeasureSpec);
        if(MeasureSpec.getMode(widthMeasureSpec)==MeasureSpec.UNSPECIFIED) width=dp(360);
        int desired=Math.round(width*(560f/1200f));
        desired=Math.max(dp(190),Math.min(desired,dp(430)));
        int height=resolveSize(desired,heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(svg==null)return;
        viewport.set(0,0,getWidth(),getHeight());
        svg.renderToCanvas(canvas,viewport);
    }

    private int dp(int v){
        return Math.round(v*getResources().getDisplayMetrics().density);
    }
}
