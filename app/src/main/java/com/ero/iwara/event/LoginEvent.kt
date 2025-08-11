package com.ero.iwara.event

import com.ero.iwara.model.session.Session

/**
 * 用户登录事件
 */
data class LoginEvent(
    val session: Session
)