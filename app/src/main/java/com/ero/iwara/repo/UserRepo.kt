package com.ero.iwara.repo

import com.ero.iwara.api.IwaraApi
import com.ero.iwara.api.Response
import com.ero.iwara.model.session.Session
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.api.result.MCount
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun login(username: String, password: String): Response<String> = iwaraApi.login(username, password)

    suspend fun getToken(token: String): Response<String> = iwaraApi.getToken(token)

    suspend fun getSelf(): Response<Self> = iwaraApi.getSelf()

    suspend fun getUser(username: String): Response<UserData> = iwaraApi.getUser( username)

    suspend fun getCount(): Response<MCount> = iwaraApi.getCount()
}