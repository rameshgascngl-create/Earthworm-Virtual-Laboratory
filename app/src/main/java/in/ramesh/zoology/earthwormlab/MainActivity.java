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
import android.widget.ScrollView;
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
    static final String PRIVACY_URL = "https://github.com/rameshgascngl-create/Earthworm-Virtual-Laboratory/blob/main/PRIVACY.md";
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
    private boolean dashboardVisible;
    private String pendingNativeTarget;

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
        showDashboard();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private TextView dashboardText(String text, float size, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, dp(4), 0, dp(8));
        return view;
    }

    private Button dashboardButton(String label, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(16);
        button.setMinHeight(dp(54));
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(6), 0, dp(6));
        button.setLayoutParams(lp);
        return button;
    }

    private void destroyWebView() {
        if (webView == null) return;
        try { webView.stopLoading(); } catch (RuntimeException ignored) { }
        try { webView.destroy(); } catch (RuntimeException ignored) { }
        webView = null;
    }

    private void showDashboard() {
        clearBackRequest();
        stopSpeech();
        root.removeAllViews();
        destroyWebView();
        dashboardVisible = true;
        pendingNativeTarget = null;

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(20), dp(24), dp(20), dp(30));
        panel.setBackgroundColor(Color.rgb(6,21,21));
        scroll.addView(panel, new ScrollView.LayoutParams(-1,-2));

        TextView title = dashboardText("Earthworm Virtual Laboratory", 27, Color.WHITE);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(title);

        TextView edition = dashboardText("Version " + BuildConfig.VERSION_NAME + " · Offline Android laboratory", 14, Color.rgb(185,211,203));
        edition.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        panel.addView(edition);

        TextView intro = dashboardText(
            "Interactive bilingual virtual practical for earthworm anatomy, guided dissection, assessment and revision.\n\n" +
            "தமிழ் / English · No login · No advertising · Study progress stays on this device.",
            16, Color.rgb(238,249,244));
        intro.setLineSpacing(0, 1.15f);
        panel.addView(intro);

        panel.addView(dashboardButton("Continue Laboratory / ஆய்வகத்தைத் தொடர்க",
            v -> openLaboratory("resume")));
        panel.addView(dashboardButton("Guided Dissection / வழிகாட்டும் பிரித்தாய்வு",
            v -> openLaboratory("guided")));
        panel.addView(dashboardButton("Explore Anatomy / உடற்கூறியல் ஆராய்வு",
            v -> openLaboratory("anatomy")));
        panel.addView(dashboardButton("Assessment & Revision / மதிப்பீடு மற்றும் மீள்பார்வை",
            v -> openLaboratory("assessment")));
        panel.addView(dashboardButton("About & Privacy / அறிமுகம் மற்றும் தனியுரிமை",
            v -> showPrivacyDialog()));

        TextView nativeFeatures = dashboardText(
            "Android features: offline bundled lessons, native text-to-speech, Android print/save, predictive Back navigation, renderer recovery and device-local progress.",
            13, Color.rgb(185,211,203));
        nativeFeatures.setPadding(0, dp(16), 0, dp(4));
        panel.addView(nativeFeatures);

        root.addView(scroll, new FrameLayout.LayoutParams(-1,-1));
    }

    private void openLaboratory(String target) {
        pendingNativeTarget = target;
        createWebView();
    }

    private void applyNativeLaunchTarget(WebView view) {
        String target = pendingNativeTarget;
        pendingNativeTarget = null;
        if (target == null || "resume".equals(target)) return;
        String script;
        switch (target) {
            case "guided":
                script = "(function(){var s=document.querySelector('#systemTabs [data-system=\"setup\"]');if(s)s.click();setTimeout(function(){var b=document.querySelector('#modeButtons [data-mode=\"guided\"]');if(b)b.click();},0);})();";
                break;
            case "anatomy":
                script = "(function(){var s=document.querySelector('#systemTabs [data-system=\"digestive\"]');if(s)s.click();setTimeout(function(){var b=document.querySelector('#modeButtons [data-mode=\"explore\"]');if(b)b.click();},0);})();";
                break;
            case "assessment":
                script = "(function(){var s=document.querySelector('#systemTabs [data-system=\"setup\"]');if(s)s.click();setTimeout(function(){var b=document.querySelector('#modeButtons [data-mode=\"assessment\"]');if(b)b.click();},0);})();";
                break;
            default:
                return;
        }
        try { view.evaluateJavascript(script, null); } catch (RuntimeException ignored) { }
    }

    private String privacyText() {
        return "Earthworm Virtual Laboratory — Privacy Policy\n\n" +
            "Version: " + BuildConfig.VERSION_NAME + "\n\n" +
            "This educational app does not require an account and does not collect a student's name, email address, phone number, precise location, contacts, camera images, microphone audio or files. " +
            "The app requests no INTERNET, camera, microphone, storage or location permission and contains no advertising or analytics SDK.\n\n" +
            "Language preference, guided-study progress, assessment results and review dates are stored only on this device in the app's local WebView storage. Android cloud backup is disabled. " +
            "The information remains until app data is cleared or the app is uninstalled.\n\n" +
            "Narration uses an installed Android text-to-speech voice selected on the device. The app does not upload narration text to an online speech service.\n\n" +
            "When a user deliberately opens a scientific reference or this public privacy policy, Android opens the external browser. The destination website and browser then apply their own privacy practices. " +
            "Printing uses the Android print service selected by the user.\n\n" +
            "Developer: Department of Zoology, Government Arts and Science College, Nagercoil.\n" +
            "Public policy: " + PRIVACY_URL;
    }

    private void showPrivacyDialog() {
        ScrollView scroll = new ScrollView(this);
        TextView policy = new TextView(this);
        policy.setText(privacyText());
        policy.setTextSize(16);
        policy.setTextColor(Color.rgb(20,20,20));
        policy.setPadding(dp(20), dp(12), dp(20), dp(18));
        policy.setLineSpacing(0, 1.15f);
        scroll.addView(policy, new ScrollView.LayoutParams(-1,-2));
        new AlertDialog.Builder(this)
            .setTitle("About & Privacy / அறிமுகம் மற்றும் தனியுரிமை")
            .setView(scroll)
            .setNeutralButton("Open public policy", (dialog, which) -> openPrivacyPolicy())
            .setPositiveButton("Close", null)
            .show();
    }

    private void openPrivacyPolicy() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL))
                .addCategory(Intent.CATEGORY_BROWSABLE));
        } catch (ActivityNotFoundException absent) {
            toast("No browser is available. The full privacy policy is shown inside the app.");
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView() {
        root.removeAllViews();
        dashboardVisible = false;

        LinearLayout labShell = new LinearLayout(this);
        labShell.setOrientation(LinearLayout.VERTICAL);
        labShell.setBackgroundColor(Color.rgb(6,21,21));

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dp(6), dp(4), dp(6), dp(4));
        toolbar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        toolbar.setBackgroundColor(Color.rgb(9,37,35));

        Button home = new Button(this);
        home.setText("Home");
        home.setAllCaps(false);
        home.setMinHeight(dp(44));
        home.setOnClickListener(v -> showDashboard());
        toolbar.addView(home, new LinearLayout.LayoutParams(-2,-2));

        TextView toolbarTitle = new TextView(this);
        toolbarTitle.setText("Earthworm Virtual Laboratory");
        toolbarTitle.setTextColor(Color.WHITE);
        toolbarTitle.setTextSize(16);
        toolbarTitle.setGravity(android.view.Gravity.CENTER);
        toolbar.addView(toolbarTitle, new LinearLayout.LayoutParams(0,-2,1f));

        Button privacy = new Button(this);
        privacy.setText("Privacy");
        privacy.setAllCaps(false);
        privacy.setMinHeight(dp(44));
        privacy.setOnClickListener(v -> showPrivacyDialog());
        toolbar.addView(privacy, new LinearLayout.LayoutParams(-2,-2));

        labShell.addView(toolbar, new LinearLayout.LayoutParams(-1,-2));
        root.addView(labShell, new FrameLayout.LayoutParams(-1,-1));

        try {
            webView = new WebView(this);
        } catch (RuntimeException unavailable) {
            showRecovery("Android System WebView is unavailable. Enable or update it, then reopen the laboratory.");
            return;
        }
        webView.setBackgroundColor(Color.rgb(6,21,21));
        labShell.addView(webView, new LinearLayout.LayoutParams(-1,0,1f));
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
                    if (view != webView) return;
                    if (!"true".equals(ready)) {
                        showRecovery("The lesson could not initialise. Update Android System WebView and retry.");
                        return;
                    }
                    applyNativeLaunchTarget(view);
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
        dashboardVisible=false;
        LinearLayout panel = new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(32,48,32,32);
        TextView title = new TextView(this);title.setText("Earthworm Virtual Laboratory");title.setTextSize(22);title.setTextColor(Color.WHITE);panel.addView(title);
        TextView detail = new TextView(this);detail.setText(message+"\n\n"+"பாடத்தை மீண்டும் திறக்கவும். சேமிக்கப்பட்ட முன்னேற்றம் அழிக்கப்படாது.");detail.setTextColor(Color.WHITE);detail.setTextSize(17);panel.addView(detail);
        Button retry = new Button(this);retry.setText("Reopen lesson / மீண்டும் திற");retry.setOnClickListener(v->openLaboratory("resume"));panel.addView(retry);
        Button home = new Button(this);home.setText("Home / முகப்பு");home.setOnClickListener(v->showDashboard());panel.addView(home);
        root.addView(panel);
    }
    private void toast(String text) { Toast.makeText(this,text,Toast.LENGTH_LONG).show(); }
    private void handleBack() {
        if (backPending) return;
        if (dashboardVisible) { finish();return; }
        if (webView == null) { showDashboard();return; }
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
        if (!handled) showDashboard();
    }
    private void clearBackRequest() {
        backPending=false;backRequest++;
        if (backTimeout!=null) { root.removeCallbacks(backTimeout);backTimeout=null; }
    }
    @SuppressLint("GestureBackNavigation")
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
    boolean dashboardVisibleForTest() { return dashboardVisible; }
    void launchLaboratoryForTest() { openLaboratory("resume"); }
}
