package org.sxy.optimus.dto.quiz;

import org.sxy.optimus.dto.question.QuestionCreateResDTO;

import java.util.List;

public class QuizQuestionsAddResDTO {
    private int totalQuestions;
    private int totalQuestionsAddedNow;
    private List<QuestionCreateResDTO> questionsAdded;

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public List<QuestionCreateResDTO> getQuestionsAdded() {
        return questionsAdded;
    }

    public void setQuestionsAdded(List<QuestionCreateResDTO> questionsAdded) {
        this.questionsAdded = questionsAdded;
    }

    public int getTotalQuestionsAddedNow() {
        return totalQuestionsAddedNow;
    }

    public void setTotalQuestionsAddedNow(int totalQuestionsAddedNow) {
        this.totalQuestionsAddedNow = totalQuestionsAddedNow;
    }
}
