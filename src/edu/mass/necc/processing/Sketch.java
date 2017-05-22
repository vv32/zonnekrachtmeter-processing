/**
* @author  Job Feikens
* @since   2017-05-16
*/
package edu.mass.necc.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import processing.core.PApplet;
import static processing.core.PApplet.trim;
import processing.core.PImage;
import processing.serial.Serial;

final public class Sketch extends PApplet {

    final private Graph graph_minute, graph_hour, graph_day;
    {
        graph_minute = new Graph(this, "Past minute");
        graph_hour = new Graph(this, "Past hour");
        graph_day = new Graph(this, "Past day");
    }
    
    final private List<Graph> graphs = new ArrayList(){{
        add(graph_minute);
        add(graph_hour);
        add(graph_day);
    }};

    private Serial serial = new Serial(this, Serial.list()[0], 2400);
    {
        serial.clear();
    }
    
    private Graph selectedGraph = null;
    
    @Override
    public void settings() {
        super.size(1280, 720);
    }

    @Override
    public void setup() {
        super.surface.setResizable(true);
        super.surface.setTitle("H&K Zonnekrachtmeter");
        for(Graph graph : graphs)
            graph.setup(this);
        Images.init(this);
        
    }

    private void update() {
        graph_minute.setTransform(width / 2 + 20, 20, width / 2 - 40, height / 2 - 40);
        graph_hour.setTransform(20, height / 2 + 20, width / 2 - 40, height / 2 - 40);
        graph_day.setTransform(width / 2 + 20, height / 2 + 20, width / 2 - 40, height / 2 - 40);
        graph_minute.setValues(secondValues);
        graph_hour.setValues(minuteValues);
        graph_day.setValues(hourValues);
        if(selectedGraph != null)
            return;
        for(Graph graph : graphs)
            if(graph.mouseOnGraph() && super.mousePressed)
                selectedGraph = graph;     
    }

    final private Timer timer = new Timer();
    {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                serial.write('a');
                String s = serial.readString();
                try {
                    if(s == null)
                        throw new NullPointerException();
                    connected = true;
                    float value = Float.parseFloat(trim(s));
                    newValue(value);
                } catch(NumberFormatException e) 
                {
                    
                } catch(NullPointerException e) 
                {
                    connected = false;
                    attemptReconnect();
                }
            }
        }, 1000, 1000);
    }

    private void attemptReconnect() {
        try{
            serial = new Serial(this, Serial.list()[0], 2400);
        } catch(RuntimeException e) {}
    }
    
    private boolean connected;
    
    final private float[] secondValues = new float[60];
    final private float[] minuteValues = new float[60];
    final private float[] hourValues = new float[24];
    
    private int secondCount, minuteCount;

    void newValue(float value) {   

        secondCount++;
        shiftArray(secondValues);
        secondValues[0] = value;
        if(secondCount == 60) {
            secondCount = 0;
            minuteCount++;
        } else {
            return;
        }

        shiftArray(minuteValues);
        minuteValues[0] = avg(secondValues);
        if(minuteCount == 60) {
            minuteCount = 0;
        } else {
            return;
        }

        shiftArray(hourValues);
        hourValues[0] = avg(minuteValues);
    }

    private void shiftArray(float[] array) {
        for (int k = array.length - 1; k > 0; k--) 
            array[k]=array[k-1];
    }

    private float avg(float[] array) {
        float total = 0;
        int skip = 0;
        for(int i = 0; i < array.length; i++) {
            total += array[i];
            if(array[i] < 0.005 && array[i] > -0.005)
                skip++;
        }
        return total / (array.length - skip);
    }

    @Override
    public void keyPressed() {
        if(key == ESC) {
            selectedGraph = null;
            key = 0;
        }
    }
    
    @Override
    public void draw() {
        update();
        super.background(200);
        drawLogo(Images.LOGO);      
        if(connected){
            drawConnected();
        } else {
            drawDisconnected();
        }
        update();

    }
 
    private void drawDisconnected() {

        this.text("DISCONNECTED", 10, 30);
    }
    
    private void drawConnected() {
        drawGraphs();
        drawCurrentValues();
    }
    
    private void drawLogo(PImage image) {
        tint(255, 80);
        super.image(image,
                (super.width /  2) - (image.pixelWidth / 2),
                (super.height / 2) - (image.pixelHeight / 2));
    }
    
    private void drawGraphs() {
        if(selectedGraph != null)
            selectedGraph.drawFullscreen();
        else
            for(Graph graph : graphs)
                graph.draw();
    }
    
    private void drawCurrentValues() {
        if(selectedGraph == null)
            this.text(secondValues[0] + " °C", 10, 30);
    }
}