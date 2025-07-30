# Fluid for Android

**Fluid** is a lightweight and powerful Android library for creating beautiful, shared gradient backgrounds in a `RecyclerView`, inspired by the effect seen in modern messaging apps.

It provides two distinct, high-performance methods to achieve a "fluid" background that appears continuous and static as the list scrolls, giving your UI a polished and dynamic feel.

-----

## Visual Showcase
[assets/Screen_recording_20250730_161651.gif](https://github.com/mahmoud-dev01/ProjectFluid/blob/master/assets/Screen_recording_20250730_161651.gif?raw=true)


-----

## Features

  * **Two High-Performance Techniques**: Choose between a modern `ItemDecoration` or a component-based approach.
  * **Highly Customizable**: Full control over colors, gradient types, and directions using the powerful `FlavorDrawable`.
  * **Pre-built Themes**: Get started quickly with a set of beautiful, pre-configured color themes.
  * **Easy Integration**: Add stunning effects to your `RecyclerView` with minimal setup.
  * **Clean Architecture**: Designed to be modular and easy to maintain.

-----

## Setup

### Step 1. Add the JitPack repository

Add the JitPack repository to your root `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ... other repositories
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2. Add the dependency

Add the Fluid library dependency to your app's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.mahmoud-dev01:ProjectFluid:1.0.0")
}
```

*(Replace `1.0.0` with the latest release version from JitPack).*

-----

## Usage Guide

Fluid offers two primary methods to achieve the shared background effect.

### Method 1: `ClippingGradientDecoration` (Recommended)

This is the modern, flexible, and most performant approach. It uses a `RecyclerView.ItemDecoration` to draw the background and clips the canvas to the shape of your item's content.

#### Step 1: Implement the ViewHolder Interface

Your `RecyclerView.ViewHolder` must implement the `ClippingTargetViewHolder` interface to tell the decoration which child view to use as the clipping mask.

```kotlin
import io.github.mahmoud_dev01.fluid.ClippingTargetViewHolder

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ClippingTargetViewHolder {
    // Expose the TextView as the clipping target for the decoration.
    override val clippingTarget: View = itemView.findViewById(R.id.text_content)
    // ...
}
```

#### Step 2: Configure and Add the Decoration

In your `Activity` or `Fragment`, create your backgrounds using `FlavorDrawable` and add the `ClippingGradientDecoration` to your `RecyclerView`.

```kotlin
import io.github.mahmoud_dev01.fluid.*

// --- Inside your Activity's onCreate or Fragment's onViewCreated ---

// 1. Create your backgrounds using the powerful FlavorDrawable.
val primaryBackground = FlavorDrawable().apply {
    theme = FlavorDrawable.Theme.CANDY
}
val secondaryBackground = FlavorDrawable().apply {
    theme = FlavorDrawable.Theme.TEAL_BLUE
}

// 2. Create a style configuration object.
val style = DecorationStyle(
    groupedMargin = dpToPx(4),
    regularMargin = dpToPx(12),
    groupedCornerRadius = dpToPx(8),
    regularCornerRadius = dpToPx(20),
    primaryBackground = primaryBackground,
    secondaryBackground = secondaryBackground
)

// 3. Add the decoration to your RecyclerView.
recyclerView.addItemDecoration(
    ClippingGradientDecoration(
        style = style,
        primaryViewType = MY_VIEW_TYPE,
        secondaryViewType = OTHER_VIEW_TYPE
    )
)
```

### Method 2: `RevealingBackgroundLayout` (Component-Based)

This method uses a custom layout component that encapsulates the "hole-punching" technique. It's a good alternative if you prefer a component-based approach.

#### Step 1: Update Your Main Layout

Wrap your `ImageView` (for the background) and `RecyclerView` inside the `RevealingBackgroundLayout` and link them using the custom attributes.

```xml
<io.github.mahmoud_dev01.fluid.RevealingBackgroundLayout
    android:id="@+id/chatLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:backgroundImageId="@+id/my_background_image"
    app:recyclerViewId="@+id/my_recycler_view">

    <androidx.appcompat.widget.AppCompatImageView
        android:id="@+id/my_background_image"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/my_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</io.github.mahmoud_dev01.fluid.RevealingBackgroundLayout>
```

#### Step 2: Use `MaskingLayout` in Your Item Layout

Your item layout should use the `MaskingLayout` as its root. This layout will have a solid background (e.g., white) and will create a transparent "hole" over the child view you specify.

```xml
<io.github.mahmoud_dev01.fluid.MaskingLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#FFFFFF"
    app:child_id="@id/text_content"
    app:maskCornerRadius="20dp">

    <TextView
        android:id="@+id/text_content"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@android:color/white"
        ... />

</io.github.mahmoud_dev01.fluid.MaskingLayout>
```

#### Step 3: Set the Gradient in Your Activity

In your `Activity`, get a reference to the `RevealingBackgroundLayout` and set its gradient.

```kotlin
// In your Activity's onCreate
val gradient = FlavorDrawable().apply {
    theme = FlavorDrawable.Theme.MANGO
}

val chatLayout = findViewById<RevealingBackgroundLayout>(R.id.chatLayout)
chatLayout.setGradient(gradient)
```

-----

## Comparison of Methods

| Feature | `ClippingGradientDecoration` | `RevealingBackgroundLayout` |
| :--- | :--- | :--- |
| **Architecture** | **Excellent**. Decouples drawing logic from ViewHolders. | **Good**. Encapsulates logic but requires specific XML structure. |
| **Performance** | **Higher**. Canvas clipping is extremely fast. | **Good**. Relies on bitmap creation and PorterDuff modes. |
| **Flexibility** | **Higher**. Easier to apply to existing layouts. | Less flexible, requires wrapping views in the custom layout. |
| **Recommendation** | **Recommended for most use cases.** | A solid alternative for component-based architectures. |

-----

## License

```
MIT License

Copyright (c) 2025 Mahmoud-dev01

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
