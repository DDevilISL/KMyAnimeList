package me.ddevil.mal.request

import me.ddevil.mal.MALUser
import me.ddevil.mal.exception.InvalidCredentialsException
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest

interface Request<out T> {
    @Throws(InvalidCredentialsException::class)
    fun execute(client: HttpClient): T
}

abstract class AbstractRequest<out T>(val user: MALUser) : Request<T> {
    abstract fun createRequest(): HttpUriRequest
    @Throws(InvalidCredentialsException::class)
    override fun execute(client: HttpClient): T {
        val request = createRequest()
        request.addHeader("Authorization", "Basic ${user.authorization}")
        val result = client.execute(request)
        if (result.statusLine.statusCode == 401) {
            throw InvalidCredentialsException()
        }
        return execute(result)
    }

    abstract fun execute(result: HttpResponse): T

}