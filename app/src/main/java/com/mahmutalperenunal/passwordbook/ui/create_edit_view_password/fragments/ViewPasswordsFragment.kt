package com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.adapter.LogoCompanyViewerAdapter
import com.mahmutalperenunal.passwordbook.databinding.FragmentViewPasswordsBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.util.CompanyListData

class ViewPasswordsFragment : Fragment() {

    private var _binding: FragmentViewPasswordsBinding? = null
    private val binding get() = _binding!!

    private val args: ViewPasswordsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPasswordsBinding.inflate(inflater, container, false)
        val view = binding.root

        val data = args.data

        val viewModel = (activity as CreateEditViewPasswordActivity).viewModel

        val iconId = data.icon

        val iconList = CompanyListData.companyListData
        for (icon in iconList) {
            if (icon.id == iconId) {
                binding.viewPasswordsCompanyLogoImageView.setImageResource(icon.companyIcon)
                break
            } else {
                binding.viewPasswordsCompanyLogoImageView.setImageResource(R.drawable.cl_general_account)
            }
        }

        binding.viewPasswordsCompanyNameTextView.text = data.title
        binding.viewPasswordsCategoryTextView.text = data.category
        when (data.category) {
            "Social" -> binding.viewPasswordsCategoryIconImageView.setImageResource(R.drawable.ic_social)
            "Mails" -> binding.viewPasswordsCategoryIconImageView.setImageResource(R.drawable.ic_mail)
            "Cards" -> binding.viewPasswordsCategoryIconImageView.setImageResource(R.drawable.ic_card)
            "Work" -> binding.viewPasswordsCategoryIconImageView.setImageResource(R.drawable.ic_work)
            "Other" -> binding.viewPasswordsCategoryIconImageView.setImageResource(R.drawable.ic_others)
        }

        binding.viewPasswordsBackImageView.setOnClickListener {
            val intent = Intent(requireContext(), PasswordActivity::class.java)
            startActivity(intent)
            (activity as CreateEditViewPasswordActivity).finish()
        }

        val securityClass = EncryptionDecryption()
        val entryDetailAdapter = LogoCompanyViewerAdapter(
            viewModel,
            viewLifecycleOwner,
            view,
            requireContext(),
            securityClass
        )
        binding.viewPasswordsAccountDetailsRecyclerView.apply {
            adapter = entryDetailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getAllEntryDetails(data.id).observe(viewLifecycleOwner) {
            entryDetailAdapter.differ.submitList(it)
        }

        return view
    }

}