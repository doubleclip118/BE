package PNUMEAT.Backend.global.error.Team;

import PNUMEAT.Backend.global.error.ComonException;

import static PNUMEAT.Backend.global.error.ErrorCode.TEAM_MANAGER_INVALID_ERROR;

public class TeamManagerInvalidException extends ComonException {
    public TeamManagerInvalidException(){
        super(TEAM_MANAGER_INVALID_ERROR);
    }
}
