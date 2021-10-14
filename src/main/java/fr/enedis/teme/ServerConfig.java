package fr.enedis.teme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServerConfig {

	private String host;
	private int port;
	private String authMethod; // basic, token, ... 
	private String token;
	private String username;
	private String password;
	private String accessTokenUrl;
	
	private ServerConfig(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String buildRootUrl() {
    	
    	return String.format("http%s://%s", 
    			port == 443 ? "s" : "", host) + 
    			(port == 80 || port == 443 ? "" : ":" + port) + "/";
    }
	
	public static ServerConfig localhost(int port) {
		
		return new ServerConfig("localhost", port);
	}
}
