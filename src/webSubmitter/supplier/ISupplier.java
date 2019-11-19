package webSubmitter.supplier;

import java.util.ArrayList;

import model.Local;
import model.Proxy;

public interface ISupplier {
	public ArrayList<Local> listPlaces(String localOrigin);
	public ArrayList<Proxy> listPoxies(String local, String protocol);
}
