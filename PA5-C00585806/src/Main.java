import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        TerrainLibrary library = new TerrainLibrary();
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("Menu:");
            System.out.println("1) Load a terrain from file");
            System.out.println("2) Show summary of loaded terrains");
            System.out.println("3) Write full report to output file and quit");
            System.out.println("4) Quit without writing a report");
            System.out.print("Enter your choice (1-4): ");

            String choiceLine = input.nextLine().trim();

            if (choiceLine.isEmpty()) {
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                continue;
            }

            char choice = choiceLine.charAt(0);

            switch (choice) {
                case '1':
                    System.out.print("Enter terrain data filename (e.g., Terrain3.txt): ");
                    String filename = input.nextLine().trim();

                    if (filename.isEmpty()) {
                        System.out.println("No filename entered. Returning to menu.");
                        break;
                    }

                    TerrainReport report = library.loadTerrainFromFile(filename);

                    if (report == null) {
                        System.out.println("A terrain with the same ID is already stored in the library.");
                        System.out.println("File was ignored.");
                    } else {
                        System.out.println();
                        System.out.println(report);
                    }
                    break;
                case '2':
                    int total = library.getTotalTerrains();
                    int valid = library.getNumValidTerrains();
                    int invalid = library.getNumInvalidTerrains();
                    TerrainReport best = library.getBestTerrain();

                    System.out.println();
                    System.out.println("Library Summary:");
                    System.out.println("Total terrains: " + total);
                    System.out.println("Valid terrains: " + valid);
                    System.out.println("Invalid terrains: " + invalid);

                    if (best == null) {
                        System.out.println("Best terrain: NONE");
                        System.out.println("Best connectivity score: 0");
                        System.out.println("No valid terrains loaded.");
                    } else {
                        System.out.println("Best terrain: " + best.getTerrainID());
                        System.out.println("Best connectivity score: " + best.getConnectivityScore());
                    }
                    break;
                case '3':
                    System.out.print("Enter output filename for report (e.g., report.txt): ");
                    String outName = input.nextLine().trim();

                    if (outName.isEmpty()) {
                        System.out.println("No filename entered. Returning to menu.");
                        break;
                    }

                    try (PrintWriter out = new PrintWriter(new File(outName))) {
                        out.print(library.toString());
                        System.out.println("Report written successfully to " + outName);
                        running = false;
                    } catch (FileNotFoundException e) {
                        System.out.println("Could not open output file: " + outName);
                    }
                    break;
                case '4':
                    System.out.println("Exiting without writing a report.");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                    break;
            }
        }
        input.close();
    }
}
