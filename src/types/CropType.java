package types;

public enum CropType {
    WHEAT(1, "WHEAT"), //? take rows 1 (0+1)
    POTATO(7, "POTATO");

    public final int startIndex;
    public final String id;

    CropType(int startIndex, String id) {
        this.startIndex = startIndex;
        this.id = id;
    }
}
