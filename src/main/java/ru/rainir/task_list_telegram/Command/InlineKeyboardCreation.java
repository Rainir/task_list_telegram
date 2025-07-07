package ru.rainir.task_list_telegram.Command;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InlineKeyboardCreation {

    public static InlineKeyboardButton createInlineKeyboardButton(String inlineKeyboardName, String inlineKeyboardCallbackData) {
        return InlineKeyboardButton.builder()
                .text(inlineKeyboardName)
                .callbackData(inlineKeyboardCallbackData)
                .build();
    }

    public static InlineKeyboardRow createInlineKeyboardRow(InlineKeyboardButton... inlineKeyboardButtons) {
        return new InlineKeyboardRow(inlineKeyboardButtons);
    }

    public static InlineKeyboardRow createInlineKeyboardRow(List<InlineKeyboardButton> buttonList) {
        return new InlineKeyboardRow(buttonList);
    }

    public static List<InlineKeyboardRow>  createInlineKeyboardRows(InlineKeyboardRow... inlineKeyboardRows) {
        return new ArrayList<>(Arrays.asList(inlineKeyboardRows));
    }

    public static InlineKeyboardMarkup createInlineKeyboardMarkup(List<InlineKeyboardRow> inlineKeyboardRows) {
        return  new InlineKeyboardMarkup(inlineKeyboardRows);
    }
}