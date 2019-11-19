package model;

public class Proxy {
	private String host;
	private int port;

	private boolean isAuthenticate;

	private String user;
	private String password;

	private Double speed;
	
	public Proxy(String host, int port) {
		this.host = host;
		this.port = port;
		this.isAuthenticate = false;
	}
	
	public Proxy(String host, int port, boolean isAuthenticate, String user, String password) {
		this.host = host;
		this.port = port;
		this.isAuthenticate = isAuthenticate;
		this.user = user;
		this.password = password;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean isAuthenticate() {
		return isAuthenticate;
	}
	public void setAuthenticate(boolean isAuthenticate) {
		this.isAuthenticate = isAuthenticate;
	}

	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public static class ProxyBuilder {
		private String host;
		private int port;
		
		private boolean isAuthenticate;
		
		private String user;
		private String password;
		
		private Double speed;		

		public ProxyBuilder(String host, int port) {
			this.host = host;
			this.port = port;
		}
		
		public boolean isAuthenticate() {
			return isAuthenticate;
		}
		public ProxyBuilder setAuthenticate(boolean isAuthenticate) {
			this.isAuthenticate = isAuthenticate;
			return this;
		}

		public String getUser() {
			return user;
		}
		public ProxyBuilder setUser(String user) {
			this.user = user;
			return this;
		}

		public String getPassword() {
			return password;
		}
		public ProxyBuilder setPassword(String password) {
			this.password = password;
			return this;
		}
		
		public Double getSpeed() {
			return speed;
		}
		public ProxyBuilder setSpeed(Double speed) {
			this.speed = speed;
			return this;
		}

		public Proxy build() {
			return new Proxy(this);
		}
	}

	private Proxy(ProxyBuilder builder) {
		this.host = builder.host;
		this.port = builder.port;
		this.isAuthenticate = builder.isAuthenticate;
		this.user = builder.user;
		this.password = builder.password;
		this.speed = builder.speed;
	}
}
