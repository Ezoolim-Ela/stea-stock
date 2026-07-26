package com.stea.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String type;
    private String message;
    private String canal;
    private boolean lu;
    private LocalDateTime date;
}
