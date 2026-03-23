module edu.masanz.da.prog.pdi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires opencv;

    opens edu.masanz.da.prog.pdi to javafx.fxml, javafx.graphics;
    exports edu.masanz.da.prog.pdi;
}