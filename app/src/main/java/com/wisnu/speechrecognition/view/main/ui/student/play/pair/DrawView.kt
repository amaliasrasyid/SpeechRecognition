package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View

private var paint: Paint? = null
var lines : ArrayList<Line> = ArrayList();
class DrawView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
: View(context, attrs, defStyleAttr) {
    private val TAG = DrawView::class.java.simpleName
    private var isRedraw = false
    private var densityDpi = 0

    init{
        paint = Paint()
        paint!!.color = Color.RED
        paint!!.strokeWidth = 10f
        paint!!.isAntiAlias = true
        invalidate() //force calling onDraw()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for(line in lines){
            var screenAdjust = 0f
            when(densityDpi){
                in 1..120 -> screenAdjust = densityDpi.toFloat()/ 320f //ldpi
                in 160..213 -> screenAdjust = densityDpi.toFloat()/ 320f //mdpi
                in 213..240 -> screenAdjust = densityDpi.toFloat()/ 320f //hdpi
                in 240..320 -> screenAdjust = densityDpi.toFloat()/ 320f //xhdpi
                in 480..640 -> screenAdjust = densityDpi.toFloat()/ 320f //xxxhdpi
                in 320..480 -> screenAdjust = densityDpi.toFloat()/ 320f  //xxhdpi
            }
            val settingStartY = -410 * screenAdjust
            val settingStopY = -410 * screenAdjust
            val settingStartX = -20 * screenAdjust
            val settingStopX = -20 * screenAdjust

            val startX = line.startX + settingStartX
            val stopX = line.stopX + settingStopX
            val startY = line.startY + settingStartY
            val stopY = line.stopY + settingStopY

            canvas!!.drawLine(
                startX, startY,
                stopX, stopY,
                paint!!
            )
            Log.d(TAG,"line position ${startX},${startY},${stopX},${stopY}")
            Log.d(TAG,"density dpi= ${densityDpi}")
            Log.d(TAG,"screen adjust= ${screenAdjust}")
            Log.d(TAG, line.toString())
        }
    }

    fun addSourcePoint(x1: Float, y1: Float){
        lines.add(Line(x1,y1))
    }

    fun addDestinationPoint(x2: Float, y2: Float){
        if(lines.size > 0){//BUG: lines sebelumnya dissapear sebelum dpt set end pointnya
            var currentLine = lines.get(lines.size - 1)
            currentLine.stopX = x2
            currentLine.stopY = y2
        }
        invalidate()
    }

    fun clearLines(){
        lines.clear()
        invalidate()//forcing onDraw called with empty lines
    }

    fun removeLine(x2: Float, y2: Float){
        val filteredLines = lines.filterNot {
            it.stopX == x2 && it.stopY == y2
        }
        lines.clear()
        lines.addAll(filteredLines)
        invalidate()
    }
    
    fun setDensitiyDpi(densityDpi: Int){
        this.densityDpi = densityDpi
    }

}

class Line(startX: Float,startY: Float) {
    var startX: Float = 0f
    var startY: Float = 0f
    var stopX: Float = 0f
    var stopY: Float = 0f

    init{
        this.startX = startX
        this.startY = startY
    }

    constructor(startX: Float, startY: Float, stopX: Float, stopY: Float) : this(startX, startY){
        this.startX = startX
        this.startY = startY
        this.stopX = stopX
        this.stopY = stopY
    }
}
