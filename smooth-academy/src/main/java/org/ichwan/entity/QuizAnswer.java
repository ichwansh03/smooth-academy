package org.ichwan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "quiz_answers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_result_id", nullable = false)
    private QuizResult quizResult;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    @Column(name = "num_a", nullable = false)
    private int numA;

    @Column(name = "num_b", nullable = false)
    private int numB;

    @Column(name = "correct_answer", nullable = false)
    private int correctAnswer;

    @Column(name = "user_answer", nullable = false)
    private int userAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;
}
