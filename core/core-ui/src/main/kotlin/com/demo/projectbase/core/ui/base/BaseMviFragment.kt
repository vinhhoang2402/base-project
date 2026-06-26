package com.demo.projectbase.core.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch

abstract class BaseMviFragment<VB : ViewBinding, Intent, State, Effect>(
    inflate: (android.view.LayoutInflater, android.view.ViewGroup?, Boolean) -> VB,
) : BaseFragment<VB>(inflate) {

    protected abstract val viewModel: BaseViewModel<Intent, State, Effect>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { renderState(it) } }
                launch { viewModel.effect.collect { handleEffect(it) } }
                launch { viewModel.baseEffect.collect { handleBaseEffect(it) } }
            }
        }
    }

    protected abstract fun renderState(state: State)
    protected abstract fun handleEffect(effect: Effect)

    protected open fun handleBaseEffect(effect: BaseEffect) {
        when (effect) {
            is BaseEffect.ShowError -> Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
            BaseEffect.SessionExpired -> onSessionExpired()
        }
    }

    protected open fun onSessionExpired() {}
}
