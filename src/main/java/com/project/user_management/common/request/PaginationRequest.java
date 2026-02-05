package com.project.user_management.common.request;

import lombok.Builder;

@Builder
public record PaginationRequest(
        String keyword,
        int page,
        int size,
        String sortField,
        String sortDirection
) {}
