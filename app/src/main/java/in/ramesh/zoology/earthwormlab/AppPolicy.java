package in.ramesh.zoology.earthwormlab;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/** Single public privacy-policy address used in-app and on the store listing. */
public final class AppPolicy {
    private AppPolicy() {}

    public static final String PUBLIC_POLICY_URL =
        "https://rameshgascngl-create.github.io/Earthworm-Virtual-Laboratory/privacy.html";

    public static void openPublicPolicy(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PUBLIC_POLICY_URL))
                .addCategory(Intent.CATEGORY_BROWSABLE));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No browser is available to open the public policy.", Toast.LENGTH_LONG).show();
        }
    }
}
