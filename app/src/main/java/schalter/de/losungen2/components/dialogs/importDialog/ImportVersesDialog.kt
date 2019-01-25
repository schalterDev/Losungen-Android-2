package schalter.de.losungen2.components.dialogs.importDialog

import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.Language


class ImportVersesDialog(val context: Context) {

    private lateinit var dataManagement: DataManagement

    private lateinit var spinner: Spinner
    private lateinit var linearLayout: LinearLayout
    private val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item)

    private lateinit var dialog: AlertDialog

    private var availableData: List<DataManagement.YearLanguageUrl>? = null
    private var selectedLanguage: Language? = null
    private var checkboxesValues: MutableMap<String, Boolean> = mutableMapOf()

    init {
        this.load()
    }

    fun show() {
        dialog.show()
    }

    fun close() {
        dialog.cancel()
    }

    private fun load() {
        val li = LayoutInflater.from(context)
        val dialogView = li.inflate(R.layout.dialog_import, null)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.import_) { _, _ ->
            if (availableData != null) {
                showTermsAndConditionsWhenNecessary(availableData!!.filter {
                    it.language == selectedLanguage &&
                            checkboxesValues[it.year.toString()] == true
                })
            }
        }
        dialog = builder.create()

        initDialog(dialogView)

        dataManagement = DataManagement(dialog.context)

        GlobalScope.launch {
            availableData = dataManagement.getAvailableData()
            if (availableData!!.isNotEmpty()) {
                val availableLanguages: List<String> = DataManagement.getLanguagesFromData(availableData!!)
                launch(Dispatchers.Main) {
                    spinnerAdapter.addAll(availableLanguages)
                    spinner.setSelection(0)
                    // TODO change to select own language
                }
            }
        }
    }

    private fun initDialog(dialog: View) {
        spinner = dialog.findViewById(R.id.spinner_language)
        linearLayout = dialog.findViewById(R.id.linear_layout_import)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = spinner.selectedItem as String
                updateDataForLanguage(selectedLanguage)
            }

        }
    }

    private fun updateDataForLanguage(language: String) {
        val languageEnum = Language.fromString(language)!!
        this.selectedLanguage = languageEnum
        val availableYears = DataManagement.getYearsForLanguageFromData(availableData!!, languageEnum)

        linearLayout.removeAllViews()
        checkboxesValues.clear()

        availableYears.forEach {
            val checkboxValue = false
            val checkBox = CheckBox(context)
            checkBox.text = it.toString()

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                checkboxesValues[buttonView.text.toString()] = isChecked
            }

            linearLayout.addView(checkBox)
            checkboxesValues[it.toString()] = checkboxValue
        }
    }

    private fun showTermsAndConditionsWhenNecessary(toImport: List<DataManagement.YearLanguageUrl>) {
        val anyImportHasCopyright = toImport.any { it.copyrightUrl != null }
        if (anyImportHasCopyright) {
            val copyrightTextBuilder = StringBuilder()
            val termsAndConditionsUrlsInserted: MutableSet<String> = mutableSetOf()
            toImport.forEachIndexed { index, yearLanguageUrl ->
                if (yearLanguageUrl.copyrightUrl != null &&
                        !termsAndConditionsUrlsInserted.contains(yearLanguageUrl.copyrightUrl!!)) {

                    if (index != 0) {
                        copyrightTextBuilder.append("\n\n")
                    }

                    copyrightTextBuilder.append(
                            context.getString(R.string.copyright_import,
                                    context.getString(R.string.accept),
                                    yearLanguageUrl.copyrightUrl))

                    termsAndConditionsUrlsInserted.add(yearLanguageUrl.copyrightUrl!!)
                }
            }

            // detect links
            val spannedString = SpannableString(copyrightTextBuilder.toString())
            Linkify.addLinks(spannedString, Linkify.WEB_URLS)

            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle(R.string.accept_terms_and_conditions)
            dialogBuilder.setMessage(spannedString)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setNegativeButton(R.string.cancel) { _, _ -> }
            dialogBuilder.setPositiveButton(R.string.accept) { _, _ ->
                downloadAndImport(toImport)
            }
            val dialog = dialogBuilder.show()

            // make the link clickable
            dialog.findViewById<TextView>(android.R.id.message)!!.movementMethod = LinkMovementMethod.getInstance()
        } else {
            downloadAndImport(toImport)
        }
    }

    private fun downloadAndImport(toImport: List<DataManagement.YearLanguageUrl>) {
        // TODO implement
    }
}