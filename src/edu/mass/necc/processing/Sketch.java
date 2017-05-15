package edu.mass.necc.processing;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import processing.core.PImage;

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

    private Graph selectedGraph = null;
    
    @Override
    public void settings() {
        super.size(1280, 720);
    }

    @Override
    public void setup() {
        super.surface.setResizable(true);
        super.surface.setTitle("H&K Zonnekrachtmeter");
        Images.init(this);
    }

    private void update() {
        graph_minute.setDimensions(width / 2 + 20, 20, width / 2 - 40, height / 2 - 40);
        graph_hour.setDimensions(20, height / 2 + 20, width / 2 - 40, height / 2 - 40);
        graph_day.setDimensions(width / 2 + 20, height / 2 + 20, width / 2 - 40, height / 2 - 40);
        if(selectedGraph != null)
            return;
        for(Graph graph : graphs)
            if(graph.mouseOnGraph() && super.mousePressed)
                selectedGraph = graph;     
    }

    @Override
    public void keyPressed() {
        if(super.key == ESC) {
            selectedGraph = null;
            key = 0;
        }
    }
    
    @Override
    public void draw() {
        update();
        super.background(200);
        drawLogo(Images.LOGO);
        drawGraphs();
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
}
 