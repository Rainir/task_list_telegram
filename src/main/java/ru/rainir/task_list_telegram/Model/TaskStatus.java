package ru.rainir.task_list_telegram.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TaskStatus {
    OPEN,
    COMPLETED,
    FAILED;

    @JsonValue
    public String toString() {
        return name();
    }

    @JsonCreator
    public static TaskStatus getTaskStatus(String value) {
        return TaskStatus.valueOf(value.toUpperCase());
    }
}