package org.sxy.frontier.event;

import org.sxy.frontier.dto.ParticipantQuizSessionDTO;
import org.sxy.frontier.module.ParticipantQuizSession;

import java.time.Duration;

public record ParticipantQuizSessionCachedEvent(ParticipantQuizSessionDTO sessionDTO) {
}
