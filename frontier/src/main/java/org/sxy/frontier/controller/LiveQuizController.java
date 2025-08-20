package org.sxy.frontier.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz")
public class LiveQuizController {

    @GetMapping("/{roomId}/{quizId}")
    public void getQuestion(@PathVariable String roomId, @PathVariable String quizId, @RequestParam("qIndex")int qIndex,@RequestParam("seqLabel")String seqLabel) {

    }
}