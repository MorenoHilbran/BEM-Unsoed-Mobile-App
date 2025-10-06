package com.example.bemunsoed.ui.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bemunsoed.R
import com.example.bemunsoed.databinding.FragmentCreatePostBinding
import com.example.bemunsoed.ui.dashboard.DashboardViewModel

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSendButtonListener()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSendButtonListener() {
        // Cari item menu 'action_send_post' yang sudah ada
        val sendMenuItem = binding.toolbar.menu.findItem(R.id.action_send_post)

        // Dapatkan layout kustom (FrameLayout) dari item menu
        val actionView = sendMenuItem?.actionView

        // Cari TextView di dalam layout kustom dan pasang listener
        actionView?.findViewById<TextView>(R.id.tv_send_button)?.setOnClickListener {
            val content = binding.etPostContent.text.toString().trim()
            if (content.isNotEmpty()) {
                dashboardViewModel.createPost(content)
            } else {
                Toast.makeText(context, "Konten tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.createPostResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Post berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Gagal membuat post"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                dashboardViewModel.clearCreatePostResult()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}