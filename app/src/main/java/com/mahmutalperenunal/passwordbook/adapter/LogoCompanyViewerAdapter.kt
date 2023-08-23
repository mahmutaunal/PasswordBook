package com.mahmutalperenunal.passwordbook.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.entities.EntryDetail
import com.mahmutalperenunal.passwordbook.databinding.LayoutViewAccountInfoBinding
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel


class LogoCompanyViewerAdapter(
    private val viewModel: CreateEditViewPasswordViewModel,
    private val owner: LifecycleOwner,
    private val mainView: View,
    private val mContext: Context,
    private val securityClass: EncryptionDecryption
) : RecyclerView.Adapter<LogoCompanyViewerAdapter.LogoCompanyViewerAdapterViewHolder>() {
    inner class LogoCompanyViewerAdapterViewHolder(val binding: LayoutViewAccountInfoBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )

    private val differCallback = object : DiffUtil.ItemCallback<EntryDetail>() {
        override fun areItemsTheSame(oldItem: EntryDetail, newItem: EntryDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EntryDetail, newItem: EntryDetail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogoCompanyViewerAdapterViewHolder {
        val view = LayoutViewAccountInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogoCompanyViewerAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: LogoCompanyViewerAdapterViewHolder, position: Int) {
        val entry = differ.currentList[position]

        viewModel.getAllEncryptedKeys(entry.id).observe(owner) { encryptedKeyList ->
            val encryptedKey = encryptedKeyList[0]
            val decryptedData = securityClass.decrypt(
                entry.detailContent,
                encryptedKey.emdKey,
                securityClass.getKey()
            )

            holder.binding.viewAccountInfoInfoTypeTextView.text = entry.detailType
            holder.binding.tvInfoContent.text = decryptedData
            setEntryDetailIcon(entry.detailType, holder.binding.viewAccountInfoInfoIconImageView)

            holder.binding.viewAccountInfoCopyInfoImageView.setOnClickListener {

                val clipboard: ClipboardManager? = getSystemService(
                    mContext,
                    ClipboardManager::class.java
                )
                val clip = ClipData.newPlainText(entry.detailType, decryptedData)
                clipboard?.setPrimaryClip(clip)

                Snackbar.make(mainView, "${entry.detailType} copied", Snackbar.LENGTH_SHORT).show()
            }

            if (entry.detailType == "Password") {
                holder.binding.viewAccountInfoPasswordToggleInfoImageView.visibility = View.VISIBLE

                var visiblePassword = false
                holder.binding.tvInfoContent.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                holder.binding.viewAccountInfoPasswordToggleInfoImageView.setImageResource(R.drawable.ic_visibility_off)

                holder.binding.viewAccountInfoPasswordToggleInfoImageView.setOnClickListener {
                    if (!visiblePassword) {
                        holder.binding.tvInfoContent.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        holder.binding.viewAccountInfoPasswordToggleInfoImageView.setImageResource(R.drawable.ic_visibility_on)
                        visiblePassword = true
                    } else {
                        holder.binding.tvInfoContent.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        holder.binding.viewAccountInfoPasswordToggleInfoImageView.setImageResource(R.drawable.ic_visibility_off)
                        visiblePassword = false
                    }
                }
            }

        }

    }


    private fun setEntryDetailIcon(type: String, img: ImageView) {
        when (type) {
            "Username" -> img.setImageResource(R.drawable.ic_username_info)
            "Email" -> img.setImageResource(R.drawable.ic_mail_info)
            "Phone Number" -> img.setImageResource(R.drawable.ic_phone_info)
            "Password" -> img.setImageResource(R.drawable.ic_password_change_info)
            "Website" -> img.setImageResource(R.drawable.ic_website_info)
            "Notes" -> img.setImageResource(R.drawable.ic_note_info)
        }
    }

}