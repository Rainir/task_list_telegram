package ru.rainir.task_list_telegram.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.rainir.task_list_telegram.Model.Telegram;

import static ru.rainir.task_list_telegram.Command.TaskCommand.InlineKeyboardHelper.createTaskDetailsMessage;
import static ru.rainir.task_list_telegram.Command.UserCommand.StartCommand.registrationStartMassage;
import static ru.rainir.task_list_telegram.Command.UserCommand.StartCommandHandler.callbackHandler;

@Service
public class TelegramService extends TelegramBotsLongPollingApplication implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private final Telegram telegram;
    private TelegramClient telegramClient;

    public TelegramService(Telegram telegram) {
        this.telegram = telegram;
        initTelegramClient();
    }

    private void initTelegramClient() {
        telegramClient = new OkHttpTelegramClient(telegram.getBOT_TOKEN());
        log.info("Бот запущен и работает!");
    }

    @Override
    public String getBotToken() {
        return telegram.getBOT_TOKEN();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {

        if (update.hasCallbackQuery()) {
            System.out.println(update.getCallbackQuery().getFrom().getId() + ": " + update.getCallbackQuery().getData());
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getChatId() + ": " + update.getMessage().getText());
        }

        SendMessage message = consumeHandler(update);

        if (message != null) {
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке сообщения: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private SendMessage consumeHandler(Update update) {
        Long chat_id;

        if (update.hasCallbackQuery()) {
            chat_id = update.getCallbackQuery().getFrom().getId();
            return callbackHandler(chat_id, update.getCallbackQuery().getData());
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                return switch (update.getMessage().getText()) {
                    case "/start" -> registrationStartMassage(update.getMessage().getChatId());
                    case "/task" -> createTaskDetailsMessage(update.getMessage().getChatId());
                    default -> null;
                };
            } else {
                return new SendMessage(String.valueOf(
                        update.getMessage().getChatId()), update.getMessage().getText());
            }
        }
        return null;
    }
}