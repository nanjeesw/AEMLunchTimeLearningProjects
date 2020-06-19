/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tech.innisfree.assignments.servlet.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Own Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.resourceTypes=" + "ServletAssignment/servlet/beverage" })
public class OwnServlet extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(OwnServlet.class);

	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {

		int servletResponseCode = 0;
		Object response = null;

		log.info("Beverage servlet invoked. Method = POST");

		try {

			String beverageName = req.getRequestParameter("beverageName").toString();
			ResourceResolver resourceResolver = req.getResourceResolver();
			Resource resource = resourceResolver.getResource("/content/ServletAssignment/beverages");
			Node node = resource.adaptTo(Node.class);

			if (beverageName == null || beverageName.isEmpty()) {

				throw new ServletException(
						"Incorrect input message. You must supply a valid HTTP form with a parameter called beverageName");
			} else {

				Resource resourceExist = resourceResolver
						.getResource("/content/ServletAssignment/beverages/" + beverageName);
				if (resourceExist != null) {

					throw new ItemExistsException(
							"Uniqueness constraint violation. The beverage already exists in the JCR");
				}

				Node newNode = node.addNode(beverageName, "nt:unstructured");
				newNode.setProperty("name", beverageName);
				resourceResolver.commit();

				if (beverageName.equalsIgnoreCase("Coffee")) {
					log.warn("Coffee is for the weak and timid - Prepare to be annihilated");
					response = "I am a teapot";
					servletResponseCode = HttpServletResponse.SC_OK;
				} else {
					response = "Success";
					servletResponseCode = HttpServletResponse.SC_OK;
				}

			}

		} catch (ServletException e) {

			log.error(e.getMessage(), e);
			response = e.getMessage();
			servletResponseCode = HttpServletResponse.SC_BAD_REQUEST;

		} catch (ItemExistsException e) {

			log.error(e.getMessage(), e);
			response = e.getMessage();
			servletResponseCode = HttpServletResponse.SC_CONFLICT;

		} catch (Exception e) {

			log.error(e.getMessage(), e);
			response = e.getMessage();
			servletResponseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		resp.getWriter().write(convertToJson(servletResponseCode, response));
	}

	public static String convertToJson(int code, Object responseMessage) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> response = new HashMap<>();
		response.put("responseCode", code);
		response.put("responseMessage", responseMessage);

		return mapper.writeValueAsString(response);
	}
}
