package edu.mass.necc.processing;

import processing.core.PApplet;

final public class Sketch extends PApplet {

    @Override
    public void settings() {
        super.size(1280, 720);
    }
    
    @Override
    public void setup() {
        super.surface.setResizable(true);
        super.frame.setTitle("H&K Zonnekrachtmeter");
    }
    
    @Override
    public void draw() {
        super.rect(100, 100, 100, 100);
    }
}