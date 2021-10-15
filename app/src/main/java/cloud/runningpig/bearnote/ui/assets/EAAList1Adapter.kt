package cloud.runningpig.bearnote.ui.assets

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.EaaListItemBinding
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.model.TransferDetail

class EAAList1Adapter() : ListAdapter<TransferDetail, EAAList1Adapter.ViewHolder>(ItemComparator()) {

    private var accountId: Int? = null // 必须先设置accountId, 才能在bind方法区分转入转出

    fun setAccountId(accountId: Int) {
        this.accountId = accountId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, accountId)
    }

    class ViewHolder(val binding: EaaListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransferDetail, accountId: Int?) {
            binding.apply {
                val accountSort = item.detailSort
                if (accountSort == 0) { // 收支明细
                    eaaImageView1.setImageResource(IconMap.map[item.categoryIcon] ?: R.drawable.ic_error)
                    eaaTextView1.text = item.categoryName
                    eaaTextView4.visibility = View.VISIBLE
                    eaaTextView4.text = item.accountName
                    val sort = item.categorySort
                    val s = if (sort == 0) {
                        "-${item.noteAmount}"
                    } else {
                        "+${item.noteAmount}"
                    }
                    eaaTextView2.text = s
                    val info = item.information
                    if (TextUtils.isEmpty(info)) {
                        eaaTextView3.visibility = View.GONE
                    } else {
                        eaaTextView3.visibility = View.VISIBLE
                        eaaTextView3.text = "备注：$info"
                    }
                } else { // 账户转账
                    accountId?.let { accountId ->
                        when {
                            item.fromId == item.toId -> { // 资金调整
                                eaaImageView1.setImageResource(R.drawable.ic_reset)
                                eaaTextView3.visibility = View.VISIBLE
                                eaaTextView1.text = "余额调整为"
                                eaaTextView3.text = "余额调整"
                                eaaTextView4.visibility = View.VISIBLE
                                eaaTextView4.text = item.information
                                eaaTextView2.text = item.noteAmount.toString()
                            }
                            item.fromId == accountId -> { // 转出
                                eaaImageView1.setImageResource(R.drawable.ic_toleft)
                                eaaTextView1.text = "转出"
                                eaaTextView2.text = "-${item.noteAmount}"
                                eaaTextView4.visibility = View.VISIBLE
                                eaaTextView4.text = item.fromName + ">" + item.toName
                                if (TextUtils.isEmpty(item.information)) {
                                    eaaTextView3.visibility = View.GONE
                                } else {
                                    eaaTextView3.visibility = View.VISIBLE
                                    val info = "备注：${item.information}"
                                    eaaTextView3.text = info
                                }
                            }
                            item.toId == accountId -> { // 转入
                                eaaImageView1.setImageResource(R.drawable.ic_toright)
                                eaaTextView1.text = "转入"
                                eaaTextView2.text = "+${item.noteAmount}"
                                eaaTextView4.visibility = View.VISIBLE
                                eaaTextView4.text = item.fromName + ">" + item.toName
                                if (TextUtils.isEmpty(item.information)) {
                                    eaaTextView3.visibility = View.GONE
                                } else {
                                    eaaTextView3.visibility = View.VISIBLE
                                    val info = "备注：${item.information}"
                                    eaaTextView3.text = info
                                }
                            }
                            else -> { // - -晕
                                eaaImageView1.setImageResource(R.drawable.ic_error)
                                eaaTextView1.text = "异常转账记录"
                                eaaTextView2.text = item.noteAmount.toString()
                                if (TextUtils.isEmpty(item.information)) {
                                    eaaTextView3.visibility = View.GONE
                                } else {
                                    eaaTextView3.visibility = View.VISIBLE
                                    val info = "备注：${item.information}"
                                    eaaTextView3.text = info
                                }
                                eaaTextView4.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(EaaListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

    }

    class ItemComparator : DiffUtil.ItemCallback<TransferDetail>() {
        override fun areItemsTheSame(oldItem: TransferDetail, newItem: TransferDetail): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TransferDetail, newItem: TransferDetail): Boolean {
            return oldItem == newItem
        }
    }

}