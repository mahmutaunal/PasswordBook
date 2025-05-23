package com.mahmutalperenunal.passwordbook.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.data.AppDatabase
import com.mahmutalperenunal.passwordbook.data.PasswordEntity
import com.mahmutalperenunal.passwordbook.databinding.FragmentHomeBinding
import com.mahmutalperenunal.passwordbook.utils.CryptoUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val passwordAdapter = PasswordAdapter()

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            importPasswords(uri)
        }
    }

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            exportPasswords(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = passwordAdapter
        }

        binding.flabAdd.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPasswordFragment())
        }

        binding.tbHeader.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.actionExport -> {
                    exportLauncher.launch("${requireContext().resources.getString(R.string.passwords)}_${System.currentTimeMillis()}.txt")
                    true
                }

                R.id.actionImport -> {
                    importLauncher.launch(arrayOf("text/plain", "application/json"))
                    true
                }

                R.id.actionGeneratePassword -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPasswordGenerateFragment())
                    true
                }

                R.id.actionSettings -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                    true
                }

                else -> false
            }
        }

        loadPasswords()
    }

    private fun loadPasswords() {
        CoroutineScope(Dispatchers.IO).launch {
            val passwords = AppDatabase.getInstance(requireContext()).passwordDao().getAll()
            launch(Dispatchers.Main) {
                passwordAdapter.submitList(passwords)
                if (passwords.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun importPasswords(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val encryptedText = inputStream?.bufferedReader()?.use { it.readText() }
                if (encryptedText.isNullOrBlank()) return@launch

                val decryptedJson = CryptoUtils.decrypt(encryptedText)

                val passwordList = Gson().fromJson(
                    decryptedJson,
                    Array<PasswordEntity>::class.java
                ).toList()

                val dao = AppDatabase.getInstance(requireContext()).passwordDao()
                passwordList.forEach { dao.insert(it) }

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.import_completed), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "${requireContext().resources.getString(R.string.error)}: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun exportPasswords(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val passwords = AppDatabase.getInstance(requireContext()).passwordDao().getAll()
                val json = Gson().toJson(passwords)
                val encrypted = CryptoUtils.encrypt(json)

                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(encrypted.toByteArray(Charsets.UTF_8))
                }

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(R.string.exported), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "${getString(R.string.export_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}