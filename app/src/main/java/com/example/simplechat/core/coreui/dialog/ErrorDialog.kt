package com.example.simplechat.core.coreui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.simplechat.core.coreui.extensions.makeVisible
import com.example.simplechat.databinding.DialogErrorBinding

class ErrorDialog(
    private val message: String
): DialogFragment() {

    private var _binding: DialogErrorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogErrorBinding.inflate(layoutInflater)

        binding.error.errorContainer.makeVisible(true)

        binding.error.errorTv.text = message

        binding.okBtn.setOnClickListener { dismiss() }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}