package edu.masanz.da.prog.pdi;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Ocv {

    public static final int THRESHOLD = 30;
    public static final double MIN_LINE_LENGTH = 15;
    public static final double MAX_LINE_GAP = 5;


    public static List<Segment> segments = new ArrayList<>();
    public static List<LineCar> lines = new ArrayList<>();


    // https://es.wikipedia.org/wiki/Transformada_de_Hough#Detectando_l%C3%ADneas_rectas
    // https://docs.opencv.org/4.x/d6/d10/tutorial_py_houghlines.html
    // y=m*x+n   -->  y = ( - cos(theta) / sin(theta) ) * x + ( rho / sin(theta) )
    // en coordenada polares (rho, theta) se denomina espacio de Hough
    // rho representa la distancia entre el origen de coordenadas y el punto (x,y)
    // theta representa el ángulo entre el eje x y la línea perpendicular a la línea que pasa por el origen de coordenadas
    // Para un punto arbitrario en la imagen con coordenadas (x0,y0) las rectas que pasan por ese punto son los pares
    // (rho, theta) que satisfacen la ecuación: rho = x0*cos(theta) + y0*sin(theta)
    // Esto corresponde a una curva sinusoidal en el espacio (rho, theta) que es única para ese punto.
    // Si las curvas correspondientes a dos puntos se intersectan,
    // el punto de intersección en el espacio de Hough corresponde a una línea que pasa por estos dos puntos.
    // El algoritmo de la transformada de Hough usa una matriz, llamada acumulador,
    // cuya dimensión es el número de parámetros desconocidos, que en una línea recta son dos (rho, theta).
    // Por cada punto en la imagen, se buscan todas las posibles figuras a las que puede pertenecer ese punto.
    // Las líneas se pueden detectar buscando las posiciones del acumulador con mayor valor

    public static void processarImagen(String pathToImagen) {
        processarImagen(pathToImagen, 60, 30, 10);
    }
    public static void processarImagen(String pathToImagen, int threshold, double minLineLength, double maxLineGap) {

        // cargar la imagen que está en escala de grises (B/N)
        Mat matImg = Imgcodecs.imread(pathToImagen, Imgproc.COLOR_BGR2GRAY);

        // reducir ruido
        Imgproc.GaussianBlur(matImg, matImg, new Size(3,3), 0);

        // detección de bordes (Canny)
        Mat matEdges = new Mat();
        Imgproc.Canny(matImg, matEdges, 50, 150);

        // detectar las líneas paralelas mediante transformada de Hough
        Mat matLines = new Mat();

        // HoughLinesP: Devuelve segmentos, no líneas infinitas
        // Funciona mejor con líneas incompletas. Tolera grosor distinto
        // https://opencv-laboratory.readthedocs.io/en/latest/nodes/imgproc/HoughLinesP.html
        Imgproc.HoughLinesP(
                matEdges,
                matLines,
                // The resolution of the parameter rho in pixels. We use 1 pixel.
                1,
                // The resolution of the parameter theta in radians. We use 1 degree (CV_PI/180)
                Math.PI / 180,
                // The minimum number of intersections to “detect” a line. We use 30
                threshold,     // umbral bajo → líneas incompletas
                // The minimum number of points that can form a line.
                // Lines with less than this number of points are disregarded. We use 15
                minLineLength,     // longitud mínima
                // The maximum gap between two points to be considered in the same line. We use 5
                maxLineGap      // hueco máximo permitido
        );
        // Cada fila: [x1, y1, x2, y2]

        // Cargar segmentos
        segments = new ArrayList<>();
        for (int i = 0; i < matLines.rows(); i++) {
            segments.add(new Segment(matLines.get(i, 0)));
        }

        System.out.println("Segmentos detectados: " + segments.size());

        // Agrupar segmentos casi paralelos. Tolerancia angular (≈2–3°)
        double ANGLE_TOL = Math.toRadians(3);
        List<List<Segment>> groups = new ArrayList<>();
        for (Segment s : segments) {
            boolean added = false;

            for (List<Segment> g : groups) {
                if (Math.abs(s.angle - g.get(0).angle) < ANGLE_TOL) {
                    g.add(s);
                    added = true;
                    break;
                }
            }

            if (!added) {
                List<Segment> g = new ArrayList<>();
                g.add(s);
                groups.add(g);
            }
        }

        System.out.println("Grupos de segmentos: " + groups.size());

        // Al final deberías tener 2 grupos grandes (una por línea).
        // Ajustar una recta por grupo (mínimos cuadrados)
        // Convertimos segmentos en puntos y ajustamos:
        // Recta: ax + by + c = 0

        // Seleccionar las dos líneas correctas
        // Normalmente: Ordenas grupos por número de segmentos
        // Toma los 2 más grandes

        groups.sort((a, b) -> b.size() - a.size());

        lines = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            LineCar line = fitLine(groups.get(i));
            System.out.println(line);
            lines.add(line);
        }

        if (lines.size() < 2) {
            System.out.println("No se han detectado dos líneas.");
            return;
        } else {
            System.out.printf("Líneas detectadas: %d\n", lines.size());
            double distancia = lines.get(0).distanciaConLineaParalela(lines.get(1));
            System.out.printf("Distancia entre líneas: %.2f píxeles\n", distancia);
        }
    }

    private static LineCar fitLine(List<Segment> segs) {
        List<Point> pts = new ArrayList<>();

        for (Segment s : segs) {
            pts.add(new Point(s.x1, s.y1));
            pts.add(new Point(s.x2, s.y2));
        }

        MatOfPoint2f matPts = new MatOfPoint2f();
        matPts.fromList(pts);

        Mat matLine = new Mat();
        Imgproc.fitLine(matPts, matLine, Imgproc.DIST_L2, 0, 0.01, 0.01);

        double vx = matLine.get(0,0)[0];
        double vy = matLine.get(1,0)[0];
        double x0 = matLine.get(2,0)[0];
        double y0 = matLine.get(3,0)[0];

        LineCar l = new LineCar();
        l.a = -vy;
        l.b = vx;
        l.c = vy * x0 - vx * y0;

        return l;
    }

    public static void main(String[] args) {

        // Para poder ejecutar OpenCV en esta clase
        nu.pattern.OpenCV.loadLocally();

        String pathToImagen = "img/gelatina2_temp_roi.png";

        // Con los valores por defecto
        //Ocv.processarImagen(pathToImagen);

        // Probar con varios parámetros para ver distintos resultados y decidir valores por defecto
        int[] thresholds = {30, 60};
        int[] minLineLength = {15, 30};
        int[] maxLineGaps = {5, 10};
        for (int th : thresholds) {
            for (int minLen : minLineLength) {
                for (int maxGap : maxLineGaps) {
                    System.out.printf("\nProcesando con threshold=%d, minLineLength=%d, maxLineGap=%d\n",
                            th, minLen, maxGap);
                    System.out.println("-".repeat(80));
                    Ocv.processarImagen(pathToImagen, th, minLen, maxGap);
                }
            }
        }

    }

}
