package com.mahmutalperenunal.passwordbook.ui.password.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.passwordbook.adapter.PasswordAdapter
import com.mahmutalperenunal.passwordbook.databinding.FragmentPasswordBinding
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel

class PasswordFragment : Fragment() {

    private lateinit var binding: FragmentPasswordBinding
    private lateinit var viewModel: CreateEditViewPasswordViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as PasswordActivity).viewModel

        binding = FragmentPasswordBinding.bind(view)

        val adapter = PasswordAdapter(
            requireContext(),
            viewModel,
            viewLifecycleOwner,
            view
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

            Log.d("id", it.id.toString())

            startActivity(intent)
            (activity as PasswordActivity).finish()

        }


    }

}