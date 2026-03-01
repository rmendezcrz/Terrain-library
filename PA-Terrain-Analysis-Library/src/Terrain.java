import java.util.ArrayList;
import java.util.Scanner;

/**
 * What this class does:
 * Creates the Terrain by using a double array of Cells as a grid. The terrain will parse a line of text in a text
 * file, each line will have the type of cell it is (Cell, Tower, Obstacle), its row index, its col index, its ground
 * elevation, and the height of the tower/obstacle (if the cell is a Tower or Obstacle cell).
 */
public class Terrain {
    private static final double EPS = 1E-5;
    private final double cellSize;
    private final int rows;
    private final int cols;
    private Cell[][] Grid;
    private ArrayList<TowerCell> Towers;


    public ArrayList<TowerCell> getTowers(){
        return new ArrayList<> (Towers);
    }

    /**
     * What this Constructor does:
     * - The first three tokens in spec represent the number of rows and columns of the grid as well as the size of
     * each cell.
     * - The following tokens are read in groups that describe each cell.
     * - Each group consists of the following tokens:
     *      > cell type (C, T, or O)
     *      > row index
     *      > column index
     *      > ground elevation
     *      > added height (for tower or obstacle cells)
     *   These tokens make up a Cell object entry.
     *
     * @param spec - one of the text files in the library as a string
     */
    public Terrain(String spec) {
        if (spec == null) spec = "";
        Scanner stringParser = new Scanner(spec);
        int r = stringParser.nextInt();
        int c = stringParser.nextInt();
        double s = stringParser.nextDouble();
        this.rows = (r >= 0) ? r : 0;
        this.cols = (c >= 0) ? c : 0;
        this.cellSize = (s > 0.0) ? s : EPS;
        Grid = new Cell[this.rows][this.cols];
        Towers = new ArrayList<>();
        int expectedCellLines = this.rows * this.cols;
        for (int i = 0; i < expectedCellLines && stringParser.hasNext(); i++) {
            String typeToken = stringParser.next();
            char type = (typeToken == null || typeToken.isEmpty()) ? 'C' : Character.toUpperCase(typeToken.charAt(0));
            int rr = stringParser.nextInt();
            int cc = stringParser.nextInt();
            double ground = stringParser.nextDouble();
            double added = stringParser.nextDouble();
            Cell cell;
            switch (type) {
                case 'O': {
                    double obstacle = (added >= 0.5) ? added : 0.5;
                    cell = new ObstacleCell(rr, cc, ground, obstacle);
                    break;
                }
                case 'T': {
                    double towerH = (added >= 15.0) ? added : 15.0;
                    cell = new TowerCell(rr, cc, ground, towerH);
                    break;
                }
                case 'C':
                default: {
                    cell = new Cell(rr, cc, ground);
                    break;
                }
            }
            Grid[rr][cc] = cell;
        }
    }

    /**
     * Method for calculating if two tower objects have a line of sight of each other.
     * LoS is computed by determining if a cell along the path from tower a to b blocks visibility based on height and
     * slope.
     *
     * @param a - first tower
     * @param b - second tower
     * @return false if either towers are null
     *               if both towers have the same indexes (same grid location)
     *               if the distance between both towers is less than or equal to 0.0
     *               if the line of sight is blocked
     *         true otherwise
     */
    public boolean hasLoS(TowerCell a, TowerCell b) {
        if (a == null || b == null) return false;
        final int rA = a.getRow(), cA = a.getColumn();
        final int rB = b.getRow(), cB = b.getColumn();
        if (rA == rB && cA == cB) return false;

        final double hA = a.getTotalHeight();
        final double hB = b.getTotalHeight();
        final double distAB = distance(rA, cA, rB, cB);
        if (distAB <= 0.0) return false;

        final double targetSlope = (hB - hA) / distAB;


        int r = rA, c = cA;
        int dx = Math.abs(cB - cA);
        int dy = Math.abs(rB - rA);
        int stepR = Integer.compare(rB, rA);
        int stepC = Integer.compare(cB, cA);
        int E = dx - dy;


        while (r != rB || c != cB) {
            int E2 = E * 2;
            if (E2 > -dy && E2 < dx) {
                int rNew = r + stepR, cNew = c;
                E += dx;
                if (!(rNew == rA && cNew == cA) && !(rNew == rB && cNew == cB)) {
                    if (blocksLoS(hA, targetSlope, rA, cA, rNew, cNew)) return false;
                }
                r = rNew; c = cNew;
                rNew = r; cNew = c + stepC;
                E -= dy;
                if (!(rNew == rA && cNew == cA) && !(rNew == rB && cNew == cB)) {
                    if (blocksLoS(hA, targetSlope, rA, cA, rNew, cNew)) return false;
                }
                r = rNew; c = cNew;
                continue;
            }
            if (E2 > -dy) {
                c += stepC;
                E -= dy;
                if (!(r == rA && c == cA) && !(r == rB && c == cB)) {
                    if (blocksLoS(hA, targetSlope, rA, cA, r, c)) return false;
                }
            } else {
                r += stepR;
                E += dx;
                if (!(r == rA && c == cA) && !(r == rB && c == cB)) {
                    if (blocksLoS(hA, targetSlope, rA, cA, r, c)) return false;
                }
            }
        }
        return true;
    }

