package com.yalnizfahrettin.azim.ui

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun rememberCurrentDay(): LocalDate {
    var today by remember { mutableStateOf(LocalDate.now()) }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event -> if(event == Lifecycle.Event.ON_RESUME) today = LocalDate.now() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(owner) { while(true) { today = LocalDate.now(); delay(60_000) } }
    return today
}
