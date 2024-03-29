package com.mahmutalperenunal.passwordbook.ui.password.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.passwordbook.adapter.PasswordAdapter
import com.mahmutalperenunal.passwordbook.databinding.FragmentPasswordBinding
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel

class PasswordFragment : Fragment() {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CreateEditViewPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = (activity as PasswordActivity).viewModel

        val adapter = PasswordAdapter(
            requireContext(),
            viewModel,
            viewLifecycleOwner,
            view,
            (activity as PasswordActivity)
        )

        binding.passwordRecyclerView.adapter = adapter
        binding.passwordRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.sortedList.observe(viewLifecycleOwner) {
            adapter.differ.submitList(it)
        }

        adapter.setOnItemClickListener {

            val command = "view"
            val intent = Intent(requireContext(), CreateEditViewPasswordActivity::class.java)
            intent.putExtra("command", command)
            intent.putExtra("data", it)

            startActivity(intent)
            (activity as PasswordActivity).finish()

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}