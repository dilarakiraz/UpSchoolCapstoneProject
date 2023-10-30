package com.dilarakiraz.upschoolcapstoneproject.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val binding by viewBinding(FragmentSignUpBinding::bind)

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSignUp.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString().trim()
                val nickname = etNickname.text.toString()
                val phoneNumber = etPhoneNumber.text.toString()

                viewModel.checkInfo(email, password, nickname, phoneNumber)
            }
            icSignUpToSignIn.setOnClickListener {
                findNavController().navigate(R.id.signUpToSignIn)
            }
        }
        initObservers()
    }

    private fun initObservers() = with(binding) {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                SignUpState.Loading -> progressBar.visible()

                SignUpState.GoToHome -> findNavController().navigate(R.id.signUpToHome)

                is SignUpState.Error -> {
                    progressBar.gone()
                    showErrorMessage(state.throwable.message ?: getString(R.string.error_unknown))
                }
            }
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