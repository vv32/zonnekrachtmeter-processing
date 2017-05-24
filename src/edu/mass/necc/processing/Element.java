/**
* @author  Job Feikens 360368
* @since   2017-05-16
*           Project Zonnekrachtmeter
*           Hanzehogeschool Groningen
*/
package edu.mass.necc.processing;

public abstract class Element {
    
    final protected Sketch sketch;
    
    public Element(Sketch sketch) {
        this.sketch = sketch;
    }
    
    protected int x, y, width, height;
    
    final public void setTransform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void draw();
    
    protected boolean mouseOver() {
        return sketch.mouseX > x &&
               sketch.mouseX < x + width &&
               sketch.mouseY > y &&
               sketch.mouseY < y + height;
    }
    
    final public boolean isClicked() {
        return mouseOver() && sketch.mousePressed;
    }
}
