package com.example.bemunsoed.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bemunsoed.R

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var tvSearchResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupSearchView()
    }

    private fun initViews(view: View) {
        searchView = view.findViewById(R.id.search_view)
        tvSearchResult = view.findViewById(R.id.tv_search_result)
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    tvSearchResult.text = "Masukkan kata kunci untuk mencari"
                }
                return true
            }
        })
    }

    private fun performSearch(query: String?) {
        if (query.isNullOrBlank()) {
            tvSearchResult.text = "Masukkan kata kunci untuk mencari"
            return
        }

        // For now, just show a simple message
        // Later this can be connected to actual search functionality
        tvSearchResult.text = "Mencari: $query\n\nFitur pencarian akan segera tersedia"
    }
}
