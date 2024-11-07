package PNUMEAT.Backend.domain.article.enums;

public enum Category {
    BACKEND(1, "백엔드"),
    FRONTEND(2, "프론트엔드"),
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
