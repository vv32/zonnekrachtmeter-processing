/**
* @author  Job Feikens 360368
* @since   2017-05-16
*           Project Zonnekrachtmeter
*           Hanzehogeschool Groningen
*/
package edu.mass.necc.processing;

import processing.core.PApplet;
import processing.core.PImage;

public class Images {

    public static PImage LOGO;
    public static PImage CONNECTIONLOST;
    public static PImage INDENT;
    
    public static void init(Sketch sketch) {
        LOGO = sketch.loadImage("henklogo.png");
        CONNECTIONLOST = sketch.loadImage("connectionlost.png");
        INDENT = sketch.loadImage("indent.png");
    }
}