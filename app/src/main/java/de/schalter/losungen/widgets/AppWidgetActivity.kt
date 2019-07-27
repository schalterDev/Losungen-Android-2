package de.schalter.losungen.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.OnColorSelectedListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import de.schalter.customize.Customize
import de.schalter.customize.CustomizeActivity
import de.schalter.losungen.MainActivity
import de.schalter.losungen.R
import de.schalter.losungen.components.dialogs.widgetStyleChooser.WidgetStyleChooserDialog
import de.schalter.losungen.utils.PreferenceTags
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

/**
 * All widget ids are in TAG_WIDGET_IDS
 *
 * Configure the widget:
 * color: font color
 * background: background color
 * fontSize: font size in pixel
 * content: OT, NT or both
 *
 * The configuration is saved in sharedPreferences
 * TAG{widgetId} = data
 */
class AppWidgetActivity : CustomizeActivity() {

    private lateinit var mViewModel: AppWidgetModel
    private lateinit var spinner: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var btnTextColor: Button
    private lateinit var btnBackgroundColor: Button
    private lateinit var textViewPreview: TextView
    private lateinit var btnSave: Button

    private var firstData = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(Customize.getTheme(this))
        setContentView(R.layout.activity_app_widget)
        setSupportActionBar(toolbar as Toolbar)

        val extras = intent.extras
        val widgetId = extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, WidgetData.generateWidgetId())
                ?: WidgetData.generateWidgetId()

        loadWidgets()
        initViewModel(widgetId)
    }

    private fun loadWidgets() {
        spinner = findViewById(R.id.spinner_widget_content)
        seekBar = findViewById(R.id.seekBar_font_size)
        btnTextColor = findViewById(R.id.btn_text_color)
        btnBackgroundColor = findViewById(R.id.btn_background_color)
        textViewPreview = findViewById(R.id.textView_preview)
        btnSave = findViewById(R.id.btn_widget_save)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> contentSelected(WidgetContent.OLD_TESTAMENT)
                    1 -> contentSelected(WidgetContent.NEW_TESTAMENT)
                    2 -> contentSelected(WidgetContent.OLD_AND_NEW_TESTAMENT)
                }
            }
        }
        spinner.setSelection(2)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fontSizeChanged(progress + 8) // progress starts with 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnTextColor.setOnClickListener { chooseTextColor() }
        btnBackgroundColor.setOnClickListener { chooseBackgroundColor() }

        btnSave.setOnClickListener { saveAndFinish() }
    }

    private fun initViewModel(widgetId: Int) {
        mViewModel = ViewModelProviders.of(this, AppWidgetModelFactory(this, Date()))
                .get(AppWidgetModel::class.java)

        mViewModel.getWidgetData().observe(this, Observer { updateTextView(it) })

        if (mViewModel.getWidgetData().value == null) {
            mViewModel.setWidgetData(WidgetData.load(this, widgetId))
        }
    }

    private fun contentSelected(content: WidgetContent) {
        mViewModel.updateWidgetData(content = content)
    }

    private fun fontSizeChanged(fontSize: Int) {
        mViewModel.updateWidgetData(fontSize = fontSize)
    }

    private fun chooseTextColor() {
        showColorPicker(R.string.text_color, mViewModel.getWidgetData().value!!.color, OnColorSelectedListener {
            mViewModel.updateWidgetData(color = it)
        })
    }

    private fun chooseBackgroundColor() {
        showColorPicker(R.string.background_color, mViewModel.getWidgetData().value!!.background, OnColorSelectedListener {
            mViewModel.updateWidgetData(background = it)
        })
    }

    private fun showColorPicker(title: Int, initialColor: Int, onColorSelectedListener: OnColorSelectedListener) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(title)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(onColorSelectedListener)
                .setOnColorChangedListener { onColorSelectedListener.onColorSelected(it) }
                .setPositiveButton(R.string.ok) { _, selectedColor, _ -> onColorSelectedListener.onColorSelected(selectedColor) }
                .build()
                .show()
    }

    private fun updateTextView(widgetData: WidgetData) {
        if (firstData) {
            widgetData.contentToShow?.let { content ->
                if (content != "" && content.trim() != "null") {

                    firstData = false
                    WidgetStyleChooserDialog(content).apply {
                        this.onStyleSelected = {
                            widgetData.background = it.backgroundColor
                            widgetData.color = it.fontColor
                            widgetData.fontSize = it.fontSize.toInt()

                            mViewModel.setWidgetData(widgetData)
                        }
                    }.show(supportFragmentManager, null)
                }
            }
        }

        textViewPreview.textSize = widgetData.fontSize.toFloat()
        textViewPreview.text = widgetData.contentToShow
        textViewPreview.setTextColor(widgetData.color)
        textViewPreview.setBackgroundColor(widgetData.background)
    }

    private fun saveAndFinish() {
        mViewModel.getWidgetData().value!!.save(this)

        // Return RESULT_OK from this activity
        val resultValue = Intent(this, MainActivity::class.java)
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mViewModel.getWidgetData().value!!.widgetId)
        setResult(Activity.RESULT_OK, resultValue)

        // update existing widget
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, WidgetBroadcast::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mViewModel.getWidgetData().value!!.widgetId))
        sendBroadcast(intent)

        finish()
    }
}

