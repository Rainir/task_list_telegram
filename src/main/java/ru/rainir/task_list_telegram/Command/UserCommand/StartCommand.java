package ru.rainir.task_list_telegram.Command.UserCommand;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class StartCommand {

    public static SendMessage registrationStartMassage(long chat_id) {

        SendMessage message = new SendMessage(String.valueOf(chat_id), "Регистрировались ли вы раньше на сайте?\n"+
                "Вы можете получить доступ к своим задачам\n" +
                "Для этого зайдите в личный кабинет на сайте и отправьте в чат сообщение вида 'Username id'");

        List<InlineKeyboardRow> rowsInline = new ArrayList<>();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(rowsInline);
        InlineKeyboardRow row = new InlineKeyboardRow();

        row.add(InlineKeyboardButton.builder()
                .text("Да")
                .callbackData("yas")
                .build()
        );

        row.add(InlineKeyboardButton.builder()
                .text("Нет")
                .callbackData("no")
                .build()
        );

        rowsInline.add(row);
        InlineKeyboardMarkup replyKeyboardMarkup = new InlineKeyboardMarkup(rowsInline);

        message.setReplyMarkup(replyKeyboardMarkup);
        markupInline.setKeyboard(rowsInline);

        return message;
    }

//    public static SendMessage loginStartMassage(long chat_id, boolean ) {
//        SendMessage message = new SendMessage(String.valueOf(chat_id), "");
//        return message;
//    }
}
