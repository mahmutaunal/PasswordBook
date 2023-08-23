package com.mahmutalperenunal.passwordbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.entities.EntryDetail
import com.mahmutalperenunal.passwordbook.databinding.LayoutAccountInfoBinding

class PasswordAccountInfoAdapter(private val accountDetails: MutableList<EntryDetail>) :
    RecyclerView.Adapter<PasswordAccountInfoAdapter.PasswordAccountInfoAdapterViewHolder>() {
    inner class PasswordAccountInfoAdapterViewHolder(val binding: LayoutAccountInfoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PasswordAccountInfoAdapterViewHolder {
        val view =
            LayoutAccountInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PasswordAccountInfoAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return accountDetails.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PasswordAccountInfoAdapterViewHolder, position: Int) {
        val detail = accountDetails[position]
        holder.binding.accountInfoInfoTypeTextView.text = detail.detailType
        holder.binding.accountInfoInfoContentTextView.text = detail.detailContent

        when (holder.binding.accountInfoInfoTypeTextView.text) {
            "Username" -> holder.binding.accountInfoInfoIconImageView.setImageResource(R.drawable.ic_username_info)
            "Email" -> holder.binding.accountInfoInfoIconImageView.setImageResource(R.drawable.ic_mail_info)
            "Phone Number" -> holder.binding.accountInfoInfoIconImageView.setImageResource(R.drawable.ic_phone_info)
            "Password" -> holder.binding.accountInfoInfoIconImageView.setImageResource(R.drawable.ic_password_change_info)
            "Website" -> holder.binding.accountInfoInfoIconImageView.setImageResource(R.drawable.ic_website_info)
            "Notes" -> holder.binding.accountInfoInfoIconImageView.setImageResource(R.drawable.ic_note_info)
        }

        holder.binding.accountInfoDeleteInfoButton.setOnClickListener {
            accountDetails.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }
    }
}