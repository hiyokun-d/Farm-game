package types;

public enum CropType {
    WHEAT(1), //? take rows 1 (0+1)
    POTATO(7);

    public final int startIndex;

    CropType(int startIndex) {
        this.startIndex = startIndex;
    }
}
