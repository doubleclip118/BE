package PNUMEAT.Backend.global.error.Member;


import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

public class UnauthorizedException extends ComonException {
  public UnauthorizedException() {
    super(ErrorCode.UNAUTHORIZED_MEMBER_ERROR);
  }
}