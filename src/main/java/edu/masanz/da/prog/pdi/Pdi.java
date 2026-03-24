package edu.masanz.da.prog.pdi;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.List;

public class Pdi {


    public static Image clone(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage clonedImage = new WritableImage(width, height);
        PixelWriter writer = clonedImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, color);
            }
        }
        return clonedImage;
    }

    public static Image getRoiImage(Image image, Rect rect) {
        int width = rect.width() - 1;
        int height = rect.height() - 1;

        WritableImage clonedImage = new WritableImage(width, height);
        PixelWriter writer = clonedImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        for (int y = rect.top + 1; y < rect.bottom; y++) {
            for (int x = rect.left + 1; x < rect.right; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x - rect.left - 1, y - rect.top - 1, color);
            }
        }
        return clonedImage;
    }


    public static Image drawRoiLines(Image image, Color color, List<LineCar> roiLines, int numberOfLines, Rect rect) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage linedImage = new WritableImage(width, height);
        PixelWriter writer = linedImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        // Clonar la imagen original
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, c);
            }
        }

        System.out.println("Rect: " + rect.toString());

        // Dibujar las líneas
        int num = numberOfLines > roiLines.size() ? roiLines.size() : numberOfLines;
        num = num == 0 ? roiLines.size() : num;
        for (int i = 0; i < num; i++) {
            LineCar roiLine = roiLines.get(i);
            System.out.println("Line: " + roiLine);
            // Dibujar la línea ROI en la imagen
            for (int xRoi = 1; xRoi < rect.width(); xRoi++) {
                int yRoi = (int) Math.round(roiLine.getY(xRoi ));
                int xImg = xRoi + rect.left;
                int yImg = yRoi + rect.top;
                if (yImg > rect.top && yImg < rect.bottom && xImg > rect.left && xImg < rect.right) {
                    writer.setColor(xImg, yImg, color);
                }
            }
        }

        return linedImage;
    }

    public static Image convertToGrayScale(Image image, Rect rect) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage grayImage = new WritableImage(width, height);
        PixelWriter writer = grayImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                    double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                    color = new Color(gray, gray, gray, color.getOpacity());
                }

                writer.setColor(x, y, color);
            }
        }
        return grayImage;
    }

    public static Image convertToColor(Image imagenOriginal, Color dstColor, Rect rect) {
        int width = (int) imagenOriginal.getWidth();
        int height = (int) imagenOriginal.getHeight();

        WritableImage imagenTransformada = new WritableImage(width, height);
        PixelWriter writer = imagenTransformada.getPixelWriter();
        PixelReader reader = imagenOriginal.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                    // cogemos el valor de cada color en el pixel dado, valor de 0-255
                    double pixelRojo = color.getRed() * 255;
                    double pixelVerde = color.getGreen() * 255;
                    double pixelAzul = color.getBlue() * 255;
                    // multiplicamos cada canal por el color destino
                    pixelRojo *= dstColor.getRed();
                    pixelVerde *= dstColor.getGreen();
                    pixelAzul *= dstColor.getBlue();
                    // nos aseguramos de que no nos pasamos de los valores del 0 al 1.0
                    double r = Math.max(0, Math.min(255, pixelRojo)) / 255.0;
                    double g = Math.max(0, Math.min(255, pixelVerde)) / 255.0;
                    double b = Math.max(0, Math.min(255, pixelAzul)) / 255.0;
                    // y ahora genero el pixel otra vez con los colores manipulados
                    color = new Color(r, g, b, color.getOpacity());
                }
                // finalmente coloco ese pixel en la nueva imagen que estamos generando
                writer.setColor(x, y, color);
            }
        }

        return imagenTransformada;
    }

    public static Image invertColor(Image imagenOriginal, Rect rect) {
        int width = (int) imagenOriginal.getWidth();
        int height = (int) imagenOriginal.getHeight();

        WritableImage imagenTransformada = new WritableImage(width, height);
        PixelWriter writer = imagenTransformada.getPixelWriter();
        PixelReader reader = imagenOriginal.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                    // cogemos el valor de cada color en el pixel dado, valor de 0-255
                    double pixelRojo = color.getRed() * 255;
                    double pixelVerde = color.getGreen() * 255;
                    double pixelAzul = color.getBlue() * 255;
                    // restamos al 255 el color
                    pixelRojo = 255 - pixelRojo;
                    pixelVerde = 255 - pixelVerde;
                    pixelAzul = 255 - pixelAzul;
                    // nos aseguramos de que no nos pasamos de los valores del 0 al 1.0
                    double r = Math.max(0, Math.min(255, pixelRojo)) / 255.0;
                    double g = Math.max(0, Math.min(255, pixelVerde)) / 255.0;
                    double b = Math.max(0, Math.min(255, pixelAzul)) / 255.0;
                    // y ahora genero el pixel otra vez con los colores manipulados
                    color = new Color(r, g, b, color.getOpacity());
                }
                // finalmente coloco ese pixel en la nueva imagen que estamos generando
                writer.setColor(x, y, color);
            }
        }

        return imagenTransformada;
    }

    public static Image convertToBW(Image imagenOriginal, int threshold, Rect rect) {
        int width = (int) imagenOriginal.getWidth();
        int height = (int) imagenOriginal.getHeight();

        WritableImage imagenTransformada = new WritableImage(width, height);
        PixelWriter writer = imagenTransformada.getPixelWriter();
        PixelReader reader = imagenOriginal.getPixelReader();

        double r,g,b;
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                Color color = reader.getColor(x, y);
                if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                    int pixelRojo = (int) (color.getRed() * 255);
                    int pixelVerde = (int) (color.getGreen() * 255);
                    int pixelAzul = (int) (color.getBlue() * 255);
                    // Thresholding
//                int promedio = (pixelRojo + pixelVerde + pixelAzul) / 3;
//                if (promedio < 30) {
                    if (pixelRojo > threshold || pixelVerde > threshold || pixelAzul > threshold) {
                        r = 1;
                        g = 1;
                        b = 1;
                    } else {
                        r = 0;
                        g = 0;
                        b = 0;
                    }
                    // Y ahora genero el pixel otra vez con los colores manipulados
                    color = new Color(r, g, b, 1.0);
                }
                // Y finalmente coloco ese pixel en la nueva imagen que estamos generando
                writer.setColor(x, y, color);
            }
        }
        return imagenTransformada;
    }


    public static Image convertToEdges(Image imagenOriginal, Rect rect) {
        double[][] filtro = {
                {-1, -1, -1},
                {-1,  8, -1},
                {-1, -1, -1}
        };
        return applyConvolution(imagenOriginal, filtro, rect);
    }

    public static Image convertToBlur(Image imagenOriginal, Rect rect) {
        double[][] filtro = {
                {0.0625, 0.125, 0.0625},
                {0.125,  0.25, 0.125},
                {0.0625, 0.125, 0.0625}
        };
        return applyConvolution(imagenOriginal, filtro, rect);
    }

    private static Image applyConvolution(Image imagenOriginal, double[][] filtro, Rect rect) {
        int width = (int) imagenOriginal.getWidth();
        int height = (int) imagenOriginal.getHeight();

        WritableImage imagenTransformada = new WritableImage(width, height);
        PixelWriter writer = imagenTransformada.getPixelWriter();
        PixelReader reader = imagenOriginal.getPixelReader();

        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {

                Color color = reader.getColor(x, y);
                if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                    int pixelRojo = 0;
                    int pixelVerde = 0;
                    int pixelAzul = 0;
                    for (int f = y - 1; f <= y + 1; f++) {
                        for (int c = x - 1; c <= x + 1; c++) {
                            color = reader.getColor(c, f);
                            pixelRojo += (int) (color.getRed() * 255 * filtro[f - (y - 1)][c - (x - 1)]);
                            pixelVerde += (int) (color.getGreen() * 255 * filtro[f - (y - 1)][c - (x - 1)]);
                            pixelAzul += (int) (color.getBlue() * 255 * filtro[f - (y - 1)][c - (x - 1)]);
                        }
                    }
                    // Nos aseguramos de que no nos pasamos de los valores del 0 al 1.0
                    double r = Math.max(0, Math.min(255, pixelRojo)) / 255.0;
                    double g = Math.max(0, Math.min(255, pixelVerde)) / 255.0;
                    double b = Math.max(0, Math.min(255, pixelAzul)) / 255.0;
                    // Y ahora genero el pixel otra vez con los colores manipulados
                    color = new Color(r, g, b, 1.0);
                }
                // Y finalmente coloco ese pixel en la nueva imagen que estamos generando
                writer.setColor(x, y, color);
            }
        }
        return imagenTransformada;
    }

    public static Image severalFilters(Image originalImage, Color color, int threshold, Rect rect) {
        // color, suavizado, grises, bordes, b/n
        return  convertToBW(
                    convertToEdges(
                            convertToGrayScale(
                                    convertToBlur(
                                            convertToColor(
                                                    originalImage,
                                                    color,
                                                    rect),
                                            rect
                                    ),
                                    rect
                            ),
                            rect),
                threshold,
                rect);
    }

    public static Image rotate(Image originalImage) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        // Cambio ancho y alto para la nueva imagen
        WritableImage imagenTransformada = new WritableImage(height, width);
        PixelWriter writer = imagenTransformada.getPixelWriter();
        PixelReader reader = originalImage.getPixelReader();
        // Rotamos 90 grados
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Leemos el color
                Color color = reader.getColor(x, y);
