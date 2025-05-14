package com.mahmutalperenunal.passwordbook.ui.settings

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mahmutalperenunal.passwordbook.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = binding.tbHeader
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.llTheme.setOnClickListener { onThemeClicked() }
        binding.llLanguage.setOnClickListener { onLanguageClicked() }
    }

    private fun onThemeClicked() {
        BottomSheetTheme().show(parentFragmentManager, "ThemeSheet")
    }

    private fun onLanguageClicked() {
        BottomSheetLanguage().show(parentFragmentManager, "LanguageSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}