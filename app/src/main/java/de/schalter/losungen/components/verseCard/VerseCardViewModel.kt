package de.schalter.losungen.components.verseCard

import de.schalter.losungen.dataAccess.VersesDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VerseCardViewModel(
        private val database: VersesDatabase
) {

    private var verseCardData: VerseCardData? = null
    private var newNotes: String? = null

    fun setData(verseCardData: VerseCardData) {
        this.verseCardData = verseCardData
    }

    fun getData(): VerseCardData? = verseCardData

    fun updateNotes(newNotes: String) {
        this.newNotes = newNotes
    }

    fun saveNotes() {
        verseCardData?.let { verseCardData ->
            if (newNotes != null && verseCardData.notes != newNotes) {
                verseCardData.date?.let { date ->
                    GlobalScope.launch {
                        when (verseCardData.type) {
                            VerseCardData.Type.DAILY ->
                                database.dailyVerseDao().updateNotes(date, newNotes!!)
                            VerseCardData.Type.WEEKLY ->
                                database.weeklyVerseDao().updateNotes(date, newNotes!!)
                            VerseCardData.Type.MONTHLY ->
                                database.monthlyVerseDao().updateNotes(date, newNotes!!)
                        }
                    }
                }
            }
        }
    }

    fun toggleIsFavourite() {
        saveNotes()

        verseCardData?.let { verseCardData ->
            verseCardData.date?.let { date ->
                GlobalScope.launch {
                    when (verseCardData.type) {
                        VerseCardData.Type.DAILY ->
                            database.dailyVerseDao().updateIsFavourite(date, !verseCardData.isFavourite)
                        VerseCardData.Type.WEEKLY ->
                            database.weeklyVerseDao().updateIsFavourite(date, !verseCardData.isFavourite)
                        VerseCardData.Type.MONTHLY ->
                            database.monthlyVerseDao().updateIsFavourite(date, !verseCardData.isFavourite)
                    }
                }
            }
        }
    }
}