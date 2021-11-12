package webSubmitter.supplier;

public enum Suppliers implements ISupplierList {
	PROXY_NOVA {
		@Override
		public String getName() {
			return "proxynova";
		}

		@Override
		public ISupplier getSupplier() {
			return new ProxyNovaSupplier();
		}
	},
	
	MY_IP_HIDE {
		@Override
		public String getName() {
			return "myiphide";
		}

		@Override
		public ISupplier getSupplier() {
			return new MyIPHideSupplier();
		}
	}, 
	
	FREE_PROXY_LIST {
		@Override
		public String getName() {
			return "freeproxylist";
		}

		@Override
		public ISupplier getSupplier() {
			return new FreeProxyListSupplier();
		}
	};
}
