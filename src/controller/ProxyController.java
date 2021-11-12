package controller;

import service.WebSubmitterService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/proxy")
public class ProxyController {
	private final WebSubmitterService controllerWebSubmitter;

	public ProxyController(){
		this.controllerWebSubmitter = WebSubmitterService.getInstance();
	}

	@GET
	@Path("/places")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String listPlaces(
		@QueryParam("supplier") final String supplier,
		@QueryParam("origin") final String origin
	) {
   	   	return this.controllerWebSubmitter.listPlaces(supplier, origin);
	}	

	@GET
	@Path("/proxies")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String listProxies(
		@QueryParam("supplier") final String supplier,
		@QueryParam("origin") final String origin,
		@QueryParam("protocol") final String protocol
	) {
		return this.controllerWebSubmitter.listProxies(supplier, origin, protocol);
	}
}