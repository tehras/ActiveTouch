package com.koshkin.tehras.activetouchsample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.koshkin.tehras.activetouch.touchlisteners.ActiveTouchBehavior

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val v = findViewById(R.id.clickable_view)

        ActiveTouchBehavior.builder(v)
                .setContainerView(findViewById(R.id.container_view) as ViewGroup)
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
