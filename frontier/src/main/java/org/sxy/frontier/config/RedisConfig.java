package org.sxy.frontier.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.sxy.frontier.redis.dto.QuestionCacheDTO;
import org.sxy.frontier.redis.dto.QuizDetailCacheDTO;
import org.sxy.frontier.redis.dto.RoomUserDetailsCache;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    @Qualifier("String-QuestionCacheDTO")
    public RedisTemplate<String , QuestionCacheDTO> redisTemplate(RedisConnectionFactory factory){
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
    public RedisTemplate<String , QuizDetailCacheDTO> redisTemplateForQuizDetailCacheDTO(RedisConnectionFactory factory){
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

    @Bean
    @Qualifier("String-RoomUserDetailsCache")
    public RedisTemplate<String, RoomUserDetailsCache> redisTemplateForRoomUserDetails(RedisConnectionFactory factory){
        RedisTemplate<String, RoomUserDetailsCache> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //for key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        //for value
        Jackson2JsonRedisSerializer<RoomUserDetailsCache> serializer = new Jackson2JsonRedisSerializer<>(RoomUserDetailsCache.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    @Qualifier("String-String")
    public RedisTemplate<String, String> redisTemplateForString(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key and Hash key as String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value and Hash value as String
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }

}
