package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.entity.Reaction;
import com.example.bluetech.repository.ReactionCountProjection;
import com.example.bluetech.repository.ReactionRepository;
import com.example.bluetech.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;

    @Override
    public Reaction reactToPost(String postId, String userId, ReactionType reactionType) {
        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);

        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if (reaction.getReactionType() == reactionType) {
                // Nếu cùng loại reaction thì xóa (toggle)
                reactionRepository.delete(reaction);
                return null;
            } else {
                // Nếu khác loại thì update
                reaction.setReactionType(reactionType);
                return reactionRepository.save(reaction);
            }
        } else {
            // Nếu chưa reaction thì tạo mới
            Reaction newReaction = new Reaction();
            newReaction.setPostId(postId);
            newReaction.setUserId(userId);
            newReaction.setReactionType(reactionType);
            newReaction.setCreatedAt(System.currentTimeMillis());
            return reactionRepository.save(newReaction);
        }
    }

    @Override
    public Map<ReactionType, Integer> getReactionCounts(String postId) {
        List<ReactionCountProjection> counts = reactionRepository.countReactionsByPostId(postId);
        Map<ReactionType, Integer> reactionCounts = new HashMap<>();
        for (ReactionCountProjection count : counts) {
            reactionCounts.put(count.getReactionType(), count.getCount());
        }
        return reactionCounts;
    }

    @Override
    public Optional<ReactionType> getUserReaction(String postId, String userId) {
        return reactionRepository.findReactionTypeByPostIdAndUserId(postId, userId)
                .map(Reaction::getReactionType);
    }
}