package com.mahmutalperenunal.passwordbook.ui.lock.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.entities.Lock
import com.mahmutalperenunal.passwordbook.databinding.CreateLockBottomSheetBinding
import com.mahmutalperenunal.passwordbook.databinding.FragmentCreateLockBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.lock.LockActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.util.Passwords.Companion.PASSWORD1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateLockFragment : Fragment() {

    private var _binding: FragmentCreateLockBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateLockBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.createLockGetStartedButton.setOnClickListener {
            val mBottomSheetDialog = RoundedBottomSheetDialog(requireContext())
            val sheetView = layoutInflater.inflate(R.layout.create_lock_bottom_sheet, null)
            mBottomSheetDialog.setContentView(sheetView)

            val mBottomSheetBinding = CreateLockBottomSheetBinding.bind(sheetView)
            var cbBruteForce: Int

            mBottomSheetBinding.createLockBottomSheetBruteForceHelpImageView.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.anti_bruteforce_mechanism_text)
                    .setMessage(R.string.anti_bruteforce_mechanism_description_text)
                    .setPositiveButton(getString(R.string.ok_text)) { d, _ ->
                        d.dismiss()
                    }.create().show()
            }

            mBottomSheetDialog.show()

            val viewModel = (activity as LockActivity).viewModel
            mBottomSheetBinding.createLockBottomSheetCreateAccountButton.setOnClickListener {
                val password =
                    mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordEditText.editText?.text.toString()
                val hint =
                    mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordHintEditText.editText?.text.toString()

                cbBruteForce =
                    if (mBottomSheetBinding.createLockBottomSheetAntiBruteForceCheckBox.isChecked) {
                        1
                    } else {
                        0
                    }

                if (password.isBlank() || password.isEmpty()) {
                    mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordEditText.error =
                        getString(R.string.password_cannot_blank_text)
                } else {
                    if (password.length <= 3) {
                        mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordEditText.error =
                            getString(R.string.password_must_least_4_letters_long_text)
                    } else {
                        if (hint.isBlank() || hint.isEmpty()) {
                            mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordEditText.isErrorEnabled =
                                false
                            mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordHintEditText.error =
                                getString(R.string.password_hint_cannot_blank_text)
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    dialog = ProgressDialog.show(
                                        requireContext(),
                                        getString(R.string.please_wait_text),
                                        getString(R.string.creating_account_text),
                                        true,
                                        false
                                    )
                                    dialog.show()
                                    withContext(Dispatchers.IO) {
                                        val securityClass = EncryptionDecryption()
                                        val password1 = PASSWORD1
                                        val encryptedPasswordObject = securityClass.encrypt(
                                            password,
                                            password1,
                                            securityClass.getKey()
                                        )
                                        val lock = Lock(
                                            0,
                                            encryptedPasswordObject.encryptedData,
                                            encryptedPasswordObject.key,
                                            hint,
                                            cbBruteForce
                                        )

                                        async { viewModel.setLock(lock) }.await()
                                        delay(2000)
                                        withContext(Dispatchers.Main) {
                                            mBottomSheetDialog.dismiss()
                                            dialog.dismiss()
                                            val intent = Intent(
                                                requireContext(),
                                                PasswordActivity::class.java
                                            )
                                            startActivity(intent)
                                            requireActivity().finish()
                                            requireActivity().overridePendingTransition(
                                                R.anim.slide_in_left,
                                                R.anim.slide_out_right
                                            )
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