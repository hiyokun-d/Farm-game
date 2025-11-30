package types;

public enum CropType {
    WHEAT(1, "WHEAT"),
    POTATO(7, "POTATO");

    public final int rowIndex;
    public final String id;

    CropType(int rowIndex, String id) {
        this.rowIndex = rowIndex;
        this.id = id;
    }
}
