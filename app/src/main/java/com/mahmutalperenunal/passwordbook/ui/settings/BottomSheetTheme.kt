package com.mahmutalperenunal.passwordbook.ui.settings

import com.mahmutalperenunal.passwordbook.utils.ThemeHelper
import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mahmutalperenunal.passwordbook.databinding.BottomSheetThemeBinding

class BottomSheetTheme : BottomSheetDialogFragment() {

    private var _binding: BottomSheetThemeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        _binding = BottomSheetThemeBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        when (ThemeHelper.getSavedTheme(requireContext())) {
            ThemeHelper.ThemeMode.LIGHT -> binding.radioLight.isChecked = true
            ThemeHelper.ThemeMode.DARK -> binding.radioDark.isChecked = true
            ThemeHelper.ThemeMode.SYSTEM -> binding.radioSystem.isChecked = true
        }

        binding.radioLight.setOnClickListener {
            ThemeHelper.setTheme(requireContext(), ThemeHelper.ThemeMode.LIGHT)
            requireActivity().recreate()
            dismiss()
        }

        binding.radioDark.setOnClickListener {
            ThemeHelper.setTheme(requireContext(), ThemeHelper.ThemeMode.DARK)
            requireActivity().recreate()
            dismiss()
        }

        binding.radioSystem.setOnClickListener {
            ThemeHelper.setTheme(requireContext(), ThemeHelper.ThemeMode.SYSTEM)
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