package com.example.bemunsoed.ui.infokos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.adapter.InfoKosAdapter
import com.google.android.material.appbar.MaterialToolbar

class InfoKosFragment : Fragment() {

    private lateinit var viewModel: InfoKosViewModel
    private lateinit var infoKosAdapter: InfoKosAdapter
    private lateinit var rvInfoKos: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_infokos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("InfoKosFragment", "Fragment created")

        viewModel = ViewModelProvider(this)[InfoKosViewModel::class.java]
        
        initViews(view)
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        rvInfoKos = view.findViewById(R.id.rv_infokos)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        infoKosAdapter = InfoKosAdapter { infoKos ->
            Log.d("InfoKosFragment", "Info kos clicked: ${infoKos.nama}")
        }

        rvInfoKos.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context, 
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, 
                false
            )
            adapter = infoKosAdapter
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            Log.d("InfoKosFragment", "Refreshing info kos data")
            viewModel.refreshData()
        }
    }

    private fun observeViewModel() {
        viewModel.infoKosList.observe(viewLifecycleOwner) { infoKosList ->
            Log.d("InfoKosFragment", "Info kos list updated: ${infoKosList.size} items")
            infoKosAdapter.submitList(infoKosList)
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
