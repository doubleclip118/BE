package PNUMEAT.Backend.global.error.articles;


import PNUMEAT.Backend.global.error.ComonException;
import PNUMEAT.Backend.global.error.ErrorCode;

public class ArticleNotFoundException extends ComonException {
    public ArticleNotFoundException() {
        super(ErrorCode.ARTICLE_NOT_FOUND_ERROR);
    }

}
