package org.example.model.pagination;

import java.util.List;

public record TaskConnection(
        List<TaskEdge> edges,
        PageInfo pageInfo
) {}