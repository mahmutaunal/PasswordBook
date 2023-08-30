package com.mahmutalperenunal.passwordbook.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.get
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mahmutalperenunal.passwordbook.databinding.PasswordBinding
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel
import com.mahmutalperenunal.passwordbook.util.CompanyListData
import kotlinx.coroutines.*


class PasswordAdapter(
    private val mContext: Context,
    private val viewModel: CreateEditViewPasswordViewModel,
    private val owner: LifecycleOwner,
    private val fragmentView: View,
    private val activity: PasswordActivity
) : RecyclerView.Adapter<PasswordAdapter.PasswordAdapterViewHolder>() {
    inner class PasswordAdapterViewHolder(val binding: PasswordBinding) : RecyclerView.ViewHolder(
        binding.root
    )


    private val differCallback = object : DiffUtil.ItemCallback<Entry>() {
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordAdapterViewHolder {
        val view = PasswordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PasswordAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private var onItemClickListener: ((Entry) -> Unit)? = null


    override fun onBindViewHolder(holder: PasswordAdapterViewHolder, position: Int) {

        val entry = differ.currentList[position]

        viewModel.getAllEntryDetails(entry.id).observe(owner) { entryDetailList ->

            holder.binding.passwordPasswordTitleTextView.setBackgroundColor(0x00000000)
            holder.binding.passwordPasswordInfoTextView.setBackgroundColor(0x00000000)
            holder.binding.passwordPasswordIconImageView.setBackgroundColor(0x00000000)

            holder.binding.passwordPasswordTitleTextView.text = entry.title
            holder.binding.passwordPasswordInfoTextView.text = entry.category

            val iconId = entry.icon
            val iconList = CompanyListData.companyListData
            for (icon in iconList) {
                if (icon.id == iconId) {
                    holder.binding.passwordPasswordIconImageView.setImageResource(icon.companyIcon)
                    break
                } else {
                    holder.binding.passwordPasswordIconImageView.setImageResource(R.drawable.cl_general_account)
                }
            }

            holder.binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(entry)
                }
            }

            holder.binding.passwordOptionsImageView.setOnClickListener {

                val popupMenu = PopupMenu(mContext, it)
                popupMenu.menuInflater.inflate(R.menu.password_options_menu, popupMenu.menu)

                if (entry.favourite == 0) {
                    popupMenu.menu[2].title = "Add to Favourites"
                } else {
                    popupMenu.menu[2].title = "Remove from Favourites"
                }

                popupMenu.show()

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.passwordOptions_edit -> {

                            val intent = Intent(
                                mContext.applicationContext,
                                CreateEditViewPasswordActivity::class.java
                            )
                            intent.putExtra("command", "edit")
                            intent.putExtra("data", entry)
                            mContext.startActivity(intent)

                        }

                        R.id.passwordOptions_delete -> {
                            AlertDialog.Builder(mContext)
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to delete this entry named as ${entry.title}")
                                .setPositiveButton("Yes") { d, _ ->
                                    for (entryDetail in entryDetailList) {
                                        viewModel.deleteEncryptedKeys(entryDetail.id)
                                    }

                                    viewModel.deleteEntryDetails(entry.id)
                                    viewModel.deleteEntry(entry)
                                    d.dismiss()
                                    Snackbar.make(
                                        fragmentView,
                                        " \"${entry.title}\" Deleted Successfully",
                                        Snackbar.LENGTH_SHORT
                                    ).show()


                                }.setNegativeButton("No") { d, _ ->
                                    d.dismiss()
                                }.create().show()

                        }

                        R.id.passwordOptions_favourites -> {

                            if (entry.favourite == 0) {

                                CoroutineScope(Dispatchers.IO).launch {
                                    entry.favourite = 1
                                    async { viewModel.upsertEntry(entry) }.await()
                                    withContext(Dispatchers.Main) {
                                        Snackbar.make(
                                            fragmentView,
                                            "Added to Favourites",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        viewModel.getAllEntries().observe(owner) { it1 ->
                                            viewModel.sortedList.postValue(it1)
                                            activity.binding.passwordNavView.setSelectionAtPosition(1, true)
                                        }
                                    }
                                }

                            } else {

                                CoroutineScope(Dispatchers.IO).launch {
                                    entry.favourite = 0
                                    async { viewModel.upsertEntry(entry) }.await()
                                    withContext(Dispatchers.Main) {
                                        Snackbar.make(
                                            fragmentView,
                                            "Removed to Favourites",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        viewModel.getAllEntries().observe(owner) { it1 ->
                                            viewModel.sortedList.postValue(it1)
                                            activity.binding.passwordNavView.setSelectionAtPosition(1, true)
                                        }
                                    }

                                }

                            }
                        }
                    }
                    popupMenu.dismiss()
                    true
                }
            }

        }


    }

    fun setOnItemClickListener(listener: (Entry) -> Unit) {
        onItemClickListener = listener
    }

}