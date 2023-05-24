package com.github.geohunt.app.ui.components.utils.intents

import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable


// Once upon a time, orpheus descended to hell with only a lyre. Reading
// through this code, you'll better understand the suffering and pain
// he went through. As for orpheous in order to ascent from Hades' reign,
// you will need to walk past this code without watching at it. The
// simplest glare could send you back down to the bottom of the tartarus.
// Finally, shall your hubris bring you to think that you can improve this code
// by using the espresso framework, I welcome you to try. For future reference
// here is the amount of time I wasted on doing so :
//               3h
//
object IntentsMocking {
    interface ActivityLauncher<I> {
        fun launch(input: I) : Unit
    }

    val doNotUseThisCursedMap = mutableMapOf<Int, Any>()

    @Composable
    inline fun <reified I, reified O> rememberLauncherForActivityResult(
        contract: ActivityResultContract<I, O>,
        noinline onResult: (O) -> Unit
    ) : ActivityLauncher<I> {
        val mocked = doNotUseThisCursedMap[contract::class.java.hashCode()]?.run {
            (this as (((O) -> Unit) -> ActivityLauncher<I>))(onResult)
        }

        return mocked ?: defaultValue(contract = contract, onResult = onResult)
    }

    inline fun <reified I, reified O> mock(
        contract: ActivityResultContract<I, O>,
        noinline callback: (I, (O) -> Unit) -> Unit,
    ) : AutoCloseable {
        val factory : ((O) -> Unit) -> ActivityLauncher<I> = { onResult ->
            object : ActivityLauncher<I> {
                override fun launch(input: I) {
                    callback(input, onResult)
                }
            }
        }

        val key = contract::class.java.hashCode()
        doNotUseThisCursedMap[key] = factory

        return AutoCloseable {
            doNotUseThisCursedMap.remove(key, factory)
        }
    }

    @Composable
    fun <I, O> defaultValue(
        contract: ActivityResultContract<I, O>,
        onResult: (O) -> Unit
    ) : ActivityLauncher<I>  {
        return androidx.activity.compose.rememberLauncherForActivityResult(
            contract = contract,
            onResult = onResult
        ).run {
            val managedActivityResultLauncher = this
            object : ActivityLauncher<I> {
                override fun launch(input: I) {
                    managedActivityResultLauncher.launch(input)
                }
            }
        }
    }
}