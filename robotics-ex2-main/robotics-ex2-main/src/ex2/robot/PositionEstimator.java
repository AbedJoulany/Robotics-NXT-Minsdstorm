package ex2.robot;

import lejos.robotics.RangeReadings;
import lejos.geom.Line;
import lejos.geom.Point;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Pose;

import java.util.ArrayList;
import java.util.List;

public class PositionEstimator {

    public static Pose estimatePosition(LineMap map, RangeReadings readings) {
        List<Line> parallelLines = new ArrayList<>();

        // Extract the range readings
        float R0 = readings.getRange(270f);
        float R90 = readings.getRange(180f);
        float R180 = readings.getRange(0f);
        float R270 = readings.getRange(90f);

        // Create parallel lines based on readings
        addParallelLines(map, 0, R0, parallelLines);
        addParallelLines(map, 90, R90, parallelLines);
        addParallelLines(map, 180, R180, parallelLines);
        addParallelLines(map, 270, R270, parallelLines);

        // Find intersection points of parallel lines
        List<Point> intersectionPoints = findIntersections(parallelLines);

        // Estimate the position by averaging the intersection points
        float avgX = 0;
        float avgY = 0;
        for (Point p : intersectionPoints) {
            avgX += p.x;
            avgY += p.y;
        }
        avgX /= intersectionPoints.size();
        avgY /= intersectionPoints.size();

        
        return new Pose(avgX, avgY, 0);
    }

    private static void addParallelLines(LineMap map, int angle, float distance, List<Line> parallelLines) {
        Line[] lines = map.getLines();
        for (Line line : lines) {
            Line parallelLine = createParallelLine(line, angle, distance);
            if (parallelLine != null) {
                parallelLines.add(parallelLine);
            }
        }
    }

    private static Line createParallelLine(Line line, int angle, float distance) {
        float dx = (float) (distance * Math.cos(Math.toRadians(angle)));
        float dy = (float) (distance * Math.sin(Math.toRadians(angle)));

        Point start = null;
        Point end = null;

        switch (angle) {
            case 0:
                start = new Point(line.x1 + dx, line.y1);
                end = new Point(line.x2 + dx, line.y2);
                break;
            case 90:
                start = new Point(line.x1, line.y1 + dy);
                end = new Point(line.x2, line.y2 + dy);
                break;
            case 180:
                start = new Point(line.x1 - dx, line.y1);
                end = new Point(line.x2 - dx, line.y2);
                break;
            case 270:
                start = new Point(line.x1, line.y1 - dy);
                end = new Point(line.x2, line.y2 - dy);
                break;
        }

        
        return new Line(start.x, start.y, end.x, end.y);
    }

    private static List<Point> findIntersections(List<Line> lines) {
        List<Point> intersectionPoints = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                Point intersection = findIntersection(lines.get(i), lines.get(j));
                if (intersection != null) {
                    intersectionPoints.add(intersection);
                }
            }
        }

        return intersectionPoints;
    }

    private static Point findIntersection(Line line1, Line line2) {
        float x1 = line1.x1, y1 = line1.y1, x2 = line1.x2, y2 = line1.y2;
        float x3 = line2.x1, y3 = line2.y1, x4 = line2.x2, y4 = line2.y2;

        float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0) {
            return null; // Lines are parallel
        }

        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1) {
            float x = x1 + ua * (x2 - x1);
            float y = y1 + ua * (y2 - y1);
            return new Point(x, y);
        }

        return null; // Intersection point is outside the segments
    }

   
}
