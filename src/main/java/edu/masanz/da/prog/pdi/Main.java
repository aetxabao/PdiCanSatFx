package edu.masanz.da.prog.pdi;

public class Main {


    // cargar la librería nativa:
    static {
        nu.pattern.OpenCV.loadLocally();
    }


    public static void main(String[] args) {
        AppPdi.main(args);
    }

}
