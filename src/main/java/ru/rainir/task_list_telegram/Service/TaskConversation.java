package ru.rainir.task_list_telegram.Service;


import org.springframework.context.annotation.Scope;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.rainir.task_list_telegram.Model.Task;

import java.time.LocalDateTime;

@Scope("prototype")
public class TaskConversation {
    private Long chatId;
    public String title;
    public String description;
    private Integer priority;

    public boolean conversationActive = false;

    public void startConversation(Long chatId) {
        this.chatId = chatId;
        conversationActive = true;
    }

    public SendMessage processMessage(String message, TaskApiService taskApiService, Long userId) {
        if (!conversationActive) {
            return null;
        }

        if (title == null) {
            title = message;
            return new SendMessage(chatId.toString(), "Введите описание задачи:");
        } else if (description == null) {
            description = message;
            return new SendMessage(chatId.toString(), "Укажите приоритет (1-5):");
        } else if (priority == null) {
            try {
                priority = Integer.parseInt(message);
                if (priority < 1 || priority > 5) {
                    return new SendMessage(chatId.toString(), "Некорректный приоритет. Введите число от 1 до 5:");
                }

                Task task = new Task();
                task.setAuthorId(userId);
                task.setTitle(title);
                task.setDescription(description);
                task.setPriority(priority);
                task.setCompletedAt(LocalDateTime.now().plusDays(7));

                Task createdTask = taskApiService.createTask(task);

                conversationActive = false;
                return new SendMessage(chatId.toString(), "Задача успешно создана!\n" + createdTask.toString());
            } catch (NumberFormatException e) {
                return new SendMessage(chatId.toString(), "Некорректный приоритет. Введите число от 1 до 5:");
            }
        }

        return null;
    }
}