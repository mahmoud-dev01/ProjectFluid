package io.github.mahmoud_dev01.fluid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes

/**
 * A custom [ConstraintLayout] that creates a transparent "hole" or "mask"
 * over a specific child view.
 *
 * The child view to be used as a template for the mask is identified via the
 * `app:child_id` custom XML attribute.
 *
 * @property maskCornerRadius The corner radius of the transparent mask, in pixels.
 */
class MaskingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var childId: Int? = null
    private var targetView: View? = null
    private val eraserPaint: Paint
    private val maskRect = RectF()

    var maskCornerRadius: Float = 0f
        set(value) {
            field = value
            invalidate() // Redraw the view when the radius changes.
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.MaskingLayout) {
            childId = getResourceId(R.styleable.MaskingLayout_child_id, 0)
            maskCornerRadius = getDimension(R.styleable.MaskingLayout_maskCornerRadius, 0f)
        }

        if (childId == 0) {
            throw IllegalArgumentException("The 'app:child_id' attribute is required for MaskingLayout.")
        }

        eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, android.R.color.transparent)
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    /**
     * Finds the target child view after the layout has been inflated.
     * This is a more robust approach than using onViewAdded.
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        targetView = findViewById(childId!!)
            ?: throw IllegalStateException("The view with the ID specified in 'app:child_id' was not found.")
    }

    /**
     * The core drawing method. It draws a transparent shape on top to create the mask.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        targetView?.let {
            maskRect.set(
                it.left.toFloat(), it.top.toFloat(),
                it.right.toFloat(), it.bottom.toFloat()
            )
            canvas.drawRoundRect(maskRect, maskCornerRadius, maskCornerRadius, eraserPaint)
        }
    }

}