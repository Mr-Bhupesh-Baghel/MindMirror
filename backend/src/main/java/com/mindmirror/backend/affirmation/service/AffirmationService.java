package com.mindmirror.backend.affirmation.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mindmirror.backend.affirmation.dto.AffirmationRequest;
import com.mindmirror.backend.affirmation.dto.AffirmationResponse;
import com.mindmirror.backend.affirmation.entity.Affirmation;
import com.mindmirror.backend.affirmation.repository.AffirmationRepository;
import com.mindmirror.backend.exception.ApiException;
import com.mindmirror.backend.user.entity.User;

@Service
public class AffirmationService {

    private final AffirmationRepository affirmationRepository;

    public AffirmationService(AffirmationRepository affirmationRepository) {
        this.affirmationRepository = affirmationRepository;
    }

    @Transactional(readOnly = true)
    public List<AffirmationResponse> affirmations(User user) {
        return affirmationRepository.findByUserAndActiveTrueOrderByCreatedAtAsc(user)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AffirmationResponse create(User user, AffirmationRequest request) {
        String text = normalizeText(request.getText());
        Affirmation affirmation = affirmationRepository.findByUserAndText(user, text)
            .orElseGet(Affirmation::new);
        affirmation.setUser(user);
        affirmation.setText(text);
        affirmation.setActive(true);

        return toResponse(affirmationRepository.save(affirmation));
    }

    @Transactional
    public void delete(User user, Long id) {
        Affirmation affirmation = affirmationRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Affirmation not found"));
        affirmation.setActive(false);
        affirmationRepository.save(affirmation);
    }

    private String normalizeText(String text) {
        String normalized = text == null ? "" : text.trim();
        if (normalized.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "text is required");
        }
        return normalized;
    }

    private AffirmationResponse toResponse(Affirmation affirmation) {
        AffirmationResponse response = new AffirmationResponse();
        response.setId(affirmation.getId());
        response.setText(affirmation.getText());
        return response;
    }
}
