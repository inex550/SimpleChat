package com.example.simplechat.screens.chats.presentation

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.R
import com.example.simplechat.utils.extensions.dp

class SwipeToDeleteCallback(
    context: Context,
    private val listener: Listener
): ItemTouchHelper.SimpleCallback(
    0, ItemTouchHelper.LEFT
) {

    private val clearPaint = Paint()
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#d92e29")
    }
    private val deletePaint = Paint().apply {
        color = Color.WHITE
    }
    private val deleteIconBitmap: Bitmap = ContextCompat.getDrawable(context, R.drawable.ic_trash)!!
        .toBitmap()
    private val deleteIconMatrix = Matrix()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction != ItemTouchHelper.LEFT) return

        val position = viewHolder.adapterPosition
        listener.onItemDeleteSwiped(position)
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

        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val right = itemView.right - kotlin.math.exp(-dX/150)

        val isCancelled = dX == 0f && !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        println(dX)

        var rectLeft = itemView.right + dX + 8.dp(itemView.context)
        if (rectLeft > right) rectLeft = right

        c.drawRoundRect(
            rectLeft,
            itemView.top.toFloat(),
            right,
            itemView.bottom.toFloat(),
            30f, 30f,
            backgroundPaint
        )

        if (-dX > deleteIconBitmap.width) {
            var delIconMargin = -dX.toInt() / 2
            val delIconMarginMax = (itemHeight - deleteIconBitmap.height) / 2
            if (delIconMargin > delIconMarginMax) delIconMargin = delIconMarginMax

            val delIconTop = itemView.top + (itemHeight - deleteIconBitmap.height) / 2
            val delIconLeft = right - delIconMargin - deleteIconBitmap.width / 2

            deleteIconMatrix.setTranslate(delIconLeft, delIconTop.toFloat())

            c.drawBitmap(deleteIconBitmap, deleteIconMatrix, deletePaint)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return .7f
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    interface Listener {
        fun onItemDeleteSwiped(position: Int)
    }
}