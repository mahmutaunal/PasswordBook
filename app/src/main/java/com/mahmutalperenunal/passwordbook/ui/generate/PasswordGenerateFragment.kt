package com.mahmutalperenunal.passwordbook.ui.generate

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mahmutalperenunal.passwordbook.databinding.FragmentPasswordGenerateBinding
import com.mahmutalperenunal.passwordbook.utils.ClipboardUtil
import com.mahmutalperenunal.passwordbook.utils.PasswordGenerator

class PasswordGenerateFragment : Fragment() {

    private var _binding: FragmentPasswordGenerateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordGenerateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = binding.tbHeader
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.flabGenerate.setOnClickListener {
            val length = binding.sliderLength.value.toInt()
            val password = PasswordGenerator.generate(
                length = length,
                useLowercase = binding.cbLowercase.isChecked,
                useUppercase = binding.cbUppercase.isChecked,
                useDigits = binding.cbDigits.isChecked,
                useSpecials = binding.cbSpecials.isChecked
            )
            binding.tvResult.text = password
        }

        binding.tvResult.setOnClickListener {
            val text = binding.tvResult.text.toString()
            if (text.isNotBlank()) {
                ClipboardUtil.copyText(requireContext(), text)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}