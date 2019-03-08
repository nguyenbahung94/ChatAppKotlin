package com.example.firebasechatapp.model

import java.util.*

object MessageType {
    const val TEXT = "TEXT"
    const val Message = "MESSAGE"
}

interface Message {
    val time: Date
    val senderId: String
    val type: String
}