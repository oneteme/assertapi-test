package fr.enedis.teme;

import static fr.enedis.teme.RestTemplateClientHttpRequestInitializer.init;

import org.springframework.web.client.RestTemplate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestTemplateConfig {
	
    public static RestTemplate configure(ServerConfig conf) {
    	
    	var rt = new RestTemplate();
    	RootUriTemplateHandler.addTo(rt, conf.buildRootUrl());
    	rt.getClientHttpRequestInitializers().add(init(conf));
    	return rt;
    }
	
    
}