package facade;

import controller.ControllerWebSubmitter;

public class Facede {
	private static Facede instance;
	
	public static Facede getInstance(){
		if(instance == null) {
			instance = new Facede();
		}
		return instance;
	}
	
	private Facede() {}

	public String listPlaces(String supplier, String localOrigin){
		return ControllerWebSubmitter.getInstance().listPlaces(supplier, localOrigin);
	}
	
	public String listPoxies(String supplier, String local, String protocol){
		return ControllerWebSubmitter.getInstance().listPoxies(supplier, local, protocol);
	} 
}
