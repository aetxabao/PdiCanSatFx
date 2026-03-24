package edu.masanz.da.prog.pdi;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;

import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

public class PdiController {

    private Image originalImage;
    private Image transformedImage;

    private String pathToImage;

    private TextField txtP = new TextField();

    private Point p, p1, p2, p3, p4, p5, p6;

    private boolean isDrawingPoint = false;

    @FXML
    private BorderPane borderPane;

    @FXML
    private StackPane originalPane;

    @FXML
//    private ImageView originalView;
    private WrappedImageView originalView;

    @FXML
    private StackPane transformedPane;

    @FXML
//    private ImageView transformedView;
    private WrappedImageView transformedView;

    @FXML
    private CheckBox chkRoiMargins;

    @FXML
    private TextField txtLeft;

    @FXML
    private TextField txtRight;

    @FXML
    private TextField txtTop;

    @FXML
    private TextField txtBottom;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField txtBWThreshold;

    @FXML
    private TextField txtMinIntersections;

    @FXML
    private TextField txtMinLength;

    @FXML
    private TextField txtMaxGap;

    @FXML
    private TextField txtDistance;

    @FXML
    private TextField txtPosition;

    @FXML
    private TextField txtP1;
    @FXML
    private TextField txtP2;
    @FXML
    private TextField txtP3;
    @FXML
    private TextField txtP4;
    @FXML
    private TextField txtP5;
    @FXML
    private TextField txtP6;

    @FXML
    private Button btnP1;
    @FXML
    private Button btnP2;
    @FXML
    private Button btnP3;
    @FXML
    private Button btnP4;
    @FXML
    private Button btnP5;
    @FXML
    private Button btnP6;
    @FXML
    private Button btnL1;
    @FXML
    private Button btnL2;
    @FXML
    private Button btnL3;

    @FXML
    private TextField txtL1;
    @FXML
    private TextField txtL2;
    @FXML
    private TextField txtL3;

