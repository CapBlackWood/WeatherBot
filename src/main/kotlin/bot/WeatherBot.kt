package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import data.remote.API_KEY
import data.remote.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val IS_DAY = 1

private const val GIF_WAITING_URL = "https://tenor.com/ru/view/waiting-waiting-patiently-waiting-for-you-waiting-on-you-gif-15489516379864441176"
private const val BOT_ANSWER_TIMEOUT = 30
private const val BOT_TOKEN = "6813495714:AAHpI4VqyisnaNBSfp87CzD3KQXGDaCJyVE"


class WeatherBot(private val weatherRepository: WeatherRepository) {

    private lateinit var country: String
    private var _chatId: ChatId? = null
    private val chatId by lazy { requireNotNull(_chatId) }


    fun createBot(): Bot {
        return bot {
            timeout = BOT_ANSWER_TIMEOUT
            token = BOT_TOKEN
            logLevel = LogLevel.Error

            dispatch {
                setUpCommands()
                setUpCallbacks()
            }
        }
    }

    private fun Dispatcher.setUpCallbacks() {
        callbackQuery(callbackData = "getMyLocation") {
            bot.sendMessage(
                chatId = chatId,
                text = "Отправь мне свою локацию"
            )
            location {
                CoroutineScope(Dispatchers.IO).launch {
                    val userCountry = weatherRepository.getReverseGeoCodingCountryName(
                        location.latitude.toString(), location.longitude.toString(),
                        "json"
                    ).address.country

                    country = userCountry

                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = " Да, верно",
                                callbackData = "yes_label"
                            )
                        )
                    )

                    bot.sendMessage(
                        chatId = chatId,
                        text = "Твой город - ${country}, верно? \n Если неверно, скинь локацию еще раз",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }
            }
        }

        callbackQuery(callbackData = "enterManually") {
            bot.sendMessage(
                chatId = chatId,
                text = "Хорошо, введи свой город."
            )
            message(Filter.Text) {
                country = message.text.toString()

                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = " Да, верно",
                            callbackData = "yes_label"
                        )
                    )
                )

                bot.sendMessage(
                    chatId = chatId,
                    text = "Твой город - ${country}, верно? \n Если неверно, введи свой город еще раз.",
                    replyMarkup = inlineKeyboardMarkup
                )
            }
        }

        callbackQuery(callbackData = "yes_label"){
            bot.apply {
                sendAnimation(
                    chatId = chatId,
                    animation = TelegramFile.ByUrl(GIF_WAITING_URL)
                )
                bot.sendMessage(
                    chatId = chatId,
                    text = "Узнаем вашу погоду..."
                )
                sendChatAction(
                    chatId = chatId,
                    action = ChatAction.TYPING
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                val currentWeather = weatherRepository.getCurrentWeather(
                    apiKey = API_KEY,
                    countryName = country,
                    airQualityData = "no"
                )
                bot.sendMessage(
                    chatId = chatId,
                    text = """
                        ☁ Облачность: ${currentWeather.current.cloud}
                            🌡 Температура (градусы): ${currentWeather.current.tempDegrees}
                            🙎 ‍Ощущается как: ${currentWeather.current.feelsLikeDegrees}
                            💧 Влажность: ${currentWeather.current.humidity}
                            🌪 Направление ветра: ${currentWeather.current.windDirection}
                            🧭 Давление: ${currentWeather.current.pressureIn}
                            🌓 Сейчас день? ${if (currentWeather.current.isDay == IS_DAY) "Да" else "Нет"}
                    """.trimIndent()
                )

                bot.sendMessage(
                    chatId = chatId,
                    text = "Если вы хотите запросить погоду еще раз, \n воспользуйтесь командой /weather"
                )
                country = ""
            }
        }
    }

    private fun Dispatcher.setUpCommands() {
        command("start") {
            _chatId = ChatId.fromId(message.chat.id)
            bot.sendMessage(
                chatId = chatId,
                text = "Привет! Я бот, умеющий отображать погоду! \n Для запуска бота введи команду /weather"
            )
        }
        command("weather") {
            val inLineKeyboardMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Определить мой город (для мобильных устройств)",
                        callbackData = "getMyLocation"
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Ввести город вручную",
                        callbackData = "enterManually"
                    )
                )
            )

            bot.sendMessage(
                chatId = chatId,
                text = "Для того, чтобы я смог отправить тебе погоду, \n мне нужно знать твой город.",
                replyMarkup = inLineKeyboardMarkup
            )
        }
    }
}

