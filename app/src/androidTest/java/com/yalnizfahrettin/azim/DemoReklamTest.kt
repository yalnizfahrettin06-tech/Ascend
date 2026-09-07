package com.yalnizfahrettin.azim

import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.yalnizfahrettin.azim.core.AzimTema
import com.yalnizfahrettin.azim.ui.DemoReklamDurumu
import com.yalnizfahrettin.azim.ui.demoActivity
import com.yalnizfahrettin.azim.ui.demoSayfasiniAc
import com.yalnizfahrettin.azim.ui.rememberDemoReklam
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class DemoReklamTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private class DemoOwner : LifecycleOwner {
        val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle get() = registry
        fun resume() = registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun pause() = registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    private fun resumedOwner(): DemoOwner {
        lateinit var owner: DemoOwner
        compose.runOnUiThread {
            owner = DemoOwner()
            owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            owner.registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            owner.resume()
        }
        return owner
    }

    @Test fun stateRequiresSuccessfulLaunchAndPauseBeforeOneReturn() {
        val state = DemoReklamDurumu()
        assertNull(state.olay(Lifecycle.Event.ON_RESUME))
        assertTrue(state.hazirla("selected"))
        assertNull(state.olay(Lifecycle.Event.ON_RESUME))
        state.acilisSonucu(false)
        assertNull(state.olay(Lifecycle.Event.ON_PAUSE))
        assertNull(state.olay(Lifecycle.Event.ON_RESUME))
        assertNull(state.bekleyenGrup)

        assertTrue(state.hazirla("selected"))
        state.acilisSonucu(true)
        assertFalse(state.hazirla("another"))
        assertNull(state.olay(Lifecycle.Event.ON_RESUME))
        assertNull(state.olay(Lifecycle.Event.ON_PAUSE))
        assertEquals("selected", state.olay(Lifecycle.Event.ON_RESUME))
        assertNull(state.bekleyenGrup)
        state.olay(Lifecycle.Event.ON_PAUSE)
        assertNull(state.olay(Lifecycle.Event.ON_RESUME))
    }

    @Test fun realObserverRejectsFailedLaunchAndUnlocksOnlySelectedGroupOnce() {
        val owner = resumedOwner()
        var launchOk = false
        var launches = 0
        var errors = 0
        val granted = mutableListOf<String>()
        compose.setContent {
            AzimTema {
                val open = rememberDemoReklam(
                    acildi = { granted += it },
                    hata = { errors++ },
                    baslat = { launches++; launchOk },
                    lifecycle = owner.lifecycle,
                )
                Column {
                    Button(onClick = { open("selected") }) { Text("Demo selected") }
                    Button(onClick = { open("another") }) { Text("Demo another") }
                }
            }
        }
        compose.runOnIdle { assertTrue(granted.isEmpty()) }
        compose.onNodeWithText("Demo selected").performClick()
        compose.runOnIdle {
            assertEquals(1, launches)
            assertEquals(1, errors)
            owner.pause(); owner.resume()
            assertTrue(granted.isEmpty())
            launchOk = true
        }
        compose.onNodeWithText("Demo selected").performClick()
        // Repeated taps must not replace the pending group or launch another browser.
        compose.onNodeWithText("Demo another").performClick()
        compose.runOnIdle {
            assertEquals(2, launches)
            assertTrue(granted.isEmpty())
            owner.pause(); owner.resume()
            assertEquals(listOf("selected"), granted)
            owner.pause(); owner.resume()
            assertEquals(listOf("selected"), granted)
        }
    }

    @Test fun savedPendingGroupSurvivesRestorationWithoutPrematureUnlock() {
        val owner = resumedOwner()
        val granted = mutableListOf<String>()
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            AzimTema {
                val open = rememberDemoReklam(
                    acildi = { granted += it },
                    hata = { fail("The injected browser launch succeeds") },
                    baslat = { true },
                    lifecycle = owner.lifecycle,
                )
                Button(onClick = { open("saved-group") }) { Text("Open saved demo") }
            }
        }
        compose.onNodeWithText("Open saved demo").performClick()
        // Observer re-registration on an already resumed owner must not unlock.
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertTrue(granted.isEmpty())
            owner.pause()
        }
        // Restore while away: the selected key and observed pause must both survive.
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertTrue(granted.isEmpty())
            owner.resume()
            assertEquals(listOf("saved-group"), granted)
            owner.pause(); owner.resume()
            assertEquals(1, granted.size)
        }
    }

    @Test fun browserIntentUnwrapsActivityAndLaunchFailureIsReported() {
        compose.runOnUiThread {
            val wrapped = ContextWrapper(ContextWrapper(compose.activity))
            assertSame(compose.activity, wrapped.demoActivity())
            var captured: Intent? = null
            assertTrue(demoSayfasiniAc(wrapped) { activity, intent ->
                assertSame(compose.activity, activity)
                captured = intent
            })
            assertEquals(Intent.ACTION_VIEW, captured?.action)
            assertEquals("https://www.google.com", captured?.dataString)
            assertTrue(captured?.categories?.contains(Intent.CATEGORY_BROWSABLE) == true)
            assertFalse(demoSayfasiniAc(wrapped) { _, _ -> throw ActivityNotFoundException() })
            assertFalse(demoSayfasiniAc(wrapped) { _, _ -> throw SecurityException() })
            assertFalse(demoSayfasiniAc(compose.activity.applicationContext))
        }
    }
}
