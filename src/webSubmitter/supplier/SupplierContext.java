package webSubmitter.supplier;

import util.ConstantUtils;

import java.util.Arrays;

public class SupplierContext {
	private ISupplier supplier;

	public SupplierContext() {
		this.supplier = Suppliers.FREE_PROXY_LIST.getSupplier();
	}

	public void setSupplier(final String supplierName){
		try {
			final boolean isInvalidValue = supplierName == null || supplierName.equals(ConstantUtils.NO_VALUE);

			if(!isInvalidValue)
				this.supplier = setSupplierByName(supplierName).getSupplier();
		} catch (final Exception exception) {
			exception.printStackTrace();
		}
	}

	private Suppliers setSupplierByName(final String supplierName){
		return Arrays.stream(Suppliers.values())
			 	.filter(supplier -> supplierName.equals(supplier.getName()))
				.findFirst()
				.orElse(Suppliers.FREE_PROXY_LIST);
	}

	public ISupplier getSupplier(final String supplierName){
		this.setSupplier(supplierName);

		return this.supplier;
	}
}