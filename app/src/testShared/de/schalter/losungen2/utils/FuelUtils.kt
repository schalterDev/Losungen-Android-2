package de.schalter.losungen2.utils

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import io.mockk.mockk

object FuelUtils {
    fun mockFuel(): Client {
        val client: Client = mockk()
        FuelManager.instance.client = client

        return client
    }
}