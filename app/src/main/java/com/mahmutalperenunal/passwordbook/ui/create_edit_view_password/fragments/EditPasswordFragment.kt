package com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.core.view.children
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.adapter.LogoCompanyChooserAdapter
import com.mahmutalperenunal.passwordbook.adapter.PasswordAccountInfoAdapter
import com.mahmutalperenunal.passwordbook.database.entities.EncryptedKey
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.database.entities.EntryDetail
import com.mahmutalperenunal.passwordbook.databinding.BottomSheetOptionsBinding
import com.mahmutalperenunal.passwordbook.databinding.CompanyChooserSheetBinding
import com.mahmutalperenunal.passwordbook.databinding.FragmentEditPasswordBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel
import com.mahmutalperenunal.passwordbook.util.CompanyList
import com.mahmutalperenunal.passwordbook.util.CompanyListData
import com.mahmutalperenunal.passwordbook.util.Passwords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

class EditPasswordFragment : Fragment() {

    private var _binding: FragmentEditPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PasswordAccountInfoAdapter

    private lateinit var viewModel: CreateEditViewPasswordViewModel

    private lateinit var accountDetailList: MutableList<EntryDetail>

    private val args: EditPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        val mBottomSheetDialog = RoundedBottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_options, null)
        mBottomSheetDialog.setContentView(sheetView)
        val sheetBinding: BottomSheetOptionsBinding = BottomSheetOptionsBinding.bind(sheetView)

        val data = args.data

        binding.editPasswordEntryTitleEditText.editText?.setText(data.title)

        when (data.category) {
            "Social" -> binding.editPasswordChipSocial.isChecked = true
            "Mails" -> binding.editPasswordChipMail.isChecked = true
            "Cards" -> binding.editPasswordChipCards.isChecked = true
            "Work" -> binding.editPasswordChipWork.isChecked = true
            "Other" -> binding.editPasswordChipOther.isChecked = true
            else -> binding.editPasswordChipWork.isChecked = true
        }

        viewModel = (activity as CreateEditViewPasswordActivity).viewModel

        accountDetailList = mutableListOf()

        val securityClass = EncryptionDecryption()

        val rvAccountDetails = binding.editPasswordAccountDetailsRecyclerView
        rvAccountDetails.layoutManager = LinearLayoutManager(requireContext())

        CoroutineScope(Dispatchers.IO).launch {

            val oldEntryDetailList = viewModel.getAllEntryDetailsOneTime(data.id)
            for (i in oldEntryDetailList.indices) {

                val oldEntryDetailKey =
                    viewModel.getAllEncryptedKeysOneTime(oldEntryDetailList[i].id)[0]
                val decryptedData = securityClass.decrypt(
                    oldEntryDetailList[i].detailContent,
                    oldEntryDetailKey.emdKey,
                    securityClass.getKey()
                )

                val decryptedEntryDetail = EntryDetail(
                    oldEntryDetailList[i].id,
                    oldEntryDetailList[i].entryId,
                    oldEntryDetailList[i].detailType,
                    decryptedData
                )
                accountDetailList.add(decryptedEntryDetail)
            }

            withContext(Dispatchers.Main) {
                adapter = PasswordAccountInfoAdapter(accountDetailList)
                rvAccountDetails.adapter = adapter
            }
        }

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val startPos = viewHolder.adapterPosition
                val endPos = target.adapterPosition
                Collections.swap(accountDetailList, startPos, endPos)
                adapter.notifyItemMoved(startPos, endPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })

        itemTouchHelper.attachToRecyclerView(binding.editPasswordAccountDetailsRecyclerView)

        var companyIcon = data.icon

        binding.editPasswordBackButton.setOnClickListener {
            val intent = Intent(
                requireContext(),
                PasswordActivity::class.java
            )
            startActivity(intent)
            (activity as CreateEditViewPasswordActivity).finish()
            (activity as CreateEditViewPasswordActivity).overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        binding.editPasswordIconImageView.setOnClickListener {

            val iBottomSheetDialog = RoundedBottomSheetDialog(requireContext())
            val sheetView = layoutInflater.inflate(R.layout.company_chooser_sheet, null)
            iBottomSheetDialog.setContentView(sheetView)
            val companySheetBinding: CompanyChooserSheetBinding = CompanyChooserSheetBinding.bind(
                sheetView
            )
            val companyList = CompanyListData.companyListData
            val companyAdapter = LogoCompanyChooserAdapter(companyList)
            companySheetBinding.rvCompanyChooser.adapter = companyAdapter
            companySheetBinding.rvCompanyChooser.layoutManager =
                LinearLayoutManager(requireContext())
            companySheetBinding.rvCompanyChooser.isNestedScrollingEnabled = true;
            iBottomSheetDialog.show()
            companyAdapter.setOnItemClickListener {
                companyIcon = it.id
                Snackbar.make(
                    view,
                    "${it.companyName} " + getString(R.string.logo_selected_text),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                iBottomSheetDialog.dismiss()
            }

            iBottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            companySheetBinding.searchBar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val companyListData = CompanyListData.companyListData
                    val filteredList = mutableListOf<CompanyList>()

                    if (p0 != null) {
                        for (company in companyListData) {
                            if (company.companyName.contains(p0)) {
                                filteredList.add(company)
                            }
                        }
                    }

                    val filteredCompanyAdapter = LogoCompanyChooserAdapter(filteredList)
                    companySheetBinding.rvCompanyChooser.adapter = filteredCompanyAdapter

                }

                override fun afterTextChanged(p0: Editable?) {}
            })

        }

        binding.editPasswordNewEntryImageView.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.account_details_menu, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.accountDetail_username -> {
                        showBottomSheet(mBottomSheetDialog, sheetBinding, 0)
                        popupMenu.dismiss()
                    }

                    R.id.accountDetail_email -> {
                        showBottomSheet(mBottomSheetDialog, sheetBinding, 1)
                        popupMenu.dismiss()
                    }

                    R.id.accountDetail_phone -> {
                        showBottomSheet(mBottomSheetDialog, sheetBinding, 2)
                        popupMenu.dismiss()
                    }

                    R.id.accountDetail_password -> {
                        showBottomSheet(mBottomSheetDialog, sheetBinding, 3)
                        popupMenu.dismiss()
                    }

                    R.id.accountDetail_website -> {
                        showBottomSheet(mBottomSheetDialog, sheetBinding, 4)
                        popupMenu.dismiss()
                    }

                    R.id.accountDetail_note -> {
                        showBottomSheet(mBottomSheetDialog, sheetBinding, 5)
                        popupMenu.dismiss()
                    }


                }
                true
            }

        }

        binding.editPasswordSaveImageView.setOnClickListener {

            val entryTitle = binding.editPasswordEntryTitleEditText.editText?.text.toString()

            val entryCategory: String =
                (binding.editPasswordCategoryChipGroup.children.toList().filter {
                    (it as Chip).isChecked
                }[0] as Chip).text.toString()

            val entryIcon = companyIcon

            val entryDetailsList = accountDetailList

            if (entryTitle.isNotEmpty() || entryTitle.isNotBlank()) {
                if (entryCategory.isNotEmpty() || entryCategory.isNotBlank()) {
                    if (entryDetailsList.isNotEmpty()) {

                        val dialog = ProgressDialog.show(
                            requireContext(),
                            getString(R.string.saving_text),
                            getString(R.string.saving_description_text),
                            true,
                            false
                        )
                        dialog.show()

                        val password1 = Passwords.PASSWORD1

                        CoroutineScope(Dispatchers.IO).launch {

                            // Deleting Old Data
                            val oldEntryDetailList = viewModel.getAllEntryDetailsOneTime(data.id)
                            for (element in oldEntryDetailList) {
                                viewModel.deleteEncryptedKeys(element.id)
                            }
                            viewModel.deleteEntryDetails(data.id)

                            // Adding New Data
                            val entry =
                                Entry(data.id, entryTitle, entryCategory, entryIcon, data.favourite)

                            val id = async { viewModel.upsertEntry(entry) }.await()

                            for (entryDetail in entryDetailsList) {
                                val encryptedObject = securityClass.encrypt(
                                    entryDetail.detailContent, password1, securityClass.getKey()
                                )
                                val encryptedData = encryptedObject.encryptedData
                                val emdKey = encryptedObject.key

                                entryDetail.id = 0
                                entryDetail.entryId = id
                                entryDetail.detailContent = encryptedData

                                val entryDetailId = async {
                                    viewModel.upsertEntryDetail(
                                        entryDetail
                                    )
                                }.await()
                                val keyObject = EncryptedKey(0, entryDetailId, emdKey)
                                async { viewModel.upsertEncryptedKey(keyObject) }.await()
                            }

                            withContext(Dispatchers.Main) {
                                dialog.dismiss()
                                val intent = Intent(requireContext(), PasswordActivity::class.java)
                                startActivity(intent)
                                (activity as CreateEditViewPasswordActivity).finish()
                            }
                        }
                    } else {
                        Snackbar.make(
                            view,
                            getString(R.string.account_detail_error_text),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(
                        view,
                        getString(R.string.select_category_text),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    view,
                    getString(R.string.title_cannot_blank_text),
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showBottomSheet(
        mBottomSheetDialog: RoundedBottomSheetDialog,
        sheetBinding: BottomSheetOptionsBinding,
        optionType: Int
    ) {

        var detailType = ""

        sheetBinding.bottomSheetOptionsEditText.editText?.text?.clear()
        sheetBinding.bottomSheetOptionsEditText.editText?.clearFocus()

        when (optionType) {
            0 -> {
                detailType = "Username"
                sheetBinding.bottomSheetOptionsEditText.helperText =
                    getString(R.string.eg_text) + getString(R.string.username_text)
                sheetBinding.bottomSheetOptionsEditText.editText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            }

            1 -> {
                detailType = "Email"
                sheetBinding.bottomSheetOptionsEditText.helperText =
                    getString(R.string.eg_text) + getString(R.string.sample_email_text)
                sheetBinding.bottomSheetOptionsEditText.editText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL

            }

            2 -> {
                detailType = "Phone Number"
                sheetBinding.bottomSheetOptionsEditText.editText?.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_PHONE
                sheetBinding.bottomSheetOptionsEditText.helperText =
                    getString(R.string.eg_text) + getString(R.string.sample_phone_number_text)
            }

            3 -> {
                detailType = "Password"
                sheetBinding.bottomSheetOptionsEditText.isPasswordVisibilityToggleEnabled = true
                sheetBinding.bottomSheetOptionsEditText.helperText =
                    getString(R.string.strong_password_text)
                sheetBinding.bottomSheetOptionsEditText.editText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            4 -> {
                detailType = "Website"
                sheetBinding.bottomSheetOptionsEditText.helperText =
                    getString(R.string.eg_text) + getString(R.string.sample_website_text)
                sheetBinding.bottomSheetOptionsEditText.editText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            }

            5 -> {
                detailType = "Notes"
                sheetBinding.bottomSheetOptionsEditText.editText?.minLines = 3
                sheetBinding.bottomSheetOptionsEditText.helperText =
                    getString(R.string.some_notes_text)
                sheetBinding.bottomSheetOptionsEditText.editText?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
        }

        sheetBinding.bottomSheetOptionsEditText.hint = detailType

        mBottomSheetDialog.show()

        sheetBinding.bottomSheetOptionsAddOptionButton.setOnClickListener {
            val validateMessage = validateInput(
                sheetBinding.bottomSheetOptionsEditText.editText?.text.toString(), optionType
            )
            if (validateMessage == getString(R.string.validated_text)) {
                val accountDetailObj = EntryDetail(
                    1,
                    1,
                    detailType,
                    sheetBinding.bottomSheetOptionsEditText.editText?.text.toString()
                )
                accountDetailList.add(accountDetailObj)
                adapter.notifyDataSetChanged()
                mBottomSheetDialog.dismiss()
            } else {
                sheetBinding.bottomSheetOptionsEditText.error = validateMessage
            }
        }

    }

    private fun validateInput(input: String, type: Int): String {
        when (type) {
            0 -> {
                // Validate for Username
                return if (nullCheckInput(input)) {
                    getString(R.string.validated_text)
                } else {
                    getString(R.string.username_cannot_blank_text)
                }
            }

            1 -> {
                // Validate for Email
                return if (nullCheckInput(input)) {
                    if (input.contains("@") && input.contains(".")) {
                        getString(R.string.validated_text)
                    } else {
                        getString(R.string.incorrect_email_format_text)
                    }
                } else {
                    getString(R.string.email_cannot_blank_text)
                }
            }

            2 -> {
                // Validate for Phone Number
                return if (nullCheckInput(input)) {
                    if (input.length > 2) {
                        getString(R.string.validated_text)
                    } else {
                        getString(R.string.phone_number_must_more_than_two_digits_text)
                    }
                } else {
                    getString(R.string.phone_number_cannot_blank_text)
                }
            }

            3 -> {
                // Validate for Password
                return if (nullCheckInput(input)) {
                    getString(R.string.validated_text)
                } else {
                    getString(R.string.password_cannot_blank_text)
                }
            }

            4 -> {
                // Validate for Website
                return if (nullCheckInput(input)) {
                    if (input.contains(".")) {
                        getString(R.string.validated_text)
                    } else {
                        getString(R.string.incorrect_website_format_text)
                    }
                } else {
                    getString(R.string.website_cannot_blank_text)
                }
            }

            5 -> {
                // Validate for Note
                return if (nullCheckInput(input)) {
                    getString(R.string.validated_text)
                } else {
                    getString(R.string.note_cannot_blank_text)
                }
            }

            else -> return getString(R.string.error_text)
        }
    }

    private fun nullCheckInput(input: String): Boolean {
        return input.isNotEmpty() && input.isNotBlank()
    }

}