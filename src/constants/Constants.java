package constants;

import java.awt.Color;
import java.util.*; 

import sim.util.gui.ColorMap;
import sim.util.gui.SimpleColorMap;

public class Constants {
//	public static int GRID_SIZE = 30;
//	private static Color[] listeCouleur = {new Color(1f,0f,0f,.5f), //Rouge
//											new Color(0f,0f,1f,.5f), //Bleu
//											new Color(1f,0.5f,0.5f,.5f), //Rose
//											new Color(0f,1f,0f,.5f), //Vert
//											new Color(0f,0.5f,0f,.5f), //Vert Fonc
//											new Color(1f,0f,1f,.5f), //Violet
//											new Color(1f,1f,0f,.75f), //Jaune
//											new Color(0f,1f,1f,.5f), //Cyan
//											new Color(1f,0.5f,0f,.5f), //Orange
//											new Color(0f,0f,0f,.5f)}; //Noir
//	public static int NUM_VILLE = listeCouleur.length;
//	public static int NUM_CHAMPS = 20 + (int) (Math.random()*(10));
//	public static int NUM_MINE = 20 +(int) (Math.random()*(10));
//	public static int NUM_FORET = 20 + (int) (Math.random()*(10));
//	
//	public static SimpleColorMap COLOR_MAP = new SimpleColorMap(listeCouleur);
//	public static int POP_DEBUT = 50;
//	public static int NOURRITURE_DEBUT = 49;
//	public static int PIERRE_DEBUT = 49;
//	public static int BOIS_DEBUT = 49;
//	public static int NOURRITURE_PAR_CHAMPS = 10;
//	public static int PIERRE_PAR_MINE = 10;
//	public static int BOIS_PAR_FORET = 10;
//	
//	public static int DEFAULT_NOURRITURE_PAR_TOUR = 10;
//	public static int DEFAULT_BOIS_PAR_TOUR = 10;
//	public static int DEFAULT_PIERRE_PAR_TOUR = 10;
//	
//	public static int LIMITE_EXTANSION = 3;
//	public static int LIMITE_QUANTITE_RESSOURCE = 10;
//	
//	public static int FACTEUR_FORCE_PRISE_VILLE = 2;
//	public static double FACTEUR_FORCE_PRISE_CASE = 1.5;
//	
//	public static int BOIS_CONSTRUCTION_VILLE = 300;
//	public static int PIERRE_CONSTRUCTION_VILLE = 300;
//	
//	-------------------------DEBUG CONFIG------------------------------
//	---------------------------------------------------------------------

	public static int GRID_SIZE = 100;
	public static int opacite = 140;
//	private static Color[] listeCouleur = {new Color(1f,0f,0f,.5f), //Rouge
//											new Color(0f,0f,1f,.5f), //Bleu
//											new Color(1f,0.5f,0.5f,.5f), //Rose
//											new Color(0f,1f,0f,.5f), //Vert
//											new Color(0f,0.5f,0f,.5f), //Vert Fonc
//											new Color(1f,0f,1f,.5f), //Violet
//											new Color(1f,1f,0f,.75f), //Jaune
//											new Color(0f,1f,1f,.5f), //Cyan
//											new Color(1f,0.5f,0f,.5f), //Orange
//											new Color(0f,0f,0f,.5f)}; //Noir
	private static Color[] listeCouleur = 
		{
				new Color(255,85,85,opacite), //Rouge
				new Color(85,85,255,opacite), //Bleu
				new Color(85,255,85,opacite), //Vert
				new Color(255,255,85,opacite), //Jaune
				new Color(255,85,255,opacite), //Violet
				new Color(85,255,255,opacite), //Turquoise
				new Color(255,175,175,opacite), //Rose
				new Color(128,128,128,opacite), //Gris
				new Color(192,0,0,opacite), //Rouge fonce
				new Color(0,0,192,opacite), //Bleu fonce
		}; 
	public static SimpleColorMap COLOR_MAP = new SimpleColorMap(listeCouleur);
	
	public static int NUM_VILLE = 10;
	public static int NUM_CHAMPS = 120;
	public static int NUM_MINE = 120;
	public static int NUM_FORET = 0;
	
	
	public static int POP_DEBUT = 50;
	public static int NOURRITURE_DEBUT = 50;
	public static int PIERRE_DEBUT = 50;
	public static int BOIS_DEBUT = 50;
	public static int NOURRITURE_PAR_CHAMPS = 10;
	public static int PIERRE_PAR_MINE = 10;
	public static int BOIS_PAR_FORET = 10;
	
	public static int DEFAULT_NOURRITURE_PAR_TOUR = 10;
	public static int DEFAULT_BOIS_PAR_TOUR = 10;
	public static int DEFAULT_PIERRE_PAR_TOUR = 10;
	
	public static int LIMITE_EXTANSION = 13;
	public static int LIMITE_QUANTITE_RESSOURCE = 10;
	
	public static double FACTEUR_CHANCE_FORME = 0.9;
	public static double COEF_NB_VILLE = 0.01;
	public static double FACTEUR_FORCE_PRISE_VILLE = 1.03;
	public static double FACTEUR_FORCE_PRISE_CASE = 1.01;
	public static double FACTEUR_PENALITE_NB_CASE_FORCE = 0.04;
	
	public static int BOIS_CONSTRUCTION_VILLE = 300;
	public static int PIERRE_CONSTRUCTION_VILLE = 300;
	public static int FACTEUR_MULTIPLICATIF_CONSTRUCTION_VILLE = 150;
	
	public static int GRAPHIQUES_HEIGHT = 300;
	public static int GRAPHIQUES_LENGTH = 600;
}

