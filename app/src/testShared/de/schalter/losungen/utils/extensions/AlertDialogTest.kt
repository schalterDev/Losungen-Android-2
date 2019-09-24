package de.schalter.losungen.utils.extensions

import androidx.appcompat.app.AlertDialog

fun AlertDialog.getPositiveButton() =
        this.getButton(AlertDialog.BUTTON_POSITIVE)

fun AlertDialog.getNegativeButton() =
        this.getButton(AlertDialog.BUTTON_NEGATIVE)

fun AlertDialog.clickPositiveButton() {
    this.getPositiveButton().performClick()
}

fun AlertDialog.clickNegativeButton() {
    this.getNegativeButton().performClick()
}