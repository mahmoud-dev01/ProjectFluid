package io.github.mahmoud_dev01.fluid

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import androidx.core.graphics.toColorInt

/**
 * A highly configurable, performance-optimized custom [Drawable] for creating and rendering
 * complex gradients or solid colors programmatically.
 *
 * This class supports linear, radial, and sweep gradients with multiple colors and directions.
 * It is optimized to avoid creating new objects during the `draw()` call by caching the
 * generated [Shader]. The shader is only recreated when necessary, such as when the drawable's
 * bounds or a gradient-related property changes.
 *
 * It also includes a [Theme] enum for applying predefined color schemes easily.
 *
 * @property theme The current preset [Theme] applied to the drawable.
 * @property color A solid color to draw. If not transparent, this overrides any gradient settings.
 * @property gradientType The type of gradient to draw (e.g., [GradientType.LINEAR]).
 * @property startColor The starting color of the gradient.
 * @property centerColor The optional middle color of the gradient.
 * @property endColor The ending color of the gradient.
 * @property direction The direction/angle for a linear gradient.
 * @property cornerRadius The corner radius for the drawn shape, in pixels.
 * @property centerX The relative horizontal position (0.0-1.0) for the center of a radial or sweep gradient.
 * @property centerY The relative vertical position (0.0-1.0) for the center of a radial or sweep gradient.
 */
class FlavorDrawable : Drawable() {

    /** Defines the available types of gradients. */
    @IntDef(GradientType.LINEAR, GradientType.RADIAL, GradientType.SWEEP)
    @Retention(AnnotationRetention.SOURCE)
    annotation class GradientType {
        companion object {
            const val LINEAR: Int = 0
            const val RADIAL: Int = 1
            const val SWEEP: Int = 2
        }
    }

