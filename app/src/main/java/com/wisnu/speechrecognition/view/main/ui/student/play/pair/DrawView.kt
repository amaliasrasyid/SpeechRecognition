package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

private var paint: Paint? = null
var lines : ArrayList<Line> = ArrayList();
class DrawView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
: View(context, attrs, defStyleAttr) {

    private var isRedraw = false

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
            canvas!!.drawLine(
                line.startX-20, line.startY-560,
                line.stopX-20, line.stopY-560,
                paint!!
            )
            Log.d("OnDraw", line.toString())
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

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when{
//            event!!.action == MotionEvent.ACTION_DOWN -> {//A pressed gesture has started, the motion contains the initial starting location.
//
//                lines.add(Line(event.x,event.y))
//                isDrawing = true
//                return true
//            }
//            event!!.action == MotionEvent.ACTION_MOVE && lines.size > 0  -> {//A change has happened during a press gesture;press gesture is moving
//                val currentLine = lines.get(lines.size - 1) //ambil line paling terbaru (stack teratas)
//                currentLine.stopX = event.x
//                currentLine.stopY = event.y
//                invalidate()
//                return true
//            }
//            event!!.action == MotionEvent.ACTION_UP && lines.size > 0 -> { //contains the final release location
//                val currentLine = lines.get(lines.size - 1) //ambil line paling terbaru (stack teratas)
//                currentLine.stopX = event.x
//                currentLine.stopY = event.y
//                invalidate()
//                return true
//            }
//            else -> {
//                return false
//            }
//        }
//    }
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

//    init{
//        println("${startX} ${startY} ${stopX} ${stopY}")
//        this.startX = _startX
//        this.startY = _startY
//        this.stopX = _stopX
//        this.stopY = _stopY
//    }

//    constructor(_startX: Float,_startY: Float) : this(_startX,_startY, _stopX, _stopY) {
//        this(startX,startY,stopX,stopY)
//    }
}
