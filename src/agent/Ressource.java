package agent;

import main.Model;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import agent.Nation;
import constants.Constants;
public class Ressource implements Steppable {
	public int x, y;
	public Stoppable stoppable;
	public boolean exploite = false;
	public Ville exploitant = null;
	public int quantite = 1 + (int) (Math.random()*(Constants.LIMITE_QUANTITE_RESSOURCE));
	
	public int getQuantite() {return this.quantite;}
	
	
	public void setExploite() {
		this.exploite = true;
	}
	
	public boolean getExploite() {return this.exploite;}
	
	public Ville getExploitant() {return this.exploitant;}
	
	public void setExploitant(Ville newExploitant) {this.exploitant = newExploitant;}
	
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		Model model = (Model) state;
	}
	

}
