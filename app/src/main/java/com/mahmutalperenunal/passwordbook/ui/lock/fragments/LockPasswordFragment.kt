package com.mahmutalperenunal.passwordbook.ui.lock.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.databinding.FragmentLockPasswordBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.lock.LockActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class LockPasswordFragment : Fragment() {

    private var _binding: FragmentLockPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        super.onViewCreated(view, savedInstanceState)
        val viewModel = (activity as LockActivity).viewModel

        checkDeviceHasBiometric()

        var incorrectPasswordCount = 0

        viewModel.getLockPassword().observe(viewLifecycleOwner) { lockData ->

            binding.lockPasswordLockPasswordEditText.helperText =
                "${getString(R.string.password_hint_text)}: ${lockData[0].hint}"
            binding.lockPasswordLoginAccountButton.setOnClickListener {
                val securityClass = EncryptionDecryption()
                val correctPassword = securityClass.decrypt(
                    lockData[0].password,
                    lockData[0].key,
                    securityClass.getKey()
                )
                val password = binding.lockPasswordLockPasswordEditText.editText?.text.toString()
                if (password.isEmpty() || password.isBlank()) {
                    Snackbar.make(
                        view,
                        getString(R.string.password_cannot_blank_text),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    if (password == correctPassword) {
                        val intent = Intent(requireContext(), PasswordActivity::class.java)
                        startActivity(intent)
                        (activity as LockActivity).finish()
                    } else {
                        Snackbar.make(
                            view,
                            getString(R.string.incorrect_password_text),
                            Snackbar.LENGTH_SHORT
                        ).show()

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
                                                "${getString(R.string.please_wait_text)}: $timer ${
                                                    getString(
                                                        R.string.seconds_text
                                                    )
                                                }"
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

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                createBiometricListener()
                createPromptInfo()
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.biometric_error_text),
                    Toast.LENGTH_SHORT
                ).show()
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.biometric_unavailable_text),
                    Toast.LENGTH_SHORT
                ).show()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {}

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {}

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {}
        }
    }

    private fun createBiometricListener() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        requireContext(),
                        "${getString(R.string.authentication_error_text)}: $errString",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.authentication_succeeded_text), Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(requireContext(), PasswordActivity::class.java)
                    startActivity(intent)
                    (activity as LockActivity).finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_failed_text),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    private fun createPromptInfo() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText(getString(R.string.cancel_text))
            .build()
    }

}