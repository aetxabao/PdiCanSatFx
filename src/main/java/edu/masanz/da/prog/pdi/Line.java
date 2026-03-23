package edu.masanz.da.prog.pdi;

public class Line {
    // ax + by + c = 0
    public double a, b, c;

    public Line() {
        this(0,0,0);
    }

    public Line(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Line(double x1, double y1, double x2, double y2) {
        // Cómo transformar dos puntos en la forma general de la recta:
        // 1. Obtener la pendiente (m) y la ordenada al origen (h):
        //    m = (y2 - y1) / (x2 - x1)
        //    h = y1 - m * x1
        // 2. Transformar a la forma general:
        //    y = mx + h → mx - y + h = 0 → a = m, b = -1, c = h

        // Si x2 == x1, entonces la línea es vertical y la pendiente es infinita. En ese caso, a = 1, b = 0, c = -x1.
        if (x2 == x1) {
            this.a = 1;
            this.b = 0;
            this.c = -x1;
            return;
        }

        this.a = (y2 - y1) / (x2 - x1);
        this.b = -1;
        this.c = y1 - a * x1;
    }

    public double getX(double y) {
        // ax + by + c = 0 → ax = -by - c → x = (-by - c) / a
        if (a == 0) {
            return Double.NaN; // línea horizontal
        }
        return (-b * y - c) / a;
    }

    public double getY(double x) {
        // ax + by + c = 0 → by = -ax - c → y = (-ax - c) / b
        if (b == 0) {
            return Double.NaN; // línea vertical
        }
        return (-a * x - c) / b;
    }

    public double pendiente() {
        // pendiente = -a/b
        if (b == 0) {
            return 90.0; // línea vertical
        }
        return Math.toDegrees( Math.atan ( -a / b ) );
    }

    public double altura() {
        // y = mx + c → altura en x=0 → y = -c/b
        if (b == 0) {
            return Double.NaN; // línea vertical
        }
        return -c / b;
    }


    // Distancia entre dos rectas casi paralelas
    // Para dos rectas:
    // a₁x + b₁y + c₁ = 0
    // a₂x + b₂y + c₂ = 0
    // Si son casi paralelas:
    public double distanciaConLineaParalela(Line otra) {
        return Math.abs(otra.c - this.c) / Math.sqrt(this.a*this.a + this.b*this.b);
    }

    public double anguloConOtraLinea(Line otra) {
        // ángulo entre dos rectas:
        // tan(θ) = |(m1 - m2) / (1 + m1*m2)|
        // donde m1 y m2 son las pendientes de las rectas.
        if (this.b == 0 && otra.b == 0) {
            return 0.0; // ambas líneas verticales
        }
        if (this.b == 0 || otra.b == 0) {
            double m = (this.b ==0) ? (-otra.a / otra.b) : (-this.a / this.b);
            return (m ==0.0) ?90.0 : Math.toDegrees(Math.atan(Math.abs(1.0 / m)));
        }
        double m1 = -this.a / this.b;
        double m2 = -otra.a / otra.b;
        return Math.toDegrees( Math.atan( Math.abs((m1 - m2) / (1 + m1 * m2)) ) );
    }

    @Override
    public String toString() {
        // ax + by + c = 0
        return String.format("% .2f * x + % .2f * y + % .2f = 0    ang = % .2fº    h = %.2f",
                                a, b, c, pendiente(), altura());
    }

    public static void main(String[] args) {
        Line l = new Line(2, -3, 6);
        System.out.println(l);
        //
        Line l1 = new Line(1,1,1,5);
        Line l2 = new Line(2,1,3,5);

        double ang = l1.anguloConOtraLinea(l2);
        System.out.println(l1);
        System.out.println(l2);
        System.out.println("Ángulo entre l1 y l2: " + ang);
    }

}
