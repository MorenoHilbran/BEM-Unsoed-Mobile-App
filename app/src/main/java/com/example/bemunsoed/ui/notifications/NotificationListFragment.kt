package com.example.bemunsoed.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bemunsoed.R
import com.example.bemunsoed.databinding.FragmentNotificationListBinding
import com.example.bemunsoed.data.model.Notification as NotificationModel

class NotificationListFragment : Fragment() {

    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        setupUI()
        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        // Back button
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter { notification ->
            onNotificationClick(notification)
        }

        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }

    private fun observeViewModel() {
        // Observe notifications
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            notificationAdapter.submitList(notifications)

            // Show/hide empty state
            if (notifications.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvNotifications.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvNotifications.visibility = View.VISIBLE
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onNotificationClick(notification: NotificationModel) {
        // Navigate to post detail
        if (notification.postId.isNotEmpty()) {
            val bundle = Bundle().apply {
                putString("postId", notification.postId)
            }
            findNavController().navigate(R.id.postDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
