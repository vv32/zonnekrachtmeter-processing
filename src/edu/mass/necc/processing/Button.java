/**
* @author  Job Feikens 360368
* @since   2017-05-16
*           Project Zonnekrachtmeter
*           Hanzehogeschool Groningen
*/
package edu.mass.necc.processing;

public class Button extends Element {

    final private String text;
    
    final private ButtonAction action;
    
    private boolean active = false;
    
    public Button(Sketch sketch, String text, ButtonAction action) {
        super(sketch);
        this.text = text;
        this.action = action;
    }

    @Override
    public void draw() {
        sketch.fill(100, 100, 100);
        sketch.rect(x + 4, y + 4, width, height, 3);
        int white = 255;    
        if(mouseOver())
            white -= 15;
        if(active)
            sketch.fill(white-20, white-20, white-20, 255);
        else
            sketch.fill(white, white, white, 255);
        sketch.rect(x, y, width, height, 3);
        sketch.fill(0, 0, 0, 255);    
        sketch.text(text, x + (width/2) - (sketch.textWidth(text) / 2), y + 26);
        if(active)
            sketch.line(x + (width/2) - (sketch.textWidth(text) / 2), y + 30, x + (width/2) + (sketch.textWidth(text) / 2), y + 30);
    }

    public ButtonAction getButtonAction() {
        return action;
    }
    
    public void setPressed() {
        active = true;
    }
    
    public void setUnpressed() {
        active = false;
    }
}