package agent;

import main.Model;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

import constants.Constants;
import constants.RessourceType;

import java.util.Random;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import agent.Nation;

public class Ville implements Steppable {
	public int x, y;
	public Stoppable stoppable;
	//"normalement" les champs privés sont en minuscules
	//en vrai osef mais go se mettre tous d'accord sur une seule convention de nommage
	private Nation nat;
	private int population = Constants.POP_DEBUT;
	private int stockNourriture = Constants.NOURRITURE_DEBUT;
	private int stockBois = Constants.BOIS_DEBUT;
	private int stockPierre = Constants.PIERRE_DEBUT;
	
	//Attributs pour les inspecteurs
	//private double forceNation = 0;
	//public double getForceNation() {return this.forceNation;}
	//public void setForceNation() {this.forceNation = this.getNation().getForce();}
	
	//private double popNation = 0;
	//public double getPopNation() {return this.popNation;}
	//public void setPopNation() {this.popNation = this.getNation().getPopulation();}
	
	public List<Case> listeCases = new ArrayList<Case>();

	public Ville(Nation nat) {
		this.nat = nat;
	}
	
	public void setCaseDepart(Case caseDepart) {
		listeCases.add(caseDepart);
	}
	
	
	public int getPopulation() {return population;}

	public int getNourriture() {return stockNourriture;}
	public int getPierre() {return stockPierre;}
	public int getBois() {return stockBois;}
	public Nation getNation() {return nat;}
	public int NbCases() {return listeCases.size();}
	public int getRessources() {return stockPierre + stockBois + stockNourriture;}
	
	public void setNation(Nation nat) {this.nat = nat;}

	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		Model model = (Model) state;
		taMereLeBug(model);
		cumulRessource(model);
		
		if(!constructionVille(model)) {
			ajoutCase(model);
		}

		croissance_pop();
	
