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
import com.example.bemunsoed.ui.notifications.adapter.UserPostAdapter

class UserPostsTabFragment : Fragment() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var tvEmpty: TextView
    private var adapter: UserPostAdapter? = null

    companion object {
        fun newInstance(adapter: UserPostAdapter): UserPostsTabFragment {
            val fragment = UserPostsTabFragment()
            fragment.adapter = adapter
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_user_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPosts = view.findViewById(R.id.rv_posts)
        tvEmpty = view.findViewById(R.id.tv_empty_posts)

        rvPosts.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@UserPostsTabFragment.adapter
            isNestedScrollingEnabled = false
        }
    }
}

