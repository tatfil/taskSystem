package org.example.model.pagination;

import org.example.model.dto.TaskDTO;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import graphql.schema.GraphQLType;

public record TaskEdge(String cursor, TaskDTO node) {


    public String getCursor() {
        return cursor;
    }

    public TaskDTO getNode() {
        return node;
    }

    public String getCursor(int pageNumber) {
        String rawCursor = "cursor:" + pageNumber;
        return Base64.getEncoder().encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }
}
