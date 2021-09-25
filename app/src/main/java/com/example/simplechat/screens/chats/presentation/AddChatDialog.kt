package com.example.simplechat.screens.chats.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.simplechat.R
import com.example.simplechat.databinding.DialogAddChatBinding

class AddChatDialog(
    private val addChat: (String) -> Unit
): DialogFragment() {

    private var _binding: DialogAddChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddChatBinding.inflate(layoutInflater)

        binding.addBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_field_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addChat(username)
            dismiss()
        }

        binding.cancelBtn.setOnClickListener { dismiss() }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}