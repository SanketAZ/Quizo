package org.sxy.frontier.utility;

import java.util.List;

public class RedisKeys {
    public static String buildQuizSequenceKey(String quizId, String roomId, String sequenceLabel){
        return formKey(List.of("quiz",quizId,"room",roomId,sequenceLabel,"questionSequence"));
    }
    public static String buildQuizQuestionsKey(String quizId, String roomId){
        return formKey(List.of("quiz",quizId,"room",roomId,"questions"));
    }
    public static String buildQuizDetailKey(String quizId, String roomId){
        return formKey(List.of("quiz",quizId,"room",roomId, "detail"));
    }
    public static String buildRoomUsersDetailsKey(String roomId){
        return formKey(List.of("room",roomId,"users"));
    }
    public static String buildParticipantQuizSessionKey(String sessionId){
        return formKey(List.of("participant_quiz_session","session",sessionId));
    }
    public static String buildSubmissionKey(String roomId,String quizId,String questionId,String userId){
        return formKey(List.of("submissions","room",roomId,"quiz",quizId,"userId",userId,"question",questionId));
    }
    private static String formKey(List<String> segments){
        return String.join(":", segments);
    }
}
