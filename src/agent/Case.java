package agent;

import java.awt.Color;

import constants.RessourceType;
import main.Model;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Int2D;

public class Case implements Steppable{
	public int x, y;
	public Color couleur;
	private boolean isChamps;
	private boolean isMine;
	private boolean isForet;
	private boolean isVille;
	public Ville proprietaire;
	
	public Stoppable stoppable;
	
	public Case() {}
	
	public Case(Case oldCase) {
		this.x = oldCase.x;
		this.y = oldCase.y;
		this.isChamps = oldCase.isChamps();
		this.isMine = oldCase.isMine();
		this.isForet = oldCase.isForet();
		this.proprietaire = oldCase.proprietaire;
		this.isVille = oldCase.isVille();
	}
	
	public Int2D getPosition() {
		return new Int2D(x,y);
	}
	
	public boolean isVille() {
		return isVille;
	}
	
	public void setVille() {
		this.isVille = true;
	}
	
	public boolean isChamps() {
		return isChamps;
	}

	public void setChamps(boolean isChamps) {
		this.isChamps = isChamps;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public boolean isForet() {
		return isForet;
	}

	public void setForet(boolean isForet) {
		this.isForet = isForet;
	}
	
	public Case(int x, int y, RessourceType rt) {
		this.x = x;
		this.y = y;
		switch(rt) {
			case MINE:
				setMine(true);
				break;
			case FORET:
				setForet(true);
				break;
			case CHAMPS:
				setChamps(true);
				break;
			default:
				break;
		}	
	}
	
	public void setColor(Color couleur) {
		this.couleur = couleur;
	}
	
	public Color getColor() {
		return this.couleur;
	}
	
	public Ville getProprietaire() {return this.proprietaire;}
	
	public void setProprietaire(Ville newProprietaire) {
		this.proprietaire = newProprietaire;
		this.setVille();
	}

	public void step(SimState state) {
		// TODO Auto-generated method stub
		Model model = (Model) state;
	}

}


