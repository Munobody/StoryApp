package com.example.storyapp.view.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentProfileBinding
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.login.dataStore
import com.example.storyapp.view.utils.AuthViewModel
import com.example.storyapp.view.utils.PreferenceManager
import com.example.storyapp.view.utils.lightStatusBar

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var prefen: PreferenceManager
    private lateinit var name: String
    private lateinit var uid: String
    private lateinit var email: String
    private lateinit var lastlogin: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefen = PreferenceManager.getInstance(requireContext().dataStore)
        lightStatusBar(requireActivity().window, true)
        floatingClick()

        val authViewModel =
            ViewModelProvider(this, ProfileModelFactory(prefen))[AuthViewModel::class.java]
        authViewModel.getUserLoginName()
            .observe(viewLifecycleOwner) {
                name = it
                binding.tvName.text = name
            }
        authViewModel.getUserLoginUid()
            .observe(viewLifecycleOwner) {
                uid = it
                binding.tvUid.text = uid
            }
        authViewModel.getUserLoginEmail()
            .observe(viewLifecycleOwner) {
                email = it
                binding.tvEmail.text = email
            }
        authViewModel.getUserLastLoginSession()
            .observe(viewLifecycleOwner) {
                lastlogin = it
                binding.tvLastlogin.text = getString(R.string.LAST_LOGIN) + " $lastlogin"
            }
        binding.tvLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val alert = builder.create()
            builder
                .setTitle(getString(R.string.LOGOUT_CONFIRMATION_TITLE))
                .setMessage(getString(R.string.LOGOUT_CONFIRMATION_MESSAGE))
                .setPositiveButton(getString(R.string.LOGOUT_CONFIRMATION_YES)) { _, _ ->
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    authViewModel.deleteUserLoginSession()
                }
                .setNegativeButton(getString(R.string.LOGOUT_CONFIRMATION_CANCEL)) { _, _ ->
                    alert.cancel()
                }
                .show()
        }
    }

    private fun openInstagram() {
        try {
            val uri = Uri.parse("https://instagram.com/_u/louis_michael_")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (_: Exception) {
        }
    }

    private fun floatingClick() {
        binding.apply {
            tvReport.setOnClickListener{
                openInstagram()
            }
        }
    }
}
