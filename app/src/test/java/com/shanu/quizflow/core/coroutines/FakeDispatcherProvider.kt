package com.shanu.quizflow.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

class FakeDispatcherProvider(dispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val main: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}
