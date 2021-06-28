package com.futuremind.koru

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SuspendWrapper<T>(
    private val scopeProvider: ScopeProvider?,
    private val suspender: suspend () -> T
) {

    init {
        freeze()
    }

    fun subscribe(
        onSuccess: (item: T) -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = subscribe(
        scope = scopeProvider?.scope
            ?: throw IllegalArgumentException("To use implicit scope, you have to provide it via @ToNativeClass.launchOnScope and @ExportedScopeProvider."),
        onSuccess = onSuccess,
        onThrow = onThrow
    ).freeze()

    fun subscribe(
        scope: CoroutineScope,
        onSuccess: (item: T) -> Unit,
        onThrow: (error: Throwable) -> Unit
    ): Job = scope.launch {
        try {
            onSuccess(suspender().freeze())
        } catch (error: Throwable) {
            onThrow(error.freeze())
        }
    }.freeze()

}