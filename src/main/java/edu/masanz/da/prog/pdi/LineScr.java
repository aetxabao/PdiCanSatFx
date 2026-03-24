package edu.masanz.da.prog.pdi;

/**
 * Recta en coordenadas de pantalla:
 * origen arriba-izquierda,
 * X crece hacia la derecha,
 * Y crece hacia abajo.
 */
public class LineScr {

    // ax + by + c = 0
    public double a, b, c;

    public LineScr() {
        this(0, 0, 0);
    }

    public LineScr(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public LineScr(Point p1, Point p2) {
        this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public LineScr(double x1, double y1, double x2, double y2) {

        // En pantalla:
        // pendiente = (y2 - y1) / (x2 - x1)
        // ecuación: y = m x + h
        // forma general: m x - y + h = 0

        if (x2 == x1) {
            // recta vertical
            this.a = 1;
            this.b = 0;
            this.c = -x1;
            return;
        }

        double m = (y2 - y1) / (x2 - x1);
        double h = y1 - m * x1;

        this.a = m;
        this.b = -1;
        this.c = h;
    }

    public double getX(double y) {
        if (a == 0) {
            return Double.NaN; // horizontal
        }
        return (-b * y - c) / a;
    }

    public double getY(double x) {
        if (b == 0) {
            return Double.NaN; // vertical
        }
        return (-a * x - c) / b;
    }

    /**
     * Ángulo de la recta respecto al eje X de la pantalla.
     * 0º = horizontal hacia la derecha
     * 90º = vertical hacia abajo
     */
    public double angulo() {
        if (b == 0) {
            return 90.0;
        }

        double m = -a / b; // pendiente en pantalla
        return Math.toDegrees(Math.atan(m));
    }

    public double anguloInvertido() {
        if (b == 0) {
            return 90.0;
        }

        double d = Math.toDegrees(Math.atan(a / b));

        return (d < 0) ? d + 180 : d;
    }

    /**
     * Altura en x = 0 (intersección con el borde izquierdo)
     */
    public double altura() {
        if (b == 0) {
            return Double.NaN;
        }
        return -c / b;
    }

    public double distanciaConLineaParalela(LineScr otra) {
        return Math.abs(otra.c - this.c) /
                Math.sqrt(this.a * this.a + this.b * this.b);
    }

    public double anguloConOtraLinea(LineScr otra) {

        if (this.b == 0 && otra.b == 0) {
            return 0.0;
        }

        double m1 = (this.b == 0) ? Double.POSITIVE_INFINITY : -this.a / this.b;
        double m2 = (otra.b == 0) ? Double.POSITIVE_INFINITY : -otra.a / otra.b;

        if (Double.isInfinite(m1) || Double.isInfinite(m2)) {
            if (Double.isInfinite(m1) && Double.isInfinite(m2)) {
                return 0.0;
            }
            double m = Double.isInfinite(m1) ? m2 : m1;
            return Math.toDegrees(Math.atan(Math.abs(1.0 / m)));
        }

        double tan = Math.abs((m1 - m2) / (1 + m1 * m2));
        return Math.toDegrees(Math.atan(tan));
    }

    @Override
    public String toString() {
        return String.format(
                "% .2f * x + % .2f * y + % .2f = 0    ang = % .2fº    h = %.2f",
                a, b, c, angulo(), altura()
        );
    }

    public static void main(String[] args) {
        // Recta descendente en pantalla
        LineScr l1 = new LineScr(10, 10, 100, 50);

        // Recta ascendente en pantalla
        LineScr l2 = new LineScr(10, 50, 100, 10);

        System.out.println(l1);
        System.out.println(l2);

        double ang = l1.anguloConOtraLinea(l2);
        System.out.println("Ángulo entre l1 y l2: " + ang);
    }
}