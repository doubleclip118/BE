package PNUMEAT.Backend.global.error.Team;

import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

import static PNUMEAT.Backend.global.error.ErrorCode.TEAM_ALREADY_JOIN;

public class TeamAlreadyJoinException extends ComonException {
    public TeamAlreadyJoinException(){
        super(TEAM_ALREADY_JOIN);
    }
}
