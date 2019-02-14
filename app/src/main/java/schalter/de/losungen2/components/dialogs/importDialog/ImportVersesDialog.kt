package schalter.de.losungen2.components.dialogs.importDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import schalter.de.losungen2.R
import schalter.de.losungen2.backgroundTasks.ImportVersesTask
import schalter.de.losungen2.components.emptyState.EmptyStateView
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.utils.CoroutineDispatchers
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext

class ImportVersesDialog : DialogFragment(), CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = CoroutineDispatchers.Background + job

    private lateinit var dataManagement: DataManagement

    private lateinit var dialog: AlertDialog
    private lateinit var spinner: Spinner
    private lateinit var spinnerContainer: LinearLayout
    private lateinit var linearLayout: LinearLayout
    private var dialogButton: Button? = null

    private var availableData: List<DataManagement.YearLanguageUrl>? = null
    private var selectedLanguage: Language? = null
    private var checkboxesValues: MutableMap<String, Boolean> = mutableMapOf()
    private var spinnerAdapter: ArrayAdapter<String>? = null

    private lateinit var mContext: Context

    override fun onPause() {
        super.onPause()
        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        job = Job()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            dataManagement = ViewModelProviders.of(this).get(DataManagement::class.java)

            val li = LayoutInflater.from(context)
            val dialogView = li.inflate(R.layout.dialog_import, null)

            dialog = AlertDialog.Builder(context!!)
                    .setView(dialogView)
                    .setPositiveButton(R.string.import_) { _, _ ->
                        if (availableData != null) {
                            showTermsAndConditionsWhenNecessary(availableData!!.filter {
                                it.language == selectedLanguage &&
                                        checkboxesValues[it.year.toString()] == true
                            })
                        }
                    }.create()

            mContext = dialog.context

            initDialog(dialogView)
            loadData()

            dialog.setOnShowListener {
                dialogButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                dialogButton?.isEnabled = false
            }
            dialog.setOnDismissListener { close() }

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun close() {
        job.cancel()
        if (dialog.isShowing) {
            dialog.cancel()
        }
    }

    private fun initDialog(dialog: View) {
        spinner = dialog.findViewById(R.id.spinner_language)
        linearLayout = dialog.findViewById(R.id.linear_layout_import)
        spinnerContainer = dialog.findViewById(R.id.import_spinner_container)

        spinnerAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item)
        spinnerAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = spinner.selectedItem as String
                updateDataForLanguage(selectedLanguage)
            }
        }

        val emptyState = dialog.findViewById<EmptyStateView>(R.id.emptyStateImportDialog)
        emptyState.onButtonClick {
            loadData()
        }
    }

    private fun onError() {
        // hide loading spinner and show empty state
        launch(CoroutineDispatchers.Ui) {
            dialog.findViewById<EmptyStateView?>(R.id.emptyStateImportDialog)?.visibility = View.VISIBLE
            dialog.findViewById<ProgressBar?>(R.id.importLoadingSpinner)?.visibility = View.GONE
        }
    }

    private fun loadData() {
        launch(CoroutineDispatchers.Ui) {
            dialogButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            dialogButton?.isEnabled = false

            dialog.findViewById<EmptyStateView?>(R.id.emptyStateImportDialog)?.visibility = View.GONE
            dialog.findViewById<ProgressBar?>(R.id.importLoadingSpinner)?.visibility = View.VISIBLE

            dataManagement.getAvailableData().observe(this@ImportVersesDialog, Observer<List<DataManagement.YearLanguageUrl>> {
                availableData = it

                if (availableData!!.isNotEmpty()) {
                    val availableLanguages: List<String> = DataManagement.getLanguagesFromData(availableData!!)
                    spinnerContainer.visibility = View.VISIBLE

                    spinnerAdapter!!.addAll(availableLanguages)
                    spinner.setSelection(0)
                    // TODO change to select own language
                } else {
                    onError()
                }
            })
        }
    }

    private fun updateDataForLanguage(language: String) {
        val languageEnum = Language.fromString(language)!!
        this.selectedLanguage = languageEnum
        val availableYears = DataManagement.getYearsForLanguageFromData(availableData!!, languageEnum)

        linearLayout.removeAllViews()
        checkboxesValues.clear()

        availableYears.forEachIndexed { index, element ->
            val checkboxValue = false
            val checkBox = CheckBox(context)
            checkBox.text = element.toString()

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                checkboxesValues[buttonView.text.toString()] = isChecked
                val atLeastOneCheckboxChecked = checkboxesValues.any { checkbox -> checkbox.value }

                launch(CoroutineDispatchers.Ui) {
                    dialogButton?.isEnabled = atLeastOneCheckboxChecked
                }
            }

            // When running tests the linear layout has already some child. I dont know why
            if (linearLayout.getChildAt(index) != null) {
                linearLayout.removeViewAt(index)
            }
            linearLayout.addView(checkBox)
            checkboxesValues[element.toString()] = checkboxValue
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
                            context!!.getString(R.string.copyright_import,
                                    context!!.getString(R.string.accept),
                                    yearLanguageUrl.copyrightUrl))

                    termsAndConditionsUrlsInserted.add(yearLanguageUrl.copyrightUrl!!)
                }
            }

            // detect links
            val spannedString = SpannableString(copyrightTextBuilder.toString())
            Linkify.addLinks(spannedString, Linkify.WEB_URLS)

            val dialogBuilder = AlertDialog.Builder(context!!)
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
        val importTask = ImportVersesTask(mContext)
        importTask.execute(toImport)
    }
}