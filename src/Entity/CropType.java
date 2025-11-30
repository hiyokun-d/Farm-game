package Entity;

public enum CropType {
    WHEAT(0),
    POTATO(1);

    public final int rowIndex;

    CropType(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
