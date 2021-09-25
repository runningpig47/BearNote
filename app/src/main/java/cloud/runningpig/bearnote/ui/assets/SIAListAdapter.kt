package cloud.runningpig.bearnote.ui.assets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.SiaListItemBinding
import cloud.runningpig.bearnote.logic.model.Icon
import cloud.runningpig.bearnote.logic.model.IconMap

class SIAListAdapter(private val onItemClick: (position: Int) -> Unit) :
    ListAdapter<Icon, SIAListAdapter.ViewHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.bind(icon, position)
    }

    class ViewHolder(private var binding: SiaListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(icon: Icon, p1: Int) {
            binding.apply {
                siaImageView1.setImageResource(IconMap.map2[icon.iconName] ?: R.drawable.ic_error)
                siaTextView1.text = icon.iconString
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(SiaListItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<Icon>() {
        override fun areItemsTheSame(oldItem: Icon, newItem: Icon): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Icon, newItem: Icon): Boolean {
            return oldItem == newItem
        }
    }

}