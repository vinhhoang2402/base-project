package com.demo.projectbase.core.ui.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AutoClear<T> : ReadWriteProperty<Fragment, T?> {
    private var value: T? = null
    private var lifecycleObserver: LifecycleEventObserver? = null

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>,
    ): T? {
        setupObserverIfNeeded(thisRef)
        return value
    }

    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>,
        value: T?,
    ) {
        setupObserverIfNeeded(thisRef)
        this.value = value
    }

    private fun setupObserverIfNeeded(fragment: Fragment) {
        if (lifecycleObserver != null) return

        lifecycleObserver =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    value = null
                }
            }
        fragment.viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver!!)
    }
}

fun <T> autoClear() = AutoClear<T>()
