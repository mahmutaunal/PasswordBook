package com.mahmutalperenunal.passwordbook.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.passwordbook.data.PasswordEntity
import com.mahmutalperenunal.passwordbook.databinding.ItemPasswordBinding

class PasswordAdapter : RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    private var passwordList = listOf<PasswordEntity>()

    inner class PasswordViewHolder(private val binding: ItemPasswordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PasswordEntity) {
            binding.tvTitle.text = item.title
            binding.tvUsername.text = item.username

            binding.root.setOnClickListener {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToPasswordDetailFragment(item)
                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val binding = ItemPasswordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PasswordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        holder.bind(passwordList[position])
    }

    override fun getItemCount() = passwordList.size

    fun submitList(list: List<PasswordEntity>) {
        passwordList = list
        notifyDataSetChanged()
    }
}