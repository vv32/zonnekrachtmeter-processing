/**
* @author  Job Feikens
* @since   2017-05-16
*/
package edu.mass.necc.processing;

import java.util.stream.IntStream;
import org.gicentre.utils.stat.XYChart;

public class Graph extends Element {

    final private float[] values;
    
    final private String title;
    
    private XYChart graph;
 
    private int updateCount = 0;
    
    public Graph(Sketch sketch, float[] values, String title) {
        super(sketch);
        this.values = values;
        this.title = title;
    }

    public void setup(Sketch sketch) {
        graph = new XYChart(sketch);
        graph.setMinY(0);

        graph.setPointSize(1);
        graph.setLineWidth(2);
        graph.showXAxis(true);
        graph.showYAxis(true);
        setValues();
    }

    public void addValue(float value) {
        updateCount++;
        shiftArray();
        values[0] = value;
        setValues();
    }
    
    private void shiftArray() {
        for (int k = values.length - 1; k > 0; k--) 
            values[k]=values[k-1];
    }
    
    private void setValues() {
        int[] intArray = IntStream.range(0, values.length).toArray();
        float[] floatArray = new float[intArray.length];
        for(int i = 0; i < intArray.length; i++)
            floatArray[i] = intArray[i];
        graph.setData(floatArray, values);
    }
    
    public float getAverage() {
        float total = 0;
        int skip = 0;
        for(int i = 0; i < values.length; i++) {
            total += values[i];
            if(values[i] < 0.005 && values[i] > -0.005)
                skip++;
        }
        return total / (values.length - skip);
    }
    
    @Override
    public void draw() {
        if(mouseOver())
            sketch.fill(0, 255, 0, 130);
        else
            sketch.fill(0, 255, 0, 80);
        draw(x, y, width, height);
    }
    
    public void drawFullscreen() {
        sketch.fill(0, 255, 0, 80);
        draw(20, 20, sketch.width - 40, sketch.height - 40);
    }
    
    private void draw(int x, int y, int width, int height) {
        sketch.rect(x, y, width, height);
        sketch.fill(0, 0, 0);
        sketch.textSize(32);
        sketch.text(title + ":", x + 10, y + 34);
        sketch.fill(0, 0, 0);
        graph.draw(x + 60, y + 50, width - 90, height - 80);
    }
    
    public boolean updateNext() {
        if(values.length == 24)
            return false;
        if(updateCount >= values.length) {
            updateCount = 0;
            return true;
        }
        return false; 
    }
}