		//Update des attributs pour les inspecteurs
//		this.setForceNation();
//		this.setPopNation();
	}
	
	public void taMereLeBug(Model model) {
		Int2D location = new Int2D(this.x, this.y);
		Bag bag = new Bag(model.yard.getObjectsAtLocation(location));
		Object[] liste = bag.toArray();
		Case c = null;
		Ville v = null;
		for (int k = 0; k < liste.length; k++) {
		    if((liste[k].getClass().getName().equals("agent.Case"))) {	
		    	c = ((Case) liste[k]);
		    }
		    else if((liste[k].getClass().getName().equals("agent.Ville"))) {	
		    	v = ((Ville) liste[k]);
		    }
		}
		model.yard.remove(c);
		model.yard.setObjectLocation(c, location);

	}
	
	public static RessourceType checkTypeRessourceInLocation(Int2D location, Model model) {
		Bag bag = new Bag(model.yard.getObjectsAtLocation(location));
		RessourceType rt = RessourceType.AUCUNE;
		if (bag!=null) {
			Object[] liste = bag.toArray();
			boolean temp = true;
			for (int k = 0; k < liste.length; k++) {
				String name = liste[k].getClass().getName();
			    switch(name) {
			    	case "agent.Mine":
			    		rt = RessourceType.MINE;
			    		break;
			    	case "agent.Foret":
			    		rt = RessourceType.FORET;
			    		break;
			    	case "agent.Champs":
			    		rt = RessourceType.CHAMPS;
			    		break;
			    	default:
			    		break;
			    }
			}
		}
		return rt;
	}
	
	private boolean isRessource(Model model, Int2D posCandidat) {
		//Verifier si la case possede une ressource
		SparseGrid2D grille = model.yard;
		Int2D pos = new Int2D(model.yard.stx(posCandidat.x),model.yard.sty(posCandidat.y));
		//On doit verifier qu'il n'y a pas de case nous appartenant sur cette case avant de l'ajouter
		Bag bag = new Bag(grille.getObjectsAtLocation(pos));
		if (bag!=null) {
			Object[] liste = bag.toArray();
			for (int k = 0; k < liste.length; k++) {
			    if(liste[k].getClass().getName().equals("agent.Mine") ||
			    	liste[k].getClass().getName().equals("agent.Foret") ||
			    	liste[k].getClass().getName().equals("agent.Champs")) {
			    		return true;
			    }
			}
		}
		return false;
	}
	
	private boolean isCaseEnnemie(Model model, Int2D posCandidat) {
		//Verifier si la case est une case ennemie
		SparseGrid2D grille = model.yard;
		Int2D pos = new Int2D(model.yard.stx(posCandidat.x),model.yard.sty(posCandidat.y));
		//On doit verifier qu'il n'y a pas de case nous appartenant sur cette case avant de l'ajouter
		Bag bag = new Bag(grille.getObjectsAtLocation(pos));
		if (bag!=null) {
			Object[] liste = bag.toArray();
			for (int k = 0; k < liste.length; k++) {
			    if((liste[k].getClass().getName().equals("agent.Case")) && (((Case) liste[k]).getProprietaire().nat != this.getNation())) {	
			    	return true;
			    }
			}
		}
		return false;
	}
	
	private boolean isVilleEnnemie(Model model, Int2D posCandidat) {
		//Verifier si la case est une ville ennemie
		SparseGrid2D grille = model.yard;
		Int2D pos = new Int2D(model.yard.stx(posCandidat.x),model.yard.sty(posCandidat.y));
		//On doit verifier qu'il n'y a pas de case nous appartenant sur cette case avant de l'ajouter
		Bag bag = new Bag(grille.getObjectsAtLocation(pos));
		if (bag!=null) {
			Object[] liste = bag.toArray();
			for (int k = 0; k < liste.length; k++) {
			    if(liste[k].getClass().getName().equals("agent.Ville") && ((Ville) liste[k]).getNation() != this.getNation()) {
			    	return true;
			    }
			}
		}
		return false;
	}
	
	private boolean isFrontiere(Model model, Int2D posCandidat) {
		//Verifier si la case est une frontiere du territoire
		SparseGrid2D grille = model.yard;
		int caseAToi = 0;
		for (int i=-1;i<2;i++) //Pour les cases autour de cette case
		{
			for (int j=-1;j<2;j++)
			{
				Int2D pos = new Int2D(model.yard.stx(posCandidat.x+i),model.yard.sty(posCandidat.y+j));
				//On recherche les cases a nous autour de cette case
				Bag bag = new Bag(grille.getObjectsAtLocation(pos));
				if (bag!=null) {
					Object[] liste = bag.toArray();
					for (int k = 0; k < liste.length; k++) {
					    if(liste[k].getClass().getName().equals("agent.Case")) {
					    	if(((Case) liste[k]).getProprietaire().nat == this.getNation()) {
					    		caseAToi ++;
					    	}
					    }
					}
				}
				else
					break;
			}	
		}
		return (caseAToi != 9 && caseAToi > 1);
	}
	
	private Case getCase(Model model, Int2D pos) {
		//Recuperer la case a cette position
		SparseGrid2D grille = model.yard;
		
		Bag bag = new Bag(grille.getObjectsAtLocation(pos));
		if (bag!=null) {
			Object[] liste = bag.toArray();
			for (int k = 0; k < liste.length; k++) {
			    if(liste[k].getClass().getName().equals("agent.Case")) {
			    	return (Case) liste[k];
			    }
			}
		}
		return null;
	}
	
	private Ville getVille(Model model, Int2D pos) {
		//Recuperer la case a cette position
		SparseGrid2D grille = model.yard;
		
		Bag bag = new Bag(grille.getObjectsAtLocation(pos));
		if (bag!=null) {
			Object[] liste = bag.toArray();
			for (int k = 0; k < liste.length; k++) {
			    if(liste[k].getClass().getName().equals("agent.Ville")) {
			    	return (Ville) liste[k];
			    }
			}
		}
		return null;
	}
	
	private Ressource getRessource(Model model, Int2D pos) {
		//Recuperer la case a cette position
		SparseGrid2D grille = model.yard;
		
		Bag bag = new Bag(grille.getObjectsAtLocation(pos));
		if (bag!=null) {
			Object[] liste = bag.toArray();
			for (int k = 0; k < liste.length; k++) {
			    if(liste[k].getClass().getName().equals("agent.Champs") ||
		    		liste[k].getClass().getName().equals("agent.Mine") ||
		    		liste[k].getClass().getName().equals("agent.Foret")) {
			    	return (Ressource) liste[k];
			    }
			}
		}
		return null;
	}
	
	private boolean caseANous(Model model, Int2D pos) {
		//Verifier si la case est une case ennemie
		if(!Objects.isNull(getCase(model,pos)))
			//return getCase(model,pos).getProprietaire()==this;
			return getCase(model,pos).getProprietaire().nat == nat;
		else return false;
	}
	
	int ToroidalDistance (int x1, int y1, int x2, int y2)
	{
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
	 
	    if (dx > Constants.GRID_SIZE / 2)
	        dx = Constants.GRID_SIZE - dx;
	 
	    if (dy > Constants.GRID_SIZE / 2)
	        dy = Constants.GRID_SIZE - dy;
	 
	    return (int) Math.sqrt(dx*dx + dy*dy);
	}
	
	private boolean isPositionInRange(Model model, Int2D pos) {
		//Distance de Manhattan
		return (ToroidalDistance(this.x,this.y,pos.x,pos.y) < Constants.LIMITE_EXTANSION);
	}
	
