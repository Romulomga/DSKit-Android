package io.github.romulomga.dskit

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Vibração dos componentes, como o `DSHaptics` do DSKit iOS. Respeita `LocalDSHapticsEnabled` (o "Vibração"
 * do app) e a configuração de toque do sistema, porque usa `performHapticFeedback` da view.
 */
@Stable
public class DSHaptics internal constructor(private val view: View, private val enabled: Boolean) {
    public fun light(): Unit = perform(HapticFeedbackConstants.CLOCK_TICK)

    public fun medium(): Unit = perform(HapticFeedbackConstants.VIRTUAL_KEY)

    public fun success(): Unit = perform(
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.R
        ) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.VIRTUAL_KEY
        }
    )

    public fun warning(): Unit = perform(HapticFeedbackConstants.LONG_PRESS)

    public fun error(): Unit =
        perform(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.REJECT else HapticFeedbackConstants.LONG_PRESS)

    private fun perform(constant: Int) {
        if (enabled) view.performHapticFeedback(constant)
    }
}

/** Vibração da view atual, já ligada ou desligada pelo tema. */
@Composable
public fun rememberDSHaptics(): DSHaptics {
    val view = LocalView.current
    val enabled = DSTheme.hapticsEnabled
    return remember(view, enabled) { DSHaptics(view, enabled) }
}
