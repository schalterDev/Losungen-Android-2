package de.schalter.losungen.utils

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.mockk

object FuelUtils {
    fun mockFuel(): Client {
        val client: Client = mockk()
        FuelManager.instance.client = client

        return client


//        every { fuelMockClient.executeRequest(any()).statusCode } returns 200
//        every { fuelMockClient.executeRequest(any()).responseMessage } returns "OK"
//        every { fuelMockClient.executeRequest(any()).data } returns someJson.toByteArray()
    }
}