package org.sxy.optimus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sxy.optimus.service.QuizCacheLoaderService;
import org.sxy.optimus.service.RoomService;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private QuizCacheLoaderService quizCacheLoaderService;

    @Autowired
    private RoomService roomService;

    @PostMapping("/preload/quiz/{quizId}/{roomId}")
    public ResponseEntity<String> preloadTheQuiz(@PathVariable("quizId") UUID quizId,@PathVariable("roomId") UUID roomId){
        quizCacheLoaderService.preloadQuizToRedis(quizId,roomId);
        return ResponseEntity.ok().body("Quiz is uploaded to redis");

    }

    @PostMapping("/preload/users/{roomId}")
    public ResponseEntity<String> preloadTheRoomUsers(@PathVariable("roomId") UUID roomId){
        roomService.cacheRoomUsersDetailToRedis(roomId, Duration.ofMinutes(30));
        return ResponseEntity.ok().body("Room users are uploaded to redis");

    }

}
