package alpha.study.hellojson;


enum Mode {
	DEFAULT,
	STATIC,
	DYNAMIC,	
}

class ServiceConfig {
	private boolean active = false;
	private Mode mode = Mode.DEFAULT;
	double rate = 0.2;
	int count;
	
	public void setactive(boolean active) {
		this.active = active;
	}
	public boolean getactive() {
		return active;
	}
	public void setmode(Mode mode) {
		this.mode = mode;
	}
	public Mode getmode() {
		return mode;
	}
}

class Server1Config {
	private boolean active = false;
	int localPort = 0;
	String localIP = "";
	Mode mode = Mode.DEFAULT;
	double rate = 0.1;
	int count = 5;
	int[] serverPorts = new int[count];
	String[] serverIPs = new String[count];
	ServiceConfig service1Config = new ServiceConfig();
	
	public void setactive(boolean active) {
		this.active = active;
	}
	public boolean getactive() {
		return active;
	}
}

class Server2Config {
	private boolean active = false;
	int localPort = 0;
	String localIP = "localhost";
	Mode mode = Mode.DEFAULT;
	double rate = 0.5;
	ServiceConfig service1Config = new ServiceConfig();
	ServiceConfig service2Config = new ServiceConfig();
	
	public void setactive(boolean active) {
		this.active = active;
	}
	public boolean getactive() {
		return active;
	}
}

public class Config {
	private boolean active = false;
	private Mode mode = Mode.DEFAULT;
	Server1Config server1Config = new Server1Config();
	Server2Config server2Config = new Server2Config();
	
	public void setactive(boolean active) {
		this.active = active;
	}
	public boolean getactive() {
		return active;
	}
	public void setmode(Mode mode) {
		this.mode = mode;
	}
	public Mode getmode() {
		return mode;
	}
}
