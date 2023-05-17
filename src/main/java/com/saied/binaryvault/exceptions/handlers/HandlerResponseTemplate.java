package com.saied.binaryvault.exceptions.handlers;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class HandlerResponseTemplate {
    private ZonedDateTime timestamp;
    private Integer status;
    private String error;
    private Object message;
    private String path;
}
