package com.vrb.apps.test.ui.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.vrb.apps.test.R
import com.vrb.apps.test.data.models.Declaration
import kotlinx.android.synthetic.main.dialog_note.view.*
import kotlinx.android.synthetic.main.item_layout.view.*

class DataAdapter(
    private val context: Context,
    private var data: List<Declaration> = emptyList(),
    private var onBookmarkClicked: (Pair<Declaration, Boolean>) -> Unit,
    private var onDocumentClicked: (String) -> Unit
) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    enum class ListType {
        Search,
        Bookmark
    }

    private val layoutInflater = LayoutInflater.from(context)
    private var viewType = ListType.Search

    fun setData(type: ListType, data: List<Declaration>) {
        this.viewType = type
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    private fun getViewType(): ListType {
        return viewType
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: Declaration) {
            itemView.textSurname.text = data.lastName
            itemView.textFirstName.text = data.firstName
            itemView.textWorkplace.text = context.getString(R.string.place_of_work_placeholder, data.placeOfWork)

            if (data.position != null) {
                itemView.textPosition.visibility = View.VISIBLE
                itemView.textPosition.text = context.getString(R.string.position_placeholder, data.position)
            } else {
                itemView.textPosition.visibility = View.GONE
            }

            if (data.isBookmarked) {
                itemView.favourite.setImageDrawable(context.resources.getDrawable(R.drawable.favourite_enabled))
            } else {
                itemView.favourite.setImageDrawable(context.resources.getDrawable(R.drawable.favourite_border))
            }


            when (getViewType()) {
                ListType.Bookmark -> {
                    itemView.divider.visibility = View.VISIBLE
                    itemView.textNoteLayout.visibility = View.VISIBLE
                    if (data.note != null) {
                        itemView.textNote.setText(data.note)
                    }
                    itemView.textNote.clearFocus()
                }
                ListType.Search -> {
                    itemView.divider.visibility = View.GONE
                    itemView.textNoteLayout.visibility = View.GONE
                }
            }

            itemView.textNote.setOnEditorActionListener { _, action, _ ->
                var handled = false
                if (action == EditorInfo.IME_ACTION_DONE) {
                    Log.d("DataAdapter", "text note update triggered")
                    onBookmarkClicked(
                        Pair(
                            Declaration(
                                data.id,
                                data.firstName,
                                data.lastName,
                                data.placeOfWork,
                                data.position,
                                data.linkToDocument,
                                data.isBookmarked,
                                itemView.textNote.text.toString()
                            ), false
                        )
                    )
                    handled = true
                }
                handled
            }

            itemView.favourite.setOnClickListener {
                if (!data.isBookmarked) {
                    displayNoteDialog(data)
                } else {
                    itemView.favourite.setImageDrawable(context.resources.getDrawable(R.drawable.favourite_border))
                    if (getViewType() == ListType.Search) {
                        data.isBookmarked = false
                    }
                    onBookmarkClicked(Pair(data, true))
                }
                notifyDataSetChanged()
            }

            itemView.openDocument.setOnClickListener { onDocumentClicked(data.linkToDocument) }
        }

        private fun displayNoteDialog(data: Declaration) {
            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater

            val dialogView = inflater.inflate(R.layout.dialog_note, null)

            builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_save) { dialog, _ ->
                    itemView.favourite.setImageDrawable(context.resources.getDrawable(R.drawable.favourite_enabled))
                    data.note = dialogView.noteEditText.text.toString()
                    data.isBookmarked = true
                    onBookmarkClicked(Pair(data, false))
                    val inputManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(dialogView.windowToken, 0)
                    dialog.dismiss()
                }
            builder.create().show()
        }
    }
}