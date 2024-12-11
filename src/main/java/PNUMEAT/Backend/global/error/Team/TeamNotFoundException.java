package PNUMEAT.Backend.global.error.Team;

import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

public class TeamNotFoundException extends ComonException {
    public TeamNotFoundException(){
        super(ErrorCode.TEAM_NOT_FOUND_ERROR);
    }
}
