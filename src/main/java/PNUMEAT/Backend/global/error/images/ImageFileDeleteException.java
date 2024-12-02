package PNUMEAT.Backend.global.error.images;


import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.ComonException;

public class ImageFileDeleteException extends ComonException {
    public ImageFileDeleteException() {
        super(ErrorCode.IMAGE_FILE_DELETE_ERROR);
    }

}
