package com.mahmutalperenunal.passwordbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.passwordbook.databinding.LogoCompanyBinding
import com.mahmutalperenunal.passwordbook.util.CompanyList


class LogoCompanyChooserAdapter(private val companyList: List<CompanyList>) :
    RecyclerView.Adapter<LogoCompanyChooserAdapter.LogoCompanyChooserAdapterViewHolder>() {
    inner class LogoCompanyChooserAdapterViewHolder(val binding: LogoCompanyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogoCompanyChooserAdapterViewHolder {
        val view = LogoCompanyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogoCompanyChooserAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    private var onItemClickListener: ((CompanyList) -> Unit)? = null

    override fun onBindViewHolder(holder: LogoCompanyChooserAdapterViewHolder, position: Int) {
        val company = companyList[position]
        holder.binding.logoCompanyCompanyNameTextView.text = company.companyName
        holder.binding.logoCompanyCompanyLogoImageView.setImageResource(company.companyIcon)

        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(company)
            }
        }

    }

    fun setOnItemClickListener(listener: (CompanyList) -> Unit) {
        onItemClickListener = listener
    }

}