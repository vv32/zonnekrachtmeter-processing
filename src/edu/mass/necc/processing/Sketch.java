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

    private Button tempButton = new Button(this, "Temperatuur", new ButtonAction() {
        @Override
        public void clicked() {
            activeGraphs = tempGraphs;
        }
    });

    private Button anemoButton = new Button(this, "Windsnelheid", new ButtonAction() {
        @Override
        public void clicked() {
            activeGraphs = anemoGraphs;
        }
    });
    
    private Button solarButton = new Button(this, "Irradiantie", new ButtonAction() {
        @Override
        public void clicked() {
            activeGraphs = solarGraphs;
        }
    });
    
    private List<Button> buttons = new ArrayList() {{
        add(tempButton);
        add(anemoButton);
        add(solarButton);
    }};
    
    private Serial serial = new Serial(this, Serial.list()[0], 2400);
    {
        serial.clear();
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
        
        for(Button button : buttons)
            if(button.isClicked())
                button.getButtonAction().clicked();
        
        activeGraphs.get(0).setTransform(width / 2 + 20, 20, width / 2 - 40, height / 2 - 40);
        activeGraphs.get(1).setTransform(20, height / 2 + 20, width / 2 - 40, height / 2 - 40);
        activeGraphs.get(2).setTransform(width / 2 + 20, height / 2 + 20, width / 2 - 40, height / 2 - 40);

        tempButton.setTransform(20, height / 2 - 60, (int) (width * 1/6f) - 20, 40);
        anemoButton.setTransform((int) (width * 1/6f) + 10, height / 2 - 60, (int) (width * 1/6f) - 20, 40);
        solarButton.setTransform((int) (width * 1/3f) + 0, height / 2 - 60, (int) (width * 1/6f) - 20, 40);
        
        if(selectedGraph != null)
            return;

        for(Graph graph : activeGraphs)
            if(graph.isClicked()) {
                graph.isFullscreen = true;
                selectedGraph = graph;
            }
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
        serial.write('a');
        String input = trim(serial.readString());
        try {
            if(input == null)
                return;
            connected();
            parseString(input);
        } catch(NumberFormatException e) {
            return;
        }
    }

    private int dctime;
    
    private void connected() {
        connected = true;
        dctime = 0;
    }
    
    private void disconnected() {
        if(dctime++ > 1) {
            connected = false;
            attemptReconnect();
        }
    }

    private void attemptReconnect() {
        try{
            serial = new Serial(this, Serial.list()[0], 2400);
        } catch(RuntimeException e) {}
    }
    
    private void parseString(String input) throws NumberFormatException {
        String[] values = input.split(",");
        if(values.length < 3)
            return;
        for(int i = 0; i < 3; i++) {
            addValueToGraph(allGraphs.get(i * 3), Float.parseFloat(values[i]));
        }
    }
    
    private void addValueToGraph(Graph graph, float value) {
        graph.addValue(value);
        if(graph.updateNext())
            addValueToGraph(allGraphs.get(allGraphs.indexOf(graph) + 1), graph.getAverage());
    }
    
    private boolean connected;

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
    public void draw() {
        update();
        super.background(192,197,206);
        drawLogo(Images.LOGO);      
        if(connected) {
            drawConnected();
        } else {
            drawDisconnected();
        }
        update();
    }
 
    private void drawDisconnected() {
        this.noTint();
        this.image(Images.CONNECTIONLOST, 0, 0);
    }
    
    private void drawConnected() {
        if(selectedGraph == null) {
            drawGraphs();
            drawButtons();
            drawInfo();
            drawCurrentValues();
        } else {
            drawGraph(selectedGraph);
        }   
    }
    
    private void drawLogo(PImage image) {
        tint(255, 80);
        super.image(image,
                (super.width /  2) - (image.pixelWidth / 2),
                (super.height / 2) - (image.pixelHeight / 2));
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
        super.text("Temperatuur: " + graph_temp_min.getLatestValue() + "°C", 20, 132);
        super.text("Windsnelheid: " + graph_anemo_min.getLatestValue() + "m/s", 20, 156);
        super.text("Irradiantie: " + graph_solar_min.getLatestValue() + "W/m²", 20, 180);
    }
    
    private void drawCurrentValues() {

    }
}