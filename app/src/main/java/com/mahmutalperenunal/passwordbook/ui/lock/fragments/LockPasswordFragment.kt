package com.mahmutalperenunal.passwordbook.ui.lock.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.mahmutalperenunal.passwordbook.databinding.FragmentLockPasswordBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.lock.LockActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LockPasswordFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = (activity as LockActivity).viewModel
        val binding = FragmentLockPasswordBinding.bind(view)

        var incorrectPasswordCount = 0

        viewModel.getLockPassword().observe(viewLifecycleOwner) { lockData ->

            binding.lockPasswordLockPasswordEditText.helperText =
                "Password Hint: ${lockData[0].hint}"
            binding.lockPasswordLoginAccountButton.setOnClickListener {
                val securityClass = EncryptionDecryption()
                val correctPassword = securityClass.decrypt(
                    lockData[0].password,
                    lockData[0].key,
                    securityClass.getKey()
                )
                val password = binding.lockPasswordLockPasswordEditText.editText?.text.toString()
                if (password.isEmpty() || password.isBlank()) {
                    Snackbar.make(view, "Password cannot be blank", Snackbar.LENGTH_SHORT).show()
                } else {
                    if (password == correctPassword) {
                        val intent = Intent(requireContext(), PasswordActivity::class.java)
                        startActivity(intent)
                        (activity as LockActivity).finish()
                    } else {
                        Snackbar.make(view, "Incorrect Password!", Snackbar.LENGTH_SHORT).show()

                        if (lockData[0].antiBruteforceEnabled == 1) {
                            incorrectPasswordCount += 1
                            if (incorrectPasswordCount >= 4) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    var timer = 60
                                    while (timer > 0) {
                                        withContext(Dispatchers.Main) {
                                            binding.lockPasswordAntiBruteforceCountdownTextView.visibility =
                                                View.VISIBLE
                                            binding.lockPasswordAntiBruteforceCountdownTextView.text =
                                                "Try again after $timer Seconds"
                                            binding.lockPasswordLockPasswordEditText.editText?.isEnabled =
                                                false
                                            binding.lockPasswordLoginAccountButton.isEnabled = false

                                        }
                                        delay(1000)
                                        timer--
                                    }
                                    withContext(Dispatchers.Main) {
                                        incorrectPasswordCount = 0
                                        binding.lockPasswordAntiBruteforceCountdownTextView.visibility =
                                            View.GONE
                                        binding.lockPasswordLockPasswordEditText.editText?.isEnabled =
                                            true
                                        binding.lockPasswordLoginAccountButton.isEnabled = true
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