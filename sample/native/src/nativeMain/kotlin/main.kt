import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.image.ImageCreationURL
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI
import kotlinx.cinterop.toKString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import platform.posix.getenv

@OptIn(ExperimentalOpenAI::class)
fun main(): Unit = runBlocking {

    val apiKey = getenv("OPENAI_API_KEY")?.toKString()
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token)

    println("> Getting available models...")
    openAI.models().forEach(::println)

    println("\n> Getting ada model...")
    val ada = openAI.model(modelId = ModelId("text-ada-001"))
    println(ada)

    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(
        model = ada.id,
        prompt = "Somebody once told me the world is gonna roll me"
    )
    openAI.completion(completionRequest).choices.forEach(::println)

    println("\n>️ Creating completion stream...")
    openAI.completions(completionRequest)
        .onEach { print(it.choices[0].text) }
        .onCompletion { println() }
        .launchIn(this)
        .join()

    println("\n> Read files...")
    val files = openAI.files()
    println(files)

    println("\n> Create moderations...")
    val moderation = openAI.moderations(
        request = ModerationRequest(
            input = "I want to kill them."
        )
    )
    println(moderation)

    println("\n> Create images...")
    val images = openAI.image(
        creation = ImageCreationURL(
            prompt = "A cute baby sea otter",
            n = 2,
            size = ImageSize.is1024x1024
        )
    )
    println(images)
}
