package com.parker.service.api.v1.todos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    
    private String status;
}