    @FXML
    public void initialize() {

        p = new Point();
        p1 = new Point();
        p2 = new Point();
        p3 = new Point();
        p4 = new Point();
        p5 = new Point();
        p6 = new Point();


//        File file = new File("img/gelatina2.jpg");
        File file = new File("img/pic303.jpg");
        if (file != null) {
            originalImage = new Image(file.toURI().toString());
            originalView.setImage(originalImage);
            transformedImage = Pdi.clone(originalImage);
            transformedView.setImage(transformedImage);
            pathToImage = file.getAbsolutePath();
        }

        colorPicker.setValue(Color.RED);

        txtBWThreshold.setText("16");

        txtDistance.setDisable(true);
        txtPosition.setDisable(true);

        chkRoiMargins.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
//                    System.out.println("Checkbox Ticked");
                    txtLeft.setDisable(false);
                    txtRight.setDisable(false);
                    txtTop.setDisable(false);
                    txtBottom.setDisable(false);
                } else {
//                    System.out.println("Checkbox UnTicked");
                    txtLeft.setDisable(true);
                    txtRight.setDisable(true);
                    txtTop.setDisable(true);
                    txtBottom.setDisable(true);
                }
            }
        });

        transformedView.setOnMouseClicked(
            event -> {
                if (originalImage == null) return;
                double clickX = event.getX();
                double clickY = event.getY();
                double imageWidth = transformedView.getBoundsInLocal().getWidth();
                double imageHeight = transformedView.getBoundsInLocal().getHeight();
                double imgX = (clickX / imageWidth) * transformedImage.getWidth();
                double imgY = (clickY / imageHeight) * transformedImage.getHeight();
                System.out.printf("Clicked at: (%.2f, %.2f)\n", imgX, imgY);
                if (isDrawingPoint) {
                    // p será el punto que se haya elegido con los botones de la interfaz
                    p.set(imgX, imgY);
                    txtP.setText(p.toString());
                    txtP.requestFocus();
                    txtP.positionCaret(0);
                    txtP.selectAll();
                    // Pintar
                    int w = (int) (transformedView.getFitWidth() / 50); // 2% del ancho de la imagen
                    int h = (int) (transformedView.getFitHeight() / 50); // 2% del alto de la imagen
                    int a = Math.max(w, h); // mayor
                    //System.out.printf("w=%d, h=%d, a=%d\n", w, h, a);
                    transformedImage = Pdi.drawPoint(transformedImage,  colorPicker.getValue(), p, a);
                    transformedView.setImage(transformedImage);
                    // luego se desvincula el punto
                    p = new Point();
                    txtP = new TextField();
                    isDrawingPoint = false;
                }
            }
        );

        transformedView.setOnMouseMoved(
            event -> {
                if (originalImage == null) return;
                double moveX = event.getX();
                double moveY = event.getY();
                double imageWidth = transformedView.getBoundsInLocal().getWidth();
                double imageHeight = transformedView.getBoundsInLocal().getHeight();
                double imgX = (moveX / imageWidth) * transformedImage.getWidth();
                double imgY = (moveY / imageHeight) * transformedImage.getHeight();
                txtPosition.setText(String.format("(%.0f, %.0f)", imgX, imgY));
            }
        );

        borderPane.setOnKeyPressed(
            event -> {
                System.out.println("Ctrl+Shift+X pressed");
                if (event.isControlDown() && event.isShiftDown()) {
                    switch (event.getCode()){
                        case DIGIT1: btnP1.requestFocus(); btnP1.fire(); break;
                        case DIGIT2: btnP2.requestFocus(); btnP2.fire(); break;
                        case DIGIT3: btnP3.requestFocus(); btnP3.fire(); break;
                        case DIGIT4: btnP4.requestFocus(); btnP4.fire(); break;
                        case DIGIT5: btnP5.requestFocus(); btnP5.fire(); break;
                        case DIGIT6: btnP6.requestFocus(); btnP6.fire(); break;
                        case DIGIT7: btnL1.requestFocus(); btnL1.fire(); break;
                        case DIGIT8: btnL2.requestFocus(); btnL2.fire(); break;
                        case DIGIT9: btnL3.requestFocus(); btnL3.fire(); break;
                    }
                }
            }
        );

    }

    @FXML
    void load(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar imagen");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            originalImage = new Image(file.toURI().toString());
            originalView.setImage(originalImage);
            transformedImage = Pdi.clone(originalImage);
            transformedView.setImage(transformedImage);
            pathToImage = file.getAbsolutePath();
        }
    }

    @FXML
    void reset(ActionEvent event) {
        if (originalImage != null) {
            transformedImage = Pdi.clone(originalImage);
            transformedView.setImage(transformedImage);
        }
    }



    @FXML
    void convertToGrayScale(ActionEvent event) {
        if (originalImage != null) {
            transformedImage = Pdi.convertToGrayScale(transformedImage, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void convertToColor(ActionEvent event) {
        if (originalImage != null) {
            Color color = colorPicker.getValue();
            transformedImage = Pdi.convertToColor(transformedImage, color, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void invertColor(ActionEvent event) {
        if (originalImage != null) {
            transformedImage = Pdi.invertColor(transformedImage, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void convertToEdges(ActionEvent event) {
        if (originalImage != null) {
            transformedImage = Pdi.convertToEdges(transformedImage, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

   @FXML
    void convertToBlur(ActionEvent event) {
        if (originalImage != null) {
            transformedImage = Pdi.convertToBlur(transformedImage, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void convertToBW(ActionEvent event) {
        if (originalImage != null) {
            int threshold = parseValue(txtBWThreshold.getText(), 0, 255, 64);
            transformedImage = Pdi.convertToBW(transformedImage, threshold, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void severalFilters(ActionEvent event) {
        if (originalImage != null) {
            Color color = colorPicker.getValue();
            int threshold = parseValue(txtBWThreshold.getText(), 0, 255, 64);
            transformedImage = Pdi.severalFilters(transformedImage, color, threshold, getRoiRect());
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void rotate(ActionEvent event) {
        if (originalImage != null) {
            transformedImage = Pdi.rotate(transformedImage);
            transformedView.setImage(transformedImage);
        }
    }

    @FXML
    void save(ActionEvent event) {
        if (transformedImage != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar imagen transformada");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            File file = fc.showSaveDialog(stage);
            if (file != null) {
                try {
                    BufferedImage bImage = SwingFXUtils.fromFXImage(transformedImage, null);
                    ImageIO.write(bImage, "png", file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    @FXML
    public void openCV(ActionEvent actionEvent) {
        String pathToImageOCV = saveRoiToTempFile();
        if (pathToImageOCV == null) {
            System.out.println("Error saving ROI to temp file.");
            return;
        }

        System.out.println("Path to temp ROI image: " + pathToImageOCV);

        int threshold = parseValue(txtMinIntersections.getText(), 0, 255, 30);
        txtMinIntersections.setText(String.valueOf(threshold));
        int minLineLength = parseValue(txtMinLength.getText(), 0, 255, 15);
        txtMinLength.setText(String.valueOf(minLineLength));
        int maxLineGap = parseValue(txtMaxGap.getText(), 0, 255, 5);
        txtMaxGap.setText(String.valueOf(maxLineGap));
        Ocv.processarImagen(pathToImageOCV, threshold, minLineLength, maxLineGap);

        Color color = colorPicker.getValue();
        List<LineCar> lines = Ocv.lines;
        transformedImage = Pdi.drawRoiLines(transformedImage, color, lines, 0, getRoiRect());

        transformedView.setImage(transformedImage);

        if (lines.size() < 2) {
            txtDistance.setText("N/A");
        } else {
            double distancia = lines.get(0).distanciaConLineaParalela(lines.get(1));
            txtDistance.setText(String.format("%.2f", distancia));
        }

    }

    private String saveRoiToTempFile() {
        try {
            Image image = transformedImage != null ? transformedImage : originalImage;
            if (image == null) { return null; }
            Rect roiRect = getRoiRect();
            image = Pdi.getRoiImage(image, roiRect);
            String tempFilePath = pathToImage.substring(0,pathToImage.lastIndexOf('.')) + "_temp_roi.png";
            File file = new File(tempFilePath);
            BufferedImage bufImg = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufImg, "png", file);
            return tempFilePath;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Rect getRoiRect() {
        Image image = transformedImage != null ? transformedImage : originalImage;
         if (image == null) {
             return null;
         }
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int left = 0;
        int top = 0;
        int right = width;
        int bottom = height;
        if (chkRoiMargins.isSelected()) {
            left = parseValue(txtLeft.getText(), 0, 100, 0);
            top = parseValue(txtTop.getText(), 0, 100, 0);
            right = parseValue(txtRight.getText(), 0, 100, 100);
            bottom = parseValue(txtBottom.getText(), 0, 100, 100);
            right = 100 - right < left ? 0 : right;
            bottom = 100 - bottom < top ? 0 : bottom;
            txtLeft.setText(String.valueOf(left));
            txtRight.setText(String.valueOf(right));
            txtTop.setText(String.valueOf(top));
            txtBottom.setText(String.valueOf(bottom));
            left = (left * width) / 100;
            top = (top * height) / 100;
            right = width - (right * width) / 100;
            bottom = height - (bottom * height) / 100;
        }
        Rect r = new Rect(left, top, right, bottom);
        //System.out.println("ROI Rect: " + r);
        return r;
    }

    private int parseValue(String textValue, int min, int max, int defaultValue) {
        try {
            int value =  Integer.parseInt(textValue);
            return value < min || value > max ? defaultValue : value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    @FXML
    public void drawPoint(ActionEvent actionEvent) {
        String s = ((Button)(actionEvent.getSource())).getText()
                .replace(" ", "")
                .toUpperCase();
        switch (s) {
            case "P1": p = p1; txtP = txtP1; break;
            case "P2": p = p2; txtP = txtP2; break;
            case "P3": p = p3; txtP = txtP3; break;
            case "P4": p = p4; txtP = txtP4; break;
            case "P5": p = p5; txtP = txtP5; break;
            case "P6": p = p6; txtP = txtP6; break;
        }
        isDrawingPoint = true;
    }


    @FXML
    public void drawLine(ActionEvent actionEvent) {
        TextField txtL = new TextField();
        Point pA = new Point(), pB = new Point();
        String s = ((Button)(actionEvent.getSource())).getText()
                .replace(" ", "")
                .toUpperCase();
        switch (s) {
            case "L1": pA = p1; pB = p2; txtL = txtL1; break;
            case "L2": pA = p3; pB = p4; txtL = txtL2; break;
            case "L3": pA = p5; pB = p6; txtL = txtL3; break;
        }

        if (s.equals("L3")) {
            double distance = pA.distance(pB);
            txtL.setText(String.format("%.2f px", distance));
        }else {
            LineScr line = new LineScr(pA, pB);
            txtL.setText(line.anguloInvertido() + "º");
        }
        transformedImage = Pdi.drawLine(transformedImage, colorPicker.getValue(), pA, pB);
        transformedView.setImage(transformedImage);
    }


}
