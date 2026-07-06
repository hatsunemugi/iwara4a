package com.ero.iwara.crash

import com.ero.iwara.event.error
import com.ero.iwara.event.route

class CrashHandler() : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        error(ex, "崩溃")
        route("log")
    }
}