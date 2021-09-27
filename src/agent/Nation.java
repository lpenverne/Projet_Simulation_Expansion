package agent;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

import agent.Ville;
import constants.Constants;

public class Nation {
	public String Nom;
	public double Force; //calcul�e au sein de la nation = fonction croissante(habitants, ressources), d�croissante de territoire
	public List<Ville> listeVilles=new ArrayList<Ville>();
	public Color couleur;
	
	public Nation(String Nom, Ville Capitale,int couleur) {
		this.Nom=Nom;
		this.listeVilles.add(Capitale);
		this.couleur = Constants.COLOR_MAP.getColor(couleur);
	}
	
	public Nation(int couleur) {
		// TODO Auto-generated constructor stub
		this.couleur = Constants.COLOR_MAP.getColor(couleur);
	}
	
	public List<Ville> getListeVilles(){
		return listeVilles;
	}
	
	public void addCapitale(Ville cap) {
		this.listeVilles.add(0, cap);
	}
	
	public int getPopulation()
	{
		int population=0;
		for (int i=0; i<listeVilles.size();i++)
		{
			population+=listeVilles.get(i).getPopulation();
		}
		return population;
	}
	
	public int getNbCases()
	{
		int nbCase=0;
		for (int i=0; i<listeVilles.size();i++)
		{
			nbCase+=listeVilles.get(i).NbCases();
		}
		return nbCase;
	}
	
	public int getRessources() {
		int R=0;
		for (int i=0; i<listeVilles.size();i++)
		{
			R+=listeVilles.get(i).getRessources();
		}
		return R;
	}
	
	public double getForme() {
		return ThreadLocalRandom.current().nextDouble(Constants.FACTEUR_CHANCE_FORME, 1);
	}
	
	public double getForce() {
		return listeVilles.size()* Constants.COEF_NB_VILLE * getPopulation() * getForme()  / (Constants.FACTEUR_PENALITE_NB_CASE_FORCE * getNbCases()) ;
	}
	
//	public double getForce() {
//		//return ((getRessources())/5)*getForme();
//		if(listeVilles.size()==0)
//			return 0;
//		return getRessources() * getPopulation() * getForme()  / (getNbCases()) ;
//	}
	
	public Color getColor() {
		return this.couleur;
	}
	
	public double getRessourceConquete() {
		double nbVilles= listeVilles.size();
		//System.out.println(this+" Veut faire une ville de + "+listeVilles.size()+" A besoin de "+(nbVilles+1)*400);
		return (nbVilles+1)*Constants.FACTEUR_MULTIPLICATIF_CONSTRUCTION_VILLE;
	}
}
