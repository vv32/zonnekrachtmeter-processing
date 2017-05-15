package edu.mass.necc.processing;

import processing.core.PApplet;
import processing.serial.Serial;

final public class Sketch extends PApplet {

    @Override
    public void settings() {
        super.size(1280, 720);
    }
    
    final private Serial myPort = new Serial(this, "/dev/ttyACM1", 2400);
    {
        myPort.bufferUntil('.');
    }

    @Override
    public void setup() {
        super.surface.setResizable(true);
        super.surface.setTitle("H&K Zonnekrachtmeter");
        super.smooth();
        Images.setup(this);
    }
    
    String data="";
    String potmeter="";
    float wPotmeter = 50;

    float[] arrayOfFloats = new float[200];
    
    @Override
    public void draw() {
        super.background(200);
        super.image(Images.LOGO, width-142, 0, 132, 100);
        tekenGrafiek();
    }
    
    private void tekenGrafiek() {
        for(int i=0; i<arrayOfFloats.length-1; i++)
            arrayOfFloats[i] = arrayOfFloats[i+1];

        arrayOfFloats[arrayOfFloats.length-1] = wPotmeter;

        for(int i=0; i<arrayOfFloats.length; i++) {
            noStroke();
            fill(0,255,0,255);
            ellipse(width*i/arrayOfFloats.length+width/200/2,height-arrayOfFloats[i],width/200,width/200);
        }
    }
    
    public void serialEvent(Serial myPort) {
        data = myPort.readStringUntil('.');
        data = data.substring(0, data.length()-1);
        wPotmeter = Float.parseFloat(data);
        wPotmeter = map(wPotmeter, 0, 4096, 0, height);
    }
}
