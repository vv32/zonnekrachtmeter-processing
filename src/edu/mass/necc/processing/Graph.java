/**
* @author  Job Feikens
* @since   2017-05-16
*/
package edu.mass.necc.processing;

import java.util.stream.IntStream;
import org.gicentre.utils.stat.BarChart;
import org.gicentre.utils.stat.XYChart;

public class Graph {

    final private Sketch sketch;
    
    final private String title;
    
    private XYChart graph;
 
    public Graph(Sketch sketch, String title) {
        this.sketch = sketch;
        this.title = title;
    }

    public void setup(Sketch sketch) {
        graph = new XYChart(sketch);
        graph.setMinY(0);

        graph.setPointSize(1);
        graph.setLineWidth(2);
        graph.showXAxis(true);
        graph.showYAxis(true);
    }
    
    private int x, y, width, height;
    
    public void setTransform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setValues(float[] values) {
        graph.setData(values, values);
        int[] intArray = IntStream.range(0, values.length).toArray();
        float[] floatArray = new float[intArray.length];
        for(int i = 0; i < intArray.length; i++)
            floatArray[i] = intArray[i];
        graph.setData(floatArray, values);
    }
    
    public void draw() {
        if(mouseOnGraph())
            sketch.fill(0, 255, 0, 130);
        else
            sketch.fill(0, 255, 0, 80);
        draw(x, y, width, height);
    }
    
    public void drawFullscreen() {
        sketch.fill(0, 255, 0, 80);
        draw(20, 20, sketch.width - 40, sketch.height - 40);
        //graph.draw(20, 20, sketch.width, sketch.height);
    }
    
    private void draw(int x, int y, int width, int height) {
        sketch.rect(x, y, width, height);
        sketch.fill(0, 0, 0);
        sketch.textSize(32);
        sketch.text(title + ":", x + 10, y + 34);
        sketch.fill(0, 0, 0);
        graph.draw(x + 60, y + 50, width - 90, height - 80);
    }
    
    public boolean mouseOnGraph() {
        if(sketch.mouseX > x &&
                sketch.mouseX < x + width &&
                sketch.mouseY > y &&
                sketch.mouseY < y + height)
            return true;
        else
            return false;
    }
}
