package ex2;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

public class MapLoader {
    public static void main(String[] args) {
        try {
            // Load JSON data from a file
            String jsonText = readFile("mapdata.json");
            JSONObject json = new JSONObject(jsonText);

            // Parse points and create lines
            JSONArray pointsArray = json.getJSONArray("points");
            Line[] lines = new Line[pointsArray.length()];
            double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < pointsArray.length(); i++) {
                JSONArray point1 = pointsArray.getJSONArray(i);
                JSONArray point2 = pointsArray.getJSONArray((i + 1) % pointsArray.length());
                lines[i] = new Line(
                    (float) point1.getDouble(0), (float) point1.getDouble(1),
                    (float) point2.getDouble(0), (float) point2.getDouble(1)
                );
                updateBounds(point1, minX, maxX, minY, maxY);
                updateBounds(point2, minX, maxX, minY, maxY);
            }

            Rectangle bounds = new Rectangle((float) minX, (float) minY, (float) (maxX - minX), (float) (maxY - minY));

            // Create and save the line map
            LineMap map = new LineMap(lines, bounds);
            saveLineMap(map, "linemap.lmap");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void updateBounds(JSONArray point, double minX, double maxX, double minY, double maxY) {
        double x = point.getDouble(0);
        double y = point.getDouble(1);
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
    }

    private static String readFile(String filename) throws IOException {
        File file = new File(filename);
        byte[] fileContents = new byte[file.fileSize()];
        file.read(fileContents, 0, fileContents.length);
        file.close();
        return new String(fileContents);
    }

    private static void saveLineMap(LineMap map, String filename) throws IOException {
        File file = new File(filename);
        File out = File.createDataFile(filename);
        map.store(out);
        out.close();
    }
}