    /** Defines the available directions for a linear gradient, represented as angles. */
    @IntDef(
        Direction.LEFT_RIGHT, Direction.BL_TR, Direction.BOTTOM_TOP, Direction.BR_TL,
        Direction.RIGHT_LEFT, Direction.TR_BL, Direction.TOP_BOTTOM, Direction.TL_BR
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Direction {
        companion object {
            const val LEFT_RIGHT: Int = 0      // Left to Right
            const val BL_TR: Int = 45          // Bottom-left to Top-right
            const val BOTTOM_TOP: Int = 90     // Bottom to Top
            const val BR_TL: Int = 135         // Bottom-right to Top-left
            const val RIGHT_LEFT: Int = 180    // Right to Left
            const val TR_BL: Int = 225         // Top-right to Bottom-left
            const val TOP_BOTTOM: Int = 270    // Top to Bottom
            const val TL_BR: Int = 315         // Top-left to Bottom-right
        }
    }

    /** Defines a set of preset color themes for quick configuration. */
    enum class Theme {
        BERRY, CANDY, CITRUS, MANGO, AQUA,
        DEFAULT_BLUE, YELLOW, RED, GREEN, ORANGE,
        TEAL_BLUE, LAVENDER_PURPLE, CORAL_PINK, BRIGHT_PURPLE, AQUA_BLUE, HOT_PINK
    }

    private val flavorPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val flavorRectF: RectF = RectF()

    // Holds the cached shader. This is key to the performance optimization.
    private var shader: Shader? = null

    /** Determines whether to draw a solid color or a gradient. */
    var useSolidColor: Boolean = false
        private set

    var theme: Theme = Theme.CANDY
        set(value) {
            field = value
            applyTheme(value)
        }

    @ColorInt
    var color: Int = Color.TRANSPARENT
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    @GradientType
    var gradientType: Int = GradientType.LINEAR
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    @ColorInt
    var startColor: Int = Color.WHITE
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    @ColorInt
    var centerColor: Int? = null
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    @ColorInt
    var endColor: Int = Color.BLACK
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    @Direction
    var direction: Int = Direction.TOP_BOTTOM
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    var cornerRadius: Float = 0.0F
        set(value) {
            field = value
            useSolidColor = false
            // Changing the corner radius doesn't require recreating the shader, just a redraw.
            invalidateSelf()
        }

    @FloatRange(from = 0.0, to = 1.0)
    var centerX: Float = 0.5F
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    @FloatRange(from = 0.0, to = 1.0)
    var centerY: Float = 0.5F
        set(value) {
            field = value
            useSolidColor = false
            resetShader()
        }

    init {
        // Apply the default theme upon initialization.
        applyTheme(theme)
    }

    /**
     * Applies the color properties of a given [Theme].
     * This will override any manually set colors or gradient types.
     *
     * @param theme The preset theme to apply.
     */
    private fun applyTheme(theme: Theme) {
        // Reset to default state before applying theme colors
        color = Color.TRANSPARENT
        gradientType = GradientType.LINEAR
        centerColor = null
        useSolidColor = false // Default to gradient

        when (theme) {
            Theme.BERRY -> {
                startColor = "#005fff".toColorInt()
                centerColor = "#9200ff".toColorInt()
                endColor = "#ff2e19".toColorInt()
            }

            Theme.CANDY -> {
                startColor = "#ff8fb2".toColorInt()
                centerColor = "#a797ff".toColorInt()
                endColor = "#00e5ff".toColorInt()
            }

            Theme.CITRUS -> {
                startColor = "#ffd200".toColorInt()
                centerColor = "#6edf00".toColorInt()
                endColor = "#00dfbb".toColorInt()
            }

            Theme.MANGO -> {
                startColor = "#ffdc2d".toColorInt()
                centerColor = "#ff9616".toColorInt()
                endColor = "#ff4f00".toColorInt()
            }

            Theme.AQUA -> {
                startColor = "#19c9ff".toColorInt()
                centerColor = "#00e6d2".toColorInt()
                endColor = "#0ee6b7".toColorInt()
            }
            // Single-color themes
            else -> {
                useSolidColor = true
                color = when (theme) {
                    Theme.DEFAULT_BLUE -> "#0084ff".toColorInt()
                    Theme.YELLOW -> "#ffc300".toColorInt()
                    Theme.RED -> "#fa3c4c".toColorInt()
                    Theme.GREEN -> "#13cf13".toColorInt()
                    Theme.ORANGE -> "#ff7e29".toColorInt()
                    Theme.TEAL_BLUE -> "#44bec7".toColorInt()
                    Theme.LAVENDER_PURPLE -> "#d696bb".toColorInt()
                    Theme.CORAL_PINK -> "#e68585".toColorInt()
                    Theme.BRIGHT_PURPLE -> "#7646ff".toColorInt()
                    Theme.AQUA_BLUE -> "#20cef5".toColorInt()
                    Theme.HOT_PINK -> "#ff5ca1".toColorInt()
                    else -> Color.TRANSPARENT // Should not happen
                }
            }

        }
        // After applying a new theme, the shader must be recreated.
        resetShader()
    }

    override fun draw(canvas: Canvas) {
        if (useSolidColor) {
            flavorPaint.shader = null
            flavorPaint.color = color
        } else {
            if (shader == null) {
                updateShader()
            }
            flavorPaint.shader = shader
        }
        canvas.drawRoundRect(flavorRectF, cornerRadius, cornerRadius, flavorPaint)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        // Update the rectangle's dimensions and reset the shader to match the new size.
        flavorRectF.set(bounds)
        resetShader()
    }

    /**
     * Creates the appropriate [Shader] object based on the current properties.
     * This is the core of the optimization, ensuring the shader is only built when necessary.
     */
    private fun updateShader() {
        if (flavorRectF.width() <= 0f || flavorRectF.height() <= 0f) {
            return
        }

        val currentColors = if (centerColor != null) {
            intArrayOf(startColor, centerColor!!, endColor)
        } else {
            intArrayOf(startColor, endColor)
        }

        val positions = if (centerColor != null) floatArrayOf(0.0f, 0.5f, 1.0f) else null

        shader = when (gradientType) {
            GradientType.LINEAR -> createLinearGradient(currentColors, positions)
            GradientType.RADIAL -> createRadialGradient(currentColors, positions)
            GradientType.SWEEP -> createSweepGradient(currentColors, positions)
            else -> null
        }
    }

    private fun createLinearGradient(colors: IntArray, positions: FloatArray?): Shader {
        val x0: Float
        val y0: Float
        val x1: Float
        val y1: Float

        when (direction) {
            Direction.LEFT_RIGHT -> {
                x0 = flavorRectF.left; y0 = flavorRectF.top
                x1 = flavorRectF.right; y1 = flavorRectF.top
            }

            Direction.BL_TR -> {
                x0 = flavorRectF.left; y0 = flavorRectF.bottom
                x1 = flavorRectF.right; y1 = flavorRectF.top
            }

            Direction.BOTTOM_TOP -> {
                x0 = flavorRectF.left; y0 = flavorRectF.bottom
                x1 = flavorRectF.left; y1 = flavorRectF.top
            }

            Direction.BR_TL -> {
                x0 = flavorRectF.right; y0 = flavorRectF.bottom
                x1 = flavorRectF.left; y1 = flavorRectF.top
            }

            Direction.RIGHT_LEFT -> {
                x0 = flavorRectF.right; y0 = flavorRectF.top
                x1 = flavorRectF.left; y1 = flavorRectF.top
            }

            Direction.TR_BL -> {
                x0 = flavorRectF.right; y0 = flavorRectF.top
                x1 = flavorRectF.left; y1 = flavorRectF.bottom
            }

            Direction.TOP_BOTTOM -> {
                x0 = flavorRectF.left; y0 = flavorRectF.top
                x1 = flavorRectF.left; y1 = flavorRectF.bottom
            }

            Direction.TL_BR -> {
                x0 = flavorRectF.left; y0 = flavorRectF.top
                x1 = flavorRectF.right; y1 = flavorRectF.bottom
            }

            else -> {
                x0 = flavorRectF.left; y0 = flavorRectF.top
                x1 = flavorRectF.right; y1 = flavorRectF.top
            }
        }

        return LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.CLAMP)
    }

    private fun createRadialGradient(colors: IntArray, positions: FloatArray?): Shader {
        val radius =
            if (flavorRectF.width() > flavorRectF.height()) flavorRectF.width() / 2f
            else flavorRectF.height() / 2f
        return RadialGradient(
            flavorRectF.centerX(),
            flavorRectF.centerY(),
            radius,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
    }

    private fun createSweepGradient(colors: IntArray, positions: FloatArray?): Shader {
        return SweepGradient(
            flavorRectF.centerX(),
            flavorRectF.centerY(),
            colors,
            positions
        )
    }

    /**
     * Resets the cached shader and triggers a redraw. This should be called
     * whenever a property affecting the gradient's appearance is changed.
     */
    private fun resetShader() {
        shader = null
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        flavorPaint.alpha = alpha
        invalidateSelf()
    }

    @Deprecated(
        "This method is no longer used in graphics rendering",
        ReplaceWith("PixelFormat.TRANSLUCENT")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        flavorPaint.colorFilter = colorFilter
        invalidateSelf()
    }

}