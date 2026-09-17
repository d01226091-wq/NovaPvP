package com.novapvp.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class ServerResult(
    val success: Boolean,
    val message: String
)

object ServerConnection {

    private const val TIMEOUT = 5000

    private val MAGIC = byteArrayOf(
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

    suspend fun connect(
        host: String,
        port: Int
    ): ServerResult =
        withContext(Dispatchers.IO) {

            try {

                val address =
                    InetAddress.getByName(host)

                DatagramSocket().use { socket ->

                    socket.soTimeout = TIMEOUT

                    val packet =
                        createPing()

                    val sendPacket =
                        DatagramPacket(
                            packet,
                            packet.size,
                            address,
                            port
                        )

                    socket.send(sendPacket)

                    val buffer =
                        ByteArray(4096)

                    val receivePacket =
                        DatagramPacket(
                            buffer,
                            buffer.size
                        )

                    socket.receive(
                        receivePacket
                    )

                    if (receivePacket.length == 0) {
                        return@withContext ServerResult(
                            false,
                            "Пустой ответ сервера"
                        )
                    }

                    val response =
                        buffer.copyOf(
                            receivePacket.length
                        )

                    val packetId =
                        response[0].toInt() and 0xFF

                    if (packetId == 0x1C) {

                        val info =
                            parsePong(response)

                        return@withContext ServerResult(
                            true,
                            buildString {

                                append(
                                    "✓ Сервер найден\n"
                                )

                                if (
                                    info.isNotEmpty()
                                ) {
                                    append(info)
                                } else {
                                    append(
                                        "$host:$port"
                                    )
                                }
                            }
                        )
                    }

                    ServerResult(
                        false,
                        "Получен неизвестный ответ: 0x${packetId.toString(16)}"
                    )
                }

            } catch (
                e: java.net.UnknownHostException
            ) {

                ServerResult(
                    false,
                    "Сервер не найден"
                )

            } catch (
                e: java.net.SocketTimeoutException
            ) {

                ServerResult(
                    false,
                    "Сервер не ответил"
                )

            } catch (e: Exception) {

                ServerResult(
                    false,
                    "Ошибка: ${e.message ?: "Unknown error"}"
                )
            }
        }

    private fun createPing(): ByteArray {

        val buffer =
            ByteBuffer.allocate(33)
                .order(ByteOrder.BIG_ENDIAN)

        buffer.put(0x01)

        buffer.putLong(
            System.currentTimeMillis()
        )

        buffer.put(MAGIC)

        buffer.putLong(
            System.currentTimeMillis()
        )

        return buffer.array()
    }

    private fun parsePong(
        data: ByteArray
    ): String {

        if (data.size < 35) {
            return ""
        }

        var offset = 33

        if (offset + 2 > data.size) {
            return ""
        }

        val length =
            ((data[offset].toInt() and 0xFF) shl 8) or
            (data[offset + 1].toInt() and 0xFF)

        offset += 2

        if (
            length <= 0 ||
            offset + length > data.size
        ) {
            return ""
        }

        return try {

            val identifier =
                String(
                    data,
                    offset,
                    length,
                    Charsets.UTF_8
                )

            parseIdentifier(identifier)

        } catch (_: Exception) {
            ""
        }
    }

    private fun parseIdentifier(
        identifier: String
    ): String {

        val parts =
            identifier.split(";")

        if (parts.size < 2) {
            return identifier
        }

        val name =
            parts.getOrNull(1) ?: ""

        val version =
            parts.getOrNull(3) ?: ""

        val online =
            parts.getOrNull(4) ?: ""

        val max =
            parts.getOrNull(5) ?: ""

        return buildString {

            if (name.isNotEmpty()) {
                append("Название: $name\n")
            }

            if (version.isNotEmpty()) {
                append("Версия: $version\n")
            }

            if (
                online.isNotEmpty() &&
                max.isNotEmpty()
            ) {
                append(
                    "Игроки: $online/$max"
                )
            }
        }
    }
}
