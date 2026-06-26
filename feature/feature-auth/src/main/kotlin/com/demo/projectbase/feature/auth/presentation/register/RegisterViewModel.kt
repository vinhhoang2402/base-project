package com.demo.projectbase.feature.auth.presentation.register

import com.demo.projectbase.core.ui.base.BaseViewModel

class RegisterViewModel : BaseViewModel<RegisterContract.Intent, RegisterContract.State, RegisterContract.Effect>(RegisterContract.State) {

    override fun handleIntent(intent: RegisterContract.Intent) {
        when (intent) {
            RegisterContract.Intent.OpenTmdbSignup ->
                emitEffect(RegisterContract.Effect.OpenBrowser("https://www.themoviedb.org/signup"))
            RegisterContract.Intent.NavigateBack ->
                emitEffect(RegisterContract.Effect.NavigateBack)
        }
    }
}
