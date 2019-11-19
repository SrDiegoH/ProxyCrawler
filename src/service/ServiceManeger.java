package service;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import facade.Facede;
import util.JSONHelper;

@Path("/ServiceManeger")
public class ServiceManeger {
	
	@GET
	@Path("/listPlaces")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String listPlaces(@QueryParam("fonte") String fonteDados, @QueryParam("localOrigem") String localOrigem) {
   	   	return Facede.getInstance().listPlaces(fonteDados, localOrigem);
	}	
	
	@GET
	@Path("/listPoxies")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String listarPoxies (@QueryParam("fonte") String fonteDados, @QueryParam("local") String local, @QueryParam("protocol") String protocol) {
		return Facede.getInstance().listPoxies(fonteDados, local, protocol);
	}
	
	@GET
	@Path("/healthCheck/{checkParam1}")
	@Produces("application/json")
	//@FormParam("")
	//@CookieParam("")
	//@HeaderParam("")
	public String healthCheck(@PathParam("checkParam1") String checkParam1, @QueryParam("checkParam2") String checkParam2) {
		HashMap<String, String> options = new HashMap<>();
		options.put("Check_1", checkParam1);
		options.put("Check_2", checkParam2);
		
		return JSONHelper.hashMapToJsonString(options);
   	   	
	}
}
