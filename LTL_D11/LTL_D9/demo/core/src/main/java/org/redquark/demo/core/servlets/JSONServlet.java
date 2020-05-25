package org.redquark.demo.core.servlets;

import static org.redquark.demo.core.constants.AppConstants.URL;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.redquark.demo.core.utils.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(service = Servlet.class, property = {
		Constants.SERVICE_DESCRIPTION + "=JSON Servlet to read the data from the external webservice",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/readjson" })
public class JSONServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(JSONServlet.class);
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

		try {

			log.info("Reading the data from the webservice");

			String responseString = Network.readJson(URL);

			response.getWriter().println(responseString);

		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}
	}

}