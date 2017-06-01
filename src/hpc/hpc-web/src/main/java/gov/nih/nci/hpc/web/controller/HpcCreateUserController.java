/**
 * HpcCreateUserController.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See https://ncisvn.nci.nih.gov/svn/HPC_Data_Management/branches/hpc-prototype-dev/LICENSE.txt for details.
 */
package gov.nih.nci.hpc.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import gov.nih.nci.hpc.dto.datamanagement.HpcDataManagementDocListDTO;
import gov.nih.nci.hpc.dto.security.HpcUserDTO;
import gov.nih.nci.hpc.dto.security.HpcUserRequestDTO;
import gov.nih.nci.hpc.web.model.AjaxResponseBody;
import gov.nih.nci.hpc.web.model.HpcLogin;
import gov.nih.nci.hpc.web.model.HpcWebUser;
import gov.nih.nci.hpc.web.model.Views;
import gov.nih.nci.hpc.web.util.HpcClientUtil;

/**
 * <p>
 * Create User controller
 * </p>
 *
 * @author <a href="mailto:Prasad.Konka@nih.gov">Prasad Konka</a>
 * @version $Id$
 */

@Controller
@EnableAutoConfiguration
@RequestMapping("/createuser")
public class HpcCreateUserController extends AbstractHpcController {
	@Value("${gov.nih.nci.hpc.server.user}")
	private String userServiceURL;
	@Value("${gov.nih.nci.hpc.server.docs}")
	private String docsServiceURL;

	/**
	 * Prepare create user page. Populate available DOCs and Roles to assign to
	 * 
	 * @param q
	 * @param model
	 * @param bindingResult
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String home(@RequestBody(required = false) String q, Model model, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request) {
		HpcUserDTO user = (HpcUserDTO) session.getAttribute("hpcUser");
		String authToken = (String) session.getAttribute("hpcUserToken");
		if (user == null || authToken == null) {
			ObjectError error = new ObjectError("hpcLogin", "Invalid user session!");
			bindingResult.addError(error);
			HpcLogin hpcLogin = new HpcLogin();
			model.addAttribute("hpcLogin", hpcLogin);
			return "index";
		}
		initialize(model, authToken, user);
		return "createuser";
	}

	/**
	 * Create User POST action
	 * 
	 * @param hpcWebUser
	 * @param model
	 * @param bindingResult
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@JsonView(Views.Public.class)
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public AjaxResponseBody createUser(@Valid @ModelAttribute("hpcUser") HpcWebUser hpcWebUser, Model model,
			BindingResult bindingResult, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String authToken = (String) session.getAttribute("hpcUserToken");
		AjaxResponseBody result = new AjaxResponseBody();

		try {
			if (hpcWebUser.getNciUserId() == null || hpcWebUser.getNciUserId().trim().length() == 0
					|| hpcWebUser.getFirstName() == null || hpcWebUser.getFirstName().trim().length() == 0
					|| hpcWebUser.getLastName() == null && hpcWebUser.getLastName().trim().length() == 0
					|| hpcWebUser.getDoc() == null && hpcWebUser.getDoc().trim().length() == 0
					|| hpcWebUser.getUserRole() == null && hpcWebUser.getUserRole().trim().length() == 0)
				model.addAttribute("message", "Invald user input");

			HpcUserRequestDTO dto = new HpcUserRequestDTO();
			dto.setDoc(hpcWebUser.getDoc());
			dto.setFirstName(hpcWebUser.getFirstName());
			dto.setLastName(hpcWebUser.getLastName());
			dto.setUserRole(hpcWebUser.getUserRole());

			boolean created = HpcClientUtil.createUser(authToken, userServiceURL, dto, hpcWebUser.getNciUserId(),
					sslCertPath, sslCertPassword);
			if (created)
				result.setMessage("User account created");
		} catch (Exception e) {
			result.setMessage("Failed to create user: " + e.getMessage());
		} finally {
			model.addAttribute("hpcWebUser", hpcWebUser);
			HpcUserDTO user = (HpcUserDTO) session.getAttribute("hpcUser");
			initialize(model, authToken, user);
		}
		return result;
	}

	private void initialize(Model model, String authToken, HpcUserDTO user) {
		HpcWebUser webUser = new HpcWebUser();
		model.addAttribute("hpcWebUser", webUser);
		populateDOCs(model, authToken, user);
		populateRoles(model, user);

	}

	private void populateDOCs(Model model, String authToken, HpcUserDTO user) {
		List<String> userDOCs = new ArrayList<String>();
		if (user.getUserRole().equals("SYSTEM_ADMIN")) {
			HpcDataManagementDocListDTO docs = HpcClientUtil.getDOCs(authToken, docsServiceURL, sslCertPath,
					sslCertPassword);
			model.addAttribute("docs", docs.getDocs());
		} else {
			userDOCs.add(user.getDoc());
			model.addAttribute("docs", userDOCs);
		}
	}

	private void populateRoles(Model model, HpcUserDTO user) {
		List<String> roles = new ArrayList<String>();
		if (user.getUserRole().equals("SYSTEM_ADMIN")) {
			roles.add("SYSTEM_ADMIN");
			roles.add("GROUP_ADMIN");
			roles.add("USER");
		} else
			roles.add("USER");
		model.addAttribute("roles", roles);
	}

}