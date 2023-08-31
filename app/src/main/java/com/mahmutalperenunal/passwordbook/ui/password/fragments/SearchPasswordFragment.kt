package com.mahmutalperenunal.passwordbook.ui.password.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.passwordbook.adapter.PasswordAdapter
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.databinding.FragmentSearchPasswordBinding
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity

class SearchPasswordFragment : Fragment() {

    private var _binding: FragmentSearchPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewModel = (activity as PasswordActivity).viewModel

        val emptyList = listOf<Entry>()

        val adapter = PasswordAdapter(
            requireContext(),
            viewModel,
            viewLifecycleOwner,
            view,
            (activity as PasswordActivity)
        )

        binding.searchPasswordSearchEntriesRecyclerView.adapter = adapter
        binding.searchPasswordSearchEntriesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        viewModel.filteredSearchList.observe(viewLifecycleOwner) {

            if (it.isNullOrEmpty()) {
                adapter.differ.submitList(emptyList)
                binding.searchPasswordEmptySearchTextView.visibility = View.VISIBLE
            } else {
                binding.searchPasswordEmptySearchTextView.visibility = View.GONE
                adapter.differ.submitList(it)
            }

        }

        adapter.setOnItemClickListener {

            val command = "view"
            val intent = Intent(requireContext(), CreateEditViewPasswordActivity::class.java)
            intent.putExtra("command", command)
            intent.putExtra("data", it)
            startActivity(intent)

        }

        return view
    }

}