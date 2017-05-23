/**
* @author  Job Feikens
* @since   2017-05-16
*/
package edu.mass.necc.processing;

public class Button extends Element {

    final private ButtonAction action;
    
    public Button(Sketch sketch, ButtonAction action) {
        super(sketch);
        this.action = action;
    }

    @Override
    public void draw() {
        if(mouseOver())
            sketch.fill(0, 255, 0, 130);
        else
            sketch.fill(0, 255, 0, 80);
        sketch.rect(x, y, width, height);
    }

    public ButtonAction getButtonAction() {
        return action;
    }
}
