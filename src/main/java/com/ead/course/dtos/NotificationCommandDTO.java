package com.ead.course.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NotificationCommandDTO {

    private String title;
    private String message;
    private UUID userId;
}
