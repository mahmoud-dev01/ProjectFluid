package com.example.projectfluid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfluid.databinding.ActivityMaskingDemoBinding
import io.github.mahmoud_dev01.fluid.*

class MaskingLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaskingDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaskingDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup the background using FlavorDrawable
        val gradient = FlavorDrawable().apply { theme = FlavorDrawable.Theme.CANDY }
        binding.revealingLayout.setGradient(gradient)

        // Setup Adapter
        val chatAdapter = MaskingDemoAdapter(this, sampleTexts())
        binding.revealingLayout.recyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.revealingLayout.recyclerView.adapter = chatAdapter

        // Setup keyboard handling from your library
        setupKeyboardInsetsListener(binding.main, binding.revealingLayout, binding.inputLayout)
    }
}