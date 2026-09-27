package com.hfut.schedule

import com.hfut.schedule.network.api.inf.GiteeService
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import java.util.UUID

class GiteeServiceRequestContractTest {

    @Test
    fun getUpdateWithoutCredentialOmitsAccessToken() {
        val request = service().getUpdate().request()

        assertFalse(request.url.queryParameterNames.contains("access_token"))
    }

    @Test
    fun getUpdateWithCredentialRetainsAccessToken() {
        val credential = UUID.randomUUID().toString()
        val request = service().getUpdate(credential, 1, 20).request()

        assertTrue(credential == request.url.queryParameter("access_token"))
    }

    private fun service(): GiteeService = Retrofit.Builder()
        .baseUrl("https://example.invalid/")
        .build()
        .create(GiteeService::class.java)
}
