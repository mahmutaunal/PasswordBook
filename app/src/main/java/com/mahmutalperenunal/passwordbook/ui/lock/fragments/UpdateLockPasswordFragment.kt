package com.mahmutalperenunal.passwordbook.ui.lock.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.entities.Lock
import com.mahmutalperenunal.passwordbook.databinding.FragmentUpdateLockPasswordBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.lock.LockActivity
import com.mahmutalperenunal.passwordbook.util.Passwords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateLockPasswordFragment : Fragment() {

    private var _binding: FragmentUpdateLockPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateLockPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.updateLockPasswordChangePasswordScrollView.post {
            binding.updateLockPasswordChangePasswordScrollView.fullScroll(View.FOCUS_DOWN)
        }

        var cbBruteForce: Int

        binding.updateLockPasswordBruteForceHelpButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.anti_bruteforce_mechanism_text))
                .setMessage(getString(R.string.anti_bruteforce_mechanism_description_text))
                .setPositiveButton(getString(R.string.ok_text)) { d, _ ->
                    d.dismiss()
                }.create().show()
        }

        val viewModel = (activity as LockActivity).viewModel

        viewModel.getLockPassword().observe(viewLifecycleOwner) { lockPassword ->

            binding.updateLockPasswordChangePasswordButton.setOnClickListener {

                val securityClass = EncryptionDecryption()
                val correctOldPassword = securityClass.decrypt(
                    lockPassword[0].password,
                    lockPassword[0].key,
                    securityClass.getKey()
                )
                val oldPassword =
                    binding.updateLockPasswordOldLockPasswordEditText.editText?.text.toString()
                val password =
                    binding.updateLockPasswordLockPasswordEditText.editText?.text.toString()
                val hint =
                    binding.updateLockPasswordLockPasswordHintEditText.editText?.text.toString()

                if (oldPassword.isEmpty() || oldPassword.isBlank()) {
                    binding.updateLockPasswordOldLockPasswordEditText.isErrorEnabled = true
                    binding.updateLockPasswordOldLockPasswordEditText.error =
                        getString(R.string.old_password_cannot_blank_text)
                } else {
                    if (oldPassword != correctOldPassword) {
                        binding.updateLockPasswordOldLockPasswordEditText.isErrorEnabled = true
                        binding.updateLockPasswordOldLockPasswordEditText.error =
                            getString(R.string.incorrect_old_password_text)
                    } else {
                        binding.updateLockPasswordOldLockPasswordEditText.isErrorEnabled = false

                        cbBruteForce =
                            if (binding.updateLockPasswordAntiBruteForceCheckBox.isChecked) {
                                1
                            } else {
                                0
                            }

                        if (password.isBlank() || password.isEmpty()) {
                            binding.updateLockPasswordLockPasswordEditText.error =
                                getString(R.string.password_cannot_blank_text)
                        } else {
                            if (password.length <= 3) {
                                binding.updateLockPasswordLockPasswordEditText.error =
                                    getString(R.string.password_must_least_4_letters_long_text)
                            } else {
                                if (hint.isBlank() || hint.isEmpty()) {
                                    binding.updateLockPasswordLockPasswordEditText.isErrorEnabled =
                                        false
                                    binding.updateLockPasswordLockPasswordHintEditText.error =
                                        getString(R.string.password_hint_cannot_blank_text)
                                } else {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val encryptedPasswordObject = securityClass.encrypt(
                                            password,
                                            Passwords.PASSWORD1, securityClass.getKey()
                                        )
                                        val lock = Lock(
                                            0,
                                            encryptedPasswordObject.encryptedData,
                                            encryptedPasswordObject.key,
                                            hint,
                                            cbBruteForce
                                        )
                                        async { viewModel.setLock(lock) }.await()
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                requireContext().applicationContext,
                                                getString(R.string.password_changed_succesfully_text),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            requireActivity().finish()
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}