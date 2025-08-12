package com.ero.iwara.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.subscribe
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() { // 'open' 表示可以被继承

    // 通用的 Toast 消息事件
    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init{
        viewModelScope.subscribe<AppEvent.GenericMessageEvent> { it -> it
            viewModelScope.launch {
                _message.emit(it.message)
            }
        }
    }
}