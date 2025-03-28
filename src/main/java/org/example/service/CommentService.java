package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.Comment;
import org.example.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;


    @Transactional
    public Comment save(Comment comment) {
        logger.info("Saving comment for task {} by user {}", comment.getTask().getId(), comment.getAuthor());

        Comment commentSaved = commentRepository.save(comment);

        logger.info("User saved {}", commentSaved.getId());
        return commentSaved;
    }
}
