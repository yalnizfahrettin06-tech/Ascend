package com.yalnizfahrettin.azim.core

/** Shared quote typography for the home reader and theme preview. */
object ReadingLayout {
    fun quoteSize(length: Int, largeText: Boolean, narrow: Boolean): Int = when {
        largeText -> if(length <= 80) 30 else if(length > 150) 26 else 28
        narrow -> if(length <= 80) 33 else if(length > 150) 28 else 30
        else -> if(length <= 80) 34 else if(length > 150) 26 else 30
    }
    fun measure(artwork: Boolean, largeText: Boolean) = if(largeText || !artwork) 1f else .76f
}