//                // Observa el cambio (contra horario)
//                writer.setColor(y, width - x -1, color);
                // Observa el cambio (sentido horario)
                writer.setColor(height-1-y, x, color);
            }
        }
        return imagenTransformada;
    }

    public static Image drawPoint(Image image, Color color, Point p, int a) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage resImage = new WritableImage(width, height);
        PixelWriter writer = resImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        // Clonar la imagen original
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, c);
            }
        }

        int a1,a2;

        a1 = Math.max(0, (int) p.getX() - a);
        a2 = Math.min((int) p.getX() + a, width-1);
        for (int i = a1; i <= a2; i++) {
            writer.setColor(i, (int) p.getY(), color);
        }

        a1 = Math.max(0, (int) p.getY() - a);
        a2 = Math.min((int) p.getY() + a, height-1);
        for (int i = a1; i <= a2; i++) {
            writer.setColor((int) p.getX(), i, color);
        }

        return resImage;
    }


    public static Image drawLine(Image image, Color color, Point pA, Point pB) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage linedImage = new WritableImage(width, height);
        PixelWriter writer = linedImage.getPixelWriter();
        PixelReader reader = image.getPixelReader();

        // Clonar la imagen original
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, c);
            }
        }

        LineScr line = new LineScr(pA, pB);

        if (pA.getX() > pB.getX()) {
            Point temp = pA;
            pA = pB;
            pB = temp;
        }

        for (int x = (int) pA.getX(); x <= pB.getX(); x++) {
            int y = (int) Math.round(line.getY(x));
            if (y < 0) { continue; }
            if (y >= height) { continue;}
            writer.setColor(x, y, color);
        }

        if (pA.getY() > pB.getY()) {
            Point temp = pA;
            pA = pB;
            pB = temp;
        }

        for (int y = (int) pA.getY(); y <= pB.getY(); y++) {
            int x = (int) Math.round(line.getX(y));
            if (x < 0) { continue; }
            if (x >= width) { continue;}
            writer.setColor(x, y, color);
        }

        return linedImage;
    }
}

