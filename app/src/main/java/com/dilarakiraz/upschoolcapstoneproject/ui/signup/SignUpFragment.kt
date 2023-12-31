package com.dilarakiraz.upschoolcapstoneproject.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentSignUpBinding
import com.dilarakiraz.upschoolcapstoneproject.utilities.PhoneTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val binding by viewBinding(FragmentSignUpBinding::bind)

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        initObservers()
    }

    private fun initObservers() = with(binding) {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SignUpState.Loading -> progressBar.visible()
                is SignUpState.GoToHome -> findNavController().navigate(R.id.signUpToHome)
                is SignUpState.Error -> {
                    progressBar.gone()
                    showErrorMessage(state.throwable.message ?: getString(R.string.error_unknown))
                }
            }
        }
    }

    private fun setupListeners() = with(binding) {
        btnSignUp.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString().trim()
            val nickname = etNickname.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.signUpAndSaveUserData(email, password, nickname, phoneNumber)
            }
        }
        icSignUpToSignIn.setOnClickListener {
            findNavController().navigate(R.id.signUpToSignIn)
        }
        etPhoneNumber.run {
            inputType = EditorInfo.TYPE_CLASS_PHONE
            addTextChangedListener(PhoneTextWatcher(this))
        }
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}