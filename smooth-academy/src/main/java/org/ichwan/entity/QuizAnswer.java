package org.ichwan.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "quiz_answers")
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

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public QuizResult getQuizResult() { return quizResult; }
    public void setQuizResult(QuizResult quizResult) { this.quizResult = quizResult; }
    public int getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(int questionIndex) { this.questionIndex = questionIndex; }
    public int getNumA() { return numA; }
    public void setNumA(int numA) { this.numA = numA; }
    public int getNumB() { return numB; }
    public void setNumB(int numB) { this.numB = numB; }
    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }
    public int getUserAnswer() { return userAnswer; }
    public void setUserAnswer(int userAnswer) { this.userAnswer = userAnswer; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    public Integer getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Integer responseTimeMs) { this.responseTimeMs = responseTimeMs; }
}
