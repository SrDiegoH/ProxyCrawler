package webSubmitter.supplier;

import model.Local;
import model.Proxy;

import java.util.List;

public interface ISupplier {
	List<Local> listPlaces(String origin);
	List<Proxy> listProxies(String origin, String protocol);
}
