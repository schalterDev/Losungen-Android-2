package de.schalter.losungen.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
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
import de.schalter.losungen.components.widgetVerse.WidgetPreview
import de.schalter.losungen.firebase.FirebaseUtil
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class AppWidgetActivity : CustomizeActivity() {

    private lateinit var mViewModel: AppWidgetModel
    private lateinit var spinner: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var textColor: ImageView
    private lateinit var backgroundColor: ImageView
    private lateinit var btnSave: Button

    private lateinit var preview: WidgetPreview

    private var showStyleChooserDialog = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(Customize.getTheme(this))
        setContentView(R.layout.activity_app_widget)
        setSupportActionBar(toolbar as Toolbar)

        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, WidgetData.generateWidgetId())
        showStyleChooserDialog = !intent.getBooleanExtra(WidgetData.EXTRA_EDIT_WIDGET, false)

        loadWidgets()
        initViewModel(widgetId)
    }

    private fun loadWidgets() {
        spinner = findViewById(R.id.spinner_widget_content)
        seekBar = findViewById(R.id.seekBar_font_size)
        textColor = findViewById(R.id.imageViewTextColor)
        backgroundColor = findViewById(R.id.imageViewBackgroundColor)
        btnSave = findViewById(R.id.btn_widget_save)

        preview = findViewById(R.id.widget_preview)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> contentSelected(setOf(WidgetContentType.OLD_TESTAMENT))
                    1 -> contentSelected(setOf(WidgetContentType.NEW_TESTAMENT))
                    2 -> contentSelected(setOf(WidgetContentType.OLD_TESTAMENT, WidgetContentType.NEW_TESTAMENT))
                }
            }
        }
        spinner.setSelection(2)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fontSizeChanged(progress + FONT_SIZE_OFFSET) // progress starts with 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        textColor.setOnClickListener { chooseTextColor() }
        backgroundColor.setOnClickListener { chooseBackgroundColor() }

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

    private fun contentSelected(contentType: Set<WidgetContentType>) {
        mViewModel.updateWidgetData(contentType = contentType)
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
        if (showStyleChooserDialog) {
            if (widgetData.content.size > 0) {
                showStyleChooserDialog = false

                WidgetStyleChooserDialog(widgetData.content).apply {
                    this.onStyleSelected = {
                        widgetData.background = it.backgroundColor
                        widgetData.color = it.fontColor
                        widgetData.fontSize = it.fontSize.toInt()

                        mViewModel.setWidgetData(widgetData)
                    }
                }.show(supportFragmentManager, null)
            }
        }

        seekBar.progress = widgetData.fontSize - FONT_SIZE_OFFSET
        preview.setWidgetData(widgetData)

        if (widgetData.contentType.size == 2) {
            spinner.setSelection(2)
        } else if (widgetData.contentType.contains(WidgetContentType.OLD_TESTAMENT)) {
            spinner.setSelection(0)
        } else if (widgetData.contentType.contains(WidgetContentType.NEW_TESTAMENT)) {
            spinner.setSelection(1)
        }

        (textColor.background as GradientDrawable).apply {
            this.mutate()
            this.setColor(widgetData.color)
        }
        (backgroundColor.background as GradientDrawable).apply {
            this.mutate()
            this.setColor(widgetData.background)
        }
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

        FirebaseUtil.trackWidgetCreated(this)

        finish()
    }

    companion object {
        const val FONT_SIZE_OFFSET = 8
    }
}
