package edu.mass.necc.processing;

public class Label extends Element {

    public Label(Sketch sketch) {
        super(sketch);
    }

    private String text = "";
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public void draw() {
        sketch.textSize(20);
        sketch.text(text, x, y);
        sketch.rect(x, y, sketch.textWidth(text), 30, 3);
    }
}
