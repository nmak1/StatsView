package ru.netology.statsview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            500F,
            500F,
            500F,
            0F,
        )
        val textView = findViewById<TextView>(R.id.label)

        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation).apply {
                setAnimationListener(object : Animation.AnimationListener {
                    @SuppressLint("SetTextI18n")
                    override fun onAnimationStart(p0: Animation?) {
                        textView.text = "onAnimationStart"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationEnd(p0: Animation?) {
                        textView.text = "onAnimationEnd"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationRepeat(p0: Animation?) {
                        textView.text = "onAnimationRepeat"
                    }

                })
            }
        )
    }
}