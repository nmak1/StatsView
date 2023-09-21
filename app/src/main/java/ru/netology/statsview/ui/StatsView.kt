package ru.netology.statsview.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.R.color.empty_color
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
            )

        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            reInvalidate()
        }
    private var fullCircleDegrees = 360F
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var animator: Animator? = null
    private var move = 0F

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    @SuppressLint("ResourceAsColor")
    private val paintEmpty = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = empty_color
        alpha = 10
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var startAngle = -90F
        val sequentialRotation = fullCircleDegrees * move + startAngle
        canvas.drawArc(oval, startAngle, fullCircleDegrees, false, paintEmpty)
        data.forEachIndexed { index, datum ->
            val rotationAngle = (datum / data.maxOrNull()!!.times(data.count())) * fullCircleDegrees
            val sequentialRotationAngle = min(rotationAngle, sequentialRotation - startAngle)
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, sequentialRotationAngle, false, paint)
            startAngle += rotationAngle

            if (startAngle > sequentialRotation) {
                return@onDraw
            }
        }

        val text = (data.sum() / data.maxOrNull()!!.times(data.count())) * 100F
        canvas.drawText(
            "%.2f%%".format(text),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
        if (text == 100F) {
            paint.color = colors[0]
            canvas.drawArc(oval, startAngle + rotation, 1F, false, paint)
        }
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun reInvalidate() {
        animator?.apply {
            cancel()
            removeAllListeners()
        }
        animator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener {
                interpolator = LinearInterpolator()
                duration = 5_000
                move = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
}
