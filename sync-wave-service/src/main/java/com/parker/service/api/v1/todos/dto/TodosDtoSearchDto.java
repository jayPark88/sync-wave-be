package com.parker.service.api.v1.todos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodosDtoSearchDto {
    private String task;
    private String status;
}
