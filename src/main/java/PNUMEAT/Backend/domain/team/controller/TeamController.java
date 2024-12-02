package PNUMEAT.Backend.domain.team.controller;

import PNUMEAT.Backend.domain.auth.dto.request.MemberProfileRequest;
import PNUMEAT.Backend.domain.auth.entity.Member;
import PNUMEAT.Backend.domain.auth.service.MemberService;
import PNUMEAT.Backend.domain.team.dto.request.TeamRequest;
import PNUMEAT.Backend.domain.team.service.TeamService;
import PNUMEAT.Backend.global.error.dto.response.ApiResponse;
import PNUMEAT.Backend.global.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static PNUMEAT.Backend.global.response.ResponseMessageEnum.TEAM_CREATED_SUCCESS;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createTeam(@ModelAttribute @Valid TeamRequest teamRequest,
                                                              @RequestPart(value = "teamIcon", required = false) MultipartFile teamIcon,
                                                              @LoginMember Member member){
        teamService.createTeam(teamRequest, teamIcon, member);

        return ResponseEntity.status(TEAM_CREATED_SUCCESS.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.createResponseWithMessage(TEAM_CREATED_SUCCESS.getMessage()));
    }
}


