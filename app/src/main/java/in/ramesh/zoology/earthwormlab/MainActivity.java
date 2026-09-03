package in.ramesh.zoology.earthwormlab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.webkit.JavaScriptReplyProxy;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/** Offline source candidate. Build, instrumentation, and device acceptance are required before release. */
public final class MainActivity extends Activity {
    private static final String ORIGIN = "https://appassets.androidplatform.net";
    static final String START_URL = ORIGIN + "/assets/index.html";
    private FrameLayout root;
    private WebView webView;
    private TextToSpeech tts;
    private boolean speechReady;
    private boolean speechInitialised;
    private String currentUtteranceId;
    private JavaScriptReplyProxy currentSpeechReply;
    private boolean printing;
    private boolean backPending;
    private int backRequest;
    private Runnable backTimeout;
    private int printRequest;
    private JavaScriptReplyProxy printReply;
    private android.window.OnBackInvokedCallback backCallback;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(6,21,21));
        setContentView(root);
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets bars = insets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
            root.requestApplyInsets();
        }
        if (Build.VERSION.SDK_INT >= 33) {
            backCallback = this::handleBack;
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT, backCallback);
        }
        try {
            tts = new TextToSpeech(this, status -> {
                speechInitialised = true;
                speechReady = status == TextToSpeech.SUCCESS;
                if (!speechReady || tts == null) return;
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override public void onStart(String id) { speechEvent(id,"voice-ready"); }
                    @Override public void onDone(String id) { speechEvent(id,"voice-ended"); }
                    @Override public void onError(String id) { speechEvent(id,"voice-error"); }
                    @Override public void onError(String id, int errorCode) { speechEvent(id,"voice-error"); }
                    @Override public void onStop(String id, boolean interrupted) { speechEvent(id,"voice-cancelled"); }
                });
            });
        } catch (RuntimeException ignored) { speechReady = false;speechInitialised = true; }
        createWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {
        root.removeAllViews();
        try {
            webView = new WebView(this);
        } catch (RuntimeException unavailable) {
            showRecovery("Android System WebView is unavailable. Enable or update it, then reopen the laboratory.");
            return;
        }
        webView.setBackgroundColor(Color.rgb(6,21,21));
        root.addView(webView, new FrameLayout.LayoutParams(-1,-1));
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setGeolocationEnabled(false);
        settings.setBuiltInZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);
        WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                if (view != webView || !START_URL.equals(url)) return;
                view.evaluateJavascript("Boolean(window.EarthwormApp)", ready -> {
                    if (view == webView && !"true".equals(ready))
                        showRecovery("The lesson could not initialise. Update Android System WebView and retry.");
                });
            }
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // There is no network fallback for unmatched or missing local resources.
                WebResourceResponse response = loader.shouldInterceptRequest(request.getUrl());
                if (response != null) return response;
                return new WebResourceResponse("text/plain", "UTF-8", 403, "Blocked",
                    Collections.emptyMap(), new ByteArrayInputStream(new byte[0]));
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (view != webView) return true;
                Uri uri = request.getUrl();
                if ("https".equals(uri.getScheme()) && "appassets.androidplatform.net".equals(uri.getHost()) && (uri.getPort() == -1 || uri.getPort() == 443) && "/assets/index.html".equals(uri.getPath()) && uri.getQuery() == null) return false;
                if (request.isForMainFrame() && request.hasGesture() && "https".equals(uri.getScheme())) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)); }
                    catch (ActivityNotFoundException absent) { toast("No browser is available to open this reference."); }
                }
                return true;
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (view == webView && request.isForMainFrame()) showRecovery("The local lesson could not load. Retry without clearing your saved progress.");
            }
            @Override public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                if (view != webView) { root.removeView(view);view.destroy();return true; }
                stopSpeech();
                root.removeView(view);
                view.destroy();
                webView = null;
                showRecovery("The lesson renderer stopped. Reopen the lesson to restore saved progress.");
                return true;
            }
        });
        if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
            WebViewCompat.addWebMessageListener(webView, "EarthwormNative", Collections.singleton(ORIGIN),
                (view, message, sourceOrigin, isMainFrame, reply) -> {
                    if (view != webView || !isMainFrame || !ORIGIN.equals(sourceOrigin.toString())) return;
                    try {
                        String raw = message.getData();
                        if (raw == null || raw.length() > 12000) return;
                        JSONObject payload = new JSONObject(raw);
                        switch (payload.optString("type")) {
                            case "speak": speak(payload, reply); break;
                            case "stop": stopSpeech(); break;
                            case "voices": sendVoices(reply); break;
                            case "print": printLesson(reply); break;
                            default: break;
                        }
                    } catch (Exception invalid) { /* Reject malformed messages without side effects. */ }
                });
        }
        webView.loadUrl(START_URL);
    }

    private void speak(JSONObject payload, JavaScriptReplyProxy reply) throws org.json.JSONException {
        String language = payload.optString("lang");
        String text = payload.optString("text");
        String requestId = payload.optString("id");
        if (!(language.equals("ta-IN") || language.equals("en-IN")) || text.isEmpty() || text.length() > 500 || !requestId.matches("[a-z0-9-]{1,80}")) return;
        if (!speechReady || tts == null) { sendReply(reply,new JSONObject().put("type","voice-unavailable").put("id",requestId)); return; }
        Locale locale = Locale.forLanguageTag(language);
        String requested = payload.optString("voice");
        Voice selected = null;
        Set<Voice> voices = tts.getVoices();
        if (voices != null && !requested.isEmpty()) for (Voice voice : voices) {
            if (usableVoice(voice,locale) && voice.getName().equals(requested)) { selected=voice;break; }
        }
        boolean chosenByUser = selected != null;
        if (voices != null && !chosenByUser) for (Voice voice : voices) {
            if (usableVoice(voice,locale) && (selected == null || compareVoice(voice,selected,locale)<0)) selected = voice;
        }
        if (selected == null || tts.setVoice(selected) != TextToSpeech.SUCCESS) {
            sendReply(reply,new JSONObject().put("type","voice-unavailable").put("id",requestId)); return;
        }
        float rate = (float) payload.optDouble("rate", 0.8);
        if (!Float.isFinite(rate)) rate = 0.8f;
        tts.setSpeechRate(Math.max(0.8f, Math.min(1.0f, rate)));
        tts.setPitch(1f);
        currentUtteranceId=requestId;currentSpeechReply=reply;
        int result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, requestId);
        if (result != TextToSpeech.SUCCESS) speechEvent(requestId,"voice-error");
    }

    private boolean usableVoice(Voice voice, Locale locale) {
        return !voice.isNetworkConnectionRequired()
            && voice.getLocale().getLanguage().equals(locale.getLanguage())
            && (voice.getFeatures()==null || !voice.getFeatures().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED));
    }

    private int compareVoice(Voice a, Voice b, Locale locale) {
        int exactA=a.getLocale().equals(locale)?1:0,exactB=b.getLocale().equals(locale)?1:0;
        if(exactA!=exactB)return exactB-exactA;
        int quality=Integer.compare(b.getQuality(),a.getQuality());
        if(quality!=0)return quality;
        int latency=Integer.compare(a.getLatency(),b.getLatency());
        return latency!=0?latency:a.getName().compareTo(b.getName());
    }

    private void sendVoices(JavaScriptReplyProxy reply) throws org.json.JSONException {
        if(!speechInitialised){sendReply(reply,new JSONObject().put("type","voices-loading"));return;}
        if(!speechReady||tts==null){sendReply(reply,new JSONObject().put("type","voices").put("voices",new JSONArray()));return;}
        JSONArray list=new JSONArray();
        java.util.List<Voice> sorted=new java.util.ArrayList<>();
        Set<Voice> available=tts.getVoices();
        if(available!=null)for(Voice v:available)if(!v.isNetworkConnectionRequired()&&(v.getFeatures()==null||!v.getFeatures().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)))sorted.add(v);
        sorted.sort(Comparator.comparing(Voice::getName));
        for(Voice v:sorted)list.put(new JSONObject().put("voiceURI",v.getName()).put("name",v.getName()).put("lang",v.getLocale().toLanguageTag()).put("localService",true).put("quality",v.getQuality()).put("latency",v.getLatency()));
        sendReply(reply,new JSONObject().put("type","voices").put("voices",list));
    }

    private void speechEvent(String id,String type){
        runOnUiThread(()->{
            if(id==null||!id.equals(currentUtteranceId)||currentSpeechReply==null)return;
            JavaScriptReplyProxy reply=currentSpeechReply;
            if(type.equals("voice-ended")||type.equals("voice-error")||type.equals("voice-cancelled")){currentUtteranceId=null;currentSpeechReply=null;}
            try{sendReply(reply,new JSONObject().put("type",type).put("id",id));}catch(org.json.JSONException ignored){}
        });
    }

    private void stopSpeech(){currentUtteranceId=null;currentSpeechReply=null;if(tts!=null)tts.stop();}

    private void sendReply(JavaScriptReplyProxy reply,JSONObject payload){
        if(reply==null)return;runOnUiThread(()->{try{reply.postMessage(payload.toString());}catch(RuntimeException ignored){}});
    }

    private void printLesson(JavaScriptReplyProxy reply) {
        if (printing || webView == null) return;
        PrintManager manager = (PrintManager) getSystemService(PRINT_SERVICE);
        if (manager == null) { sendPrintFinished(reply);toast("Printing is unavailable on this device."); return; }
        printing = true;
        printReply = reply;
        final int request = ++printRequest;
        try {
        PrintDocumentAdapter delegate = webView.createPrintDocumentAdapter("Earthworm-Laboratory-" + BuildConfig.VERSION_NAME);
        PrintDocumentAdapter adapter = new PrintDocumentAdapter() {
            @Override public void onStart() { delegate.onStart(); }
            @Override public void onLayout(PrintAttributes oldAttrs, PrintAttributes newAttrs, CancellationSignal signal,
                LayoutResultCallback callback, Bundle extras) { delegate.onLayout(oldAttrs,newAttrs,signal,callback,extras); }
            @Override public void onWrite(PageRange[] pages, ParcelFileDescriptor dest, CancellationSignal signal,
                WriteResultCallback callback) { delegate.onWrite(pages,dest,signal,callback); }
            @Override public void onFinish() {
                try { delegate.onFinish(); } catch (RuntimeException ignored) { }
                finally { finishPrinting(request); }
            }
        };
        manager.print("Earthworm laboratory", adapter, new PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape()).build()); }
        catch (RuntimeException failed) { finishPrinting(request);toast("The print service could not start."); }
    }
    private void sendPrintFinished(JavaScriptReplyProxy reply) {
        try { sendReply(reply,new JSONObject().put("type","print-finished")); }
        catch (org.json.JSONException ignored) { }
    }
    private void finishPrinting(int request) {
        if (request != printRequest || !printing) return;
        JavaScriptReplyProxy reply=printReply;
        printing=false;printReply=null;
        sendPrintFinished(reply);
    }

    private void showRecovery(String message) {
        if (isFinishing() || isDestroyed()) return;
        stopSpeech();clearBackRequest();finishPrinting(printRequest);
        if (webView != null) { root.removeView(webView);webView.destroy();webView=null; }
        root.removeAllViews();
        LinearLayout panel = new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(32,48,32,32);
        TextView title = new TextView(this);title.setText("Earthworm Virtual Laboratory");title.setTextSize(22);title.setTextColor(Color.WHITE);panel.addView(title);
        TextView detail = new TextView(this);detail.setText(message+"\n\n"+"பாடத்தை மீண்டும் திறக்கவும். சேமிக்கப்பட்ட முன்னேற்றம் அழிக்கப்படாது.");detail.setTextColor(Color.WHITE);detail.setTextSize(17);panel.addView(detail);
        Button retry = new Button(this);retry.setText("Reopen lesson / மீண்டும் திற");retry.setOnClickListener(v->createWebView());panel.addView(retry);
        root.addView(panel);
    }
    private void toast(String text) { Toast.makeText(this,text,Toast.LENGTH_LONG).show(); }
    private void handleBack() {
        if (backPending) return;
        if (webView == null) { finish();return; }
        backPending=true;
        final WebView source=webView;
        final int request=++backRequest;
        backTimeout=()->completeBack(source,request,false);
        root.postDelayed(backTimeout,1500);
        try { source.evaluateJavascript("window.EarthwormApp ? window.EarthwormApp.back() : false",
            handled -> completeBack(source,request,"true".equals(handled))); }
        catch (RuntimeException unavailable) { completeBack(source,request,false); }
    }
    private void completeBack(WebView source,int request,boolean handled) {
        if (request!=backRequest || source!=webView || !backPending || isFinishing() || isDestroyed()) return;
        clearBackRequest();
        if (!handled) new AlertDialog.Builder(this)
                .setTitle("Close laboratory? / ஆய்வகத்தை மூடவா?")
                .setMessage("Progress already saved on this device is retained. / சேமித்த முன்னேற்றம் பாதுகாக்கப்படும்.")
                .setNegativeButton("Stay / தொடர்க",null).setPositiveButton("Close / மூடு",(dialog,which)->finish()).show();
    }
    private void clearBackRequest() {
        backPending=false;backRequest++;
        if (backTimeout!=null) { root.removeCallbacks(backTimeout);backTimeout=null; }
    }
    @Override public void onBackPressed() { handleBack(); }
    @Override protected void onPause() {
        clearBackRequest();stopSpeech();
        if (webView != null) { webView.evaluateJavascript("window.EarthwormApp && window.EarthwormApp.pause()",null);webView.onPause(); }
        super.onPause();
    }
    @Override protected void onResume() { super.onResume();if(webView!=null)webView.onResume(); }
    @Override protected void onDestroy() {
        clearBackRequest();currentUtteranceId=null;currentSpeechReply=null;printReply=null;
        if (Build.VERSION.SDK_INT >= 33 && backCallback != null) getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(backCallback);
        if (tts != null) { tts.stop();tts.shutdown();tts=null; }
        if (webView != null) { root.removeView(webView);webView.destroy();webView=null; }
        super.onDestroy();
    }
    WebView webViewForTest() { return webView; }
}
