package PNUMEAT.Backend.global.error.Member;

import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

public class MemberProfileUpdateValidException extends ComonException {
    public MemberProfileUpdateValidException() {
        super(ErrorCode.MEMBER_NOT_FOUND_ERROR);
    }
}