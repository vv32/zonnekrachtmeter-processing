package edu.mass.necc.processing;

import processing.core.PApplet;
import processing.core.PImage;

public class Images {

    public static PImage LOGO;
    
    public static void setup(PApplet sketch) {
        LOGO = sketch.loadImage("henklogo.png");
    }
}