package com.koshkin.tehras.activetouch.touchlisteners

import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import java.util.*

/**
 * Created by tehras on 7/23/16.
 *
 * This is helper that will send back the in the callback if a view is hovered upon
 */
class ActiveTouchHoverHelper(val callback: ActiveTouchBehavior.OnViewHoverOverListener) {

    var viewMap: HashMap<View, Rect> = HashMap()

    fun addView(view: View) {
        Log.d(TAG, "view with id ${view.id} getting added")
        viewMap.put(view, measureRect(view))
    }

    fun measureRect(view: View): Rect {
        val rect = Rect()
        val location = IntArray(2)

        view.getDrawingRect(rect)
        view.getLocationOnScreen(location)
        rect.offset(location[0], location[1])

        return rect
    }

    fun onTouch(ev: MotionEvent) {
        for (view in viewMap.keys) {
            val rect = viewMap[view]

            if (ev.action === MotionEvent.ACTION_MOVE) {
                if (rect!!.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    callback.onHover(view, true)
                } else {
                    callback.onHover(view, false)
                }
            }
        }
    }

    private val TAG = "ActiveTouchHoverHelper"

    @Suppress("unused") fun attachView(v: View?) {
        Log.d(TAG, "attachView $v")
        if (v != null && v is ViewGroup) {
            v.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    Log.d(TAG, "onGlobalLayout")
                    recursiveLoopChildren(v)
                    v.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            })
        }
    }

    private fun recursiveLoopChildren(parent: ViewGroup) {
        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                addView(child)
                recursiveLoopChildren(child)
            } else {
                if (child != null) {
                    addView(child)
                }
            }
        }
    }

    fun clearViews() {
        viewMap.clear()
    }
}