    /**
     * Updates each tower's list of other towers it has line of sight with. Clears the prior list and repopulates it
     * each time this method is called, only adding towers that have a mutual line of sight.
     */
    public void updateTowerLoS() {
        Towers.clear();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (Grid[r][c] instanceof TowerCell) {
                    Towers.add((TowerCell) Grid[r][c]);
                }
            }
        }
        for (int i = 0; i < Towers.size(); i++){
            TowerCell t = Towers.get(i);
            t.clearVisibleTowers();
        }
        for (int i = 0; i < Towers.size(); i++) {
            for (int j = i + 1; j < Towers.size(); j++) {
                TowerCell A = Towers.get(i);
                TowerCell B = Towers.get(j);
                if (hasLoS(A, B) && hasLoS(B, A)) {
                    A.addVisibleTower(B);
                    B.addVisibleTower(A);
                }
            }
        }
    }

    /**
     * Creates a formatted StringBuilder detailing the following information:
     * - All towers including their grid location, their total height in meters and rounded to 3 decimal spaces.
     * - The number of visible towers along with the row and column index of each.
     * - Isolated towers including the row and column index of each.
     *
     * @return a formatted String representation of all towers along with the tower visibility information of each
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("All Towers:\n");
        for (int i = 0; i < Towers.size(); i++) {
            TowerCell t = Towers.get(i);
            ArrayList<TowerCell> nbrs = t.getVisibleTowers();
            sb.append("(").append(t.getRow()).append(",").append(t.getColumn()).append("):");
            sb.append(String.format("%.3f", t.getTotalHeight())).append(" meters -");
            sb.append(" Visible Towers: ").append(nbrs.size()).append(" - [");
            for (int k = 0; k < nbrs.size(); k++) {
                if (k > 0) sb.append(",");
                TowerCell n = nbrs.get(k);
                sb.append("(").append(n.getRow()).append(",").append(n.getColumn()).append(")");
            }
            sb.append("]\n");
        }
        sb.append("Isolated towers: [");
        boolean first = true;
        for (int i = 0; i < Towers.size(); i++) {
            if (Towers.get(i).getVisibleTowers().size() == 0) {
                if (!first) sb.append(",");
                sb.append("(").append(Towers.get(i).getRow()).append(",").append(Towers.get(i).getColumn()).append(")");
                first = false;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Checks if intermediate grid cells between A and B block LoS.
     *
     * @param hA - height of tower A (source tower)
     * @param targetSlope - the slope of the LoS from tower A to target tower B
     * @param rA - the row index of tower A
     * @param cA - the column index of tower B
     * @param rX - row index of the intermediate cell being tested
     * @param cX - column index of the intermediate cell being tested
     * @return true if LoS is obstructed, false if it is not
     */
    public boolean blocksLoS(double hA, double targetSlope, int rA, int cA, int rX, int cX) {
        double distAX = distance(rA, cA, rX, cX);
        if (distAX <= 0.0) return false;
        double hX = Grid[rX][cX].getTotalHeight();
        double occluderSlope = (hX - hA) / distAX;
        return occluderSlope >= (targetSlope - EPS);
    }

    /**
     * Computes the Euclidean distance between two cells.
     * @param r1 - row index of the first cell
     * @param c1 - column index of the first cell
     * @param r2 - row index of the second cell
     * @param c2 - row index of the second cell
     * @return the distance between two cell locations
     */
    public double distance(int r1, int c1, int r2, int c2) {
        double dr = r2 - r1;
        double dc = c2 - c1;
        return cellSize * Math.sqrt(dr * dr + dc * dc);
    }

}
