package de.schalter.losungen.components.verseCard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R

class VerseCardGridAdapter(private val verseCards: MutableList<VerseCardData> = mutableListOf()) : RecyclerView.Adapter<VerseCardGridAdapter.VerseCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.verse_card_wrapper, parent, false)
        return VerseCardViewHolder(view)
    }

    override fun getItemCount(): Int = verseCards.size

    override fun onBindViewHolder(holder: VerseCardViewHolder, position: Int) {
        val verseCardData = verseCards[position]
        holder.setData(verseCardData)
    }

    fun addVerseCard(verseCard: VerseCardData) {
        verseCards.add(verseCard)
        this.notifyDataSetChanged()
    }

    fun removeVerseCard(verseCard: VerseCardData) {
        verseCards.remove(verseCard)
        this.notifyDataSetChanged()
    }

    fun setData(list: List<VerseCardData>) {
        verseCards.clear()
        verseCards.addAll(list)
        this.notifyDataSetChanged()
    }

    class VerseCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView: VerseCardView = view.findViewById(R.id.verseCard)

        fun setData(verseCardData: VerseCardData) {
            cardView.setData(verseCardData)
        }

        fun getData(): VerseCardData {
            return cardView.getData()
        }
    }

    companion object {
        fun getLayoutManager(context: Context): RecyclerView.LayoutManager {
            //Number of columns, Tablet has 2 - phone 1
            val tabletSize = context.resources.getBoolean(R.bool.isTablet)
            val layoutManager: RecyclerView.LayoutManager

            layoutManager = if (tabletSize) {
                GridLayoutManager(context, 2)
            } else {
                GridLayoutManager(context, 1)
            }

            return layoutManager
        }
    }
}