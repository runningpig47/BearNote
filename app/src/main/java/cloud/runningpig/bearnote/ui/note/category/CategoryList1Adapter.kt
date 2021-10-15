package cloud.runningpig.bearnote.ui.note.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ItemCategoryListBinding
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.model.NoteCategory

class CategoryList1Adapter(private val onItemClick: (itemId: Int) -> Unit) :
    ListAdapter<NoteCategory, CategoryList1Adapter.ViewHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noteCategory = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(noteCategory.id)
        }
        holder.binding.itemDelete.setOnClickListener {
            listener?.onItemDelete(holder.bindingAdapterPosition)
        }
        holder.bind(noteCategory)
    }

    fun onItemMove(position: Int, targetPosition: Int) {
        listener?.onItemMove(position, targetPosition)
    }

    class ViewHolder(val binding: ItemCategoryListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(noteCategory: NoteCategory) {
            binding.apply {
                itemTextView.text = noteCategory.name
                itemImageView.setImageResource(
                    IconMap.map[noteCategory.icon] ?: R.drawable.ic_error
                )
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemCategoryListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<NoteCategory>() {
        override fun areItemsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteCategory, newItem: NoteCategory): Boolean {
            return oldItem == newItem
        }
    }

    private var listener: List1AdapterListener? = null

    fun setListener(listener: List1AdapterListener) {
        this.listener = listener
    }

    interface List1AdapterListener {
        fun onItemMove(position: Int, targetPosition: Int)
        fun onItemDelete(position: Int)
    }

}