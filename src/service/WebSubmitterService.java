package service;

import model.Local;
import model.Proxy;
import util.ConstantUtils;
import webSubmitter.supplier.SupplierContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static util.MethodsUtils.convertMapToJson;
import static util.MethodsUtils.isEmpty;

public class WebSubmitterService {

	private SupplierContext supplierContext;

	private static WebSubmitterService instance;

	public static WebSubmitterService getInstance(){
		if(instance == null)
			instance = new WebSubmitterService();

		return instance;
	}

	private WebSubmitterService() {
		this.supplierContext = new SupplierContext();
	}

	public String listPlaces(final String supplier, final String origin){
		final String nonNullSupplier = Optional.ofNullable(supplier).orElse(ConstantUtils.NO_VALUE).trim();
		final String nonNullOrigin = Optional.ofNullable(origin).orElse(ConstantUtils.NO_VALUE).trim();

	  	final List<Local> places = this.supplierContext
				.getSupplier(nonNullSupplier)
				.listPlaces(nonNullOrigin);

		return convertMapToJson(Map.of("places", places));
	}

	public String listProxies(final String supplier, final String origin, final String protocol){
		final String nonNullSupplier = Optional.ofNullable(supplier).orElse(ConstantUtils.NO_VALUE).trim();
		final String nonNullOrigin = Optional.ofNullable(origin).orElse(ConstantUtils.NO_VALUE).trim();

		final String nonNullProtocol = validateProtocol(protocol);

		final List<Proxy> proxies = this.supplierContext
				.getSupplier(nonNullSupplier)
				.listProxies(nonNullOrigin, nonNullProtocol);

		return convertMapToJson(Map.of("proxies", proxies));
	}

	private String validateProtocol(final String protocol){
		final boolean isSecureOrNotEmpty = !isEmpty(protocol) && protocol.contains(ConstantUtils.HTTPS);
		final boolean isHttp = protocol.contains(ConstantUtils.HTTP);

		return !isSecureOrNotEmpty && !isHttp? ConstantUtils.NO_VALUE : protocol;
	}
}