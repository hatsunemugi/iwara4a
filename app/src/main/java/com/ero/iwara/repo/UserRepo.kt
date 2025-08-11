package com.ero.iwara.repo

import com.ero.iwara.api.IwaraApi
import com.ero.iwara.api.Response
import com.ero.iwara.model.session.Session
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.result.MCount
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun login(username: String, password: String): Response<String> = iwaraApi.login(username, password)

    suspend fun getToken(token: String): Response<String> = iwaraApi.getToken(token)

    suspend fun getSelf(session: Session): Response<Self> = iwaraApi.getSelf(session)

    suspend fun getUser(session: Session, username: String): Response<UserData> = iwaraApi.getUser(session, username)

    suspend fun getCount(session: Session): Response<MCount> = iwaraApi.getCount(session)
}