package main;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import agent.Case;
import agent.Champs;
import agent.Foret;
import agent.Mine;
import agent.Nation;
import agent.Ville;
import constants.Constants;
import constants.RessourceType;
import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class Model extends SimState {
	public SparseGrid2D yard = new SparseGrid2D(Constants.GRID_SIZE, Constants.GRID_SIZE); //Changement en static
	private List<Boolean> freeLocations = new ArrayList<>();
	public List<Nation> nations = new ArrayList<Nation>();
	public List forceNations = new ArrayList();
	
	public Model(long seed) {
		super(seed);
	}
    // Transforme un row en ligne, colonne 
	// ex : grille = 100 * 100, row = 253, ligne = 2, colonne = 53
	// ex grille = 20 * 20, row = 178, ligne = 16, colonne = 18
	private Function<Integer,Int2D> locationFromRow = row -> new Int2D(row / Constants.GRID_SIZE, row % Constants.GRID_SIZE);
	
    
    // Indique si une place est libre
		Predicate<Integer> isFreeLocation = row -> freeLocations.get(row);

	public void start() {
		System.out.println("Simulation started \n"+"Nombre de villes : "+Constants.NUM_VILLE+"\n"+"Nombre de forets : "+Constants.NUM_FORET+"\n"+"Nombre de mines : "+Constants.NUM_MINE+"\n"+"Nombre de champs : "+Constants.NUM_CHAMPS+"\n");
		super.start();
		freeLocations = IntStream.range(0, Constants.GRID_SIZE*Constants.GRID_SIZE).mapToObj(p -> true)
				.collect(toList());
		yard.clear();
		addChamps();
		addForets();
		addMines();
		addVilles();
	}

	private void addVilles() {
		int couleur = 0;
		int i=0;
		while (i!=Constants.NUM_VILLE){
			int x=(int) (Math.random() * Constants.GRID_SIZE);
            int y=(int) (Math.random() * Constants.GRID_SIZE);
            if (yard.numObjectsAtLocation(x, y)==0)
            {
            	Nation newNation = new Nation(couleur); //On crée en même temps la nation à laquelle la ville appartiendra et on lui attribue une couleur parmi la liste
            	nations.add(newNation);
    			Ville ville = new Ville(newNation); //On crée la nouvelle ville
    			newNation.addCapitale(ville); //On attribue la ville à la liste de ville de la nation
    			couleur++; //On incrémente ce compteur afin d'avoir une nouvelle couleur pour la prochaine ville
    			
    			yard.setObjectLocation(ville,yard.stx(x),yard.sty(y));
    			ville.x = x;
    			ville.y = y;

    			//ville.setCoords(x,y);
    			Case caseDepart = addCase(x,y); //On crée l'agent Case avec la couleur de la nation.
    			caseDepart.setColor(newNation.getColor()); 
    			caseDepart.setProprietaire(ville); 
    			caseDepart.setVille();
    			
    			ville.setCaseDepart(caseDepart); //On attribue cette case à la liste de case de la ville nouvellement crée
    			//Peut être intéressant de garder un Stoppable si on décide de détruire une ville quand elle est conquise
    			Stoppable stoppableVille = schedule.scheduleRepeating(ville);
    			ville.stoppable = stoppableVille; 
    			i++;
            }
		}
	}

	private void addChamps() {
		int i=0;
		while(i!=Constants.NUM_CHAMPS)
		{
			int x=(int) (Math.random() * Constants.GRID_SIZE);
            int y=(int) (Math.random() * Constants.GRID_SIZE);
            if (yard.numObjectsAtLocation(x, y)==0)
            {
            	Champs champs = new Champs();
            	yard.setObjectLocation(champs,x,y);
    			champs.x = x;
    			champs.y = y;
    			Stoppable stoppable = schedule.scheduleRepeating(champs);
    			champs.stoppable = stoppable; 
    			i++;
            }
		}
	}

	private void addMines() {
		int i=0;
		while(i!=Constants.NUM_MINE)
		{
			int x=(int) (Math.random() * Constants.GRID_SIZE);
            int y=(int) (Math.random() * Constants.GRID_SIZE);
            if (yard.numObjectsAtLocation(x, y)==0)
            {
            	Mine mine = new Mine();
            	yard.setObjectLocation(mine,x,y);
    			mine.x = x;
    			mine.y = y;
    			Stoppable stoppable = schedule.scheduleRepeating(mine);
    			mine.stoppable = stoppable; 
    			i++;
            }
		}
	}

	private void addForets() {
		int i=0;
		while(i!=Constants.NUM_FORET)
		{
			int x=(int) (Math.random() * Constants.GRID_SIZE);
            int y=(int) (Math.random() * Constants.GRID_SIZE);
            if (yard.numObjectsAtLocation(x, y)==0)
            {
            	Foret foret = new Foret();
            	yard.setObjectLocation(foret,x,y);
            	foret.x = x;
            	foret.y = y;
    			Stoppable stoppable = schedule.scheduleRepeating(foret);
    			foret.stoppable = stoppable; 
    			i++;
            }
		}
	}
	
	
	public Case addCase(int x, int y) {
		Int2D location = new Int2D(x,y);
		Bag bag = new Bag(yard.getObjectsAtLocation(location));
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
			
    	Case a = new Case(x,y, rt);
		yard.setObjectLocation(a,x,y);
		return a;
	}
	//Changement en static

	
//	public boolean free(int x, int y) {
//		int xx = yard.stx(x);
//		int yy = yard.sty(y);
//		return yard.get(xx, yy) == null;
//	}

	private Int2D getFreeLocation() {
     Long freeSize = freeLocations.stream().filter(place -> place == true).collect(counting());
		
		Long placeForAgent = random.nextLong(freeSize);
		
		int place = 0; int position = -1;
		while (place <= placeForAgent) {
			position++;
			if (isFreeLocation.test(position)) {
				place++ ;
			}
		}
		Int2D p = locationFromRow.apply(position);
		return p;
	}

	public int getNumVille() {
		return Constants.NUM_VILLE;
	}
	
//	public List<Nation> getNations() {
//		return nations;
//	}
}
