package edu.mass.necc.processing;

import processing.core.PApplet;

public class Sketch extends PApplet {

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

import processing.serial.*;
import java.awt.event.KeyEvent; 
Serial myPort;

String data="";
String potmeter="";
float wPotmeter = 50;

int num = 200;
float[] arrayOfFloats = new float[num];

PImage logo;

//int i;
void setup() {
  size(1200,800);
  surface.setResizable(true);
  smooth();
  background(0);
   myPort = new Serial(this,"COM4", 2400); 
   myPort.bufferUntil('.');
  logo = loadImage("henklogo.png");

}

 void draw() {
   background(200);
 image(logo, width-142, 00,132,100);
 tekenGrafiek();

 }
 
void serialEvent (Serial myPort){
  data = myPort.readStringUntil('.');
  data = data.substring(0,data.length()-1);
  wPotmeter = float(data);
  wPotmeter = map(wPotmeter,0, 4096,0, height);

 }  

void tekenGrafiek(){

  for (int i=0; i<arrayOfFloats.length-1; i++) {
    arrayOfFloats[i] = arrayOfFloats[i+1];
  }
 

  float newValue = wPotmeter;
 
 
  arrayOfFloats[arrayOfFloats.length-1] = newValue;
 
  
  for (int i=0; i<arrayOfFloats.length; i++) {
    noStroke();
    fill(0,255,0,255);
    ellipse(width*i/arrayOfFloats.length+width/num/2,height-arrayOfFloats[i],width/num,width/num);
  }
}
  
  
