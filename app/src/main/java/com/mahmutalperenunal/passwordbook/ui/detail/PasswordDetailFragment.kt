package com.mahmutalperenunal.passwordbook.ui.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.data.AppDatabase
import com.mahmutalperenunal.passwordbook.databinding.FragmentPasswordDetailBinding
import com.mahmutalperenunal.passwordbook.utils.ClipboardUtil
import com.mahmutalperenunal.passwordbook.utils.CryptoUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordDetailFragment : Fragment() {

    private var _binding: FragmentPasswordDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PasswordDetailFragmentArgs>()

    private var isModified = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val password = args.password

        val toolbar = binding.tbHeader
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.etTitle.setText(password.title)
        binding.etUsername.setText(password.username)
        binding.etDecryptedPassword.setText(CryptoUtils.decrypt(password.encryptedPassword))

        val originalTitle = password.title
        val originalUsername = password.username
        val originalPassword = CryptoUtils.decrypt(password.encryptedPassword)

        checkForChanges(originalTitle, originalUsername, originalPassword)

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tlTitle.error = null
                checkForChanges(originalTitle, originalUsername, originalPassword)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tlUsername.error = null
                checkForChanges(originalTitle, originalUsername, originalPassword)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etDecryptedPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tlDecryptedPassword.error = null
                checkForChanges(originalTitle, originalUsername, originalPassword)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tbHeader.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.actionDelete -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.getInstance(requireContext()).passwordDao().delete(password)
                        launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(), requireContext().resources.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                    true
                }

                else -> false
            }
        }

        binding.tlDecryptedPassword.setOnLongClickListener {
            ClipboardUtil.copyText(requireContext(), binding.etDecryptedPassword.text.toString())
            true
        }

        binding.flabSave.setOnClickListener {
            val newUsername = binding.etUsername.text.toString()
            val newPassword = binding.etDecryptedPassword.text.toString()

            var isValid = true

            if (newUsername.isBlank()) {
                binding.tlUsername.error = getString(R.string.username_required)
                isValid = false
            } else {
                binding.tlUsername.error = null
            }

            if (newPassword.isBlank()) {
                binding.tlDecryptedPassword.error = getString(R.string.password_required)
                isValid = false
            } else {
                binding.tlDecryptedPassword.error = null
            }

            if (!isValid) return@setOnClickListener
            if (!isModified) return@setOnClickListener

            val updated = password.copy(
                title = binding.etTitle.text.toString(),
                username = newUsername,
                encryptedPassword = CryptoUtils.encrypt(newPassword)
            )

            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(requireContext()).passwordDao().insert(updated)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(R.string.updated), Toast.LENGTH_SHORT).show()
                    binding.flabSave.visibility = View.GONE
                    isModified = false
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun checkForChanges(originalTitle: String, originalUsername: String, originalPassword: String) {
        isModified =
            binding.etTitle.text.toString().trim() != originalTitle ||
                    binding.etUsername.text.toString().trim() != originalUsername ||
                    binding.etDecryptedPassword.text.toString().trim() != originalPassword

        binding.flabSave.visibility = if (isModified) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}