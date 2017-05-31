/**
* @author  Job Feikens 360368
* @since   2017-05-16
*           Project Zonnekrachtmeter
*           Hanzehogeschool Groningen
*/
package edu.mass.necc.processing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import processing.core.PApplet;
import processing.core.PImage;
import processing.serial.Serial;

final public class Sketch extends PApplet {

    final private long startTime = System.currentTimeMillis();
    
    final private Graph graph_temp_min, graph_temp_hour, graph_temp_day,
                        graph_anemo_min, graph_anemo_hour, graph_anemo_day,
                        graph_solar_min, graph_solar_hour, graph_solar_day;
    {
        graph_temp_min = new Graph(this, new float[60], "Temperatuur (minuut)");
        graph_temp_hour = new Graph(this, new float[60], "Temperatuur (uur)");
        graph_temp_day = new Graph(this, new float[24], "Temperatuur (dag)");
        graph_anemo_min = new Graph(this, new float[60], "Windsnelheid (minuut)");
        graph_anemo_hour = new Graph(this, new float[60], "Windsnelheid (uur)");
        graph_anemo_day = new Graph(this, new float[24], "Windsnelheid (dag)");
        graph_solar_min = new Graph(this, new float[60], "Irradiantie (minuut)");
        graph_solar_hour = new Graph(this, new float[60], "Irradiantie (uur)");
        graph_solar_day = new Graph(this, new float[24], "Irradiantie (dag)");
    }
    
    final private List<Graph> tempGraphs = new ArrayList(){{
        add(graph_temp_min);
        add(graph_temp_hour);
        add(graph_temp_day);
    }};

    final private List<Graph> anemoGraphs = new ArrayList() {{
       add(graph_anemo_min);
       add(graph_anemo_hour);
       add(graph_anemo_day);
    }};
    
    final private List<Graph> solarGraphs = new ArrayList() {{
        add(graph_solar_min);
        add(graph_solar_hour);
        add(graph_solar_day);
    }};
    
    final private List<Graph> allGraphs = new ArrayList() {{
        addAll(tempGraphs);
        addAll(anemoGraphs);
        addAll(solarGraphs);
    }};
    
    private List<Graph> activeGraphs = tempGraphs;
    
    private Graph selectedGraph = null;

    final private Button tempButton = new Button(this, "Temperatuur", new ButtonAction() {
        @Override
        public void clicked(Button button) {
            activeGraphs = tempGraphs;
            setPressedButton(button);
        }
    });
    {
        tempButton.setPressed();
    }

    final private Button anemoButton = new Button(this, "Windsnelheid", new ButtonAction() {
        @Override
        public void clicked(Button button) {
            activeGraphs = anemoGraphs;
            setPressedButton(button);
        }
    });
    
    final private Button solarButton = new Button(this, "Irradiantie", new ButtonAction() {
        @Override
        public void clicked(Button button) {
            activeGraphs = solarGraphs;
            setPressedButton(button);
        }
    });

     final private Button backButton = new Button(this, "Terug", new ButtonAction() {
        @Override
        public void clicked(Button button) {
            selectedGraph.isFullscreen = false;
            selectedGraph = null;
        }
    });
    
    final private List<Button> buttons = new ArrayList() {{
        add(tempButton);
        add(anemoButton);
        add(solarButton);
    }};
    
    private void setPressedButton(Button button) {
        for(Button otherButton : buttons)
            otherButton.setUnpressed();
        button.setPressed();
    }
    
    private Serial serial;
    {
        attemptReconnect();
    }
    
    @Override
    public void settings() {
        super.size(1280, 720);
    }

    @Override
    public void setup() {
        super.surface.setResizable(true); 
        super.surface.setTitle("H&K Zonnekrachtmeter");
        for(Graph graph : allGraphs)
            graph.setup(this);
        Images.init(this);
        super.strokeWeight(0);
    }

    private void update() {
        activeGraphs.get(0).setTransform(width / 2 + 20, 20, width / 2 - 40, height / 2 - 40);
        activeGraphs.get(1).setTransform(20, height / 2 + 20, width / 2 - 40, height / 2 - 40);
        activeGraphs.get(2).setTransform(width / 2 + 20, height / 2 + 20, width / 2 - 40, height / 2 - 40);

        tempButton.setTransform(20, height / 2 - 60, (int) (width * 1/6f) - 20, 40);
        anemoButton.setTransform((int) (width * 1/6f) + 10, height / 2 - 60, (int) (width * 1/6f) - 20, 40);
        solarButton.setTransform((int) (width * 1/3f) + 0, height / 2 - 60, (int) (width * 1/6f) - 20, 40);
        backButton.setTransform(width - 154, 20, 134, 40);
    }

    final private TimerTask secondTask = new TimerTask() {
        @Override
        public void run() {
            secondPassed();
        }
    };  
    
