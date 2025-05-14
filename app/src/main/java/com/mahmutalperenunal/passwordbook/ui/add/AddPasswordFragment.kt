package com.mahmutalperenunal.passwordbook.ui.add

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.data.AppDatabase
import com.mahmutalperenunal.passwordbook.data.PasswordEntity
import com.mahmutalperenunal.passwordbook.databinding.FragmentAddPasswordBinding
import com.mahmutalperenunal.passwordbook.utils.CryptoUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPasswordFragment : Fragment() {

    private var _binding: FragmentAddPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = binding.tbHeader
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.flabSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            var isValid = true

            if (username.isBlank()) {
                binding.tlUsername.error = getString(R.string.username_required)
                isValid = false
            } else {
                binding.tlUsername.error = null
            }

            if (password.isBlank()) {
                binding.tlPassword.error = getString(R.string.password_required)
                isValid = false
            } else {
                binding.tlPassword.error = null
            }

            if (!isValid) return@setOnClickListener

            if (title.isBlank() || username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encryptedPassword = CryptoUtils.encrypt(password)
            val passwordEntity = PasswordEntity(
                title = title,
                username = username,
                encryptedPassword = encryptedPassword
            )

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(requireContext()).passwordDao().insert(passwordEntity)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        }

        binding.etUsername.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tlUsername.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tlPassword.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}