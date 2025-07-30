package io.github.mahmoud_dev01.fluid

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

/**
 * An interface that a [RecyclerView.ViewHolder] must implement to be targeted by the decoration.
 * It exposes the specific child view that the decoration will use as a clipping mask.
 */
interface ClippingTargetViewHolder {
    /** The target view within the ViewHolder's layout that defines the clipping area. */
    val clippingTarget: View
}

/**
 * A configuration data class for passing styling parameters to the [ClippingGradientDecoration].
 * This centralizes all styling options, making the decoration easy to configure.
 *
 * @property groupedMargin The vertical margin in pixels between adjacent items of the same type.
 * @property regularMargin The vertical margin in pixels between items of different types or single items.
 * @property groupedCornerRadius The corner radius in pixels for corners that are adjacent to an item of the same type.
 * @property regularCornerRadius The standard corner radius for corners that are not adjacent to a similar item.
 * @property primaryBackground The drawable used as the background for items of the primary view type.
 * @property secondaryBackground The drawable used as the background for items of the secondary view type.
 */
class DecorationStyle(
    @Px val groupedMargin: Int,
    @Px val regularMargin: Int,
    @Px val groupedCornerRadius: Int,
    @Px val regularCornerRadius: Int,
    val primaryBackground: Drawable,
    val secondaryBackground: Drawable
)

/**
 * A [RecyclerView.ItemDecoration] that draws a shared background behind specific item views.
 *
 * This decoration achieves a "fluid background" effect by using a single, full-sized background
 * drawable (like a gradient) that spans the entire RecyclerView. For each target item, it
 * calculates a precise shape (a path) around a specific child view. It then clips the canvas
 * to this path and draws the full-sized background, which only becomes visible within the clipped area.
 *
 * This method is highly performant as it uses a single drawable and leverages hardware-accelerated
 * canvas clipping. It is ideal for creating complex visual effects where items in a list appear
 * to share a continuous background.
 *
 * @param style The [DecorationStyle] containing all styling parameters.
 * @param primaryViewType The view type identifier for items that should use the primary background.
 * @param secondaryViewType The view type identifier for items that should use the secondary background.
 */
class ClippingGradientDecoration(
    private val style: DecorationStyle,
    private val primaryViewTypes: Set<Int>,
    private val secondaryViewTypes: Set<Int>
) : RecyclerView.ItemDecoration() {

    // Reusable objects to avoid allocations during drawing operations.
    private val path = Path()
    private val rectF = RectF()
    private val groupedCornerRadiusF = style.groupedCornerRadius.toFloat()
    private val regularCornerRadiusF = style.regularCornerRadius.toFloat()
    private val targetViewTypes = primaryViewTypes + secondaryViewTypes

    /**
     * Draws the backgrounds under the items in the RecyclerView.
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter ?: return

        // Iterate over each visible child in the RecyclerView.
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val holder = parent.findContainingViewHolder(view) ?: continue
            val itemViewType = holder.itemViewType

            // Skip any view types that are not targeted by this decoration.
            if (itemViewType !in targetViewTypes) continue

            val targetView = (holder as ClippingTargetViewHolder).clippingTarget
            val position = holder.adapterPosition
            if (position == RecyclerView.NO_POSITION) continue

            // Calculate the target view's absolute position relative to the RecyclerView.
            val (x, y) = targetView.relativeTo(parent).let {
                Pair(it.x.toFloat(), it.y.toFloat() + view.translationY)
            }

            // Check for adjacent items of the same type to determine the shape.
            val prevItemIsSameType = position > 0 &&
                    adapter.getItemViewType(position - 1) == itemViewType
            val nextItemIsSameType = position < adapter.itemCount - 1 &&
                    adapter.getItemViewType(position + 1) == itemViewType

            path.rewind()
            rectF.set(x, y, x + targetView.width, y + targetView.height)

            // Determine the corner radii based on adjacent items to create a "grouped" effect.
            if (itemViewType in primaryViewTypes) {
                path.addRoundRect(
                    rectF,
                    regularCornerRadiusF, // Top-Left
                    if (prevItemIsSameType) groupedCornerRadiusF else regularCornerRadiusF, // Top-Right
                    if (nextItemIsSameType) groupedCornerRadiusF else regularCornerRadiusF,  // Bottom-Right
                    regularCornerRadiusF // Bottom-Left
                )
            } else {
                path.addRoundRect(
                    rectF,
                    if (prevItemIsSameType) groupedCornerRadiusF else regularCornerRadiusF, // Top-Left
                    regularCornerRadiusF, // Top-Right
                    regularCornerRadiusF, // Bottom-Right
                    if (nextItemIsSameType) groupedCornerRadiusF else regularCornerRadiusF  // Bottom-Left
                )
            }
            path.close()

            // Save the canvas, clip it to the path, draw the background, and then restore the canvas.
            c.withSave {
                clipPath(path)

                val drawable = if (itemViewType in primaryViewTypes) {
                    style.primaryBackground
                } else {
                    style.secondaryBackground
                }

                // The drawable's bounds must cover the entire RecyclerView to create the shared background illusion.
                drawable.setBounds(0, 0, parent.width, parent.height)
                drawable.alpha = (view.alpha * 255.0F + 0.5F).toInt()
                drawable.draw(c)
            }
        }
    }

    /**
     * Sets the vertical spacing (offsets) between items.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.setEmpty()
        val adapter = parent.adapter ?: return
        val holder = parent.findContainingViewHolder(view) ?: return
        val itemViewType = holder.itemViewType

        if (itemViewType !in targetViewTypes) return

        val position = holder.adapterPosition
        if (position == RecyclerView.NO_POSITION) return

        // Apply a smaller top margin if the previous item is of the same type.
        outRect.top = if (position > 0 &&
            itemViewType == adapter.getItemViewType(position - 1)
        ) style.groupedMargin
        else style.regularMargin
    }

    private companion object {
        // A reusable Point object to avoid memory allocation in loops.
        private val POINT = Point()

        /**
         * Calculates the position of a View relative to a given parent ViewGroup.
         */
        private fun View.relativeTo(group: ViewGroup): Point {
            POINT.set(this.left, this.top)
            var p: ViewParent? = this.parent
            while (p is View && p != group) {
                POINT.x += p.left
                POINT.y += p.top
                // Explicitly cast p to View to resolve the overload ambiguity.
                p = (p as View).parent
            }
            return POINT
        }

        /**
         * An extension function for [Canvas] to save its state, perform an action, and then safely restore it.
         */
        private inline fun Canvas.withSave(action: Canvas.() -> Unit) {
            val saveCount = this.save()
            try {
                action()
            } finally {
                this.restoreToCount(saveCount)
            }
        }

        /**
         * An extension function for [Path] to add a rounded rectangle with different radii for each corner.
         */
        private fun Path.addRoundRect(
            rect: RectF,
            topLeft: Float, topRight: Float,
            bottomRight: Float, bottomLeft: Float
        ) {
            addRoundRect(
                rect,
                floatArrayOf(
                    topLeft, topLeft,
                    topRight, topRight,
                    bottomRight, bottomRight,
                    bottomLeft, bottomLeft
                ),
                Path.Direction.CW
            )
        }
    }

}