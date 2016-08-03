package com.github.tehras.activetouch.touchlisteners

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.github.tehras.activetouch.fragments.ActiveTouchFragment
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

    private var builder: ActiveTouchBuilder
    private var activity: FragmentActivity? = null

    constructor(activity: FragmentActivity, builder: ActiveTouchBuilder) {
        this.activity = activity
        this.builder = builder

        if (builder.hoverCallback != null)
            activeTouchHoverHelper = ActiveTouchHoverHelper(builder.hoverCallback!!)
    }

    companion object Factory {
        fun Builder(v: View, contentView: ViewGroup): ActiveTouchBuilder {
            return Factory.ActiveTouchBuilder(v, contentView)
        }

        val TAG = "ActiveTouchBehavior"
        private fun ActiveTouchBuilder.attach(activity: FragmentActivity): ActiveTouchBehavior {
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

        /**
         * Builder
         */
        open class ActiveTouchBuilder(v: View, parentView: ViewGroup) : Serializable {
            val v: View? = v
            val parentView: ViewGroup? = parentView

            var contentView: View? = null
                private set
            var contentFragV4: Fragment? = null
                private set
            var contentFrag: android.app.Fragment? = null
                private set
            var hoverCallback: OnViewHoverOverListener? = null
                private set
            var popupCallback: OnActiveTouchPopupListener? = null
                private set

            @Suppress("unused")
            fun setHoverCallback(callback: OnViewHoverOverListener): ActiveTouchBuilder {
                hoverCallback = callback
                return this
            }

            @Suppress("unused")
            fun setPopupCallback(callback: OnActiveTouchPopupListener): ActiveTouchBuilder {
                popupCallback = callback
                return this
            }

            @Suppress("unused")
            fun setContentFragment(fragment: Fragment): ActiveTouchBuilder {
                contentFragV4 = fragment
                return this
            }

            @Suppress("unused")
            fun setContentFragment(fragment: android.app.Fragment): ActiveTouchBuilder {
                contentFrag = fragment
                return this
            }

            @Suppress("unused")
            fun setContentView(contentView: ViewGroup): ActiveTouchBuilder {
                this.contentView = contentView
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

    /**
     * Show 3D touch Popup
     */
    fun startPopup() {
        if (builder.parentView != null) {
            //haptic feedback
            builder.v!!.isHapticFeedbackEnabled = true
            builder.v!!.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            startActiveTouchFragment(builder.parentView!!, builder)

            builder.popupCallback?.onShow()

            blockScroll(true)
        }
    }

    /**
     * Hide the 3D touch popup
     */
    fun hidePopup() {
        if (lastDialog != null && activity != null) {
            activeTouchHoverHelper?.clearViews()
            activity!!.onBackPressed()
            lastDialog = null

            builder.popupCallback?.onDismiss()

            blockScroll(false)
        }
    }

    var isBlocked = false

    /**
     * Block everything from intercepting
     */
    private fun blockScroll(b: Boolean) {
        builder.v!!.parent.requestDisallowInterceptTouchEvent(b) // this blocks recycler view + view Pager from intercepting

        isBlocked = b
    }

    private fun startActiveTouchFragment(parentViewGroup: ViewGroup, b: ActiveTouchBuilder) {
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

    private var activeTouchHoverHelper: ActiveTouchHoverHelper? = null

    /**
     * Touch Listener
     */
    override fun onTouch(v: View?, ev: MotionEvent): Boolean {
        if (builder.hoverCallback != null && activeTouchHoverHelper != null) {
            activeTouchHoverHelper!!.onTouch(ev)
        }

        return com.github.tehras.activetouch.touchlisteners.onTouch(ev, v!!, this, mLongPressed, isBlocked)
    }

    interface OnViewHoverOverListener {
        @Suppress("unused")
        fun onHover(v: View?, isInside: Boolean)
    }

    interface OnActiveTouchPopupListener {
        @Suppress("unused")
        fun onShow()

        @Suppress("unused")
        fun onDismiss()
    }

    var mLongPressed = Runnable { startPopup() }
}
