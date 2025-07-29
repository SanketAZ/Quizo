package org.sxy.optimus.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.sxy.optimus.dto.question.QuestionCacheDTO;
import org.sxy.optimus.dto.quiz.QuizDetailCacheDTO;

@ControllerAdvice
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    @Qualifier("String-QuestionCacheDTO")
    public RedisTemplate<String ,QuestionCacheDTO> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,QuestionCacheDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        //for values
        Jackson2JsonRedisSerializer<QuestionCacheDTO> serializer = new Jackson2JsonRedisSerializer<>(QuestionCacheDTO.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    @Qualifier("String-QuizDetailCacheDTO")
    public RedisTemplate<String ,QuizDetailCacheDTO> redisTemplateForQuizDetailCacheDTO(RedisConnectionFactory factory){
        RedisTemplate<String,QuizDetailCacheDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        //for values
        Jackson2JsonRedisSerializer<QuizDetailCacheDTO> serializer = new Jackson2JsonRedisSerializer<>(QuizDetailCacheDTO.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

}
