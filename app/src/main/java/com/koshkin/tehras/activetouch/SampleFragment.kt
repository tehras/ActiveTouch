package com.koshkin.tehras.activetouch

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by tehras on 7/19/16.
 *
 * Sample Fragment
 */
class SampleFragment : Fragment() {

    companion object Factory {
        fun getInstance(): SampleFragment {
            return SampleFragment()
        }
    }

    val TAG = "SampleFragment"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView")

        val v = inflater!!.inflate(R.layout.layout_sample_fragment, container, false)

        return v
    }
}
