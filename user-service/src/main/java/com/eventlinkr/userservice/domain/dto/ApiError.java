package com.eventlinkr.userservice.domain.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final String errorId;
    private final int status;
    private final String message;
    private final String detail;
    private final Instant timestamp;
    private List<String> errors;
    private String path;
    private String documentation;

    public ApiError(int status, String message, String detail) {
        this.errorId = UUID.randomUUID().toString();
        this.status = status;
        this.message = message;
        this.detail = detail;
        this.timestamp = Instant.now();
    }

    public void addError(String error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

    public void setDocumentationLink(String link) {
        this.documentation = link;
    }
}
