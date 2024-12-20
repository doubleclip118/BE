package PNUMEAT.Backend.global.error.articles;


import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

public class MemberNotInTeamException extends ComonException {
    public MemberNotInTeamException() {
        super(ErrorCode.MEMBER_NOT_IN_TEAM);
    }

}
