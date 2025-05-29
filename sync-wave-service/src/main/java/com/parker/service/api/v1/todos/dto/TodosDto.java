package com.parker.service.api.v1.todos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodosDto {
    private Long id;
    @NotNull(message = "{todo.task.not.null}")
    @Size(min = 1, max = 255, message = "{todo.task.size}")
    private String task;
    @NotNull(message = "{todo.start.date.not.null}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull(message = "{todo.due.date.not.null}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private String status;
}
