package io.github.mahmoud_dev01.fluid

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.withStyledAttributes

/**
 * A highly flexible component that provides a RecyclerView with a fixed, static background.
 *
 * This component works by requiring the developer to place an ImageView and a RecyclerView
 * inside it in their XML layout. The component then links to these views using the
 * `app:backgroundImageId` and `app:recyclerViewId` custom attributes.
 *
 * This approach provides maximum flexibility, allowing the developer to use any IDs and
 * configure the child views as needed, while this component handles the logic of setting
 * the static background.
 *
 * @property recyclerView The public [RecyclerView] instance that this layout manages.
 */
class RevealingBackgroundLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // These will hold references to the views provided by the developer in XML.
    private lateinit var imageView: AppCompatImageView
    lateinit var recyclerView: RecyclerView
        private set // Allow reading from outside, but only this class can set it.

    private var backgroundBitmap: Bitmap? = null

    // Store the IDs from the custom attributes.
    private var imageId: Int? = null
    private var recyclerId: Int? = null

    init {
        // Read the custom attributes provided in the XML layout.
        context.withStyledAttributes(attrs, R.styleable.RevealingBackgroundLayout) {
            imageId = getResourceId(R.styleable.RevealingBackgroundLayout_backgroundImageId, 0)
            recyclerId = getResourceId(R.styleable.RevealingBackgroundLayout_recyclerViewId, 0)
        }

        if (imageId == 0 || recyclerId == 0) {
            throw IllegalArgumentException("You must provide both app:backgroundImageId and app:recyclerViewId attributes.")
        }
    }

    /**
     * This lifecycle method is called after all child views have been inflated from XML.
     * This is the perfect place to find our target views using the IDs we retrieved earlier.
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        // Find the views that the developer has placed inside this layout.
        imageView = findViewById(imageId!!)
            ?: throw IllegalStateException("ImageView with the ID specified in app:backgroundImageId was not found.")
        recyclerView = findViewById(recyclerId!!)
            ?: throw IllegalStateException("RecyclerView with the ID specified in app:recyclerViewId was not found.")
    }

    /**
     * Sets the drawable to be used for the static background.
     *
     * The provided drawable will be converted into a bitmap that fills the component's bounds.
     * This operation is performed only once when the layout is first measured to ensure efficiency.
     *
     * @param drawable A drawable (e.g., a [FlavorDrawable] instance) to be used for the background.
     */
    fun setGradient(drawable: Drawable) {
        // Defer bitmap creation until the view has been laid out and has valid dimensions.
        this.doOnLayout { view ->
            if (backgroundBitmap == null && view.width > 0 && view.height > 0) {
                backgroundBitmap = drawable.toBitmap(view.width, view.height)
                imageView.setImageBitmap(backgroundBitmap)
            }
        }
    }

}