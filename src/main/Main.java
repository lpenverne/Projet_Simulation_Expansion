package main;

import sim.display.Console;



public class Main {
	public static void main(String[] args) {
		 runUI();
	}

	public static void runUI() {
		Model model = new Model(System.currentTimeMillis());
		Visualisation gui = new Visualisation(model);
		Console console = new Console(gui);
		console.setVisible(true);
	}
}
