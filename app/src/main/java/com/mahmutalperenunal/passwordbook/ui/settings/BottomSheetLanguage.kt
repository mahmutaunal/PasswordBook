package com.mahmutalperenunal.passwordbook.ui.settings

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mahmutalperenunal.passwordbook.databinding.BottomSheetLanguageBinding
import com.mahmutalperenunal.passwordbook.utils.LocalizationHelper

class BottomSheetLanguage : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        _binding = BottomSheetLanguageBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val currentLang = LocalizationHelper.getSavedLanguage(requireContext())

        when (currentLang) {
            "tr" -> binding.radioTr.isChecked = true
            "en" -> binding.radioEn.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }

        binding.radioTr.setOnClickListener {
            LocalizationHelper.setLocale(requireContext(), "tr")
            requireActivity().recreate()
            dismiss()
        }

        binding.radioEn.setOnClickListener {
            LocalizationHelper.setLocale(requireContext(), "en")
            requireActivity().recreate()
            dismiss()
        }

        binding.radioSystem.setOnClickListener {
            LocalizationHelper.setLocale(requireContext(), "")
            requireActivity().recreate()
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}