package com.mindmirror.backend.affirmation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mindmirror.backend.affirmation.dto.AffirmationRequest;
import com.mindmirror.backend.affirmation.dto.AffirmationResponse;
import com.mindmirror.backend.affirmation.entity.Affirmation;
import com.mindmirror.backend.affirmation.repository.AffirmationRepository;
import com.mindmirror.backend.user.entity.User;

@ExtendWith(MockitoExtension.class)
class AffirmationServiceTest {

    @Mock
    private AffirmationRepository affirmationRepository;

    @Test
    void createReactivatesExistingInactiveAffirmation() {
        User user = new User();
        Affirmation existing = new Affirmation();
        existing.setUser(user);
        existing.setText("Small actions count.");
        existing.setActive(false);

        AffirmationRequest request = new AffirmationRequest();
        request.setText(" Small actions count. ");

        when(affirmationRepository.findByUserAndText(user, "Small actions count.")).thenReturn(Optional.of(existing));
        when(affirmationRepository.save(existing)).thenReturn(existing);

        AffirmationService service = new AffirmationService(affirmationRepository);
        AffirmationResponse response = service.create(user, request);

        ArgumentCaptor<Affirmation> captor = ArgumentCaptor.forClass(Affirmation.class);
        verify(affirmationRepository).save(captor.capture());

        assertThat(captor.getValue()).isSameAs(existing);
        assertThat(captor.getValue().isActive()).isTrue();
        assertThat(response.getText()).isEqualTo("Small actions count.");
    }
}
