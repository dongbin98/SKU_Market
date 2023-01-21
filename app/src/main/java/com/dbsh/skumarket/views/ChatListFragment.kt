package com.dbsh.skumarket.views

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.dbsh.skumarket.R
import com.dbsh.skumarket.adapters.ChatListAdapter
import com.dbsh.skumarket.base.BaseFragment
import com.dbsh.skumarket.base.LinearLayoutManagerWrapper
import com.dbsh.skumarket.databinding.FragmentChatListBinding
import com.dbsh.skumarket.model.ChatListDto
import com.dbsh.skumarket.viewmodels.ChatListViewModel

class ChatListFragment : BaseFragment<FragmentChatListBinding>(R.layout.fragment_chat_list) {
    companion object{
        const val TAG = "ChatList Fragment"
    }

    private lateinit var viewModel: ChatListViewModel
    private lateinit var adapter: ChatListAdapter
    private var chatRoomList = mutableListOf<ChatListDto>()

    @SuppressLint("NotifyDataSetChanged")
    override fun init() {
        viewModel = ChatListViewModel()
        binding.apply {
            viewModel = viewModel
        }
        // RecyclerView Setting
        adapter = ChatListAdapter(chatRoomList as ArrayList<ChatListDto>)
        adapter.apply {
            setOnItemClickListener(object: ChatListAdapter.OnItemClickListener{
                override fun onItemClick(v: View, data: ChatListDto, position: Int) {
                    println("채팅방 : ${data.roomId} 입장")
                    Intent(context, ChatActivity::class.java).apply {
                        putExtra("roomId", data.roomId)
                        putExtra("opponent", data.otherOne) }.run { startActivity(this) }
                }
            })
            setOnItemLongClickListener(object: ChatListAdapter.OnItemLongClickListener{
                override fun onItemLongClick(v: View, data: ChatListDto, position: Int) {
                    Toast.makeText(context, "LongClick Event", Toast.LENGTH_SHORT).show()
                    showDialog(data.roomId, data.otherOne)
//                    viewModel.deleteChatRoom(data.roomId)
                }
            })
        }
        binding.chatListRecyclerview.apply {
            itemAnimator = null
            adapter = this@ChatListFragment.adapter
            layoutManager = LinearLayoutManagerWrapper(context)
        }

        viewModel.loadChatRoom()

        viewModel.chatList.observe(this) { it ->
            if(it != null) {
                chatRoomList.clear()
                adapter.dataClear()
//                adapter.notifyDataSetChanged()
                for(chatRoom in it) {
                    chatRoomList.add(chatRoom)
                    chatRoomList.sortByDescending { it.lastDate }
//                    adapter.notifyItemInserted(adapter.itemCount)
                }
                println("chatRoomList.size = ${chatRoomList.size}")
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun showDialog(roomId: String, opponent: String) {
        ChatRoomOutDialog(requireContext(), opponent) {
            viewModel.deleteChatRoom(roomId)
        }.show()
    }
}