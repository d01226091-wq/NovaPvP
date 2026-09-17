package com.novapvp.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicLong

data class ServerResult(
    val success: Boolean,
    val message: String,
    val host: String = "",
    val port: Int = 0,
    val serverName: String = "",
    val motd: String = "",
    val playersOnline: Int = -1,
    val playersMax: Int = -1,
    val protocol: Int = -1
)

object ServerConnection {

    private const val TIMEOUT = 5000
    private const val MTU = 1492

    /*
     * RakNet magic:
     * 00 FF FF 00 FE FE FE FE
     * FD FD FD FD 12 34 56 78
     */
    private val RAKNET_MAGIC = byteArrayOf(
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

    private val guidCounter =
        AtomicLong(System.currentTimeMillis())

    /**
     * Проверка Bedrock/RakNet сервера.
     *
     * Это безопасный первый этап подключения:
     *
     * 1. DNS
     * 2. UDP
     * 3. Unconnected Ping
     * 4. Unconnected Pong
     * 5. Чтение MOTD/server information
     * 6. Open Connection Request 1
     * 7. Open Connection Reply 1
     * 8. Open Connection Request 2
     * 9. Open Connection Reply 2
     */
    suspend fun connect(
        host: String,
        port: Int = 19132
    ): ServerResult = withContext(Dispatchers.IO) {

        if (host.isBlank()) {
            return@withContext ServerResult(
                false,
                "IP сервера не указан"
            )
        }

        if (port !in 1..65535) {
            return@withContext ServerResult(
                false,
                "Неверный порт"
            )
        }

        try {
            val address = InetAddress.getByName(host)

            DatagramSocket().use { socket ->

                socket.soTimeout = TIMEOUT

                val guid = guidCounter.incrementAndGet()

                // ---------------------------------------------------------
                // STEP 1 — RakNet Unconnected Ping
                // ---------------------------------------------------------

                val pingPacket = createUnconnectedPing(guid)

                send(
                    socket,
                    pingPacket,
                    address,
                    port
                )

                val pong = receive(socket)

                    ?: return@withContext ServerResult(
                        false,
                        "Сервер не ответил на RakNet Ping",
                        host,
                        port
                    )

                if (pong.isEmpty() ||
                    (pong[0].toInt() and 0xFF) != 0x1C
                ) {
                    return@withContext ServerResult(
                        false,
                        "Получен неизвестный RakNet-пакет",
                        host,
                        port
                    )
                }

                val serverInfo = parseUnconnectedPong(pong)

                // ---------------------------------------------------------
                // STEP 2 — Open Connection Request 1
                // ---------------------------------------------------------

                val request1 = createOpenConnectionRequest1()

                send(
                    socket,
                    request1,
                    address,
                    port
                )

                val reply1 = receive(socket)

                    ?: return@withContext ServerResult(
                        false,
                        "Нет ответа Open Connection Reply 1",
                        host,
                        port
                    )

                if (reply1[0].toInt() and 0xFF != 0x06) {
                    return@withContext ServerResult(
                        false,
                        "Сервер отклонил Open Connection Request 1",
                        host,
                        port
                    )
                }

                val mtu = parseMtu(reply1).coerceIn(
                    576,
                    MTU
                )

                // ---------------------------------------------------------
                // STEP 3 — Open Connection Request 2
                // ---------------------------------------------------------

                val request2 = createOpenConnectionRequest2(
                    address,
                    port,
                    guid,
                    mtu
                )

                send(
                    socket,
                    request2,
                    address,
                    port
                )

                val reply2 = receive(socket)

                    ?: return@withContext ServerResult(
                        false,
                        "Нет ответа Open Connection Reply 2",
                        host,
                        port
                    )

                if (reply2[0].toInt() and 0xFF != 0x08) {
                    return@withContext ServerResult(
                        false,
                        "Сервер отклонил Open Connection Request 2",
                        host,
                        port
                    )
                }

                return@withContext ServerResult(
                    success = true,
                    message = buildString {
                        append("✓ RakNet соединение установлено\n")
                        append("$host:$port\n")

                        if (serverInfo.motd.isNotEmpty()) {
                            append(serverInfo.motd)
                        }

                        append("\nMTU: $mtu")
                    },
                    host = host,
                    port = port,
                    serverName = serverInfo.name,
                    motd = serverInfo.motd,
                    playersOnline = serverInfo.online,
                    playersMax = serverInfo.max,
                    protocol = serverInfo.protocol
                )
            }

        } catch (e: Exception) {

            ServerResult(
                success = false,
                message = when (e) {
                    is java.net.UnknownHostException ->
                        "Не удалось найти сервер: $host"

                    is java.net.SocketTimeoutException ->
                        "Время ожидания сервера истекло"

                    else ->
                        "Ошибка подключения: ${e.message ?: "Unknown error"}"
                },
                host = host,
                port = port
            )
        }
    }

    /**
     * Совместимость со старым MainActivity.
     */
    suspend fun ping(
        host: String,
        port: Int
    ): ServerResult {
        return connect(host, port)
    }

    // ================================================================
    // RakNet UNCONNECTED PING
    // ================================================================

    private fun createUnconnectedPing(
        guid: Long
    ): ByteArray {

        val output = ByteArrayOutputStream()

        output.write(0x01)

        writeLongBE(
            output,
            System.currentTimeMillis()
        )

        output.write(RAKNET_MAGIC)

        writeLongBE(
            output,
            guid
        )

        return output.toByteArray()
    }

    // ================================================================
    // Open Connection Request 1
    // ================================================================

    private fun createOpenConnectionRequest1(): ByteArray {

        /*
         * RakNet:
         *
         * 05
         * Magic
         * Protocol version
         * Padding
         */

        val size = MTU - 18

        val output = ByteArrayOutputStream()

        output.write(0x05)

        output.write(RAKNET_MAGIC)

        /*
         * Bedrock commonly uses RakNet protocol 11.
         * The actual supported protocol can differ by server/version.
         */
        output.write(11)

        repeat(size.coerceAtLeast(0)) {
            output.write(0)
        }

        return output.toByteArray()
    }

    // ================================================================
    // Open Connection Request 2
    // ================================================================

    private fun createOpenConnectionRequest2(
        address: InetAddress,
        port: Int,
        guid: Long,
        mtu: Int
    ): ByteArray {

        val output = ByteArrayOutputStream()

        output.write(0x07)

        output.write(RAKNET_MAGIC)

        writeRakNetAddress(
            output,
            address,
            port
        )

        writeShortBE(
            output,
            mtu
        )

        writeLongBE(
            output,
            guid
        )

        return output.toByteArray()
    }

    // ================================================================
    // RakNet address
    // ================================================================

    private fun writeRakNetAddress(
        output: ByteArrayOutputStream,
        address: InetAddress,
        port: Int
    ) {

        val bytes = address.address

        if (bytes.size == 4) {

            output.write(4)

            for (b in bytes) {
                output.write(
                    (b.toInt() xor 0xFF) and 0xFF
                )
            }

        } else {

            /*
             * IPv6 support.
             */

            output.write(6)

            writeShortBE(
                output,
                0
            )

            writeShortBE(
                output,
                0
            )

            writeShortBE(
                output,
                0
            )

            writeShortBE(
                output,
                0
            )

            output.write(bytes)

        }

        writeShortBE(
            output,
            port
        )
    }

    // ================================================================
    // Receive
    // ================================================================

    private fun receive(
        socket: DatagramSocket
    ): ByteArray? {

        val buffer = ByteArray(4096)

        val packet = DatagramPacket(
            buffer,
            buffer.size
        )

        return try {

            socket.receive(packet)

            packet.data.copyOf(
                packet.length
            )

        } catch (_: java.net.SocketTimeoutException) {

            null
        }
    }

    // ================================================================
    // Send
    // ================================================================

    private fun send(
        socket: DatagramSocket,
        data: ByteArray,
        address: InetAddress,
        port: Int
    ) {

        val packet = DatagramPacket(
            data,
            data.size,
            address,
            port
        )

        socket.send(packet)
    }

    // ================================================================
    // Parse Unconnected Pong
    // ================================================================

    private fun parseUnconnectedPong(
        data: ByteArray
    ): ParsedServerInfo {

        /*
         * Pong:
         *
         * 1 byte ID = 1C
         * 8 bytes ping time
         * 8 bytes server GUID
         * 16 bytes magic
         * 2 bytes string length
         * server identifier string
         */

        if (data.size < 35) {
            return ParsedServerInfo()
        }

        var offset = 1

        offset += 8
        offset += 8
        offset += 16

        if (offset + 2 > data.size) {
            return ParsedServerInfo()
        }

        val length =
            ((data[offset].toInt() and 0xFF) shl 8) or
            (data[offset + 1].toInt() and 0xFF)

        offset += 2

        if (length <= 0 ||
            offset + length > data.size
        ) {
            return ParsedServerInfo()
        }

        val text = try {
            String(
                data,
                offset,
                length,
                Charsets.UTF_8
            )
        } catch (_: Exception) {
            ""
        }

        return parseServerIdentifier(text)
    }

    // ================================================================
    // Server identifier
    // ================================================================

    private fun parseServerIdentifier(
        identifier: String
    ): ParsedServerInfo {

        /*
         * Typical Bedrock server identifier:
         *
         * MCPE;
         * Server Name;
         * Protocol;
         * Version;
         * Online;
         * Max;
         * ...
         */

        if (identifier.isBlank()) {
            return ParsedServerInfo()
        }

        val parts = identifier.split(";")

        if (parts.size < 2) {
            return ParsedServerInfo(
                name = identifier,
                motd = identifier
            )
        }

        var protocol = -1
        var online = -1
        var max = -1

        if (parts.size > 2) {
            protocol = parts[2].toIntOrNull() ?: -1
        }

        if (parts.size > 4) {
            online = parts[4].toIntOrNull() ?: -1
        }

        if (parts.size > 5) {
            max = parts[5].toIntOrNull() ?: -1
        }

        return ParsedServerInfo(
            name = parts.getOrNull(1) ?: "",
            motd = parts.getOrNull(1) ?: "",
            online = online,
            max = max,
            protocol = protocol
        )
    }

    // ================================================================
    // Parse MTU
    // ================================================================

    private fun parseMtu(
        data: ByteArray
    ): Int {

        /*
         * Reply 1:
         *
         * ID
         * Magic
         * Server GUID
         * Security
         * MTU
         */

        if (data.size < 28) {
            return MTU
        }

        val index = data.size - 2

        return ((data[index].toInt() and 0xFF) shl 8) or
               (data[index + 1].toInt() and 0xFF)
    }

    // ================================================================
    // Binary helpers
    // ================================================================

    private fun writeLongBE(
        output: ByteArrayOutputStream,
        value: Long
    ) {

        for (shift in 56 downTo 0 step 8) {
            output.write(
                ((value shr shift) and 0xFF).toInt()
            )
        }
    }

    private fun writeShortBE(
        output: ByteArrayOutputStream,
        value: Int
    ) {

        output.write(
            (value shr 8) and 0xFF
        )

        output.write(
            value and 0xFF
        )
    }

    // ================================================================
    // Internal server data
    // ================================================================

    private data class ParsedServerInfo(
        val name: String = "",
        val motd: String = "",
        val online: Int = -1,
        val max: Int = -1,
        val protocol: Int = -1
    )
}
