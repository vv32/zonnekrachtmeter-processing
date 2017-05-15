package edu.mass.necc.processing;

public class Graph {

    final private Sketch sketch;
    
    final private String title;
    
    public Graph(Sketch sketch, String title) {
        this.sketch = sketch;
        this.title = title;
    }

    private int x, y, width, height;
    
    public void setDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
    }
    
    private void draw(int x, int y, int width, int height) {
        sketch.rect(x, y, width, height);
        sketch.fill(0, 0, 0);
        sketch.textSize(32);
        sketch.text(title + ":", x + 10, y + 34);
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
