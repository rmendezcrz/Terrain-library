public class ObstacleCell extends Cell {
    private final double obstacleHeight;

    /**
     * Constructs an Obstacle Cell by using the row, column, and groundElevation of the Cell object.
     * If obstacleHeight is less than 0.5 it is set to 0.5.
     *
     * @param row - row index of the Cell
     * @param column - column index of the Cell
     * @param groundElevation - base value of the Cell
     * @param obstacleHeight - height of the obstacle above ground
     */
    public ObstacleCell(int row, int column, double groundElevation, double obstacleHeight) {
        super(row, column, groundElevation);
        this.obstacleHeight = (obstacleHeight >= 0.5) ? obstacleHeight : 0.5;
    }

    /**
     * Copy Constructor
     * If the object passed is null, set the obstacle height of the current ObstacleCell at 0.5.
     * Otherwise, set it to the obstacle height of the ObstacleCell passed if its height is more than 0.5.
     *
     * @param other - the Obstacle Cell object being compared
     */
    public ObstacleCell(ObstacleCell other) {
        super(other);
        if (other == null) {
            this.obstacleHeight = 0.5;
        } else {
            this.obstacleHeight = (other.obstacleHeight >= 0.5) ? other.obstacleHeight : 0.5;
        }
    }

    /**
     * Adds the height of the obstacle with the ground elevation to get the total height of the Obstacle object.
     * @return the total height of the ObstacleCell
     */
    @Override
    public double getTotalHeight() {
        return super.getTotalHeight() + obstacleHeight;
    }

    /**
     * Creates a formatted string representation of the Obstacle Cell.
     * @return a formatted string
     */
    @Override
    public String toString() {
        return "(" + getRow() + "," + getColumn() + "):" + getTotalHeight();
    }
}
