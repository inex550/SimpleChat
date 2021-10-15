package com.example.simplechat.core.coreui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.simplechat.databinding.DialogOkBinding

class OkDialog(
    private val message: String,
    private val title: String = "Внимание",
    private val onDismissDialog: (() -> Unit)? = null
): DialogFragment() {

    private var _binding: DialogOkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogOkBinding.inflate(layoutInflater)

        binding.titleTv.text = title
        binding.messageTv.text = message
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissDialog?.invoke()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDismissDialog?.invoke()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}