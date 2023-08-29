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
import com.mahmutalperenunal.passwordbook.databinding.FragmentFavouritePasswordBinding
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity

class FavouritePasswordFragment : Fragment() {

    private var _binding: FragmentFavouritePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritePasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewModel = (activity as PasswordActivity).viewModel

        val adapter = PasswordAdapter(requireContext(), viewModel, viewLifecycleOwner, view)
        binding.favouritePasswordsRecyclerView.adapter = adapter
        binding.favouritePasswordsRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        viewModel.getAllFavouriteEntries().observe(viewLifecycleOwner) {
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

        return view
    }

}