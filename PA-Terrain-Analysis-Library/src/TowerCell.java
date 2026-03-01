import java.util.ArrayList;

public class TowerCell extends Cell {
    private final double towerHeight;
    private ArrayList<TowerCell> visibleTowers;

    /**
     * Constructs a TowerCell object using the given row, column, groundElevation, and tower height.
     * Sets the tower height at 15.0 if the tower height given is less than 15.0.
     * Initializes an array list of the class type (TowerCell).
     *
     * @param row
     * @param column
     * @param groundElevation
     * @param towerHeight
     */
    public TowerCell(int row, int column, double groundElevation, double towerHeight) {
        super(row, column, groundElevation);
        this.towerHeight = (towerHeight >= 15.0) ? towerHeight : 15.0;
        this.visibleTowers = new ArrayList<>();
    }

    /**
     * Copy Constructor
     *
     * If Object TowerCell passed is null, set its tower height to 15.0 and initialize the visible towers array list.
     * Otherwise,
     * > the tower height is copied from the provided TowerCell. With a value of 15.0 if the TowerCell provided has a
     * tower height of less than 15.0
     * > If the array list of the provided TowerCell is null, it is initialized. If it is not null its contents are
     * copied.
     *
     * @param other - the provided TowerCell to copy
     */
    public TowerCell(TowerCell other) {
        super(other);
        if (other == null) {
            this.towerHeight = 15.0;
            this.visibleTowers = new ArrayList<> ();
        } else {
            this.towerHeight = (other.towerHeight >= 15.0) ? other.towerHeight : 15.0;
            if (other.visibleTowers == null) {
                this.visibleTowers = new ArrayList<> ();
            }
            else {
                this.visibleTowers = new ArrayList<>(other.visibleTowers);
            }
        }
    }

    /** @return the total height of the TowerCell by adding tower height to ground elevation */
    @Override
    public double getTotalHeight() {
        return super.getTotalHeight() + towerHeight;
    }

    /** @return array list of visible towers */
    public ArrayList<TowerCell> getVisibleTowers() {
        return new ArrayList<>(visibleTowers);
    }


    /**
     * Adds a TowerCell object to the visibleTowers array list.
     * @param t - the provided TowerCell object to add to the array list
     * @return false if the TowerCell object provided is null, if it already exists in the array list, or if it is the
     *         same object as the current TowerCell
     *         true otherwise
     */
    public boolean addVisibleTower(TowerCell t) {
        if (t == null) return false;
        if (this == t) return false;
        if (visibleTowers.contains(t)) return false;
        visibleTowers.add(t);
        return true;
    }

    /** Clears Array List visibleTowers when called */
    public void clearVisibleTowers() {
        visibleTowers.clear();
    }

    /** @return a string representation of the current TowerCell */
    @Override
    public String toString() {
        return "(" + getRow() + "," + getColumn() + "):" + getTotalHeight();
    }
}