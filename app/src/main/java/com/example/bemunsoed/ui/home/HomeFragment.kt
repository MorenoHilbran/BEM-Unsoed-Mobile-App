package com.example.bemunsoed.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.adapter.BannerAdapter
import com.example.bemunsoed.ui.adapter.MerchAdapter
import com.example.bemunsoed.ui.notifications.ProfileViewModel
import com.example.bemunsoed.ui.webview.WebViewFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var tvWelcome: TextView
    private lateinit var profileViewModel: ProfileViewModel


    // Views for layout
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
    private lateinit var bannerAdapter: BannerAdapter

    private var currentBannerPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "Creating HomeFragment view")
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "HomeFragment view created, initializing components")

        initViews(view)
        setupBannerSlider()
        setupMerchandiseRecyclerView()
        setupSwipeRefresh()
        setupMenuClickListeners()
        observeViewModel()
        tvWelcome = view.findViewById(R.id.tv_welcome)

// Ambil ViewModel profil
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

// Observasi data user untuk menampilkan nama
        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val name = user.name ?: "Pengguna"
            tvWelcome.text = "Selamat datang, $name!"
        }

    }

    private fun initViews(view: View) {
        Log.d("HomeFragment", "Initializing views")
        try {
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

            Log.d("HomeFragment", "All views initialized successfully")
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error initializing views", e)
        }
    }

    private fun setupBannerSlider() {
        Log.d("HomeFragment", "Setting up banner slider")
        bannerAdapter = BannerAdapter()
        bannerSlider.adapter = bannerAdapter

        // Update dots indicator saat slide
        bannerSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentBannerPosition = position
                updateDotsIndicatorActive(position)
            }
        })
    }

    private fun setupDotsIndicator(count: Int) {
        Log.d("HomeFragment", "Setting up dots indicator with count: $count")
        dotsIndicator.removeAllViews()

        if (count <= 0) {
            Log.w("HomeFragment", "No banners to show dots for")
            return
        }

        for (i in 0 until count) {
            val dot = ImageView(context)
            dot.setImageResource(if (i == 0) R.drawable.ic_circle_filled else R.drawable.ic_circle_outline)
            val params = LinearLayout.LayoutParams(24, 24)
            params.setMargins(8, 0, 8, 0)
            dotsIndicator.addView(dot, params)
        }
    }

    private fun updateDotsIndicatorActive(position: Int) {
        val count = dotsIndicator.childCount
        for (i in 0 until count) {
            val dot = dotsIndicator.getChildAt(i) as? ImageView
            dot?.setImageResource(if (i == position) R.drawable.ic_circle_filled else R.drawable.ic_circle_outline)
        }
    }

    private fun setupMerchandiseRecyclerView() {
        Log.d("HomeFragment", "Setting up merchandise RecyclerView")
        merchAdapter = MerchAdapter { merch ->
            Log.d("HomeFragment", "Merch item clicked: ${merch.name}")
            openUrl(merch.linkUrl)
        }

        rvMerch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = merchAdapter
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            Log.d("HomeFragment", "Pull to refresh triggered")
            homeViewModel.refreshData()
        }
    }

    private fun setupMenuClickListeners() {
        menuInfoKos.setOnClickListener { showComingSoonDialog() }
        menuKuliner.setOnClickListener { showComingSoonDialog() }

        // Menu dengan WebView
        menuPodcast.setOnClickListener {
            openWebView("https://apps.bem-unsoed.com/soedtify", "Soedtify")
        }

        menuEmagz.setOnClickListener {
            openWebView("https://apps.bem-unsoed.com/e-magz", "E-Magz")
        }

        menuKomik.setOnClickListener {
            openWebView("https://apps.bem-unsoed.com/komik", "Komik BEM Unsoed")
        }
    }



    private fun showComingSoonDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Coming Soon!")
            .setMessage("Fitur ini sedang dalam tahap pengembangan. Nantikan ya!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun observeViewModel() {
        Log.d("HomeFragment", "Setting up ViewModel observers")

        homeViewModel.banners.observe(viewLifecycleOwner) { banners ->
            Log.d("HomeFragment", "Banners updated: ${banners.size} items")
            if (banners.isNotEmpty()) {
                bannerAdapter.updateBanners(banners)
                setupDotsIndicator(banners.size)
            } else {
                setupDotsIndicator(0)
            }
        }

        homeViewModel.merch.observe(viewLifecycleOwner) { merchList ->
            Log.d("HomeFragment", "Merchandise updated: ${merchList.size} items")
            merchAdapter.submitList(merchList)
        }

        homeViewModel.events.observe(viewLifecycleOwner) { events ->
            Log.d("HomeFragment", "Events updated: ${events.size} items")
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                homeViewModel.clearError()
            }
        }
    }

    private fun openUrl(url: String) {
        if (url.isNotEmpty()) {
            val bundle = bundleOf(
                "url" to url,
                "title" to "Merchandise"
            )
            findNavController().navigate(R.id.webViewFragment, bundle)
        }
    }
    private fun openWebView(url: String, title: String) {
        val bundle = bundleOf(
            "url" to url,
            "title" to title
        )
        findNavController().navigate(R.id.webViewFragment, bundle)
    }

}
