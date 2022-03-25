package com.smish.justScribble

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.smish.justScribble.FragmentMain.Companion.brush
import kotlin.math.*

class PaintView: View {
    private var params: ViewGroup.LayoutParams? = null

    private var mStartX = 0f
    private var mStartY = 0f

    private var mX: Float = 0f
    private var mY: Float = 0f
    private var mPath = Path()

    private lateinit var bitmap: Bitmap

    private var isDrawing = false

    companion object {
        var currentBrushColor = R.color.black
        var currentShape = "LINE"
        lateinit var canvas: Canvas
    }

    constructor(context: Context) : this(context, null) {
        initData()

    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        initData()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initData()
    }

    private fun initData() {
        brush.isAntiAlias = true
        brush.isDither = true
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeCap = Paint.Cap.ROUND
        brush.strokeWidth = 8f
        brush.color = currentBrushColor

        params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

//        Get points
        mX = event.x
        mY = event.y

        when (currentShape) {
            "LINE" -> {
                onTouchEventLine(event)
            }

            "ARROW" -> {
                onTouchEventArrow(event)
            }

            "RECTANGLE" -> {
                onTouchEventRectangle(event)
            }

            "CIRCLE" -> {
                onTouchEventCircle(event)
            }
        }
        return true
    }

    private fun onTouchEventLine(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mStartX = mX
                mStartY = mY

                mPath.reset()
                mPath.moveTo(mX, mY)

                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {

                mPath.quadTo(
                    mStartX,
                    mStartY,
                    (mX + mStartX) / 2,
                    (mY + mStartY) / 2
                )
                mStartX = mX
                mStartY = mY

                canvas.drawPath(mPath, brush)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
                mPath.lineTo(mStartX, mStartY)
                canvas.drawPath(mPath, brush)
                mPath.reset()
                invalidate()
            }
        }
    }

    private fun onTouchEventArrow(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mStartX = mX
                mStartY = mY
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawArrow(canvas, brush)
                invalidate()

            }
        }

    }

    private fun drawArrow(canvas: Canvas?, brush: Paint) {
        brush.color = currentBrushColor
        val angleRad: Float
        val radius = 30f
        val angle = 35f

        angleRad = ((3.14 * angle / 180.0f).toFloat())
        val lineAngle: Float = atan2(mY - mStartY, mX - mStartX)

        canvas?.drawLine(mStartX, mStartY, mX, mY, brush)

        val aPath = Path()
        aPath.fillType = Path.FillType.EVEN_ODD

        aPath.moveTo(mX, mY)
        aPath.lineTo(
            (mX - radius * cos(lineAngle - angleRad / 2.0)).toFloat(),
            (mY - radius * sin(lineAngle - angleRad / 2.0)).toFloat()
        )
        aPath.lineTo(
            (mX - radius * cos(lineAngle + angleRad / 2.0)).toFloat(),
            (mY - radius * sin(lineAngle + angleRad / 2.0)).toFloat()
        )
        aPath.close()

        canvas?.drawPath(aPath, brush)
    }

    private fun onTouchEventRectangle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mStartX = mX
                mStartY = mY
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
                drawRectangle(canvas, brush)
                invalidate()
            }
        }
    }

    private fun onTouchEventCircle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                mStartX = mX
                mStartY = mY
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isDrawing = false
                canvas.drawCircle(
                    mStartX,
                    mStartY,
                    getRadius(mStartX, mStartY, mX, mY),
                    brush
                )
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.drawBitmap(bitmap, 0f, 0f, brush)

        if (isDrawing) {
            when (currentShape) {
                "LINE" -> {

                }

                "ARROW" -> {
                    onDrawArrow(canvas)
                }

                "RECTANGLE" -> {
                    onDrawRectangle(canvas)
                }

                "CIRCLE" -> {
                    onDrawCircle(canvas)
                }
            }
        }
    }

    private fun onDrawArrow(canvas: Canvas?) {
        drawArrow(canvas, brush)
    }

    private fun onDrawCircle(canvas: Canvas?) {
        brush.color = currentBrushColor
        canvas?.drawCircle(
            mStartX,
            mStartY,
            getRadius(mStartX, mStartY, mX, mY),
            brush
        )
    }

    private fun getRadius(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))
    }

    private fun onDrawRectangle(canvas: Canvas?) {
        drawRectangle(canvas, brush)
    }

    private fun drawRectangle(mCanvas: Canvas?, mBrush: Paint) {
        mBrush.color = currentBrushColor
        val right = if (mStartX > mX) mStartX else mX
        val left = if (mStartX > mX) mX else mStartX
        val bottom = if (mStartY > mY) mStartY else mY
        val top = if (mStartY > mY) mY else mStartY
        mCanvas?.drawRect(left, top, right, bottom, mBrush)
    }
}