//	private boolean isPositionInRange(Model model, Int2D pos) {
//		//Distance de Manhattan
//		return (Math.abs(model.yard.stx(this.x) - model.yard.stx(pos.x)) + 
//				Math.abs(model.yard.sty(this.y) - model.yard.sty(pos.y)) < Constants.LIMITE_EXTANSION);
//	}
	
	private void ajoutCase(Model model) {
		SparseGrid2D grille = model.yard;
		
		//Initialisation des listes
		List<Int2D> listePosVide = new ArrayList<Int2D>();
		List<Int2D> listePosRessource = new ArrayList<Int2D>();
		List<Int2D> listePosRessourceEnnemie = new ArrayList<Int2D>();
		List<Int2D> listePosEnnemie = new ArrayList<Int2D>();
		List<Int2D> listePosVilleEnnemie = new ArrayList<Int2D>();
		
		for (Case _case : listeCases) {
			if(isFrontiere(model, new Int2D(_case.x,_case.y)) || !Objects.isNull(getVille(model,new Int2D(_case.x,_case.y)))) {
				//Remplissage des listes
				for (int i=-1;i<2;i++) //Pour les cases autour de cette case
				{
					for (int j=-1;j<2;j++)
					{
						Int2D pos = new Int2D(model.yard.stx(_case.x+i),model.yard.sty(_case.y+j));
						if(isPositionInRange(model,pos) && !caseANous(model,pos)) {
							if(isVilleEnnemie(model,pos)) 
								listePosVilleEnnemie.add(pos);
							else if(isCaseEnnemie(model,pos) && isRessource(model,pos))
								listePosRessourceEnnemie.add(pos);
							else if(isRessource(model,pos))
								listePosRessource.add(pos);
							else if(isCaseEnnemie(model,pos))
								listePosEnnemie.add(pos);
							else 
								listePosVide.add(pos);
						}
					}
				}
			}
		}
		//Analyse des listes
		Int2D bestPos = null;
		double force = this.getNation().getForce();
		if(listePosVilleEnnemie.size()>0) {
			double bestForceEnnemie = 0;
			for(Int2D _pos : listePosVilleEnnemie) {
				Case tmp = getCase(model,_pos);
				double forceEnnemie = tmp.getProprietaire().getNation().getForce();
				if(force > Constants.FACTEUR_FORCE_PRISE_VILLE * forceEnnemie && bestForceEnnemie<forceEnnemie) {
					bestPos = _pos;
					bestForceEnnemie = forceEnnemie;
				}	
			}
			if(bestPos!=null) {
				//On conquiert la case
				conqueteVille(model,bestPos);
				return;
			}		
		}
		if(listePosRessourceEnnemie.size()>0) {
			double bestQuantite = 0;
			for( Int2D _pos : listePosRessourceEnnemie) {
				Ressource tmp = getRessource(model,_pos);
				RessourceType rt = checkTypeRessourceInLocation(_pos, model);
				Case tmpCase = getCase(model,_pos);
				double forceEnnemie = tmpCase.getProprietaire().getNation().getForce();
				if(force > Constants.FACTEUR_FORCE_PRISE_CASE * forceEnnemie && bestQuantite < ((Ressource) tmp).getQuantite()) {
					bestPos = _pos;
					bestQuantite = ((Ressource) tmp).getQuantite();
				}	
			}
			if(bestPos!=null) {
				conqueteCase(model,bestPos);
				return ;
			}
		}
		if(listePosRessource.size()>0) {
			double bestQuantite = -1;
			for( Int2D _pos : listePosRessource) {
				Ressource tmp = getRessource(model,_pos);
				RessourceType rt = checkTypeRessourceInLocation(_pos, model);
				Case tmpCase = getCase(model,_pos);
				if(bestQuantite < ((Ressource) tmp).getQuantite()) {
					bestPos = _pos;
					bestQuantite = ((Ressource) tmp).getQuantite();
				}	
			}
			RessourceType rt = checkTypeRessourceInLocation(bestPos, model);
			Case newCase = new Case(bestPos.x,bestPos.y, rt);
			model.yard.setObjectLocation(newCase, bestPos);
			newCase.setColor(this.getNation().getColor());
			newCase.setProprietaire(this);
			listeCases.add(newCase);
			return ;
		}
		if(listePosEnnemie.size()>0) {
			double bestForceEnnemie = 0;
			for( Int2D _pos : listePosEnnemie) {
				Case tmp = getCase(model,_pos);
				double forceEnnemie = tmp.getProprietaire().getNation().getForce();
				if(force > Constants.FACTEUR_FORCE_PRISE_CASE * forceEnnemie && bestForceEnnemie<forceEnnemie) {
					bestPos = _pos;
					bestForceEnnemie = forceEnnemie;
				}	
			}
			if(bestPos!=null) {
				conqueteCase(model,bestPos);
				return ;
			}
		}
		if(listePosVide.size()>0) {
			int r = new Random().nextInt(listePosVide.size());
			Int2D newPos = listePosVide.get(r);
			Case newCase = new Case(newPos.x,newPos.y, RessourceType.AUCUNE);
			model.yard.setObjectLocation(newCase, newPos);
			newCase.setColor(this.getNation().getColor());
			newCase.setProprietaire(this);
			listeCases.add(newCase);
			return ;
		}
	}
	
	public void conqueteVille(Model model, Int2D posBataille) {
		//Les steps a faire quand on conquiert une ville ennemie
		//1) Retirer la ville de la liste de ville de la nation ennemie
		//2) L'ajouter dans la liste de ville de notre nation
		//3) Modifier les couleurs des cases de son territoire
		//4) Repeter les etapes de conqueteCase
		Case bestCase = getCase(model, posBataille);
		Ville villeEnnemie = getVille(model, posBataille);
		Nation nationEnnemie = villeEnnemie.getNation();
		Nation notreNation = this.getNation();
		
		nationEnnemie.listeVilles.remove(villeEnnemie);
		villeEnnemie.setNation(notreNation);
		notreNation.listeVilles.add(villeEnnemie);
		
		for(Case _case : villeEnnemie.listeCases) {
			_case.setColor(this.getNation().getColor());
			//_case.setProprietaire(this);
			//this.listeCases.add(_case);
			//proprietaire.listeCases.remove(_case);
		}
		
	}
	
	public void conqueteCase(Model model, Int2D posBataille) {
		//Les steps a faire quand on conquiert une case ennemie :
		// 1) Retirer la case de la liste de son ancien proprietaire
		// 2) Ajouter cette case dans la liste de sa nouvelle ville, changer son propriétaire et sa couleur
		Case bestCase = getCase(model, posBataille);
		
//		System.out.println("La castagne est en : "+bestCase.x+bestCase.y);
//		System.out.println("La castagne est sur la case " + bestCase);
		Ville proprietaire = bestCase.getProprietaire();
		proprietaire.listeCases.remove(bestCase);
		bestCase.setColor(this.getNation().getColor());
		bestCase.setProprietaire(this);
		this.listeCases.add(bestCase);
	}
	
	private boolean constructionVille(Model model) {
		double boisnecessaire = nat.getRessourceConquete();
		double pierrenecessaire = nat.getRessourceConquete();
		if(stockBois >= boisnecessaire && stockPierre >= pierrenecessaire) {
			Case caseOfNewVille = getRandomCaseWithoutRessources(model);
			if(caseOfNewVille == null) {
				return false;
			}
			//System.out.println("je suis" + this.nat + " je construit en " + caseOfNewVille.getPosition());
			Bag bag = model.yard.removeObjectsAtLocation(caseOfNewVille.x, caseOfNewVille.y);
			Ville newVille = new Ville(this.nat);
			model.yard.setObjectLocation(newVille,caseOfNewVille.x,caseOfNewVille.y);
			newVille.x = caseOfNewVille.x;
			newVille.y = caseOfNewVille.y;
			if (bag!=null) {
				Object[] liste = bag.toArray();
				for (int k = 0; k < liste.length; k++) {
				    if(liste[k].getClass().getName().equals("agent.Case")) {
				    	model.yard.setObjectLocation(liste[k], new Int2D(caseOfNewVille.x, caseOfNewVille.y));
				    }
				}
			}
			
			caseOfNewVille.setProprietaire(newVille); 
			newVille.setCaseDepart(caseOfNewVille); 
			caseOfNewVille.setColor(nat.getColor()); 
			caseOfNewVille.setVille();
			listeCases.remove(caseOfNewVille); //On retire la case de l'agent actuel car elle appartient desormais a la nouvelle ville
			Stoppable stoppableVille = model.schedule.scheduleRepeating(newVille);
			newVille.stoppable = stoppableVille; 
			this.nat.listeVilles.add(newVille);
			stockBois = (int) (stockBois - boisnecessaire);
			stockPierre = (int) (stockPierre - pierrenecessaire);
			return true;
		}
		return false;
	}

	
	private Case getRandomCaseWithoutRessources(Model model) {
		//On choppe les case sans ressources et on renvoie une au hasard
		List<Case> listeTmp = new ArrayList<Case>();
		for (Case _case : listeCases) {
			if(!_case.isChamps() && !_case.isForet() && !_case.isMine() && Objects.isNull(getVille(model,new Int2D(_case.x,_case.y))) && isFrontiere(model, _case.getPosition())) {
				listeTmp.add(_case);
			}
		}
		if(!listeTmp.isEmpty()) {
			int r = new Random().nextInt(listeTmp.size());
			Case randomCase = listeTmp.get(r);
			return randomCase;
		}
		else return null;
	}
	
	public void croissance_pop() {
		population += (int)(0.1*stockNourriture);
		stockNourriture = Math.max((int) Math.ceil(stockNourriture - 0.1*stockNourriture),0); 
	}
	
	public void cumulRessource(Model model) {
		stockNourriture += Constants.DEFAULT_NOURRITURE_PAR_TOUR;
		stockBois += Constants.DEFAULT_BOIS_PAR_TOUR;
		stockPierre += Constants.DEFAULT_PIERRE_PAR_TOUR;
		
		for (Case _case : listeCases) {
			if(_case.isChamps()) {
				Int2D location = new Int2D(_case.x,_case.y);
				stockNourriture += getQuantiteRessource(model, location);
			}
			if(_case.isMine()) {
				Int2D location = new Int2D(_case.x,_case.y);
				stockPierre += getQuantiteRessource(model, location);
			}
			if(_case.isForet()) {
				Int2D location = new Int2D(_case.x,_case.y);
				stockBois += getQuantiteRessource(model, location);
			}
		}
	}

	public int getQuantiteRessource(Model model, Int2D position) {
		Bag bag = new Bag(model.yard.getObjectsAtLocation(position));
		int quantite = 0;
		if (bag!=null) {
			Object[] liste = bag.toArray();
			boolean temp = true;
			for (int k = 0; k < liste.length; k++) {
				if(liste[k].getClass().getName().equals("agent.Champs")){
					Champs res = (Champs) liste[k];
					if(!res.getExploite()) {
						res.setExploite();
						res.setExploitant(this);
					}else {
						if(res.getExploitant()==this)
							quantite = res.quantite;
					}
				}
				if(liste[k].getClass().getName().equals("agent.Mine")){
					Mine res = (Mine) liste[k];
					if(!res.getExploite()) {
						res.setExploite();
						res.setExploitant(this);
					}else {
						if(res.getExploitant()==this)
							quantite = res.quantite;
					}
				}
				if(liste[k].getClass().getName().equals("agent.Foret")){
					Foret res = (Foret) liste[k];
					if(!res.getExploite()) {
						res.setExploite();
						res.setExploitant(this);
					}else {
						if(res.getExploitant()==this) {
							quantite = res.quantite;
						}
							
					}
				}
			}
		}
		return quantite;
	}
	
}
