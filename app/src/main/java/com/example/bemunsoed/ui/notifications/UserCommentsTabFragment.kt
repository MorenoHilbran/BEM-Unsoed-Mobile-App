package com.example.bemunsoed.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.notifications.adapter.UserCommentAdapter

class UserCommentsTabFragment : Fragment() {

    private lateinit var rvComments: RecyclerView
    private lateinit var tvEmpty: TextView
    private var adapter: UserCommentAdapter? = null

    companion object {
        fun newInstance(adapter: UserCommentAdapter): UserCommentsTabFragment {
            val fragment = UserCommentsTabFragment()
            fragment.adapter = adapter
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_user_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvComments = view.findViewById(R.id.rv_comments)
        tvEmpty = view.findViewById(R.id.tv_empty_comments)

        rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@UserCommentsTabFragment.adapter
            isNestedScrollingEnabled = false
        }
    }
}

