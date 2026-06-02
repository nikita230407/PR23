package com.example.pr23.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Fragment.collectWhenStarted(block: suspend CoroutineScope.() -> Unit) {
    // Универсальный helper для безопасного сбора StateFlow:
    // подписка активна только пока View фрагмента находится в STARTED.
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}
