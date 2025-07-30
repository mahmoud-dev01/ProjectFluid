package com.example.projectfluid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfluid.databinding.ActivityClippingDemoBinding
import io.github.mahmoud_dev01.fluid.*

class ClippingDecorationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClippingDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClippingDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Adapter
        val chatAdapter = ClippingDemoAdapter(this, sampleTexts())
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.recyclerView.adapter = chatAdapter

        // Setup Decoration from your library
        val outgoingBackground = FlavorDrawable().apply { theme = FlavorDrawable.Theme.BERRY }
        val incomingBackground = FlavorDrawable().apply { theme = FlavorDrawable.Theme.DEFAULT_BLUE }

        val decorationStyle = DecorationStyle(
            groupedMargin = dpToPx(4),
            regularMargin = dpToPx(12),
            groupedCornerRadius = dpToPx(8),
            regularCornerRadius = dpToPx(20),
            primaryBackground = outgoingBackground,
            secondaryBackground = incomingBackground
        )

        binding.recyclerView.addItemDecoration(
            ClippingGradientDecoration(
                style = decorationStyle,
                primaryViewType = ClippingDemoAdapter.OUTGOING_TYPE,
                secondaryViewType = ClippingDemoAdapter.INCOMING_TYPE
            )
        )

        // Setup keyboard handling from your library
        setupKeyboardInsetsListener(binding.main, binding.recyclerView, binding.inputLayout)
    }
}