package edu.masanz.da.prog.pdi;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
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
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class PdiController {

    private Image originalImage;
    private Image transformedImage;

    private String pathToImage;

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
    public void initialize() {

        File file = new File("img/gelatina2.jpg");
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
        List<Line> lines = Ocv.lines;
        transformedImage = Pdi.drawRoiLines(transformedImage, color, lines, 0, getRoiRect());

        transformedView.setImage(transformedImage);

        if (lines.size() < 2) {
            txtDistance.setText("N/A");
        } else {
            double distancia = lines.get(0).distanciaConLineaParalela(lines.get(1));
            txtDistance.setText(String.format("%.2f", distancia));
        }

//        // ------------------------------
//        //  PRUEBAS DE DIBUJO DE LÍNEAS
//        // ------------------------------
//
//        Color color = colorPicker.getValue();
//        List<Line> lines = new ArrayList<>();
//
//////        // threshold=30, minLineLength=15, maxLineGap=5  <-- VALORES QUE MEJOR FUNCIONAN
////        lines.add(new Line(-0.03, 1, -126.07));
////        lines.add(new Line(0.05, 1, -281.12));
//
//        // threshold=60, minLineLength=30, maxLineGap=10
//        lines.add(new Line(-0.01, 1, -128.29));
//        lines.add(new Line(0.09, 1, -287.09));
//
//
////        public static Image drawRoiLines(Image image, Color color, List<Line> lines, int numberOfLines, Rect rect) {
//        transformedImage = Pdi.drawRoiLines(transformedImage, color, lines, 0, getRoiRect());
//
//        transformedView.setImage(transformedImage);
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

}
