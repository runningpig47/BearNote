package cloud.runningpig.bearnote.ui.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.CsfListItemBinding
import cloud.runningpig.bearnote.logic.model.ChartMonthBean
import cloud.runningpig.bearnote.logic.model.IconMap

class CSFListAdapter : ListAdapter<ChartMonthBean, CSFListAdapter.ViewHolder>(ItemComparator()) {

    private var amount: Double = 1.0

    fun setAmount(amount: Double) {
        this.amount = amount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, amount)
    }

    class ViewHolder(private var binding: CsfListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChartMonthBean, amount: Double) {
            binding.apply {
                csfImageView1.setImageResource(IconMap.map[item.categoryIcon] ?: R.drawable.ic_error)
                csfTextView1.text = item.categoryName
                val sumNoteAmount = "%.0f".format(item.sumNoteAmount)
                csfTextView2.text = sumNoteAmount
                val percent = "%.1f".format((item.sumNoteAmount / amount) * 100)
                val count = item.countCategoryId
                val text = "${percent}% ${count}笔"
                csfTextView3.text = text
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(CsfListItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

//    override fun submitList(list: List<ChartMonthBean>?) {
//        super.submitList(list?.let { ArrayList(it) })
//    }

    class ItemComparator : DiffUtil.ItemCallback<ChartMonthBean>() {
        override fun areItemsTheSame(oldItem: ChartMonthBean, newItem: ChartMonthBean): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ChartMonthBean, newItem: ChartMonthBean): Boolean {
            return oldItem == newItem
        }
    }

}