package controller;

import java.util.ArrayList;
import java.util.HashMap;

import model.Local;
import model.Proxy;
import util.ConstantesUtil;
import util.JSONHelper;
import util.MethodsUtil;
import webSubmitter.supplier.SupplierContext;

public class ControllerWebSubmitter {
	
	private SupplierContext supplierContext;

	private static ControllerWebSubmitter instance;
		
	public static ControllerWebSubmitter getInstance(){
		if(instance == null) {
			instance = new ControllerWebSubmitter();
		}
		return instance;
	}
	
	private ControllerWebSubmitter() {
		this.supplierContext = new SupplierContext();
	}
	
	public String listPlaces(String supplier, String localOrigin){
		supplier = MethodsUtil.nullOrEmptyToOther(supplier, ConstantesUtil.NO_VALUE);
		
		localOrigin = MethodsUtil.nullOrEmptyToOther(localOrigin, ConstantesUtil.NO_VALUE);
		
	  	ArrayList<Local> places = this.supplierContext.getSupplier(supplier).listPlaces(localOrigin);
  	   	 
		HashMap<String, ArrayList<Local>> options = new HashMap<>();
		options.put("places", places); 
		
		return JSONHelper.hashMapToJsonString(options);
	}
	
	public String listPoxies(String supplier, String local, String protocol){
		supplier = MethodsUtil.nullOrEmptyToOther(supplier, ConstantesUtil.NO_VALUE);
		
		local = MethodsUtil.nullOrEmptyToOther(local, ConstantesUtil.NO_VALUE);
		
		boolean isSecureOrNotEmpty = !MethodsUtil.isEmpty(protocol) && protocol.contains(ConstantesUtil.HTTPS); 
		
		if(!isSecureOrNotEmpty && !protocol.contains(ConstantesUtil.HTTP))
			protocol = ConstantesUtil.NO_VALUE;
		
		ArrayList<Proxy> proxies = this.supplierContext.getSupplier(supplier).listPoxies(local, protocol);
		
		HashMap<String, ArrayList<Proxy>> options = new HashMap<>();
		options.put("proxies", proxies);
		
		return JSONHelper.hashMapToJsonString(options);
	}
}
