package com.demo.projectbase.feature.auth.presentation.register

import android.content.Intent
import android.net.Uri
import androidx.core.widget.doAfterTextChanged
import com.demo.projectbase.core.ui.base.BaseMviFragment
import com.demo.projectbase.feature.auth.databinding.FragmentRegisterBinding
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseMviFragment<FragmentRegisterBinding, RegisterContract.Intent, RegisterContract.State, RegisterContract.Effect>(
    FragmentRegisterBinding::inflate,
) {
    override val viewModel: RegisterViewModel by viewModel()

    override fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.handleIntent(RegisterContract.Intent.NavigateBack)
        }

        binding.etPassword.doAfterTextChanged { text ->
            viewModel.handleIntent(RegisterContract.Intent.PasswordChanged(text?.toString().orEmpty()))
        }

        binding.etConfirmPassword.doAfterTextChanged { text ->
            viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged(text?.toString().orEmpty()))
        }

        binding.btnRegister.setOnClickListener {
            viewModel.handleIntent(RegisterContract.Intent.OpenTmdbSignup)
        }

        binding.btnTmdbSignup.setOnClickListener {
            viewModel.handleIntent(RegisterContract.Intent.OpenTmdbSignup)
        }
    }

    override fun renderState(state: RegisterContract.State) {
        binding.tilPassword.setErrorIfChanged(state.passwordError?.let { getString(it) })
        binding.tilConfirmPassword.setErrorIfChanged(state.confirmPasswordError?.let { getString(it) })
        binding.btnRegister.isEnabled = state.isRegisterEnabled
    }

    override fun handleEffect(effect: RegisterContract.Effect) {
        when (effect) {
            is RegisterContract.Effect.OpenBrowser -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
                startActivity(intent)
            }
            RegisterContract.Effect.NavigateBack -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            RegisterContract.Effect.ShowTermsDialog -> {
                showTermsDialog()
            }
        }
    }

    private fun showTermsDialog() {
        val dialog = com.demo.projectbase.core.ui.dialog.TermsDialog(requireContext(), object : com.demo.projectbase.core.ui.dialog.TermsDialog.Listener {
            override fun onAccept() {
                // Potential leak and NPE after onDestroyView
                binding.btnRegister.isEnabled = false
            }
        })
        dialog.show()
    }

    private fun TextInputLayout.setErrorIfChanged(message: String?) {
        if (error != message) error = message
    }
}
