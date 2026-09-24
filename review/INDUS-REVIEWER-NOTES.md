Indus Appstore — reviewer notes
App: Earthworm Virtual Laboratory
Package: in.ramesh.zoology.earthwormlab
Version submitted: 1.3.9

This is an offline undergraduate Zoology teaching app from the Department of Zoology, Government Arts and Science College, Nagercoil, Tamil Nadu. It is not a wrapper of a live website.

How to review
1. Open the app. The first screen is a native Android home (NativeHomeActivity), not a browser.
2. Tap About & Privacy. The full privacy policy is on this native screen.
3. Tap “View public Privacy Policy” to open the public policy URL:
   https://rameshgascngl-create.github.io/Earthworm-Virtual-Laboratory/privacy.html
4. On the native home screen, tap Test English voice or Test Tamil voice. Those buttons use Android TextToSpeech and do not open a website.
5. Return to the native home and open a laboratory module. The dissection content is bundled inside the APK and loaded from local assets. The app has no INTERNET permission and cannot load a remote site in WebView.

Privacy
- Listing URL and in-app URL are the same public HTML page above.
- No accounts, no ads, no analytics SDK, no collection of name / phone / location / files.
- Learning progress stays on the device.

Why this is an Android application rather than a bundled website
- Native launcher, module list, About & Privacy, speech and print.
- Laboratory HTML is local courseware owned by the same department, served through AndroidX WebViewAssetLoader, not a third-party website.
- No remote URL is loaded inside the app WebView.
- Scientific citations open only after a user tap, in the system browser.
