package com.example.projectfluid

import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import com.example.projectfluid.databinding.ActivityMainBinding
import io.github.mahmoud_dev01.fluid.ClippingGradientDecoration
import io.github.mahmoud_dev01.fluid.DecorationStyle
import io.github.mahmoud_dev01.fluid.FlavorDrawable
import io.github.mahmoud_dev01.fluid.dpToPx
import io.github.mahmoud_dev01.fluid.setupKeyboardInsetsListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        //enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        setupRecyclerViewAndDecoration()

        // Use the powerful and reusable utility from your library to manage all keyboard interactions.
        setupKeyboardInsetsListener(
            rootView = binding.main,
            scrollView = binding.recyclerView,
            inputView = binding.inputLayout
        )

    }

    private fun setupRecyclerViewAndDecoration() {
        chatAdapter = ChatAdapter(this, sampleTexts())
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = chatAdapter

        // --- Showcase the power of your Fluid library ---

        // 1. Create the background for outgoing messages using FlavorDrawable.
        //    You can change the theme to BERRY, CITRUS, etc., with one line!
        val outgoingBackground = FlavorDrawable().apply {
            theme = FlavorDrawable.Theme.CANDY
        }

        // 2. Create the background for incoming messages.
        val incomingBackground = FlavorDrawable().apply {
            theme = FlavorDrawable.Theme.TEAL_BLUE
        }

        // 3. Create the configuration object for the decoration.
        val decorationStyle = DecorationStyle(
            groupedMargin = dpToPx(18), // 4
            regularMargin = dpToPx(18), // 12
            groupedCornerRadius = dpToPx(22), // 8
            regularCornerRadius = dpToPx(28), // 20
            primaryBackground = outgoingBackground,
            secondaryBackground = incomingBackground
        )

        // 4. Add the decoration from your library to the RecyclerView.
        binding.recyclerView.addItemDecoration(
            ClippingGradientDecoration(
                style = decorationStyle,
                primaryViewType = ChatAdapter.OUTGOING_MESSAGE_TYPE,
                secondaryViewType = ChatAdapter.INCOMING_MESSAGE_TYPE
            )
        )
    }

}