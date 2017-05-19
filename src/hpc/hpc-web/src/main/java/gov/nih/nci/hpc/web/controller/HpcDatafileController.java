/**
 * HpcDatafileController.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See https://ncisvn.nci.nih.gov/svn/HPC_Data_Management/branches/hpc-prototype-dev/LICENSE.txt for details.
 */
package gov.nih.nci.hpc.web.controller;

import java.util.ArrayList;
import java.util.Enumeration;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gov.nih.nci.hpc.domain.metadata.HpcMetadataEntry;
import gov.nih.nci.hpc.dto.datamanagement.HpcDataManagementModelDTO;
import gov.nih.nci.hpc.dto.datamanagement.HpcDataObjectDTO;
import gov.nih.nci.hpc.dto.datamanagement.HpcDataObjectListDTO;
import gov.nih.nci.hpc.dto.datamanagement.HpcDataObjectRegistrationDTO;
import gov.nih.nci.hpc.dto.datamanagement.HpcUserPermissionDTO;
import gov.nih.nci.hpc.dto.security.HpcUserDTO;
import gov.nih.nci.hpc.web.model.HpcDatafileModel;
import gov.nih.nci.hpc.web.model.HpcLogin;
import gov.nih.nci.hpc.web.model.HpcMetadataAttrEntry;
import gov.nih.nci.hpc.web.util.HpcClientUtil;

/**
 * <p>
 * Controller to display data file details and update metadata.
 * </p>
 *
 * @author <a href="mailto:Prasad.Konka@nih.gov">Prasad Konka</a>
 * @version $Id: HpcDatafileController.java
 */

@Controller
@EnableAutoConfiguration
@RequestMapping("/datafile")
public class HpcDatafileController extends AbstractHpcController {
	@Value("${gov.nih.nci.hpc.server.dataObject}")
	private String serviceURL;
	@Value("${gov.nih.nci.hpc.server.model}")
	private String hpcModelURL;

