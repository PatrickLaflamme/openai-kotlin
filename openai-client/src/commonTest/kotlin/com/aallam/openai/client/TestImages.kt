package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.file.FilePath
import com.aallam.openai.api.image.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import okio.Buffer
import okio.Path.Companion.toPath
import okio.Source
import ulid.ULID
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalOpenAI::class)
class TestImages : TestOpenAI() {

    private val httpClient = HttpClient()

    @Test
    fun imageCreationURL() = runTest {
        val request = ImageCreation(
            prompt = "A cute baby sea otter", n = 2, size = ImageSize.is1024x1024
        )
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageCreationJSON() = runTest {
        val request = ImageCreation(
            prompt = "A cute baby sea otter",
            n = 2,
            size = ImageSize.is1024x1024,
        )
        val response = openAI.imageJSON(request)
        assertTrue { response.isNotEmpty() }
        println(response)
    }

    @Test
    fun imageEditURL() = runTest {
        val imagePath = writeImage(bytes = httpClient.get("https://i.imgur.com/mXFcDNB.png").body())
        val maskPath = writeImage(bytes = httpClient.get("https://i.imgur.com/D4MURbj.png").body())

        val request = ImageEdit(
            image = FilePath(imagePath),
            mask = FilePath(maskPath),
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.imageURL(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageEditJSON() = runTest {
        val imagePath = writeImage(bytes = httpClient.get("https://i.imgur.com/mXFcDNB.png").body())
        val maskPath = writeImage(bytes = httpClient.get("https://i.imgur.com/D4MURbj.png").body())

        val imageSource = httpClient.get("https://i.imgur.com/mXFcDNB.png").body<ByteArray>().asSource()
        val maskSource = httpClient.get("https://i.imgur.com/D4MURbj.png").body<ByteArray>().asSource()

        val request = ImageEdit(
            imageFilename = ULID.randomULID(),
            imageSource = imageSource,
            maskFilename = ULID.randomULID(),
            maskSource = maskSource,
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
        val response = openAI.image(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationURL() = runTest {
        val imagePath = writeImage(bytes = httpClient.get("https://i.imgur.com/iN0VFnF.png").body())

        val request = ImageVariationURL(
            image = FilePath(imagePath), n = 1, size = ImageSize.is1024x1024
        )
        val response = openAI.image(request)
        assertTrue { response.isNotEmpty() }
    }

    @Test
    fun imageVariationJSON() = runTest {
        val imagePath = writeImage(bytes = httpClient.get("https://i.imgur.com/iN0VFnF.png").body())

        val request = ImageVariationJSON(
            image = FilePath(imagePath), n = 1, size = ImageSize.is1024x1024
        )
        val response = openAI.image(request)
        assertTrue { response.isNotEmpty() }
    }

    private fun writeImage(bytes: ByteArray): String {
        val filename = "${ULID.randomULID()}.png"
        val filePath = filename.toPath()
        fileSystem.write(filePath) { write(bytes) }
        return filename
    }

    private fun ByteArray.asSource(): Source {
        val buffer = Buffer()
        buffer.write(this)
        return buffer
    }
}