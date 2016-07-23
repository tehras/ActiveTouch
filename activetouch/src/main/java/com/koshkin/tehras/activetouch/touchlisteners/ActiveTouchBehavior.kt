package com.koshkin.tehras.activetouch.touchlisteners

import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.koshkin.tehras.activetouch.fragments.ActiveTouchFragment
import java.io.Serializable

/**
 * Created by tehras on 7/19/16.
 *
 * TouchBehavior
 */
class ActiveTouchBehavior : View.OnTouchListener, ActiveTouchFragment.OnLoadHelper {
    override fun loaded(v: View?) {
        if (activeTouchHoverHelper != null)
            activeTouchHoverHelper!!.attachView(v)
    }

    private var builder: Builder
    private val LONG_TAP_THRESHOLD = 250L
    private var activity: FragmentActivity? = null
    private var lastDialog: ActiveTouchFragment? = null

    constructor(activity: FragmentActivity, builder: Builder) {
        this.activity = activity
        this.builder = builder

        if (builder.hoverCallback != null)
            activeTouchHoverHelper = ActiveTouchHoverHelper(builder.hoverCallback!!)
    }

    private var recyclerView: RecyclerView? = null

    companion object Factory {
        fun builder(v: View): Factory.Builder {
            return Factory.Builder(v)
        }

        val TAG = "ActiveTouchBehavior"
        private fun Builder.attach(activity: FragmentActivity): ActiveTouchBehavior {
            //do a check
            if (this.v == null)
                throw RuntimeException("View cannot be null")
            if (this.contentFrag == null && this.contentFragV4 == null && this.contentView == null)
                throw RuntimeException("Must pass a fragment or a view")
            if (this.parentView == null)
                throw RuntimeException("Container cannot be null")

            val a = ActiveTouchBehavior(activity, this)
            a.attachTouchListener()
            Log.d(TAG, "Builder has attached")

            return a
        }

        class Builder(v: View) : Serializable {
            val v: View? = v
            var contentView: View? = null
            var contentFragV4: Fragment? = null
            var contentFrag: android.app.Fragment? = null
            var parentView: ViewGroup? = null
            var hoverCallback: OnViewHoverOverListener? = null
            var blockCallback: BlockScrollableParentListener? = null

            @Suppress("unused")
            fun setHoverCallback(callback: OnViewHoverOverListener): Builder {
                hoverCallback = callback
                return this
            }

            @Suppress("unused")
            fun setBlockScrollableCallback(callback: BlockScrollableParentListener): Builder {
                blockCallback = callback
                return this
            }

            @Suppress("unused")
            fun setContentView(view: View?): Builder {
                contentView = view
                return this
            }

            @Suppress("unused")
            fun setContentFragment(fragment: Fragment): Builder {
                contentFragV4 = fragment
                return this
            }

            @Suppress("unused")
            fun setContentFragment(fragment: android.app.Fragment): Builder {
                contentFrag = fragment
                return this
            }

            @Suppress("unused")
            fun setContainerView(parentView: ViewGroup): Builder {
                this.parentView = parentView
                return this
            }

            @Suppress("unused")
            fun build(activity: FragmentActivity) {
                this.attach(activity)
            }
        }
    }

    private fun attachTouchListener() {
        builder.v!!.setOnTouchListener(this)
    }

    private fun startPopup() {
        if (builder.parentView != null) {
            //haptic feedback
            builder.v!!.isHapticFeedbackEnabled = true
            builder.v!!.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            startActiveTouchFragment(builder.parentView!!, builder)

            blockScroll(true)
        }
    }


    private fun blockScroll(b: Boolean) {
        if (builder.blockCallback != null) {
            builder.blockCallback!!.onBlock(b)
        }
    }

    private fun hidePopup() {
        if (lastDialog != null && activity != null) {
            activeTouchHoverHelper?.clearViews()
            activity!!.onBackPressed()
            lastDialog = null

            blockScroll(false)
        }
    }

    private fun startActiveTouchFragment(parentViewGroup: ViewGroup, b: Builder) {
        if (activity == null)
            return

        Log.d(TAG, "startActiveTouchFragment")

        val fm = activity!!.supportFragmentManager
        lastDialog = ActiveTouchFragment.getInstance(b, this)

        fm.beginTransaction()
                .add(parentViewGroup.id, lastDialog)
                .addToBackStack("ActiveTouchFragment")
                .commit()
    }

    private var isInside: Boolean = false
    private var activeTouchHoverHelper: ActiveTouchHoverHelper? = null

    /**
     * Touch Listener
     */
    override fun onTouch(v: View?, ev: MotionEvent): Boolean {

        // this section will keep track of any hover views
        if (builder.hoverCallback != null && activeTouchHoverHelper != null) {
            activeTouchHoverHelper!!.onTouch(ev)
        }

        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isInside = true
                handler.postDelayed(mLongPressed, LONG_TAP_THRESHOLD)
                return true
            }
            MotionEvent.ACTION_UP -> {
                isInside = false
                handler.removeCallbacks(mLongPressed)
                hidePopup()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                isInside = false
                hidePopup()
                if (!isShowing())
                    handler.removeCallbacks(mLongPressed)
                return true
            }
            MotionEvent.ACTION_MOVE -> return true
        }

        return false
    }

    private fun isShowing(): Boolean = lastDialog != null

    val handler = Handler()
    var mLongPressed: Runnable = Runnable { startPopup() }

    interface OnViewHoverOverListener {
        @Suppress("unused")
        fun onHover(v: View?, isInside: Boolean)
    }

    interface BlockScrollableParentListener {
        @Suppress("unused")
        fun onBlock(b: Boolean)
    }
}
