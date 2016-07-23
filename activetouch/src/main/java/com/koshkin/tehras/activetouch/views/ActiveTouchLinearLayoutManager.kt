package com.koshkin.tehras.activetouch.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

/**
 * Created by tehras on 7/23/16.
 *
 * Custom Linear Layout Manager that will help for preventing scroll intercepting
 */
class ActiveTouchLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {
    var blockScroll = false

    constructor(context: Context?) : this(context, VERTICAL, false)

    override fun canScrollVertically(): Boolean {
        return !blockScroll && super.canScrollVertically()
    }
}
