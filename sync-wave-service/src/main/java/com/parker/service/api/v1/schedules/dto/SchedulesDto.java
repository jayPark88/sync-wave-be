package com.parker.service.api.v1.schedules.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchedulesDto {

    @NotNull(message = "{schedules.title.not.null}")
    @Size(min = 1, max = 100, message = "{user.name.size}")
    private String title;

    @NotNull(message = "{schedules.description.not.null}")
    @Size(min = 1, max = 500, message = "{user.name.size}")
    private String description;

    @NotNull(message = "{schedules.date.time.not.null}")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @NotNull(message = "{schedules.date.time.not.null}")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;
}
