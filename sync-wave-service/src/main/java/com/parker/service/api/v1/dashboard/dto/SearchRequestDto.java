package com.parker.service.api.v1.dashboard.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class SearchRequestDto {
    private String userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;
}
