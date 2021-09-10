package cloud.runningpig.bearnote.ui.detail

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.NlfListItemBinding
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.model.NoteDetail

class NLFListAdapter(private val onItemClick: (position: Int, noteDetail: NoteDetail) -> Unit) :
    ListAdapter<NoteDetail, NLFListAdapter.ViewHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(position, item)
        }
        holder.bind(item, position)
    }

    class ViewHolder(private var binding: NlfListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NoteDetail, position: Int) {
            binding.apply {
                nliImageView1.setImageResource(IconMap.map[item.categoryIcon] ?: R.drawable.ic_error)
                nliTextView1.text = item.categoryName
                val sort = item.categorySort
                val s = if (sort == 0) {
                    "-${item.noteAmount}"
                } else {
                    item.noteAmount.toString()
                }
                nliTextView2.text = s
                if (!TextUtils.isEmpty(item.information)) {
                    nliTextView3.visibility = View.VISIBLE
                    val info = "备注：${item.information}"
                    nliTextView3.text = info
                } else {
                    nliTextView3.visibility = View.GONE
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(NlfListItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<NoteDetail>() {
        override fun areItemsTheSame(oldItem: NoteDetail, newItem: NoteDetail): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: NoteDetail, newItem: NoteDetail): Boolean {
            return oldItem.noteAmount == newItem.noteAmount
//            val noteId: Int,
//            var noteAmount: Double,
//            var noteDate: Date,
//            var information: String?,
//            val uid: Int,
//            var categorySort: Int,
//            var categoryIcon: String,
//            var categoryName: String,
        }
    }

}