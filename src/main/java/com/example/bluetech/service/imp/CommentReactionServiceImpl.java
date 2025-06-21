package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.entity.Comment;
import com.example.bluetech.entity.CommentReaction;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.CommentReactionCountProjection;
import com.example.bluetech.repository.CommentReactionRepository;
import com.example.bluetech.service.CommentReactionService;
import com.example.bluetech.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentReactionServiceImpl implements CommentReactionService {
    private final CommentReactionRepository commentReactionRepository;
    private final CommentService commentService;

    @Override
    public CommentReaction reactToComment(String commentId, String userId, ReactionType reactionType) {
        // Kiểm tra comment có tồn tại không
        Comment comment = commentService.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Optional<CommentReaction> existingReaction = commentReactionRepository.findByCommentIdAndUserId(commentId, userId);

        if (existingReaction.isPresent()) {
            CommentReaction reaction = existingReaction.get();
            if (reaction.getReactionType() == reactionType) {
                // Nếu cùng loại reaction thì xóa (toggle)
                commentReactionRepository.delete(reaction);
                return null;
            } else {
                // Nếu khác loại thì update
                reaction.setReactionType(reactionType);
                return commentReactionRepository.save(reaction);
            }
        } else {
            // Nếu chưa reaction thì tạo mới
            CommentReaction newReaction = new CommentReaction();
            newReaction.setCommentId(commentId);
            newReaction.setUserId(userId);
            newReaction.setReactionType(reactionType);
            newReaction.setCreatedAt(System.currentTimeMillis());
            return commentReactionRepository.save(newReaction);
        }
    }

    @Override
    public Map<ReactionType, Integer> getReactionCounts(String commentId) {
        // Kiểm tra comment có tồn tại không
        commentService.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        List<CommentReactionCountProjection> counts = commentReactionRepository.countReactionsByCommentId(commentId);
        Map<ReactionType, Integer> reactionCounts = new HashMap<>();
        for (CommentReactionCountProjection count : counts) {
            reactionCounts.put(count.getReactionType(), count.getCount());
        }
        return reactionCounts;
    }

    @Override
    public Optional<ReactionType> getUserReaction(String commentId, String userId) {
        // Kiểm tra comment có tồn tại không
        commentService.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        return commentReactionRepository.findReactionTypeByCommentIdAndUserId(commentId, userId)
                .map(CommentReaction::getReactionType);
    }
}