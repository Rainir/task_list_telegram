package ru.rainir.task_list_telegram.Command.UserCommand;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class StartCommandHandler {

    public static SendMessage callbackHandler(Long chatId, String callbackData) {

        String messageText = switch (callbackData) {
            case "yes" -> callbackYes();
            case "no" -> callbackNo();
            default -> "";
        };

        return new SendMessage(String.valueOf(chatId), messageText);
    }

    private static String callbackYes() {
        return "yes";
    }

    private static String callbackNo() {
        return "no";
    }
}