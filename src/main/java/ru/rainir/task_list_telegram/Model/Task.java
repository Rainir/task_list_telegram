package ru.rainir.task_list_telegram.Model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Task {
    private Long id;
    private Long authorId;

    private String title;
    private String description;

    private int priority;
    private TaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    @Override
    public String toString() {
        return "Название задачи: " + title + "\r\n" +
               "Описание: " + description + "\r\n" +
               "Приоритет: " + priority + "\r\n" +
               "Статус: " + TaskStatus.OPEN + "\r\n";
    }
}
