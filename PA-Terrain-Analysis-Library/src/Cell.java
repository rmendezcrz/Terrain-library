public class Cell {
    private final int row;
    private final int column;
    private final double groundElevation;

    /**
     * Constructs a Cell object at the given position with the given ground elevation.
     * Negative values are defaulted to 0.
     * @param row - row index
     * @param column - col index
     * @param groundElevation - base elevation at the current grid location
     */
    public Cell(int row, int column, double groundElevation) {
        this.row = (row >= 0) ? row : 0;
        this.column = (column >= 0) ? column : 0;
        this.groundElevation = (groundElevation >= 0.0) ? groundElevation : 0.0;
    }

    /**
     * Copy constructor.
     * If the other Cell object is null, the Cell is initialized to location (0,0) with ground elevation 0.0.
     * @param other - the Cell that is copied
     */
    public Cell(Cell other) {
        if (other == null) {
            this.row = 0;
            this.column = 0;
            this.groundElevation = 0.0;
        } else {
            this.row = other.row;
            this.column = other.column;
            this.groundElevation = other.groundElevation;
        }
    }

    /** @return the row index of the current cell */
    public int getRow() {
        return row;
    }

    /** @return the column index of current cell */
    public int getColumn() {
        return column;
    }

    /**
     * In its default state, the total height of a Cell object is just ground elevation
     * Other subclasses (towerCell or ObstacleCell) may override this to add additional height
     * @return the total height of the current cell
     */
    public double getTotalHeight() {
        return groundElevation;
    }

    /**
     * Compares cells by grid location.
     *
     * If the current Cell and the object passed as a parameter point to the same thing, return true.
     * If the object isn't a Cell object, return false.
     * Otherwise, treat object as a Cell object and return true if it has the same row and column indexes as the
     * current Cell object. If not, return false.
     *
     * @param obj - any object used for comparison
     * @return true if the object passed is a Cell and has the same row and column indexes as the current Cell object
     *         false if the object passed isn't an instance of Cell or if its row and col indexes are different
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cell)) {
            return false;
        }
        Cell other = (Cell) obj;
        return this.row == other.row && this.column == other.column;
    }

    /**
     * Creates a formatted string representation of the Cell.
     * @return a formatted string
     */
    public String toString() {
        return "(" + row + "," + column + "):" + getTotalHeight();
    }
}
