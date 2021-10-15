package cloud.runningpig.bearnote.ui.assets

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.SadialogListItemBinding
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.logic.model.IconMap

class SADialogList1Adapter(private val onItemClick: (item: Account, position: Int) -> Unit) :
    ListAdapter<Account, SADialogList1Adapter.ViewHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accountItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(accountItem, position)
        }
        holder.bind(accountItem)
    }

    fun onItemMove(position: Int, targetPosition: Int) {
        listener?.onItemMove(position, targetPosition)
    }

    class ViewHolder(val binding: SadialogListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Account) {
            binding.apply {
                afImageView1.setImageResource(IconMap.map2[item.icon] ?: R.drawable.ic_error)
                afTextView1.text = item.name
                if (!TextUtils.isEmpty(item.information)) {
                    afTextView3.visibility = View.VISIBLE
                    afTextView3.text = item.information
                } else {
                    afTextView3.visibility = View.GONE
                }
                afTextView2.text = item.balance.toString()
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(SadialogListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

    }

    class ItemComparator : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }

    private var listener: List1AdapterListener? = null

    fun setListener(listener: List1AdapterListener) {
        this.listener = listener
    }

    interface List1AdapterListener {
        fun onItemMove(position: Int, targetPosition: Int)
    }

}