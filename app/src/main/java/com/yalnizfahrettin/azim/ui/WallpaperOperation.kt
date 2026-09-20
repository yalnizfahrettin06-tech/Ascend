package com.yalnizfahrettin.azim.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Wallpaper color changes can recreate the Activity while Android completes the operation. */
class WallpaperOperation : ViewModel() {
    var busy by mutableStateOf(false)
        private set
    var result by mutableStateOf<String?>(null)
        private set
    private var session = ""
    fun begin(id: String) { if(session != id && !busy) { session = id; result = null } }
    fun clearResult() { if(!busy) result = null }
    fun apply(work: suspend () -> Boolean) {
        if(busy) return
        busy = true; result = null
        viewModelScope.launch {
            try { result = if(work()) "done" else "error" }
            catch(e: CancellationException) { throw e }
            catch(_: Exception) { result = "error" }
            finally { busy = false }
        }
    }
}
