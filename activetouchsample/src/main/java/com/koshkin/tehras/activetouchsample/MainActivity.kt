package com.koshkin.tehras.activetouchsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.koshkin.tehras.activetouch.touchlisteners.ActiveTouchBehavior
import java.util.*

class MainActivity : AppCompatActivity(), ActiveTouchBehavior.OnActiveTouchPopupListener {
    override fun onShow() {
        idList.clear()
    }

    override fun onDismiss() {
        Toast.makeText(this, "Hover over - $idList", Toast.LENGTH_SHORT).show()
    }

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

    var idList = ArrayList<Int>()

    fun addLongHolder(v: View?) {
        ActiveTouchBehavior.Builder(v!!, this.findViewById(R.id.container_view) as ViewGroup)
                .setPopupCallback(this)
                .setHoverCallback(object : ActiveTouchBehavior.OnViewHoverOverListener {

                    override fun onHover(v: View?, isInside: Boolean) {
                        Log.d("MainActivity", "view - ${v?.id} - inside - $isInside")
                        if (v != null && (v.id == R.id.add_button || v.id == R.id.remove_button || v.id == R.id.cancel_button)) {

                            if (isInside) {
                                if (!idList.contains(v.id)) {
                                    idList.add(v.id)
                                    v.background = this@MainActivity.resources.getDrawable(R.color.colorAccent)
                                    v.isHapticFeedbackEnabled = true
                                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                }
                            } else {
                                idList.remove(v.id)
                                v.background = this@MainActivity.resources.getDrawable(android.R.color.white)
                            }
                        }
                    }

                })
                .setContentFragment(SampleFragment.getInstance())
                .build(this)
    }

}
