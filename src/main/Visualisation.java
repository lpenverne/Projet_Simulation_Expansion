package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import agent.Case;
import agent.Champs;
import agent.Foret;
import agent.Mine;
import agent.Nation;
import agent.Ville;
import constants.Constants;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.gui.SimpleColorMap;
import sim.util.media.chart.ChartGenerator;

import org.jfree.data.xy.XYSeries;

//import com.sun.org.apache.bcel.internal.classfile.Constant;


public class Visualisation extends GUIState {
	public static int FRAME_SIZE = 800;
	public Display2D display;
	
	//JFrames (fenetres)
	public JFrame displayFrame; //Fenetre simulation
	public JFrame chartPopFrame;
	public JFrame chartForceFrame;
	public JFrame chartCaseFrame;
	
	SparseGridPortrayal2D yardPortrayal = new SparseGridPortrayal2D();
	
	List<XYSeries> popNations = new ArrayList<XYSeries>();    // the data series we'll add to
	List<XYSeries> forceNations = new ArrayList<XYSeries>();  // the data series we'll add to
	List<XYSeries> caseNations = new ArrayList<XYSeries>(); 
	
    sim.util.media.chart.TimeSeriesChartGenerator chartPop;  // graphique pop
    sim.util.media.chart.TimeSeriesChartGenerator chartForce;
    sim.util.media.chart.TimeSeriesChartGenerator chartCase;
    
	public Visualisation(SimState state) {
		super(state);
	}
	public static String getName() {
		return "Simulation d'expansion de fronti�res"; 
	}
	public void start() {
	  super.start();
	  setupPortrayals();
	  chartPop.removeAllSeries();
	  chartPop.setXAxisLabel("Temps");
	  chartPop.setYAxisLabel("Population");
	  
	  chartForce.removeAllSeries();
	  chartForce.setXAxisLabel("Temps");
	  chartForce.setYAxisLabel("Force");
	  
	  chartCase.removeAllSeries();
	  chartCase.setXAxisLabel("Temps");
	  chartCase.setYAxisLabel("Etendu (nombre de cases)");
	  
	  initSeries(state);
	  addSeriesToChart();
	  
      scheduleRepeatingImmediatelyAfter((Steppable) new Steppable() {
    	  
    	  public void step(SimState state) {
			  Model model = (Model) state;
			  double x = state.schedule.time(); 
			  int i = 0;
			  
			  for (XYSeries serie : popNations) {
				  double y = model.nations.get(i).getPopulation();
				  serie.add(x, y, true);
				  i++;
			  }
			  i = 0;
			  for (XYSeries serie : forceNations) {
				  double y = model.nations.get(i).getForce();
				  serie.add(x, y, true);
				  i++;
			  }
			  i = 0;
			  for (XYSeries serie : caseNations) {
				  double y = model.nations.get(i).getNbCases();
				  serie.add(x, y, true);
				  i++;
			  }
			 
			  //update chart:
			  chartPop.updateChartWithin(state.schedule.getSteps(), 1000); 
			  chartForce.updateChartWithin(state.schedule.getSteps(), 1000); 
			  chartCase.updateChartWithin(state.schedule.getSteps(), 1000); 
//			  chartPop.updateChartLater(state.schedule.getSteps());
//			  chartForce.updateChartLater(state.schedule.getSteps());
//			  chartCase.updateChartLater(state.schedule.getSteps());
             }
         });
	}
	
	public void initSeries(SimState state) {
		Model model = (Model)state;
		int i = 0;
		for (Nation nat : model.nations) {
			//adding pop
			XYSeries serie = new XYSeries("popNation" + i,false);
			popNations.add(serie);
			
			//adding force
			serie = new XYSeries("forceNation" + i,false);
			forceNations.add(serie);
			
			//adding nb case
			serie = new XYSeries("etenduNation" + i,false);
			caseNations.add(serie);
			i++;
		}
	}
	
	public void addSeriesToChart() {
		for (XYSeries serie : popNations) {
			chartPop.addSeries(serie, null);
		}
		for (XYSeries serie : forceNations) {
			chartForce.addSeries(serie, null);
		}
		for (XYSeries serie : caseNations) {
			chartCase.addSeries(serie, null);
		}
	}
	

	public void load(SimState state) {
	  super.load(state);
	  setupPortrayals();
	}
	
