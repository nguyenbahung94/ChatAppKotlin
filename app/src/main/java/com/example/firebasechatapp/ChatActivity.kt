package com.example.firebasechatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.model.MessageType
import com.example.firebasechatapp.model.TextMessage
import com.example.firebasechatapp.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messageListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            messageListenerRegistration = FirestoreUtil.addChatMessagesListener(
                channelId, this, this::updateRyceclerView
            )
            imageView_send.setOnClickListener {
                val messageToSend = TextMessage(
                    editText_message.text.toString(), Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid, MessageType.TEXT
                )
                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }
            fab_send_image.setOnClickListener {
                TODO("send image message")
            }
        }
    }

    private fun updateRyceclerView(message: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(message)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(message)
        if (shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
        recycler_view_messages.scrollToPosition((recycler_view_messages.adapter?.itemCount!! - 1))
    }
}
