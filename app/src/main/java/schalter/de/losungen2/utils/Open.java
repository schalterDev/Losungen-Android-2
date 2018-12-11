package schalter.de.losungen2.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import schalter.de.losungen2.R;

public abstract class Open {

    private static final String urlPrivacyWebsite = "https://losungen.webdesign-schalter.de/privacy";
    private static final String programmerMail = "schalter.dev@gmail.com";

    public static void appInPlayStore(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Open.website(context, "http://play.google.com/store/apps/details?id=" + context.getPackageName());
        }
    }

    public static void sendMailToProgrammer(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", programmerMail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Losungen - APP Feedback");
        //intent.putExtra(Intent.EXTRA_TEXT, message);
        // TODO send relevant system info
        context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.send_mail)));
    }

    public static void privacyWebsite(Context context) {
        Open.website(context, urlPrivacyWebsite);
    }

    public static void website(Context context, String website) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
    }

}