	public void setupPortrayals() {
	  Model model = (Model) state;	
	  yardPortrayal.setField(model.yard );
	  yardPortrayal.setPortrayalForClass(Ville.class, new CircledPortrayal2D(getVillePortrayal(),0,0.5,null,true)); //Permet d'afficher la position et des propri�t�s de l'agent s�lectionn�
	  yardPortrayal.setPortrayalForClass(Champs.class, new CircledPortrayal2D(getChampsPortrayal(),0,0.5,null,true));
	  yardPortrayal.setPortrayalForClass(Mine.class, new CircledPortrayal2D(getMinePortrayal(),0,0.5,null,true));
	  yardPortrayal.setPortrayalForClass(Foret.class, new CircledPortrayal2D(getForetPortrayal(),0,0.5,null,true));
	  yardPortrayal.setPortrayalForClass(Case.class, new CircledPortrayal2D(getCasePortrayal(),0,0.5,null,true));
      yardPortrayal.setGridLines(true); // Lignes visibles
	  yardPortrayal.setGridModulus(1); // Lignes toutes les cellules
	  yardPortrayal.setGridLineFraction(0.001); // �paisseur
	  yardPortrayal.setGridColor(Color.gray); // couleur
	  display.reset();
	  display.setBackdrop(Color.white);
		// redraw the display
	  addBackgroundImage();
	  display.repaint();
	}
	
	private ImagePortrayal2D getVillePortrayal() {
		Image i = new ImageIcon(getClass().getResource("ville-mars.jpg")).getImage();
		ImagePortrayal2D r = new ImagePortrayal2D(i);
		//r.paint = Color.BLACK;
		r.filled = true;
		return r;
	}
	
	private ImagePortrayal2D getChampsPortrayal() {
		Image i = new ImageIcon(getClass().getResource("1f33d.png")).getImage();
		ImagePortrayal2D r = new ImagePortrayal2D(i);
		//r.paint = Color.BLACK;
		r.filled = true;
		return r;
	}
	
	private ImagePortrayal2D getMinePortrayal() {
		Image i = new ImageIcon(getClass().getResource("mine-2.png")).getImage();
		ImagePortrayal2D r = new ImagePortrayal2D(i);
		//r.paint = Color.BLACK;
		r.filled = true;
		return r;
	}
	
	private ImagePortrayal2D getForetPortrayal() {
		Image i = new ImageIcon(getClass().getResource("1f333.png")).getImage();
		ImagePortrayal2D r = new ImagePortrayal2D(i);
		//r.paint = Color.BLACK;
		r.filled = true;
		return r;
	}
	
	private RectanglePortrayal2D getCasePortrayal() {
		RectanglePortrayal2D r = new RectanglePortrayal2D()
        {
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        //Gr�ce � cette m�thode draw, la couleur des cases devrait �tre ajust�e automatiquement selon leur attribut couleur
            {
            Case newCase = (Case)object;
            paint = newCase.getColor();
            super.draw(object, graphics, info);
            }
        };
		return r;
	}
	
	public Object getSimulationInspectedObject() { return state; }
	public Inspector getInspector() {
	   Inspector i = super.getInspector();
	   i.setVolatile(true);
	   return i;
	}
	
	@SuppressWarnings("deprecation")
	public void init(Controller c) {
		  super.init(c);
		  display = new Display2D(FRAME_SIZE,FRAME_SIZE,this);
		  display.setClipping(false);
		  displayFrame = display.createFrame();
		  displayFrame.setTitle("Projet");
		  c.registerFrame(displayFrame); // so the frame appears in the "Display" list
		  displayFrame.setVisible(true);
		  display.attach( yardPortrayal, "Yard" );
		  
		  
		  chartPop = new sim.util.media.chart.TimeSeriesChartGenerator();
		  chartPop.setTitle("Population");
		  chartPopFrame = chartPop.createFrame();
		  chartPopFrame.setTitle("graphique_Pop");
		  chartPopFrame.setVisible(true);
		  chartPopFrame.pack();
	      c.registerFrame(chartPopFrame);
	      
	      chartForce = new sim.util.media.chart.TimeSeriesChartGenerator();
	      chartForce.setTitle("Force");
		  chartForceFrame = chartForce.createFrame();
		  chartForceFrame.setTitle("graphique_Force");
		  chartForceFrame.setVisible(true);
		  chartForceFrame.pack();
	      c.registerFrame(chartForceFrame);
	      
	      
	      chartCase = new sim.util.media.chart.TimeSeriesChartGenerator();
	      chartCase.setTitle("Etendu");
	      //ChartGenerator chart2 = (ChartGenerator)chartCase;
		  chartCaseFrame = chartCase.createFrame();
		  chartCaseFrame.setTitle("graphique_entendue");
		  //chartCaseFrame.setSize(Constants.GRAPHIQUES_LENGTH, Constants.GRAPHIQUES_HEIGHT);
		  chartCaseFrame.setVisible(true);
		  chartCaseFrame.pack();
	      c.registerFrame(chartCaseFrame);

		}
	
	private void addBackgroundImage() {
		Image i = new ImageIcon(getClass().getResource("mars2.jpg")).getImage();
		int w = i.getWidth(null);
		int h = i.getHeight(null);
		BufferedImage b = display.getGraphicsConfiguration().createCompatibleImage(w,h);
		Graphics g = b.getGraphics();
		g.drawImage(i,0,0,w,h,null);
		g.dispose();
		display.setBackdrop(new TexturePaint(b, new Rectangle(0,0,w,h)));
	}
}
