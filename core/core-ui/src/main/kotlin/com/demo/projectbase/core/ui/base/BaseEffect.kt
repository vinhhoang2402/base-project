package com.demo.projectbase.core.ui.base

sealed class BaseEffect {
    data class ShowError(val message: String) : BaseEffect()
    data object SessionExpired : BaseEffect()
}
