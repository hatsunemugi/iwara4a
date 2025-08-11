package com.ero.iwara.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


/**
 * 使用 Kotlin Flow 实现的事件总线
 *
 * 提供一个全局的 SharedFlow 来发送和接收事件。
 * 通过泛型和 filter 操作来实现对特定事件类型的订阅。
 */
object FlowEventBus {

    // 使用 SupervisorJob，这样子协程失败不会影响到 EventBus 本身
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // _events 是可变的 SharedFlow，用于内部发送事件
    // replay = 0: 新的订阅者不会收到旧的事件 (类似于 EventBus 的非粘性事件)
    // extraBufferCapacity = 64: 缓冲容量，防止快速发送事件时挂起发送者
    // onBufferOverflow = BufferOverflow.DROP_OLDEST: 当缓冲满时，丢弃最旧的事件
    // 你可以根据需求调整这些参数，例如，如果需要粘性事件，可以设置 replay > 0
    private val _events = MutableSharedFlow<AppEvent>(
        replay = 0, // 对于类似 EventBus 的行为，通常不回放旧事件给新订阅者
        extraBufferCapacity = 64 // 为并发事件提供缓冲
        // onBufferOverflow = BufferOverflow.SUSPEND // 或者其他策略
    )

    /**
     * 对外暴露的只读 SharedFlow
     */
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    /**
     * 发送一个事件。
     * 事件会在 FlowEventBus 的协程作用域内被发送。
     *
     * @param event 要发送的事件，必须是 AppEvent 类型
     */
    fun post(event: AppEvent) {
        scope.launch {
            _events.emit(event)
        }
    }

    /**
     * 订阅特定类型的事件。
     *
     * 使用示例:
     *
     * CoroutineScope(Dispatchers.Main).launch {
     *   FlowEventBus.subscribe<AppEvent.UserLoggedInEvent> { event ->
     *     // 处理 UserLoggedInEvent
     *     Log.d("EventBus", "UserLoggedIn: ${event.username}")
     *   }
     * }
     * @param T 要订阅的事件的具体类型 (必须是 AppEvent 的子类型)
     * @return 返回一个 Flow，调用者需要在一个协程中 collect 这个 Flow 来开始接收事件
     * 通常，你会在一个生命周期感知的协程中收集它*/
    inline fun <reified T : AppEvent> eventsOfType(): Flow<T>
    {
        return events.filter { it is T }.map { it as T }
    }
}
/**
 * 快速触发一个事件 (AppEvent 类型)
 */
fun postFlowEvent(event: AppEvent) = FlowEventBus.post(event)

/**
 * 订阅特定类型的事件并执行操作。
 * 这个函数是一个便利的包装，用于在一个新的协程中启动订阅。
 * **注意**: 这个简化的 subscribe 函数会在提供的 CoroutineScope 中启动一个新的协程。
 * 调用者需要管理这个 scope 的生命周期以避免内存泄漏。
 * 在 Android 中，通常使用 lifecycleScope 或 viewModelScope。
 *
 * @param CoroutineScope 协程作用域，用于收集事件
 * @param onEvent 当接收到指定类型的事件时执行的回调
 */
inline fun <reified T : AppEvent> CoroutineScope.subscribe(
    crossinline onEvent: suspend (event: T) -> Unit
) {
    this.launch { // 在传入的 scope 中启动收集
        eventsOfType<T>().collect { event ->
            onEvent(event)
        }
    }
}
/**Kotlin
* @param T 要订阅的事件的具体类型 (必须是 AppEvent 的子类型)
* @return 返回一个 Flow，调用者需要在一个协程中 collect 这个 Flow 来开始接收事件。
* 通常，你会在一个生命周期感知的协程中收集它。    */
inline fun <reified T : AppEvent> eventsOfType(): Flow<T> = FlowEventBus.eventsOfType()