package edu.masanz.da.prog.pdi;

class Segment {
    double x1, y1, x2, y2;
    double angle;

    Segment(double[] l) {
        x1 = l[0];
        y1 = l[1];
        x2 = l[2];
        y2 = l[3];
        angle = Math.atan2(y2 - y1, x2 - x1);
    }
}

