package com.example.firebasechatapp.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.AppConstants
import com.example.firebasechatapp.ChatActivity
import com.example.firebasechatapp.R
import com.example.firebasechatapp.recycleview.item.PersonItem
import com.example.firebasechatapp.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_people.*


class PeopleFragment : Fragment() {
    private lateinit var userListener: ListenerRegistration
    private var shouldIinitRecycleView = true
    private lateinit var peopleSection: Section
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        userListener = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecycleview)
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    private fun updateRecycleview(items: List<Item>) {
        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClickListener)
                }
            }
            shouldIinitRecycleView = false

        }

        fun updateItems() = peopleSection.update(items)
        if (shouldIinitRecycleView) {
            init()
        } else {
            updateItems()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener(userListener)
        shouldIinitRecycleView = true
    }

    private val onItemClickListener = OnItemClickListener { item, view ->
        if (item is PersonItem) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(AppConstants.USER_NAME, item.person.name)
            intent.putExtra(AppConstants.USER_ID, item.userId)
            startActivity(intent)
        }
    }

}
