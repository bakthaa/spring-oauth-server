/**
 * 
 */
package xyz.baktha.oaas.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import xyz.baktha.oaas.data.exception.InvalidClientException;
import xyz.baktha.oaas.data.exception.InvalidUserException;
import xyz.baktha.oaas.web.model.ClientModel;
import xyz.baktha.oaas.web.model.UserModel;
import xyz.baktha.oaas.web.service.AdminService;

/**
 * @author power-team
 *
 */
@RestController
public class AdminController {

	Validator validator = ESAPI.validator();

	@Autowired
	private AdminService adminService;

	@PreAuthorize("hasAuthority('ROLE_NIRVAGI')")
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<UserModel> getUsers() {

		return adminService.getAllUser();
	}

	@PreAuthorize("hasAuthority('ROLE_NIRVAGI')")
	@RequestMapping(value = "/clients", method = RequestMethod.GET)
	public List<ClientModel> getClients() {

		return adminService.getClients();
	}

	@PreAuthorize("hasAuthority('ROLE_NIRVAGI')")
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public void adduser(@ModelAttribute("user") UserModel form, BindingResult result) {
		
		List<String> errs = isValidUser(form);
		if (0 < errs.size()) {

			throw new InvalidUserException(StringUtils.collectionToDelimitedString(errs, ","));
		}
		adminService.addUser(form);		
	}

	@PreAuthorize("hasAuthority('ROLE_NIRVAGI')")
	@RequestMapping(value = "/nirvagam_client", method = RequestMethod.POST)
	public void addClient(@ModelAttribute("client") ClientModel form, BindingResult result, Model model) {

		List<String> errs = isValidClient(form);
		if (0 < errs.size()) {

			throw new InvalidClientException(StringUtils.collectionToDelimitedString(errs, ","));
		}
		adminService.addClient(form);
	}

	protected List<String> isValidUser(UserModel form) {

		List<String> errMsg = new ArrayList<String>();

		if (!validator.isValidInput("userName", form.getUname(), "AccountName", 20, false)) {

			errMsg.add("Invalid Username");
		}
		if (validator.isValidInput("Password", form.getPwd(), "Password", 15, false)) {

			if (!form.getPwd().equals(form.getRePwd())) {

				errMsg.add("Password Not Match");
			}
		} else {

			errMsg.add("Invalid Password");
		}
		return errMsg;
	}

	protected List<String> isValidClient(ClientModel form) {

		List<String> errMsg = new ArrayList<String>();

		if (!validator.isValidInput("resourceId", form.getResourceId(), "AccountName", 20, false)) {

			errMsg.add("Invalid resourceId");
		}
		if (!validator.isValidInput("clientId", form.getClientId(), "AccountName", 20, false)) {

			errMsg.add("Invalid ClientId");
		}
		if (validator.isValidInput("clientSec", form.getClientSec(), "Password", 15, false)) {

			if (!form.getClientSec().equals(form.getReClientSec())) {

				errMsg.add("clientSec Not Match");
			}
		} else {

			errMsg.add("Invalid ClientSec");
		}

		if (!validator.isValidInput("validity", form.getValidity(), "Number5", 5, false)) {

			errMsg.add("Invalid validity");
		}

		if (!validator.isValidInput("dirUrl", form.getDirUrl(), "URL", 200, true)) {

			errMsg.add("Invalid dirUrl");
		}

		if (CollectionUtils.isEmpty(form.getGrants())) {

			errMsg.add("Invalid Grants");
		}

		if (CollectionUtils.isEmpty(form.getScopes())) {

			errMsg.add("Invalid Scopes");
		}
		return errMsg;
	}

}