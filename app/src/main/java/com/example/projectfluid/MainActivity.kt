package com.example.projectfluid

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private val messagesList = sampleTexts() // Store the list here to modify it

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
        setupKeyboardInsetsListener(binding.main, binding.recyclerView, binding.inputLayout)
        setupSendButton()

        // Use the powerful and reusable utility from your library to manage all keyboard interactions.
        /*setupKeyboardInsetsListener(
            rootView = binding.main,
            scrollView = binding.recyclerView,
            inputView = binding.inputLayout
        )*/

    }

    private fun setupRecyclerViewAndDecoration() {
        chatAdapter = ChatAdapter(this, messagesList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerView.adapter = chatAdapter

        // --- Library Showcase ---
        val outgoingBackground = FlavorDrawable().apply {
            theme = FlavorDrawable.Theme.CANDY
        }
        val incomingBackground = FlavorDrawable().apply {
            theme = FlavorDrawable.Theme.DEFAULT_BLUE
        }
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
                primaryViewType = ChatAdapter.OUTGOING_MESSAGE_TYPE,
                secondaryViewType = ChatAdapter.INCOMING_MESSAGE_TYPE
            )
        )
    }

    /**
     * Sets up the click listener for the send button.
     */
    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val messageText = binding.editText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Add the user's message
                val newMessage = Message(messageText, isMine = true)
                chatAdapter.addMessage(newMessage)
                binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                binding.editText.text.clear()

                // Simulate a reply after a short delay
                simulateReply()
            }
        }
    }

    /**
     * Simulates a reply from the other user after 1 second.
     */
    private fun simulateReply() {
        Handler(Looper.getMainLooper()).postDelayed({
            val replyMessage = Message("Thanks for the message!", isMine = false)
            chatAdapter.addMessage(replyMessage)
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }, 11000)
    }

}