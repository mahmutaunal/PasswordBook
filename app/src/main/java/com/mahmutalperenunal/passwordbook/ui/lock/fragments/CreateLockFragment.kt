package com.mahmutalperenunal.passwordbook.ui.lock.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
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

    private lateinit var dialog: ProgressDialog

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateLockBinding.bind(view)

        binding.createLockGetStartedButton.setOnClickListener {
            val mBottomSheetDialog = RoundedBottomSheetDialog(requireContext())
            val sheetView = layoutInflater.inflate(R.layout.create_lock_bottom_sheet, null)
            mBottomSheetDialog.setContentView(sheetView)

            val mBottomSheetBinding = CreateLockBottomSheetBinding.bind(sheetView)
            var cbBruteForce: Int

            mBottomSheetBinding.createLockBottomSheetBruteForceHelpImageView.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Anti Brute Force Mechanism")
                    .setMessage("If you enable this mechanism, you would need to wait for 30 seconds every time you enter a wrong password for three consecutive times. We highly recommend you to enable this option")
                    .setPositiveButton("Ok") { d, _ ->
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
                        "Password cannot be blank"
                } else {
                    if (password.length <= 3) {
                        mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordEditText.error =
                            "Your password should be at least 4 letters long"
                    } else {
                        if (hint.isBlank() || hint.isEmpty()) {
                            mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordEditText.isErrorEnabled =
                                false
                            mBottomSheetBinding.createLockBottomSheetLayoutLockPasswordHintEditText.error =
                                "Password Hint cannot be blank"
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    dialog = ProgressDialog.show(
                                        requireContext(),
                                        "Please wait",
                                        "We are creating your account",
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