package org.sxy.frontier.module;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "participant_quiz_session")
@Entity
public class ParticipantQuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id",updatable = false,nullable = false)
    private UUID sessionId;

    @Column(name = "quiz_id",nullable = false)
    private UUID quizId;

    @Column(name = "user_id",nullable = false)
    private UUID userId;

    @Column(name = "room_id",nullable = false)
    private UUID roomId;

    @Column(name="status",nullable = false)
    private String status;

    @Column(name="start_time",nullable = false)
    private Instant startTime;

    @Column(name="end_time")
    private Instant endTime;

    @Column(name="final_end_time",nullable = false)
    private Instant finalEndTime;

    @Column(name = "current_index")
    private Integer currentIndex;

    @Column(name = "sequence_label", nullable = false)
    private String sequenceLabel;

}
