package org.sxy.optimus.utility.redis;

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

    private static String formKey(List<String> segments){
        return String.join(":", segments);
    }
}
