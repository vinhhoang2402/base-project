package com.demo.projectbase.core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.projectbase.core.network.ErrorMapper
import com.demo.projectbase.core.network.NetworkException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Intent, State, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    private val _baseEffect = Channel<BaseEffect>(Channel.BUFFERED)
    val baseEffect: Flow<BaseEffect> = _baseEffect.receiveAsFlow()

    protected fun updateState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    protected fun showBaseError(throwable: Throwable) {
        viewModelScope.launch { _baseEffect.send(BaseEffect.ShowError(ErrorMapper.getMessage(throwable))) }
    }

    protected fun <T> handleApiResult(
        result: Result<T>,
        onUnauthorized: (() -> Unit)? = null,
        onHttpError: ((NetworkException.HttpError) -> Unit)? = null,
        onSuccess: (T) -> Unit,
    ) {
        result.fold(
            onSuccess = onSuccess,
            onFailure = { throwable ->
                when (throwable) {
                    is NetworkException.Unauthorized ->
                        if (onUnauthorized != null) onUnauthorized()
                        else viewModelScope.launch { _baseEffect.send(BaseEffect.SessionExpired) }
                    is NetworkException.HttpError -> onHttpError?.invoke(throwable) ?: showBaseError(throwable)
                    else -> showBaseError(throwable)
                }
            },
        )
    }

    abstract fun handleIntent(intent: Intent)
}
