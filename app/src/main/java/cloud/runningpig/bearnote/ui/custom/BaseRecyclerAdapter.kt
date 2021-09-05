package cloud.runningpig.bearnote.ui.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T>(context: Context, dataList: List<T>, resId: Int) :
    RecyclerView.Adapter<ViewHolder>() {

    private var mData: List<T> = dataList
    private val mContext: Context = context
    private val mResId: Int = resId
    private var mItemClickListener: OnItemClickListener<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(mContext).inflate(mResId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t: T = mData[position]
        holder.getRootView().setOnClickListener {
            mItemClickListener?.onItemClick(t, position)
            notifyDataSetChanged()
        }
        onBindViewHolder(holder, t, mSelectedItem, position)
    }

    override fun getItemCount(): Int = mData.size

    abstract fun onBindViewHolder(viewHolder: ViewHolder, itemVO: T, mSelectedItem: T?, position: Int)

    interface OnItemClickListener<T> {
        fun onItemClick(t: T, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<T>): BaseRecyclerAdapter<T> {
        this.mItemClickListener = listener
        return this
    }

    private var mSelectedItem: T? = null

    fun setSelectedItem(t: T) {
        mSelectedItem = t
    }

    fun getSelectedItem(): T? {
        return mSelectedItem
    }

}