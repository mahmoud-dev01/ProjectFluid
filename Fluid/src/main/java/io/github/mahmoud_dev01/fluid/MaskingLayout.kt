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
 * This layout is designed to have a solid background (e.g., white). It then uses
 * a special [Paint] with [PorterDuff.Mode.CLEAR] to "erase" a portion of its own
 * background, revealing whatever is drawn behind it in the parent layout.
 *
 * The child view to be used as a template for the mask is identified via the
 * `app:child_id` custom XML attribute. The corner radius of the mask can also
 * be customized using the `app:maskCornerRadius` attribute.
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
        // Read the custom attributes from the XML layout.
        context.withStyledAttributes(attrs, R.styleable.MaskingLayout) {
            childId = getResourceId(R.styleable.MaskingLayout_child_id, 0)
            maskCornerRadius = getDimension(R.styleable.MaskingLayout_maskCornerRadius, 0f)
        }

        if (childId == 0) {
            throw IllegalArgumentException("The 'app:child_id' attribute is required for MaskingLayout.")
        }

        // Initialize the Paint object that will act as the "eraser".
        eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, android.R.color.transparent)
            // The magic happens here: CLEAR mode erases pixels.
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        // Hardware acceleration is required for PorterDuff modes to work correctly.
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    /**
     * Called when a new child view is added. We use this to find and store a
     * reference to the view specified by 'child_id'.
     */
    override fun onViewAdded(view: View) {
        super.onViewAdded(view)
        if (view.id == childId) {
            this.targetView = view
        }
    }

    /**
     * The core drawing method. It first draws its own background and children,
     * then draws a transparent shape on top to create the mask.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Ensure the target view has been found before trying to draw.
        targetView?.let {
            // Get the bounds of the target view.
            maskRect.set(
                it.left.toFloat(), it.top.toFloat(),
                it.right.toFloat(), it.bottom.toFloat()
            )
            // Draw a rounded rectangle using the eraser paint, which punches the hole.
            canvas.drawRoundRect(maskRect, maskCornerRadius, maskCornerRadius, eraserPaint)
        }
    }

}