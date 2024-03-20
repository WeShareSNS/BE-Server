package com.weshare.api.v1.controller.comment;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(@NotBlank String content) {
}
