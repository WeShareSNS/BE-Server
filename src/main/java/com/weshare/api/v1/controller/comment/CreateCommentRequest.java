package com.weshare.api.v1.controller.comment;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest (@NotBlank String content){
}
