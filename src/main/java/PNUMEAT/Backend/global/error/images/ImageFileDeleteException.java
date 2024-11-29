package PNUMEAT.Backend.global.error.images;


import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.Team24Exception;

public class ImageFileDeleteException extends Team24Exception {
    public ImageFileDeleteException() {
        super(ErrorCode.IMAGE_FILE_DELETE_ERROR);
    }

}
