/**
* @author  Job Feikens
* @since   2017-05-16
*/
package edu.mass.necc.processing;

import processing.core.PApplet;
import processing.core.PImage;

public class Images {

    public static PImage LOGO;
    
    public static void init(PApplet sketch) {
        LOGO = sketch.loadImage("henklogo.png");
    }
}