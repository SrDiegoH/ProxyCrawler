package model;

public class Proxy {
	private String host;
	private int port;

	private boolean isAuthenticate;

	private String user;
	private String password;

	private Double speed;

	private Proxy(final ProxyBuilder builder) {
		this.host = builder.host;
		this.port = builder.port;
		this.isAuthenticate = builder.isAuthenticate;
		this.user = builder.user;
		this.password = builder.password;
		this.speed = builder.speed;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean isAuthenticate() {
		return isAuthenticate;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public Double getSpeed() {
		return speed;
	}

	public static class ProxyBuilder {
		private String host;
		private int port;
		
		private boolean isAuthenticate;
		
		private String user;
		private String password;
		
		private Double speed;		

		public ProxyBuilder(final String host, final int port) {
			this.host = host;
			this.port = port;
		}

		public ProxyBuilder setAuthenticate(final boolean isAuthenticate) {
			this.isAuthenticate = isAuthenticate;
			return this;
		}

		public ProxyBuilder setUser(final String user) {
			this.user = user;
			return this;
		}

		public ProxyBuilder setPassword(final String password) {
			this.password = password;
			return this;
		}

		public ProxyBuilder setSpeed(final Double speed) {
			this.speed = speed;
			return this;
		}

		public Proxy build() {
			return new Proxy(this);
		}
	}
}
