package com.mindmirror.backend.feedback;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.feedback.dto.CreateFeedbackRequest;
import com.mindmirror.backend.feedback.dto.FeedbackResponse;
import com.mindmirror.backend.feedback.entity.FeedbackEntry;
import com.mindmirror.backend.feedback.repository.FeedbackRepository;
import com.mindmirror.backend.user.entity.User;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public FeedbackResponse create(CreateFeedbackRequest request, User user) {
        FeedbackEntry entry = new FeedbackEntry();
        entry.setUser(user);
        entry.setName(request.name().trim());
        entry.setEmail(request.email().trim().toLowerCase());
        entry.setRating(request.rating().byteValue());
        entry.setMessage(request.message().trim());
        entry.setFeedbackDate(request.feedbackDate() == null ? LocalDate.now() : request.feedbackDate());

        return FeedbackResponse.from(feedbackRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponse> list(Pageable pageable) {
        return feedbackRepository.findAll(pageable).map(FeedbackResponse::from);
    }

    @Transactional(readOnly = true)
    public FeedbackResponse get(Long id) {
        return FeedbackResponse.from(findById(id));
    }

    @Transactional
    public void delete(Long id) {
        FeedbackEntry entry = findById(id);
        feedbackRepository.delete(entry);
    }

    private FeedbackEntry findById(Long id) {
        return feedbackRepository.findById(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Feedback not found"));
    }
}
