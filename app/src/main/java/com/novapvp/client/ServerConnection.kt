package com.novapvp.client

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ServerResult(
    val success: Boolean,
    val message: String
)

object ServerConnection {

    suspend fun ping(
        host: String,
        port: Int
    ): ServerResult = withContext(Dispatchers.IO) {

        try {
            val address = InetAddress.getByName(host)

            DatagramSocket().use { socket ->

                socket.soTimeout = 3000

                val buffer = ByteBuffer
                    .allocate(33)
                    .order(ByteOrder.BIG_ENDIAN)

                // RakNet Unconnected Ping
                buffer.put(0x01.toByte())

                buffer.putLong(
                    System.currentTimeMillis()
                )

                // RakNet magic
                buffer.put(
                    byteArrayOf(
                        0x00,
                        0xFF.toByte(),
                        0xFF.toByte(),
                        0x00,
                        0xFE.toByte(),
                        0xFE.toByte(),
                        0xFE.toByte(),
                        0xFE.toByte(),
                        0xFD.toByte(),
                        0xFD.toByte(),
                        0xFD.toByte(),
                        0xFD.toByte(),
                        0x12,
                        0x34,
                        0x56,
                        0x78
                    )
                )

                val data = buffer.array()

                val packet = DatagramPacket(
                    data,
                    data.size,
                    address,
                    port
                )

                socket.send(packet)

                val responseBuffer = ByteArray(2048)

                val response = DatagramPacket(
                    responseBuffer,
                    responseBuffer.size
                )

                socket.receive(response)

                ServerResult(
                    true,
                    "Сервер отвечает!\n" +
                    "${response.address.hostAddress}:${response.port}"
                )
            }

        } catch (e: Exception) {

            ServerResult(
                false,
                "Не удалось подключиться:\n${e.message ?: "Неизвестная ошибка"}"
            )
        }
    }
}
