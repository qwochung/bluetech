package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.entity.Post;
import com.example.bluetech.entity.Reaction;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.ReactionCountProjection;
import com.example.bluetech.repository.ReactionRepository;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostService postService;

    @Override
    public Reaction reactToPost(String postId, String userId, ReactionType reactionType) {
        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getReactionType() == reactionType) {
                // Nếu cùng loại reaction thì xóa (toggle)
                reactionRepository.delete(reaction);
            } else {
                // Nếu khác loại thì update
                reaction.setReactionType(reactionType);
                reactionRepository.save(reaction);
            }
        } else {
            // Nếu chưa reaction thì tạo mới
            Reaction newReaction = new Reaction();
            newReaction.setPostId(postId);
            newReaction.setUserId(userId);
            newReaction.setReactionType(reactionType);
            newReaction.setCreatedAt(System.currentTimeMillis());
            reactionRepository.save(newReaction);
        }

        // Cập nhật lại số lượng reactions và trạng thái reacted
        updatePostReactionInfo(postId, userId);
        return reactionRepository.findByPostIdAndUserId(postId, userId).orElse(null);
    }

    @Override
    public Map<ReactionType, Integer> getReactionCounts(String postId) {
        List<ReactionCountProjection> counts = reactionRepository.countReactionsByPostId(postId);
        return counts.stream()
                .collect(Collectors.toMap(
                        ReactionCountProjection::getReactionType,
                        ReactionCountProjection::getCount
                ));
    }

    @Override
    public Optional<ReactionType> getUserReaction(String postId, String userId) {
        return reactionRepository.findReactionTypeByPostIdAndUserId(postId, userId)
                .map(Reaction::getReactionType);
    }
    private void updatePostReactionInfo(String postId, String userId) {
        postService.findById(postId, userId).ifPresent(post -> {
            long reactionCount = reactionRepository.countByPostId(postId);
            post.setNoOfReactions((int) reactionCount);
            postService.save(post);
        });
    }

}
