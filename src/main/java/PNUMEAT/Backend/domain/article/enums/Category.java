package PNUMEAT.Backend.domain.article.enums;

public enum Category {
    FRONTEND(1, "프론트엔드"),
    BACKEND(2, "백엔드"),
    ANDROID(3, "안드로이드"),
    GAME(4, "게임");

    private final int code;
    private final String name;

    Category(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
