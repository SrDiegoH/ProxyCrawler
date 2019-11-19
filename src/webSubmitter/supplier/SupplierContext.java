package webSubmitter.supplier;

import util.ConstantesUtil;

public class SupplierContext {
	private ISupplier supplier;
	private final ISupplier DEFAULT_SUPPLIER;
	
	public SupplierContext() {
		this.DEFAULT_SUPPLIER = new FreeProxyListSupplier();
		this.supplier = DEFAULT_SUPPLIER;
	}

	public void setSupplier(String supplierName){
		try {
			
			if(!supplierName.equals(ConstantesUtil.NO_VALUE)) {
				for (SuppliersList enumItem : SuppliersList.values()) {
					
					if(enumItem.getName().equals(supplierName)) {
						this.supplier = enumItem.getSupplier();
						return;
					}
				}
			}
			
			this.supplier = DEFAULT_SUPPLIER;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ISupplier getSupplier(){
		return this.supplier;
	}
	
	public ISupplier getSupplier(String supplierName){
		this.setSupplier(supplierName);
		
		return this.supplier;
	}
}
