package cloud.runningpig.bearnote.ui.note.category

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.AcaListItemBinding
import cloud.runningpig.bearnote.logic.model.Icon
import cloud.runningpig.bearnote.logic.model.IconMap

class ACAListAdapter(private val onItemClick: (position: Int) -> Unit) :
    ListAdapter<Icon, ACAListAdapter.ViewHolder>(ItemComparator()) {

    private var mSelectedIconName: String = "ic1"

    fun setSelectedIconName(iconName: String) {
        mSelectedIconName = iconName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(position)
            mSelectedIconName = icon.iconName
            notifyDataSetChanged()
        }
        holder.bind(icon,mSelectedIconName)
    }

    class ViewHolder(private var binding: AcaListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(icon: Icon, mSelectedIconName: String) {
            binding.apply {
                acaListImageView.setImageResource(IconMap.map[icon.iconName] ?: R.drawable.ic_error)
                if (icon.iconName == mSelectedIconName) {
                    acaListImageView.setBackgroundResource(R.drawable.oval_solid_ff5722)
                } else {
                    acaListImageView.setBackgroundResource(R.drawable.oval_solid_f5f5f5)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(AcaListItemBinding.inflate(LayoutInflater.from(parent.context)))
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