	/**
	 * Get operation to display data file details and its metadata
	 * 
	 * @param body
	 * @param path
	 * @param action
	 * @param model
	 * @param bindingResult
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String home(@RequestBody(required = false) String body, @RequestParam String path,
			@RequestParam String action, Model model, BindingResult bindingResult, HttpSession session,
			HttpServletRequest request) {
		try {
			HpcUserDTO user = (HpcUserDTO) session.getAttribute("hpcUser");
			String userId = (String) session.getAttribute("hpcUserId");
			String authToken = (String) session.getAttribute("hpcUserToken");
			if (user == null || authToken == null) {
				ObjectError error = new ObjectError("hpcLogin", "Invalid user session!");
				bindingResult.addError(error);
				HpcLogin hpcLogin = new HpcLogin();
				model.addAttribute("hpcLogin", hpcLogin);
				return "index";
			}

			if (path == null)
				return "dashboard";

			HpcDataObjectListDTO datafiles = HpcClientUtil.getDatafiles(authToken, serviceURL, path, false, sslCertPath,
					sslCertPassword);
			if (datafiles != null && datafiles.getDataObjects() != null && datafiles.getDataObjects().size() > 0) {
				HpcDataManagementModelDTO modelDTO = (HpcDataManagementModelDTO) session.getAttribute("userDOCModel");
				if (modelDTO == null)
					modelDTO = HpcClientUtil.getDOCModel(authToken, hpcModelURL, user.getDoc(), sslCertPath,
							sslCertPassword);
				HpcDataObjectDTO dataFile = datafiles.getDataObjects().get(0);
				HpcDatafileModel hpcDatafile = buildHpcDataObject(dataFile,
						modelDTO.getDataObjectSystemGeneratedMetadataAttributeNames());
				hpcDatafile.setPath(path);
				model.addAttribute("hpcDatafile", hpcDatafile);
				if (action != null && action.equals("edit"))
					model.addAttribute("action", "edit");
				HpcUserPermissionDTO permission = HpcClientUtil.getPermissionForUser(authToken, path, userId,
						serviceURL, sslCertPath, sslCertPassword);
				model.addAttribute("userpermission", permission.getPermission().toString());
			} else {
				String message = "Data file not found!";
				model.addAttribute("error", message);
				return "dashboard";
			}
		} catch (Exception e) {
			model.addAttribute("error", "Failed to get data file: " + e.getMessage());
			e.printStackTrace();
			return "dashboard";
		}
		return "datafile";
	}

	/**
	 * Post operation to update metadata
	 * 
	 * @param hpcDatafile
	 * @param model
	 * @param bindingResult
	 * @param session
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String updateCollection(@Valid @ModelAttribute("hpcDatafile") HpcDatafileModel hpcDatafile, Model model,
			BindingResult bindingResult, HttpSession session, HttpServletRequest request, HttpServletResponse response,
			final RedirectAttributes redirectAttributes) {
		String[] action = request.getParameterValues("action");
		if (action != null && action.length > 0 && action[0].equals("cancel"))
			return "redirect:/datafile?path=" + hpcDatafile.getPath() + "&action=view";
		String authToken = (String) session.getAttribute("hpcUserToken");
		try {
			if (hpcDatafile.getPath() == null || hpcDatafile.getPath().trim().length() == 0)
				model.addAttribute("error", "Invald Data file path");

			HpcDataObjectRegistrationDTO registrationDTO = constructRequest(request, session, hpcDatafile.getPath());

			boolean updated = HpcClientUtil.updateDatafile(authToken, serviceURL, registrationDTO,
					hpcDatafile.getPath(), sslCertPath, sslCertPassword);
			if (updated) {
				redirectAttributes.addFlashAttribute("error", "Data file " + hpcDatafile.getPath() + " is Updated!");
				session.removeAttribute("selectedUsers");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Failed to update data file: " + e.getMessage());
		} finally {
			model.addAttribute("hpcDatafile", hpcDatafile);
		}
		return "redirect:/datafile?path=" + hpcDatafile.getPath() + "&action=view";
	}

	private HpcDatafileModel buildHpcDataObject(HpcDataObjectDTO datafile, List<String> systemAttrs) {
		HpcDatafileModel model = new HpcDatafileModel();
		systemAttrs.add("collection_type");
		model.setDataObject(datafile.getDataObject());
		for (HpcMetadataEntry entry : datafile.getMetadataEntries().getSelfMetadataEntries()) {
			HpcMetadataAttrEntry attrEntry = new HpcMetadataAttrEntry();
			attrEntry.setAttrName(entry.getAttribute());
			attrEntry.setAttrValue(entry.getValue());
			attrEntry.setAttrUnit(entry.getUnit());
			if (systemAttrs != null && systemAttrs.contains(entry.getAttribute()))
				attrEntry.setSystemAttr(true);
			else
				attrEntry.setSystemAttr(false);
			model.getSelfMetadataEntries().add(attrEntry);
		}

		for (HpcMetadataEntry entry : datafile.getMetadataEntries().getParentMetadataEntries()) {
			HpcMetadataAttrEntry attrEntry = new HpcMetadataAttrEntry();
			attrEntry.setAttrName(entry.getAttribute());
			attrEntry.setAttrValue(entry.getValue());
			attrEntry.setAttrUnit(entry.getUnit());
			if (systemAttrs != null && systemAttrs.contains(entry.getAttribute()))
				attrEntry.setSystemAttr(true);
			else
				attrEntry.setSystemAttr(false);
			model.getParentMetadataEntries().add(attrEntry);
		}
		return model;
	}

	private HpcDataObjectRegistrationDTO constructRequest(HttpServletRequest request, HttpSession session,
			String path) {
		Enumeration<String> params = request.getParameterNames();
		HpcDataObjectRegistrationDTO dto = new HpcDataObjectRegistrationDTO();
		List<HpcMetadataEntry> metadataEntries = new ArrayList<>();

		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			if (paramName.startsWith("zAttrStr_")) {
				HpcMetadataEntry entry = new HpcMetadataEntry();
				String attrName = paramName.substring("zAttrStr_".length());
				String[] attrValue = request.getParameterValues(paramName);
				entry.setAttribute(attrName);
				entry.setValue(attrValue[0]);
				metadataEntries.add(entry);
			}
		}
		dto.getMetadataEntries().addAll(metadataEntries);
		dto.setSource(null);
		return dto;
	}

}
