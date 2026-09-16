package com.novapvp.client

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameView(context: Context) : GLSurfaceView(context) {

    private val renderer: GameRenderer

    private var previousX = 0f
    private var previousY = 0f

    init {
        setEGLContextClientVersion(2)

        renderer = GameRenderer()
        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {

                val dx = event.x - previousX
                val dy = event.y - previousY

                renderer.rotateCamera(dx, dy)

                previousX = event.x
                previousY = event.y

                return true
            }
        }

        return true
    }
}

class GameRenderer : GLSurfaceView.Renderer {

    private val cube = Cube()

    private var angleX = 20f
    private var angleY = 35f

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {

        GLES20.glClearColor(
            0.25f,
            0.45f,
            0.65f,
            1f
        )

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {

        GLES20.glViewport(
            0,
            0,
            width,
            height
        )
    }

    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClear(
            GLES20.GL_COLOR_BUFFER_BIT or
                    GLES20.GL_DEPTH_BUFFER_BIT
        )

        cube.draw(
            angleX,
            angleY
        )
    }

    fun rotateCamera(dx: Float, dy: Float) {

        angleY += dx * 0.5f
        angleX += dy * 0.5f

        angleX = angleX.coerceIn(
            -80f,
            80f
        )
    }
}

class Cube {

    private val vertices = floatArrayOf(

        -1f, -1f, 1f,
         1f, -1f, 1f,
         1f,  1f, 1f,
        -1f,  1f, 1f,

        -1f, -1f, -1f,
         1f, -1f, -1f,
         1f,  1f, -1f,
        -1f,  1f, -1f
    )

    private val vertexBuffer: FloatBuffer =
        ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }

    fun draw(
        angleX: Float,
        angleY: Float
    ) {

        GLES20.glPushMatrix()

        GLES20.glRotatef(
            angleX,
            1f,
            0f,
            0f
        )

        GLES20.glRotatef(
            angleY,
            0f,
            1f,
            0f
        )

        GLES20.glColorMask(
            true,
            true,
            true,
            true
        )

        GLES20.glEnableClientState(
            GLES20.GL_VERTEX_ARRAY
        )

        GLES20.glVertexPointer(
            3,
            GLES20.GL_FLOAT,
            0,
            vertexBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_LINE_LOOP,
            0,
            4
        )

        GLES20.glDisableClientState(
            GLES20.GL_VERTEX_ARRAY
        )

        GLES20.glPopMatrix()
    }
}
