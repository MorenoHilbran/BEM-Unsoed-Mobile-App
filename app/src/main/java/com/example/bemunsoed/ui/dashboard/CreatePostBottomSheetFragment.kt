package com.example.bemunsoed.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.bemunsoed.R

class CreatePostBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var etPostContent: EditText
    private lateinit var btnPost: Button
    private lateinit var tvCharacterCount: TextView
    private lateinit var btnClose: ImageView

    private var onPostCreated: ((String) -> Unit)? = null

    companion object {
        fun newInstance(onPostCreated: (String) -> Unit): CreatePostBottomSheetFragment {
            return CreatePostBottomSheetFragment().apply {
                this.onPostCreated = onPostCreated
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupTextWatcher()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        etPostContent = view.findViewById(R.id.et_post_content)
        btnPost = view.findViewById(R.id.btn_post)
        tvCharacterCount = view.findViewById(R.id.tv_character_count)
        btnClose = view.findViewById(R.id.btn_close)
    }

    private fun setupTextWatcher() {
        etPostContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                val remaining = 280 - length

                tvCharacterCount.text = remaining.toString()
                tvCharacterCount.setTextColor(
                    if (remaining < 20)
                        requireContext().getColor(android.R.color.holo_red_dark)
                    else
                        requireContext().getColor(android.R.color.darker_gray)
                )

                btnPost.isEnabled = length > 0 && length <= 280
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        btnClose.setOnClickListener {
            dismiss()
        }

        btnPost.setOnClickListener {
            val content = etPostContent.text.toString().trim()
            if (content.isNotEmpty()) {
                onPostCreated?.invoke(content)
                dismiss()
                Toast.makeText(context, "Post berhasil dibuat!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
