package com.yalnizfahrettin.azim.paylas

import android.graphics.Bitmap
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.opengl.GLUtils
import android.view.Surface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/*
 * MediaCodec'in giriş yüzeyi (createInputSurface) Canvas ile ÇİZİLEMEZ —
 * OpenGL için yapılandırılmıştır. Bu yüzden kareleri GL ile basmamız
 * gerekiyor: burada minimal bir EGL ortamı ve tam ekran dokulu dörtgen var.
 *
 * Akış: KartCizici bir Bitmap üretir → burada dokuya yüklenir → encoder'ın
 * yüzeyine çizilir → swapBuffers ile kodlayıcıya gider.
 */
internal class EglOrtam(yuzey: Surface) {

    private var ekran: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var baglam: EGLContext = EGL14.EGL_NO_CONTEXT
    private var pencere: EGLSurface = EGL14.EGL_NO_SURFACE
    private var program = 0
    private var dokuKimlik = 0

    init {
        ekran = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        EGL14.eglInitialize(ekran, IntArray(2), 0, IntArray(2), 1)

        val ayarlar = intArrayOf(
            EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            KAYDEDILEBILIR, 1,     // encoder yüzeyine çizebilmek için şart
            EGL14.EGL_NONE,
        )
        val konfigler = arrayOfNulls<EGLConfig>(1)
        EGL14.eglChooseConfig(ekran, ayarlar, 0, konfigler, 0, 1, IntArray(1), 0)

        baglam = EGL14.eglCreateContext(
            ekran, konfigler[0], EGL14.EGL_NO_CONTEXT,
            intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE), 0,
        )
        pencere = EGL14.eglCreateWindowSurface(
            ekran, konfigler[0], yuzey, intArrayOf(EGL14.EGL_NONE), 0,
        )
        EGL14.eglMakeCurrent(ekran, pencere, pencere, baglam)
        programiKur()
    }

    private fun programiKur() {
        val vs = """
            attribute vec4 konum;
            attribute vec2 dokuKoord;
            varying vec2 vKoord;
            void main() { gl_Position = konum; vKoord = dokuKoord; }
        """.trimIndent()
        val fs = """
            precision mediump float;
            varying vec2 vKoord;
            uniform sampler2D doku;
            void main() { gl_FragColor = texture2D(doku, vKoord); }
        """.trimIndent()
        program = GLES20.glCreateProgram().also { p ->
            GLES20.glAttachShader(p, derle(GLES20.GL_VERTEX_SHADER, vs))
            GLES20.glAttachShader(p, derle(GLES20.GL_FRAGMENT_SHADER, fs))
            GLES20.glLinkProgram(p)
        }
        val d = IntArray(1)
        GLES20.glGenTextures(1, d, 0)
        dokuKimlik = d[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dokuKimlik)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }

    private fun derle(tip: Int, kaynak: String): Int =
        GLES20.glCreateShader(tip).also {
            GLES20.glShaderSource(it, kaynak)
            GLES20.glCompileShader(it)
        }

    /** Bitmap'i tam ekran çizer. Dikey çevrim doku koordinatlarında yapılır. */
    fun ciz(bmp: Bitmap, genislik: Int, yukseklik: Int) {
        GLES20.glViewport(0, 0, genislik, yukseklik)
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dokuKimlik)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "doku"), 0)

        val konum = GLES20.glGetAttribLocation(program, "konum")
        val koord = GLES20.glGetAttribLocation(program, "dokuKoord")
        GLES20.glEnableVertexAttribArray(konum)
        GLES20.glVertexAttribPointer(konum, 2, GLES20.GL_FLOAT, false, 0, KOSELER)
        GLES20.glEnableVertexAttribArray(koord)
        GLES20.glVertexAttribPointer(koord, 2, GLES20.GL_FLOAT, false, 0, DOKULAR)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    fun zamanDamgasi(nanosaniye: Long) {
        EGLExt.eglPresentationTimeANDROID(ekran, pencere, nanosaniye)
    }

    fun gonder(): Boolean = EGL14.eglSwapBuffers(ekran, pencere)

    fun kapat() {
        if (ekran != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(
                ekran, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT,
            )
            EGL14.eglDestroySurface(ekran, pencere)
            EGL14.eglDestroyContext(ekran, baglam)
            EGL14.eglTerminate(ekran)
        }
        ekran = EGL14.EGL_NO_DISPLAY
        baglam = EGL14.EGL_NO_CONTEXT
        pencere = EGL14.EGL_NO_SURFACE
    }

    private companion object {
        /** EGL_RECORDABLE_ANDROID — resmi sabit listesinde yok, değeri sabittir. */
        const val KAYDEDILEBILIR = 0x3142

        val KOSELER: FloatBuffer = tampon(
            floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
        )
        // Dikey çevrim: Bitmap yukarıdan aşağı, GL aşağıdan yukarı.
        val DOKULAR: FloatBuffer = tampon(
            floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
        )

        fun tampon(veri: FloatArray): FloatBuffer =
            ByteBuffer.allocateDirect(veri.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply { put(veri); position(0) }
    }
}
