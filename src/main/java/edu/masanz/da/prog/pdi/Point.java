package edu.masanz.da.prog.pdi;

public class Point {
    private double x;
    private double y;

    public Point() {
        this(0,0);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(double imgX, double imgY) {
        this.x = imgX;
        this.y = imgY;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distance(Point other) {
        return Math.sqrt( Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) );
    }

    @Override
    public String toString() {
        return String.format("(%.0f, %.0f)", x, y);
    }

    public Point upwards(double height) {
        // En coordenadas de imagen, el origen (0,0) está en la esquina superior izquierda, y el eje Y crece hacia abajo.
        double h = height - y;
        return new Point(x, h);
    }
}
