package com.koshkin.tehras.activetouchsample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.koshkin.tehras.activetouch.touchlisteners.ActiveTouchBehavior

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var blockedScroll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView!!.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return !blockedScroll
            }
        }
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = SampleAdapter(this)
    }

    class SampleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
        val activity: MainActivity

        constructor(activity: MainActivity) {
            this.activity = activity
        }

        var lastInt = -1

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (position > lastInt)
                activity.addLongHolder(holder!!.itemView)
            lastInt = position
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.layout_sample_list_view, parent, false))
        }

        override fun getItemCount(): Int {
            return 20
        }

        class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        }

    }

    fun addLongHolder(v: View?) {
        Log.d("MainActivity", "addingView")
        ActiveTouchBehavior.builder(v!!)
                .setContainerView(this.findViewById(R.id.container_view) as ViewGroup)
                .setBlockScrollableCallback(object : ActiveTouchBehavior.BlockScrollableParentListener {
                    override fun onBlock(b: Boolean) {
                        blockedScroll = b
                    }

                })
                .setHoverCallback(object : ActiveTouchBehavior.OnViewHoverOverListener {

                    override fun onHover(v: View?, isInside: Boolean) {
                        if (v != null && v.id == R.id.sample_image && v is ImageView) {
                            if (isInside)
                                v.setColorFilter(Color.argb(50, 0, 0, 0))
                            else
                                v.setColorFilter(Color.argb(0, 0, 0, 0))
                        }
                    }

                })
                .setContentFragment(SampleFragment.getInstance()).build(this)
    }

}
