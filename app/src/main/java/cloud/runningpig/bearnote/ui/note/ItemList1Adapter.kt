package cloud.runningpig.bearnote.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.CategoryItemBinding
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.model.NoteCategory

class ItemList1Adapter(private val onItemClick: (position: Int) -> Unit) :
    ListAdapter<NoteCategory, ItemList1Adapter.ViewHolder>(ItemComparator()) {
    private var mSelectedPosition: Int = -1

    fun setSelectedPosition(p: Int) {
        mSelectedPosition = p
    }

    fun getSelectedPosition() = mSelectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteCategory = getItem(position)
        holder.itemView.setOnClickListener {
            if (position != currentList.lastIndex) {
                onItemClick(position)
                mSelectedPosition = holder.bindingAdapterPosition
                notifyDataSetChanged()
            } else {
                onItemClick(-1)
            }
        }
        holder.bind(noteCategory, position, mSelectedPosition)
    }

    class ViewHolder(private var binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(noteCategory: NoteCategory, p1: Int, p2: Int) {
            binding.apply {
                textView.text = noteCategory.name
                imageView.setImageResource(IconMap.map[noteCategory.icon] ?: R.drawable.ic_error)
                if (p1 == p2) {
                    imageView.setBackgroundResource(R.drawable.oval_solid_ff5722)
                } else {
                    imageView.setBackgroundResource(R.drawable.oval_solid_eeeeee)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<NoteCategory>() {
        override fun areItemsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem.name == newItem.name
        }
    }
}