package org.sxy.optimus.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sxy.optimus.dto.pojo.RoomUserDetails;
import org.sxy.optimus.service.RoomService;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/v1/room")
public class RoomInternalController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/{roomId}/user/{userId}/details")
    public ResponseEntity<RoomUserDetails> getRoomUserDetail(@PathVariable UUID roomId, @PathVariable UUID userId){
        RoomUserDetails resDTO=roomService.getRoomUsersDetailCache(roomId, userId);
        return ResponseEntity
                .ok(resDTO);
    }

}
