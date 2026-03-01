import java.util.ArrayList;

public class TerrainReport {
    public final String terrainID;
    private final boolean valid;
    private final String errorMessage;

    private final int numTowers;
    private final int numIsolated;
    private final int numMutualPairs;
    private final int connectivityScore;
    private final String terrainSummary;

    /**
     * Constructs a report of the terrain by using a terrain ID and its respective terrain file. Deals with null
     * terrain IDs and null terrains.
     * Provides the following terrain information:
     * - the number of isolated towers.
     * - the number of mutual pairs (towers that have LoS of each other).
     * - the terrain's connectivity score.
     * - as well as the Terrain's formatted summary string.
     *
     * @param terrainID - The name of the terrain file (minus .txt)
     * @param terrain - The Terrain object constructed from the terrain file
     */
    public TerrainReport(String terrainID, Terrain terrain){
        if (terrainID == null){
            terrainID = "";
        }
        this.terrainID = terrainID;

        if (terrain == null){
            this.valid = false;
            this.errorMessage = "Invalid Terrain: null reference";
            this.numTowers = 0;
            this.numIsolated = 0;
            this.numMutualPairs = 0;
            this.connectivityScore = 0;
            this.terrainSummary = null;
            return;
        }
        this.valid = true;
        this.errorMessage = null;

        ArrayList<TowerCell> towers = terrain.getTowers();
        this.numTowers = towers.size();

        int isolated = 0;
        int mutualPairs = 0;

        for (int i = 0; i < towers.size(); i++){
            TowerCell a = towers.get(i);
            ArrayList<TowerCell> neighborsA = a.getVisibleTowers();

            if (neighborsA.isEmpty()){
                isolated++;
            }
            for (int j = i + 1; j < towers.size(); j++){
                TowerCell b = towers.get(j);
                if (neighborsA.contains(b)){
                    mutualPairs++;
                }
            }
        }
        this.numIsolated = isolated;
        this.numMutualPairs = mutualPairs;
        this.connectivityScore = numMutualPairs - numIsolated;
        this.terrainSummary = terrain.toString();
    }

    /**
     * Constructor that deals with specific errors related to terrainID
     * @param terrainID - the terrainID of one of the files
     * @param errorMessage - the displayed message caused by the error
     */
    public TerrainReport(String terrainID, String errorMessage){
        if (terrainID == null){
            terrainID = "";
        }

        this.terrainID = terrainID;
        this.valid = false;
        this.errorMessage = (errorMessage == null) ? "" : errorMessage;

        this.numTowers = 0;
        this.numIsolated = 0;
        this.numMutualPairs = 0;
        this.connectivityScore = 0;
        this.terrainSummary = null;
    }

    public boolean isValid(){
        return valid;
    }
    public String getErrorMessage(){
        return errorMessage;
    }
    public int getNumTowers(){
        return numTowers;
    }
    public int getNumIsolatedTowers(){
        return numIsolated;
    }
    public int getNumMutualPairs(){
        return numMutualPairs;
    }
    public int getConnectivityScore(){
        return connectivityScore;
    }
    public String getTerrainID(){
        return terrainID;
    }

    /**
     * Creates a formatted String report of the Terrain. Handling the validity of the terrain provided as well as
     * appending an error message detailing the cause of the issue.
     * @return a formatted String representation of the Terrain
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Terrain ID: ").append(terrainID).append("\n");
        sb.append("Filename: ").append(terrainID).append(".txt").append("\n");

        if (!valid) {
            sb.append("Status: INVALID").append("\n");
            sb.append("Error: ").append(errorMessage);
            return sb.toString();
        }

        sb.append("Status: VALID").append("\n");
        sb.append("Towers: ").append(numTowers).append("\n");
        sb.append("Isolated: ").append(numIsolated).append("\n");
        sb.append("Mutual LoS pairs: ").append(numMutualPairs).append("\n");
        sb.append("Connectivity score: ").append(connectivityScore).append("\n");

        sb.append("\n");

        if (terrainSummary != null) {
            sb.append(terrainSummary);
        }

        return sb.toString();
    }
}
