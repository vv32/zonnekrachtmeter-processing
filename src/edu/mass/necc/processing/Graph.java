/**
* @author  Job Feikens 360368
* @since   2017-05-16
*           Project Zonnekrachtmeter
*           Hanzehogeschool Groningen
*/
package edu.mass.necc.processing;

import java.util.stream.IntStream;
import org.gicentre.utils.stat.XYChart;

public class Graph extends Element {

    final private float[] values;
    
    final private String title;
    
    private XYChart graph;
 
    private int updateCount = 0;
    
    public boolean isFullscreen;
    
    public Graph(Sketch sketch, float[] values, String title) {
        super(sketch);
        this.values = values;
        this.title = title;
        this.label = new Label(sketch);
    }

    public void setup(Sketch sketch) {
        graph = new XYChart(sketch);
        graph.setMinY(0);
        graph.setMaxX(values.length);
        
        graph.setPointSize(4);
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
        if(skip == values.length)
            return 0;
        return total / (values.length - skip);
    }

    @Override
    public void draw() {
        
        draw(x, y, width, height);
    }
    
    private boolean mouseOnGraph() {
        return 
                sketch.mouseX > 100 &&
                sketch.mouseX < sketch.width - 100 &&
                sketch.mouseY > 70 &&
                sketch.mouseY < sketch.height - 70;
    }
    
    public void drawFullscreen() {
        sketch.fill(0, 255, 0, 80);
        draw(20, 20, sketch.width - 40, sketch.height - 40);
        if(mouseOnGraph())
            drawLabel();
    }
    
    final private Label label;
    
    private void drawLabel() {
        int absMouseX = sketch.mouseX - 100;
        int graphWidth = sketch.width - 200;
        float factor = absMouseX / (float) graphWidth;
        int pos = (int) (factor * values.length);
        
        int graphHeight = sketch.height - 140;
        float y = sketch.height - (values[pos] / graph.getMaxY() * graphHeight) - 70;
        
        float x = pos / graph.getMaxX() * graphWidth;
        
        label.setTransform((int) x, (int) y, 0, 0);
        label.setText(Float.toString(values[pos]));
        label.draw();
    }
    
    private void draw(int x, int y, int width, int height) {
        sketch.fill(100, 100, 100);
        sketch.rect(x + 4, y + 4, width, height, 3);
        if(mouseOver())
            sketch.fill(235, 235, 235, 255);
        else
            sketch.fill(245, 245, 245, 255);
        sketch.rect(x, y, width, height, 3);
        sketch.fill(0, 0, 0);
        sketch.textSize(20);
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

    @Override
    public boolean mouseOver() {
        if(isFullscreen)
            return false;
        return super.mouseOver();
    }
    
    public float getLatestValue() {
        return values[0];
    }
}
