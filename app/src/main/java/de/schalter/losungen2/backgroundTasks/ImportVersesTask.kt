package de.schalter.losungen2.backgroundTasks

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.rx.rx_response
import de.schalter.losungen2.R
import de.schalter.losungen2.components.async.RxAsyncTask
import de.schalter.losungen2.components.dialogs.importDialog.DataManagement
import de.schalter.losungen2.dataAccess.DatabaseHelper
import de.schalter.losungen2.xmlProcessing.LosungenXmlParser
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ImportVersesTask(val context: Context) : RxAsyncTask<List<DataManagement.YearLanguageUrl>, String, Unit>() {
    private var dialog: ProgressDialog? = null

    @SuppressLint("CheckResult")
    override fun doInBackground(params: List<DataManagement.YearLanguageUrl>) {
        params.forEachIndexed { index, yearLanguageUrl ->
            publishProgress(context.getString(R.string.downloading_file, index + 1, params.size))

            try {
                var file: File? = null
                Fuel.download(yearLanguageUrl.url).destination { _, _ ->
                    file = File.createTempFile(yearLanguageUrl.year.toString() + yearLanguageUrl.language, ".zip", context.cacheDir)
                    file!!
                }.rx_response().blockingGet()

                publishProgress(context.getString(R.string.uncompressing_file, index + 1, params.size))
                val directory = unzipFile(file!!)

                publishProgress(context.getString(R.string.importing_file, index + 1, params.size))
                val databaseHelper = DatabaseHelper(context)

                if (yearLanguageUrl.fileNameDailyVerses != null && yearLanguageUrl.fileNameDailyVerses != "") {
                    val fileInputStream = FileInputStream(File(directory, yearLanguageUrl.fileNameDailyVerses))
                    val parsedVerses = LosungenXmlParser(yearLanguageUrl.language).parseVerseXml(fileInputStream).map { verse -> verse.toDailyVerse() }
                    databaseHelper.importDailyVerses(parsedVerses)
                }

                if (yearLanguageUrl.fileNameWeeklyVerses != null && yearLanguageUrl.fileNameWeeklyVerses != "") {
                    val fileInputStream = FileInputStream(File(directory, yearLanguageUrl.fileNameWeeklyVerses))
                    val parsedVerses = LosungenXmlParser(yearLanguageUrl.language).parseVerseXml(fileInputStream).map { verse -> verse.toWeeklyVerse() }
                    databaseHelper.importWeeklyVerses(parsedVerses)
                }

                if (yearLanguageUrl.fileNameMonthlyVerses != null && yearLanguageUrl.fileNameMonthlyVerses != "") {
                    val fileInputStream = FileInputStream(File(directory, yearLanguageUrl.fileNameMonthlyVerses))
                    val parsedVerses = LosungenXmlParser(yearLanguageUrl.language).parseVerseXml(fileInputStream).map { verse -> verse.toMonthlyVerse() }
                    databaseHelper.importMonthlyVerses(parsedVerses)
                }

                directory.deleteRecursively()
                file!!.delete()
            } catch (error: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, R.string.error_loading_data_check_connection, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onProgress(progress: String) {
        dialog?.setMessage(progress)
    }

    override fun onPreExecute() {
        dialog = ProgressDialog.show(context, "", "")
    }

    override fun onPostExecute(result: Unit) {
        dialog?.cancel()
    }

    override fun onError(error: Throwable) {
        dialog?.cancel()
    }

    /**
     * @return the directory of the extracted zip file
     */
    private fun unzipFile(zipFile: File): File {
        val directory = File(context.cacheDir, zipFile.nameWithoutExtension)

        val zipInput = ZipInputStream(
                BufferedInputStream(FileInputStream(zipFile)))

        zipInput.use { zis ->
            var count: Int
            val buffer = ByteArray(8192)

            var zipEntry: ZipEntry? = zis.nextEntry
            while (zipEntry != null) {
                val file = File(directory, zipEntry.name)
                val dir = if (zipEntry.isDirectory) file else file.parentFile

                if (!dir.isDirectory && !dir.mkdirs())
                    throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)

                if (!zipEntry.isDirectory) {
                    val fileOutput = FileOutputStream(file)
                    fileOutput.use { fout ->
                        count = zis.read(buffer)
                        while (count != -1) {
                            fout.write(buffer, 0, count)
                            count = zis.read(buffer)
                        }
                    }
                }

                zipEntry = zis.nextEntry
            }
        }

        return directory
    }
}
