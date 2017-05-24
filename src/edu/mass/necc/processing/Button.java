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
    
    public Button(Sketch sketch, String text, ButtonAction action) {
        super(sketch);
        this.text = text;
        this.action = action;
    }

    @Override
    public void draw() {
        sketch.fill(100, 100, 100);
        sketch.rect(x + 4, y + 4, width, height, 3);
        if(mouseOver())
            sketch.fill(240, 240, 240, 255);
        else
            sketch.fill(255, 255, 255, 255);
        sketch.rect(x, y, width, height, 3);
        sketch.fill(0, 0, 0);
        sketch.text(text, x + (width/2) - (sketch.textWidth(text) / 2), y + 26);
    }

    public ButtonAction getButtonAction() {
        return action;
    }
}