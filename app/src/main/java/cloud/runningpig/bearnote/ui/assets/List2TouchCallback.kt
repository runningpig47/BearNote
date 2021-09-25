package cloud.runningpig.bearnote.ui.assets

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class List2TouchCallback(private val adapter: AFList1Adapter) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE
        val dragFlags = if (recyclerView.layoutManager is GridLayoutManager) {
            makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        } else {
            makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(
            viewHolder.bindingAdapterPosition,
            target.bindingAdapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        Log.d("tewatsqqqq", "onChildDraw: $actionState, $isCurrentlyActive")
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
            // 拖拽时改变Item的透明度
            viewHolder.itemView.alpha = 0.5f
        } else {
            viewHolder.itemView.alpha = 1f
        }
    }

}