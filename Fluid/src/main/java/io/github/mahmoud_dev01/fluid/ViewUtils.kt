package io.github.mahmoud_dev01.fluid

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * @file Contains utility extension functions for Android's [Context], [Activity], and [View] classes.
 */

/**
 * Converts a value from density-independent pixels (dp) to its equivalent in pixels (px).
 *
 * This is a convenient way to specify dimensions in code that will scale correctly
 * across different screen densities.
 *
 * @receiver The [Context] used to access display metrics.
 * @param dp The value in dp to convert.
 * @return The equivalent value in pixels as an [Int].
 */
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

/**
 * Attaches a listener to the root view to gracefully handle keyboard (IME) visibility changes.
 *
 * This function implements the modern approach using [WindowInsetsCompat] to manage UI
 * adjustments when the software keyboard appears or disappears. It is particularly
 * useful for implementing edge-to-edge user interfaces.
 *
 * How it works:
 * 1. It applies bottom padding to a designated scrollable view, ensuring its content can be
 * scrolled up and is not hidden behind the keyboard.
 * 2. It simultaneously translates a designated input view vertically, keeping it visible
 * above the keyboard.
 *
 * @receiver The [Activity] hosting the views.
 * @param rootView The root view of the layout (e.g., `binding.root`). The listener is attached
 * here to receive window inset changes for the entire window.
 * @param scrollView The scrollable content view (e.g., a [RecyclerView]) that needs bottom
 * padding to be applied when the keyboard is visible.
 * @param inputView The view containing the input field (e.g., a `LinearLayout` wrapping an
 * `EditText`) that should be translated vertically to stay above the keyboard.
 * @see WindowInsetsCompat
 */
fun Activity.setupKeyboardInsetsListener(rootView: View, scrollView: View, inputView: View) {
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
        val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

        // Apply padding to the scrollable view to ensure its content is not obscured.
        scrollView.setPadding(0, 0, 0, imeHeight)

        // Translate the input view upwards by the height of the keyboard.
        inputView.translationY = -imeHeight.toFloat()

        // Return the insets so the system can continue to dispatch them to other views.
        insets
    }
}