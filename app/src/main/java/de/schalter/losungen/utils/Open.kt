package de.schalter.losungen.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

import de.schalter.losungen.R

object Open {

    fun appInPlayStore(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            website(context, "http://play.google.com/store/apps/details?id=" + context.packageName)
        }
    }

    fun sendMailToProgrammer(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", Constants.programmerMail, null))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Losungen - APP Feedback")
        intent.putExtra(Intent.EXTRA_TEXT, AppSystemData.getDebugInformationAsString(context))
        context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.send_mail)))
    }

    fun privacyWebsite(context: Context) {
        website(context, Constants.urlPrivacyWebsite)
    }

    fun website(context: Context, website: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website)))
    }

}
