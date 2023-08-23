package com.mahmutalperenunal.passwordbook.ui.lock.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUpdateLockPasswordBinding.bind(view)

        binding.updateLockPasswordChangePasswordScrollView.post {
            binding.updateLockPasswordChangePasswordScrollView.fullScroll(View.FOCUS_DOWN)
        }

        var cbBruteForce: Int

        binding.updateLockPasswordBruteForceHelpButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Anti Brute Force Mechanism")
                .setMessage("If you enable this mechanism, you would need to wait for 30 seconds every time you enter a wrong password for three consecutive times. We highly recommend you to enable this option")
                .setPositiveButton("Ok") { d, _ ->
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
                        "Old Password cannot be blank"
                } else {
                    if (oldPassword != correctOldPassword) {
                        binding.updateLockPasswordOldLockPasswordEditText.isErrorEnabled = true
                        binding.updateLockPasswordOldLockPasswordEditText.error =
                            "Incorrect Old Password"
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
                                "Password cannot be blank"
                        } else {
                            if (password.length <= 3) {
                                binding.updateLockPasswordLockPasswordEditText.error =
                                    "Your password should be at least 4 letters long"
                            } else {
                                if (hint.isBlank() || hint.isEmpty()) {
                                    binding.updateLockPasswordLockPasswordEditText.isErrorEnabled =
                                        false
                                    binding.updateLockPasswordLockPasswordHintEditText.error =
                                        "Password Hint cannot be blank"
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
                                                "Password Changed Successfully",
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

    }

}