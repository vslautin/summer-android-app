package airport.transfer.sale

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import java.util.ArrayList


class SwipeCallback(private val mContext: Context, dragDirs: Int, swipeDirs: Int, val textImage: String? = null) :
        ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private var background: Drawable? = null
    private var delMark: Drawable? = null
    private var delMarkMargin: Int = 0
    private var initiated: Boolean = false
    private var mCallback: SwipingCallback? = null
    private var blockedPositions: MutableList<Int>? = null
    private var textImageSize: Pair<Int, Int>? = null
    private lateinit var mTextPaint: Paint

    private fun init() {
        background = ColorDrawable(Color.RED)
        delMark = ContextCompat.getDrawable(mContext, R.drawable.ic_delete)
        delMark!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        delMarkMargin = mContext.dip(23)
        textImageSize = Pair(mContext.dip(60), mContext.dip(18))
        mTextPaint = Paint()
        with(mTextPaint) {
            color = Color.WHITE
            textSize = mContext.dip(16).toFloat()
        }
        initiated = true
    }

    fun setCallback(callback: SwipingCallback) {
        mCallback = callback
    }

    fun addBlockedPositions(vararg positions: Int) {
        if (blockedPositions == null) blockedPositions = ArrayList<Int>()
        positions.forEach { blockedPositions?.add(it) }
    }

    fun setBlockedPositions(positions: List<Int>) {
        blockedPositions = ArrayList<Int>()
        positions.forEach { blockedPositions?.add(it) }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (blockedPositions != null && blockedPositions!!.contains(viewHolder.adapterPosition)) return
        if (mCallback != null) mCallback!!.onSwiped(viewHolder)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (blockedPositions != null && blockedPositions!!.contains(viewHolder.adapterPosition)) return
        val itemView = viewHolder.itemView
        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.adapterPosition == -1) return// not interested in those
        if (!initiated) init()
        // draw red background
        background!!.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right,
                itemView.bottom)
        background!!.draw(c)
        // draw del mark
        val itemHeight = itemView.bottom - itemView.top
        val intrinsicWidth = delMark!!.intrinsicWidth
        val intrinsicHeight = delMark!!.intrinsicWidth

        val delMarkLeft = itemView.right - delMarkMargin - intrinsicWidth
        val delMarkRight = itemView.right - delMarkMargin
        val delMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val delMarkBottom = delMarkTop + intrinsicHeight
        if (textImage != null) {
            val x = itemView.width - delMarkMargin.toFloat() - textImageSize!!.first
            val y = itemView.top + (itemView.height / 2 + textImageSize!!.second / 2).toFloat()
            c.drawText(textImage, x, y, mTextPaint)
            Log.d("SwipeHelper", "$textImage x=$x y=$y canvas = $c")
        } else {
            delMark!!.setBounds(delMarkLeft, delMarkTop, delMarkRight, delMarkBottom)
            delMark!!.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface SwipingCallback {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder)
    }
}