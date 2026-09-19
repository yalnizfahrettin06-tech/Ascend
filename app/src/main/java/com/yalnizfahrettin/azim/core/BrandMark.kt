package com.yalnizfahrettin.azim.core

/** One silhouette for Compose, exported cards and Android icons: A crown / helmet. */
object BrandMark {
    const val PATH = "M12,2 L20,15 L15,13 L12,7 L9,13 L4,15 Z M4,17 L10,15 L10,19 L7,22 L3,20 Z M20,17 L14,15 L14,19 L17,22 L21,20 Z"
    fun draw(canvas: android.graphics.Canvas, x: Float, y: Float, size: Float, paint: android.graphics.Paint) {
        val shape = androidx.core.graphics.PathParser.createPathFromPathData(PATH) ?: return
        val fill = android.graphics.Paint(paint).apply { style = android.graphics.Paint.Style.FILL }
        canvas.save()
        canvas.translate(x,y)
        canvas.scale(size / 24f,size / 24f)
        canvas.drawPath(shape,fill)
        canvas.restore()
    }
}
