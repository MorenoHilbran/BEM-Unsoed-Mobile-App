package com.example.bemunsoed.ui.kuliner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.adapter.KulinerAdapter
import com.google.android.material.appbar.MaterialToolbar

class KulinerFragment : Fragment() {

    private lateinit var viewModel: KulinerViewModel
    private lateinit var kulinerAdapter: KulinerAdapter
    private lateinit var rvKuliner: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kuliner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("KulinerFragment", "Fragment created")

        viewModel = ViewModelProvider(this)[KulinerViewModel::class.java]
        
        initViews(view)
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        rvKuliner = view.findViewById(R.id.rv_kuliner)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        kulinerAdapter = KulinerAdapter { kuliner ->
            Log.d("KulinerFragment", "Kuliner clicked: ${kuliner.nama}")
        }

        rvKuliner.apply {
            // Vertikal scroll untuk menampilkan semua item
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context, 
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, 
                false
            )
            adapter = kulinerAdapter
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            Log.d("KulinerFragment", "Refreshing kuliner data")
            viewModel.refreshData()
        }
    }

    private fun observeViewModel() {
        viewModel.kulinerList.observe(viewLifecycleOwner) { kulinerList ->
            Log.d("KulinerFragment", "Kuliner list updated: ${kulinerList.size} items")
            kulinerAdapter.submitList(kulinerList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
}
