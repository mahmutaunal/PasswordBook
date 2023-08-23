package com.mahmutalperenunal.passwordbook.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahmutalperenunal.passwordbook.repository.PasswordManagerRepository
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel

class CreateEditViewPasswordViewModelProviderFactory(private val repository: PasswordManagerRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateEditViewPasswordViewModel(repository) as T
    }
}