data class WidgetData(
        val widgetId: Int,
        var color: Int,
        var background: Int,
        var fontSize: Int,
        var content: WidgetContent,
        var contentToShow: String? = null) {

    fun save(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val ids = preferences.getStringSet(PreferenceTags.WIDGET_IDS, mutableSetOf())!!
        if (!ids.contains(widgetId.toString())) {
            ids.add(widgetId.toString())
        }

        val editor = preferences.edit()
        editor.putInt(PreferenceTags.WIDGET_COLOR + widgetId, color)
        editor.putInt(PreferenceTags.WIDGET_BACKGROUND + widgetId, background)
        editor.putInt(PreferenceTags.WIDGET_FONT_SIZE + widgetId, fontSize)
        editor.putString(PreferenceTags.WIDGET_CONTENT + widgetId, content.toString())
        editor.putStringSet(PreferenceTags.WIDGET_IDS, ids)
        editor.apply()
    }

    companion object {
        fun remove(context: Context, widgetId: Int) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            val ids = preferences.getStringSet(PreferenceTags.WIDGET_IDS, mutableSetOf())!!
            ids.remove(widgetId.toString())

            val editor = preferences.edit()
            editor.remove(PreferenceTags.WIDGET_COLOR + widgetId)
            editor.remove(PreferenceTags.WIDGET_BACKGROUND + widgetId)
            editor.remove(PreferenceTags.WIDGET_CONTENT + widgetId)
            editor.remove(PreferenceTags.WIDGET_FONT_SIZE + widgetId)
            editor.putStringSet(PreferenceTags.WIDGET_IDS, ids)
            editor.apply()
        }

        fun load(context: Context, widgetId: Int): WidgetData {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return WidgetData(
                    widgetId,
                    preferences.getInt(PreferenceTags.WIDGET_COLOR + widgetId, Color.BLACK),
                    preferences.getInt(PreferenceTags.WIDGET_BACKGROUND + widgetId, Color.WHITE),
                    preferences.getInt(PreferenceTags.WIDGET_FONT_SIZE + widgetId, 12),
                    WidgetContent.valueOf(preferences.getString(PreferenceTags.WIDGET_CONTENT + widgetId, WidgetContent.OLD_AND_NEW_TESTAMENT.toString())!!))
        }

        fun generateWidgetId(): Int {
            return (Math.random() * Int.MAX_VALUE).toInt()
        }
    }
}

enum class WidgetContent {
    NEW_TESTAMENT,
    OLD_TESTAMENT,
    OLD_AND_NEW_TESTAMENT
}
