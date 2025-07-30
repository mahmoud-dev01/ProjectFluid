package com.example.projectfluid

data class Message(val content: String, val isMine: Boolean)

fun sampleTexts(): ArrayList<Message> =
    ArrayList(
        (1..20).map {
            val isMine = it % 3 != 0
            val messageContent = if (isMine) {
                "Hello, this is my message number $it. This can be a very long message to test how the bubble wraps."
            } else {
                "This is a reply."
            }
            Message(messageContent, isMine)
        }
    )