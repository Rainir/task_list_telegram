package ru.rainir.task_list_telegram.Command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rainir.task_list_telegram.Command.TaskCommand.TaskHandler;
import ru.rainir.task_list_telegram.Command.UserCommand.UserHandler;
import ru.rainir.task_list_telegram.Service.TaskApiService;
import ru.rainir.task_list_telegram.Service.TelegramApiService;

@Component
public class GlobalHandler {
    private final UserHandler userHandler;
    private final TaskHandler taskHandler;

    public GlobalHandler(UserHandler userHandler, TaskHandler taskHandler) {
        this.userHandler = userHandler;
        this.taskHandler = taskHandler;
    }

    public SendMessage handler(Update update, TelegramApiService telegramApiService, TaskApiService taskApiService) {
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().startsWith("callbackUser")) {
                return userHandler.userHandler(update, telegramApiService, taskApiService);
            }
            if (update.getCallbackQuery().getData().startsWith("callbackTask")) {
                return taskHandler.taskHandler(update, taskApiService);
            }
        }
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            if (message.equals("/start") || message.equals("/profile") ||  message.startsWith("/register")) {
                return userHandler.userHandler(update, telegramApiService, taskApiService);
            } else {
                return taskHandler.taskHandler(update, taskApiService);
            }
        }
        return new SendMessage(update.getMessage().getChatId().toString(), "Ошибка обработки запроса");
    }
}
