package com.mahmutalperenunal.passwordbook.ui.password.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.mahmutalperenunal.passwordbook.databinding.FragmentGeneratePasswordBinding
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthCalculator
import java.util.Random

class GeneratePasswordFragment : Fragment() {

    private var _binding: FragmentGeneratePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratePasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        var passwordLength = 13

        binding.generatePasswordPasswordInputMeter.setPasswordStrengthCalculator(object :
            PasswordStrengthCalculator {
            override fun calculatePasswordSecurityLevel(password: String?): Int {
                return if (password != null) {
                    getPasswordScore(password)
                } else {
                    0
                }
            }

            override fun getMinimumLength(): Int {
                return 1
            }

            override fun passwordAccepted(level: Int): Boolean {
                return true
            }

            override fun onPasswordAccepted(password: String?) {

            }
        })

        val generatedPassword = generatePassword(
            passwordLength,
            includeUpperCaseLetters = binding.generatePasswordUppercaseCheckBox.isChecked,
            includeLowerCaseLetters = binding.generatePasswordLowercaseCheckBox.isChecked,
            includeSymbols = binding.generatePasswordSymbolsCheckBox.isChecked,
            includeNumbers = binding.generatePasswordNumbersCheckBox.isChecked
        )

        binding.generatePasswordPasswordInputMeter.setEditText(binding.generatePasswordGeneratedPasswordEditText)
        binding.generatePasswordGeneratedPasswordEditText.setText(generatedPassword)

        binding.generatePasswordSliderPasswordStrength.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {
                passwordLength = slider.value.toInt()
            }


        })

        binding.generatePasswordSliderPasswordStrength.addOnChangeListener(object :
            Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                slider.setLabelFormatter(LabelFormatter {
                    return@LabelFormatter "${value.toInt()} Letters"
                })
            }
        })



        binding.generatePasswordGeneratePasswordButton.setOnClickListener {

            val generatedPassword = generatePassword(
                passwordLength,
                includeUpperCaseLetters = binding.generatePasswordUppercaseCheckBox.isChecked,
                includeLowerCaseLetters = binding.generatePasswordLowercaseCheckBox.isChecked,
                includeSymbols = binding.generatePasswordSymbolsCheckBox.isChecked,
                includeNumbers = binding.generatePasswordNumbersCheckBox.isChecked
            )

            if (generatedPassword.isBlank()) {
                if (passwordLength == 0) {
                    Snackbar.make(view, "Password length cannot be zero", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    Snackbar.make(view, "Please check at least one item", Snackbar.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.generatePasswordGeneratedPasswordEditText.setText(generatedPassword)
            }
        }

        binding.generatePasswordCopyPasswordImageView.setOnClickListener {
            val clipboard: ClipboardManager? = ContextCompat.getSystemService(
                requireContext(),
                ClipboardManager::class.java
            )
            val clip = ClipData.newPlainText(
                "Generated Password",
                binding.generatePasswordGeneratedPasswordEditText.text.toString()
            )
            clipboard?.setPrimaryClip(clip)
            Snackbar.make(view, "Password Copied to Clipboard", Snackbar.LENGTH_SHORT).show()
        }

        return view
    }

    private fun getPasswordScore(password: String): Int {
        if (password.isEmpty() || password.isBlank()) {
            return 0
        } else {
            return if (password.isEmpty()) {
                0
            } else if (password.length in 1..3) {
                1
            } else if (password.length in 4..6) {
                2
            } else if (password.length in 7..9) {
                3
            } else if (password.length in 10..12) {
                4
            } else {
                5
            }
        }
    }


    private fun <E> MutableList<E>.mRandom(): E? =
        if (size > 0) get(Random().nextInt(size)) else null

    private fun generatePassword(
        length: Int, includeUpperCaseLetters: Boolean, includeLowerCaseLetters: Boolean,
        includeSymbols: Boolean, includeNumbers: Boolean
    ): String {
        var password = ""
        val list = mutableListOf<Int>()
        if (includeUpperCaseLetters)
            list.add(0)
        if (includeLowerCaseLetters)
            list.add(1)
        if (includeNumbers)
            list.add(2)
        if (includeSymbols)
            list.add(3)

        for (i in 1..length) {
            when (list.toMutableList().mRandom()) {
                0 -> password += ('A'..'Z').toMutableList().mRandom().toString()
                1 -> password += ('a'..'z').toMutableList().mRandom().toString()
                2 -> password += ('0'..'9').toMutableList().mRandom().toString()
                3 -> password += listOf(
                    '!',
                    '@',
                    '#',
                    '$',
                    '%',
                    '&',
                    '*',
                    '+',
                    '=',
                    '-',
                    '~',
                    '?',
                    '/',
                    '_'
                ).toMutableList().mRandom().toString()
            }
        }
        return password
    }

}