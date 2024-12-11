package PNUMEAT.Backend.global.error.Team;

import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

import static PNUMEAT.Backend.global.error.ErrorCode.TEAM_PASSWORD_INVALID;

public class TeamPasswordInvalidException extends ComonException {
    public TeamPasswordInvalidException(){
        super(TEAM_PASSWORD_INVALID);
    }
}
