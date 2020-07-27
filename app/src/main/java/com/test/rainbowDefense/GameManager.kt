package com.test.rainbowDefense

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.view.MotionEvent
import android.view.WindowManager

class GameManager(val content: Context, val v: CanvasView) {

    // 화면 크기 추출
    var displayWidth = v.width
    var displayHeight = v.height

    // 핸들러 쓰레드
    var handler: Handler? = Handler()
    var thread = ThreadClass()

    // 핑, 웨이브, 투사체
    val ping = 120
    var waveTime: Int = 5 * ping
    var waveCounter: Int = 0
    var arrowTime: Int = (0.1 * ping).toInt()
    var arrowCounter: Int = 0

    // 화면 좌표
    var touchX: Float = 100f
    var touchY: Float = 500f
    var isTouch: Boolean = false

    // 리소스 Drawable
    val cursorDrawable = v.resources.getDrawable(R.drawable.cursor1, content.theme)
    val backgroundDrawable = v.resources.getDrawable(R.drawable.background, content.theme)
    val mountainDrawable = v.resources.getDrawable(R.drawable.mountains, content.theme)
    val treesDrawable = v.resources.getDrawable(R.drawable.trees, content.theme)

    // 초기화
    init {
        v.cursor = Shape(touchX.toInt(),touchY.toInt(),12*3,19*3,cursorDrawable)
        v.background = Shape(0,0,displayWidth,displayHeight,backgroundDrawable)
        v.mountain = Shape(0,0,displayWidth,displayHeight,mountainDrawable)
        v.trees = Shape(0,0,displayWidth,displayHeight,treesDrawable)

        v.setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.performClick()
                    onStartTouchEvent(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    onMoveTouchEvent(x, y)
                }
                MotionEvent.ACTION_UP -> {
                    upTouchEvent()
                }
            }
            true
        }
    }

    // 1초에 120번 실행 ( 게임 진행 )
    fun run() {
        handler?.postDelayed(thread, 1000/ping.toLong())
    }
    inner class ThreadClass : Thread() {
        override fun run() {
            arrowCheck()
            waveCheck()
            checkProjectile(v.projectile_array)
            checkCollision(v.monster_array, v.projectile_array)
            v.monster_array.forEach{it.move()}
            v.projectile_array.forEach{it.move()}
            v.invalidate()
            handler?.postDelayed(this, 1000/ping.toLong())
        }
    }

    // 터치 이벤트 관련
    private fun onStartTouchEvent(x: Float, y: Float) {
        touchX = x
        touchY = y
        v.cursor?.x = x.toInt()
        v.cursor?.y = y.toInt()
        isTouch = true
        v.invalidate()
    }
    private fun onMoveTouchEvent(x: Float, y: Float) {
        val dx = Math.abs(x - touchX)
        val dy = Math.abs(y - touchY)
        if (dx >= CanvasView.TOLERANCE || dy >= CanvasView.TOLERANCE) {
            touchX = x
            touchY = y
            v.cursor?.x = x.toInt()
            v.cursor?.y = y.toInt()
        }
        v.invalidate()
    }
    private fun upTouchEvent() {
        isTouch = false
    }

    // 투사체, 웨이브 딜레이 체크
    fun arrowCheck() {
        if (isTouch && arrowCounter >= arrowTime) {
            arrowStart()
            arrowCounter = 0
        } else {
            arrowCounter++
        }
    }
    fun arrowStart() {
        val arrawDrawable = v.resources.getDrawable(R.drawable.arrow, content.theme)
        v.projectile_array.add(Projectile(10, touchY.toInt(), 137, 17, arrawDrawable))
    }
    fun waveCheck() {
        if (waveCounter >= waveTime) {
            waveStart()
            waveCounter = 0
        } else {
            waveCounter++
        }
    }
    fun waveStart() {

        val monsterDrawable = v.resources.getDrawable(R.drawable.monster_1, content.theme)
        v.monster_array.add(Monster(2500, 200, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(2700, 500, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(2900, 700, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(3100, 300, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(3300, 600, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(3500, 800, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(3700, 100, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(3900, 400, 1452 / 10, 1148 / 10, monsterDrawable))
        v.monster_array.add(Monster(4100, 600, 1452 / 10, 1148 / 10, monsterDrawable))
    }

    // 투사체 수명 체크, 충돌 체크
    fun checkProjectile(arrayList: ArrayList<Projectile>) {
        var n: Int = 0
        while (n < arrayList.size) {
            if (arrayList[n].lifetime == 0) {
                arrayList.removeAt(n)
                n--
            }
            n++
        }
    }
    fun checkCollision(monster: ArrayList<Monster>, projectile: ArrayList<Projectile>) {
        var n: Int = 0
        var m: Int = 0
        while (n < monster.size) {
            while (m < projectile.size) {
                val condition1: Boolean =
                    monster[n].x < projectile[m].x + projectile[m].width && monster[n].x + monster[n].width > projectile[m].x
                val condition2: Boolean =
                    monster[n].y < projectile[m].y + projectile[m].height && monster[n].y + monster[n].height > projectile[m].y
                if (condition1 && condition2) {
                    monster.removeAt(n)
                    projectile.removeAt(m)
                    m = 0
                    break
                }
                m++
            }
            m = 0
            n++
        }
    }

}