package com.weshare.api.v1.repository.schedule.query;

import org.springframework.util.StringUtils;

public record SearchCondition(String search) {
    public SearchCondition(String search) {
        if (!StringUtils.hasText(search)) {
            this.search = null;
            return;
        }
        this.search = search;
    }
}