    final private Timer timer = new Timer();
    {
        timer.schedule(secondTask, 1000, 1000);
    }

    private void secondPassed() {
        try {
            serial.write('a');
            serial.readStringUntil('b');
            String input = trim(serial.readStringUntil('c'));
            System.out.println(input);
            if(input == null) throw new NullPointerException();
            connected();
            parseString(input.split("c")[0]);
            serial.clear();
        } 
        catch(NumberFormatException | ArrayIndexOutOfBoundsException unparseable) {
            attemptReconnect();
        }
        catch(NullPointerException e) {
            disconnected();
        }
    }
    
    private boolean connected;
    private int dctime;
    
    private void connected() {
        connected = true;
        dctime = 0;
    }
    
    private void disconnected() {
        if(dctime++ > 1) {
            connected = false;
            if(attemptReconnect())
                connected();
        }
    }

    private boolean attemptReconnect() {
        try{
            serial = new Serial(this, Serial.list()[0], 2400);
            serial.clear();
            return true;
        } catch(RuntimeException disconnected) {
            return false;
        }
    }
    
    @SuppressWarnings("empty-statement")
    private void parseString(String input) throws NumberFormatException {
        String[] values = input.split(",");
        if(values.length == 3) {
            for(int i = 0, c = 0; i < 3; i++, c = 0)
                while(addValueToGraph(allGraphs.get(i * 3 + c++), Float.parseFloat(values[i])));
        }
    }
    
    private boolean addValueToGraph(Graph graph, float value) {
        synchronized(graph) {
            graph.addValue(value);
            return graph.updateNext();
        }
    }

    @Override
    public void keyPressed() {
        if(key == ESC) {
            if(selectedGraph != null) {
                selectedGraph.isFullscreen = false;
                selectedGraph = null;
            }
            key = 0;
        }
    }
    
    @Override
    public void mousePressed() {
        if(selectedGraph == null) {
            for(Button button : buttons)
                if(button.mouseOver())
                    button.getButtonAction().clicked(button);
            for(Graph graph : activeGraphs)
                if(graph.mouseOver()) {
                    graph.isFullscreen = true;
                    selectedGraph = graph;
                }
        }
        else 
            if(backButton.mouseOver())
                backButton.getButtonAction().clicked(backButton);
    }
    
    @Override
    public void draw() {
        update();
        super.background(192,197,206);
        drawLogo(Images.LOGO);      
        if(connected) {
            drawConnected();
        } else {
            drawDisconnected();
        }
        drawCredits();
    }
 
    private void drawDisconnected() {
        this.image(Images.CONNECTIONLOST, 0, 0);
    }
    
    private void drawConnected() {
        if(selectedGraph == null) {
            drawGraphs();
            drawButtons();
            drawInfo();
        } else {
            drawGraph(selectedGraph);
            drawBackButton();
        }   
    }
    
    private void drawLogo(PImage image) {
        tint(255, 80);
        super.image(image,
                (super.width /  2) - (image.pixelWidth / 2),
                (super.height / 2) - (image.pixelHeight / 2));
        super.noTint();
    }
    
    private void drawGraphs() {
        for(Graph graph : activeGraphs)
            graph.draw();
    }
    
    private void drawGraph(Graph graph) {
        graph.drawFullscreen();
    }
    
    private void drawButtons() {
        for(Button button : buttons)
            button.draw();
    }
    
    private void drawBackButton() {
        super.image(Images.INDENT, width - 165, 20);
        backButton.draw();
    }
    
    final private TimeZone tz = TimeZone.getTimeZone("UTC");
    final private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    {
        df.setTimeZone(tz);
    }
    
    private void drawInfo() {
        super.fill(0, 0, 0);
        super.textSize(20);
        super.text(df.format(new Date(System.currentTimeMillis() - startTime)), 20, 60);
        super.text("Verbonden met: " + serial.port.getPortName(), 20, 84);
        super.text("Temperatuur: " + String.format("%.2f", graph_temp_min.getLatestValue()) + "°C", 20, 132);
        float wind = graph_anemo_min.getLatestValue();
        super.text("Windsnelheid: " + wind + "m/s" + "\t " + VtoB(wind) + "B", 20, 156);
        super.text("Irradiantie: " + graph_solar_min.getLatestValue() + "W/m²", 20, 180);
    }
    
    private float VtoB(float v) {
        return (float) ((100*(Math.pow(v, (2/3f)))) / (9*(Math.pow(31, 2/3f))));
    }
    
    private void drawCredits() {
        super.textSize(12);
        super.text("© Job Feikens, Jelle Pek, Emiel de Vries, Sven Westerhof  |  Projectgroep 2 Elektronica  |  2016-2017, Hanzehogeschool Groningen", 2, height - 4);
    }
}