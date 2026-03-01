import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TerrainLibrary {
    private ArrayList<TerrainReport> reports;

    public TerrainLibrary() {
        reports = new ArrayList<>();
    }

    /**
     * Derives a terrainID from a file.
     * @param filename - the filename of a terrain file
     * @return the terrainID (without .txt)
     */
    private String deriveTerrainID(String filename) {
        if (filename == null) {
            return "";
        }
        filename = filename.trim();
        if (filename.endsWith(".txt")) {
            return filename.substring(0, filename.length() - 4);
        }
        return filename;
    }

    /**
     * Loads a terrain file from a text file and generates a terrain report.
     *
     * The file contents are read into a single specification string, which is then passed to Terrain(String) in
     * Terrain.java where the terrain grid is built. After which the tower LoS is computed in updateTowerLoS().
     *
     * If a report with the same ID already exists in the library, the method returns null as to not load the file again.
     * If the file cannot be found, or if the terrain data cannot be parsed into a valid Terrain, an invalid TerrainReport
     * is created, stored, and returned.
     * @param filename - the filename of the terrain .txt file
     * @return the created terrain report
     *         null if a terrain report with the same terrain ID already exits
     */
    public TerrainReport loadTerrainFromFile(String filename) {
        String terrainID = deriveTerrainID(filename);

        for (TerrainReport report : reports) {
            if (report.getTerrainID().equals(terrainID)) {
                return null;
            }
        }

        StringBuilder specBuilder = new StringBuilder();

        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (specBuilder.length() > 0) {
                    specBuilder.append('\n');
                }
                specBuilder.append(line);
            }

            String spec = specBuilder.toString();

            try {
                Terrain terrain = new Terrain(spec);
                terrain.updateTowerLoS();

                TerrainReport report = new TerrainReport(terrainID, terrain);
                reports.add(report);
                return report;
            } catch (NoSuchElementException e) {
                TerrainReport report = new TerrainReport(terrainID, "Invalid terrain data in file: " + filename);
                reports.add(report);
                return report;
            } catch (RuntimeException e) {
                TerrainReport report = new TerrainReport(terrainID, "Invalid terrain data in file: " + filename);
                reports.add(report);
                return report;
            }
        } catch (FileNotFoundException e) {
            TerrainReport report = new TerrainReport(terrainID, "File not found: " + filename);
            reports.add(report);
            return report;

        }
    }

    public int getTotalTerrains() {
        return reports.size();
    }

    /**
     * Counts how many valid terrain reports are stored.
     * @return the number of valid terrain reports
     */
    public int getNumValidTerrains() {
        int count = 0;
        for (TerrainReport report : reports) {
            if (report.isValid()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of invalid terrain reports that are stored.
     * @return - the number of invalid terrain reports
     */
    public int getNumInvalidTerrains() {
        int count = 0;
        for (TerrainReport report : reports) {
            if (!report.isValid()) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the TerrainReport with the best connectivity score
     *         null if no TerrainReport is valid
     */
    public TerrainReport getBestTerrain() {
        TerrainReport best = null;
        int bestScore = Integer.MIN_VALUE;

        for (TerrainReport report : reports) {
            if (report.isValid()) {
                int score = report.getConnectivityScore();
                if (best == null || score > bestScore) {
                    best = report;
                    bestScore = score;
                }
            }
        }
        return best;
    }

    public ArrayList<TerrainReport> getReports() {
        return new ArrayList<>(reports);
    }

    /**
     * Builds a formatted string containing the following info:
     * - The full report output of each terrain stored.
     * - Aggregate library statistics (total/valid/invalid).
     * - The best terrain ID and its connectivity score (if any valid terrains exist).
     * @return a formatted String representation of the TerrainLibrary
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < reports.size(); i++) {
            sb.append(reports.get(i).toString());
            if (i < reports.size() - 1) {
                sb.append("\n\n");
            }
        }

        if (!reports.isEmpty()) {
            sb.append("\n\n");
        }

        int total = getTotalTerrains();
        int valid = getNumValidTerrains();
        int invalid = getNumInvalidTerrains();
        TerrainReport best = getBestTerrain();

        sb.append("Total terrains: ").append(total).append("\n");
        sb.append("Valid terrains: ").append(valid).append("\n");
        sb.append("Invalid terrains: ").append(invalid).append("\n");

        if (best == null) {
            sb.append("Best terrain: NONE").append("\n");
            sb.append("Best connectivity score: 0");
        } else {
            sb.append("Best terrain: ").append(best.getTerrainID()).append("\n");
            sb.append("Best connectivity score: ").append(best.getConnectivityScore());
        }

        return sb.toString();
    }

}