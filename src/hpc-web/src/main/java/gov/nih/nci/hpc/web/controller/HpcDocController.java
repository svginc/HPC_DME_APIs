/**
 * HpcDocController.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See https://ncisvn.nci.nih.gov/svn/HPC_Data_Management/branches/hpc-prototype-dev/LICENSE.txt for details.
 */
package gov.nih.nci.hpc.web.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import gov.nih.nci.hpc.dto.security.HpcUserDTO;
import gov.nih.nci.hpc.web.model.HpcLogin;
import gov.nih.nci.hpc.web.model.HpcWebUser;

/**
 * <p>
 * Controller to get DOC details
 * </p>
 *
 * @author <a href="mailto:Prasad.Konka@nih.gov">Prasad Konka</a>
 * @version $Id: HpcDocController.java
 */

@Controller
@EnableAutoConfiguration
@RequestMapping("/doc")
public class HpcDocController extends AbstractHpcController {
	@Value("${gov.nih.nci.hpc.server}")
	private String serverURL;
	@Value("${gov.nih.nci.hpc.server.user}")
	private String serviceUserURL;

	@RequestMapping(method = RequestMethod.GET)
	public String home(@RequestBody(required = false) String q, Model model, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request) {
		HpcUserDTO user = (HpcUserDTO) session.getAttribute("hpcUser");
		if (user == null) {
			ObjectError error = new ObjectError("hpcLogin", "Invalid user session!");
			bindingResult.addError(error);
			HpcLogin hpcLogin = new HpcLogin();
			model.addAttribute("hpcLogin", hpcLogin);
		}
		return "doc";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String register(@Valid @ModelAttribute("hpcUser") HpcWebUser hpcUser, BindingResult bindingResult,
			Model model) {
		RestTemplate restTemplate = new RestTemplate();

		try {

			URI uri = new URI(serviceUserURL + "/" + hpcUser.getNciUserId());
			model.addAttribute("registrationStatus", true);
			model.addAttribute("hpcUser", hpcUser);
		} catch (Exception e) {
			ObjectError error = new ObjectError("nciUserId", "Failed to enroll: " + e.getMessage());
			bindingResult.addError(error);
			return "enroll";
		}
		return "enrollresult";
	}
}
