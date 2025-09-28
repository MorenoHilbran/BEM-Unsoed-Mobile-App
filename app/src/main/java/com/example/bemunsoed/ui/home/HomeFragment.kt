package com.example.bemunsoed.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.adapter.MerchAdapter
import com.example.bemunsoed.data.model.Merch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    // Views for new layout
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var bannerSlider: ViewPager2
    private lateinit var dotsIndicator: LinearLayout
    private lateinit var rvMerch: RecyclerView

    // Menu items
    private lateinit var menuInfoKos: LinearLayout
    private lateinit var menuKuliner: LinearLayout
    private lateinit var menuPodcast: LinearLayout
    private lateinit var menuEmagz: LinearLayout
    private lateinit var menuKomik: LinearLayout

    // Adapters
    private lateinit var merchAdapter: MerchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupBannerSlider()
        setupMerchandiseRecyclerView()
        setupSwipeRefresh()
        setupMenuClickListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        bannerSlider = view.findViewById(R.id.banner_slider)
        dotsIndicator = view.findViewById(R.id.dots_indicator)
        rvMerch = view.findViewById(R.id.rv_merch)

        // Menu items
        menuInfoKos = view.findViewById(R.id.menu_info_kos)
        menuKuliner = view.findViewById(R.id.menu_kuliner)
        menuPodcast = view.findViewById(R.id.menu_podcast)
        menuEmagz = view.findViewById(R.id.menu_emagz)
        menuKomik = view.findViewById(R.id.menu_komik)
    }

    private fun setupBannerSlider() {
        // Create test banner data using test1.png multiple times
        val bannerImages = listOf(
            R.drawable.test1,
            R.drawable.test1,
            R.drawable.test1
        )

        // Simple banner setup for now - will implement ViewPager2 adapter later
        setupDotsIndicator(bannerImages.size)
    }

    private fun setupDotsIndicator(count: Int) {
        dotsIndicator.removeAllViews()

        for (i in 0 until count) {
            val dot = ImageView(context)
            dot.setImageResource(if (i == 0) R.drawable.ic_circle_filled else R.drawable.ic_circle_outline)

            val params = LinearLayout.LayoutParams(24, 24)
            params.setMargins(8, 0, 8, 0)
            dotsIndicator.addView(dot, params)
        }
    }

    private fun setupMerchandiseRecyclerView() {
        merchAdapter = MerchAdapter { merch ->
            openUrl(merch.linkUrl)
        }

        rvMerch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = merchAdapter
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            homeViewModel.refreshData()
        }
    }

    private fun setupMenuClickListeners() {
        menuInfoKos.setOnClickListener {
            openExternalLink("https://bem-unsoed.com/info-kos") // Ganti dengan link yang sesuai
        }

        menuKuliner.setOnClickListener {
            openExternalLink("https://bem-unsoed.com/kuliner") // Ganti dengan link yang sesuai
        }

        menuPodcast.setOnClickListener {
            openExternalLink("https://bem-unsoed.com/podcast") // Ganti dengan link yang sesuai
        }

        menuEmagz.setOnClickListener {
            openExternalLink("https://bem-unsoed.com/e-magazine") // Ganti dengan link yang sesuai
        }

        menuKomik.setOnClickListener {
            openExternalLink("https://bem-unsoed.com/komik") // Ganti dengan link yang sesuai
        }
    }

    private fun observeViewModel() {
        homeViewModel.merch.observe(viewLifecycleOwner) { merchList ->
            merchAdapter.submitList(merchList)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                homeViewModel.clearError()
            }
        }
    }

    private fun openExternalLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl(url: String) {
        if (url.isNotEmpty()) {
            openExternalLink(url)
        } else {
            Toast.makeText(context, "Link tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }
}