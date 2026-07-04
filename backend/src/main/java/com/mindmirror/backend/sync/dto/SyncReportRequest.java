package com.mindmirror.backend.sync.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record SyncReportRequest(
    String state,

    @Min(0)
    Integer uploaded,

    @Min(0)
    Integer failed,

    @Min(0)
    Integer queued,

    @Min(0)
    Integer conflicts,

    @Size(max = 1000)
    String lastError
) {
}
