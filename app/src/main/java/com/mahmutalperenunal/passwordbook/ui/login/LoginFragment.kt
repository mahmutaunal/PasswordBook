package com.mahmutalperenunal.passwordbook.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.databinding.FragmentLoginBinding
import com.mahmutalperenunal.passwordbook.utils.PasswordValidator
import com.mahmutalperenunal.passwordbook.utils.SecurePrefsHelper

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!SecurePrefsHelper.isPasswordSet(requireContext())) {
            binding.btnBiometric.visibility = View.GONE
            binding.btnLogin.text = requireContext().resources.getString(R.string.signup)

            binding.etMasterPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        binding.passwordCriteriaLayout.visibility = View.GONE
                    } else {
                        binding.passwordCriteriaLayout.visibility = View.VISIBLE
                        updatePasswordCriteria(s.toString())
                    }

                    if (binding.tlMasterPassword.isErrorEnabled) {
                        binding.tlMasterPassword.error = null
                        binding.tlMasterPassword.isErrorEnabled = false
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            binding.btnLogin.setOnClickListener {
                val newPass = binding.etMasterPassword.text.toString()
                if (newPass.isBlank()) {
                    binding.tlMasterPassword.error = getString(R.string.cant_be_an_empty_password)
                    binding.tlMasterPassword.isErrorEnabled = true
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.cant_be_an_empty_password), Toast.LENGTH_SHORT).show()
                } else {
                    binding.tlMasterPassword.error = null
                    SecurePrefsHelper.savePassword(requireContext(), newPass)
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.password_saved), Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }
        } else {
            binding.passwordCriteriaLayout.visibility = View.GONE

            binding.btnLogin.setOnClickListener {
                val input = binding.etMasterPassword.text.toString()
                if (SecurePrefsHelper.checkPassword(requireContext(), input)) {
                    binding.tlMasterPassword.error = null
                    navigateToHome()
                } else {
                    binding.tlMasterPassword.error = getString(R.string.password_incorrect)
                    binding.tlMasterPassword.isErrorEnabled = true
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnBiometric.setOnClickListener {
                showBiometricPrompt()
            }

            binding.etMasterPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (binding.tlMasterPassword.isErrorEnabled) {
                        binding.tlMasterPassword.error = null
                        binding.tlMasterPassword.isErrorEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            binding.etMasterPassword.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.nestedScrollView.post {
                        binding.nestedScrollView.smoothScrollTo(0, binding.passwordCriteriaLayout.bottom)
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(requireContext(), getString(R.string.biometric_not_available), Toast.LENGTH_SHORT).show()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(requireContext().resources.getString(R.string.login_with_biometric))
            .setSubtitle(getString(R.string.use_fingerprint_face_recognition_or_screen_lock))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    navigateToHome()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(requireContext(), "${requireContext().resources.getString(R.string.error)}: $errString", Toast.LENGTH_SHORT).show()
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun updatePasswordCriteria(password: String) {
        val criteriaList = PasswordValidator.validate(requireContext(), password)

        binding.passwordCriteriaLayout.removeAllViews()

        criteriaList.forEach { criteria ->
            val tv = TextView(requireContext()).apply {
                text = if (criteria.isValid) "✔ ${criteria.description}" else "✘ ${criteria.description}"
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (criteria.isValid) R.color.green else R.color.red
                    )
                )
                textSize = 14f
            }
            binding.passwordCriteriaLayout.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}