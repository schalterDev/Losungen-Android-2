package de.schalter.losungen.utils

import android.content.Context

object AppSystemData {

    fun getAppVersion(context: Context) = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    fun getAndroidVersion() = android.os.Build.VERSION.SDK_INT
    fun getDevice() = android.os.Build.DEVICE!!
    fun getModel() = android.os.Build.MODEL!!
    fun getProduct() = android.os.Build.PRODUCT!!

    fun getDebugInformationAsString(context: Context): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("App version: " + getAppVersion(context) + "\n")
        stringBuilder.append("OS SDK version: " + getAndroidVersion() + "\n")
        stringBuilder.append("Device: " + getDevice() + "\n")
        stringBuilder.append("Model (and Product): " + getModel() + "(" + getProduct() + ")\n")
        stringBuilder.append("-----------------------\n\n")

        return stringBuilder.toString()
    }
}