package com.flexicore.product.interfaces;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.iot.ExternalServer;
import com.flexicore.iot.ExternalServerUser;
import com.flexicore.product.iot.request.ExternalServerCreate;
import com.flexicore.product.iot.request.ExternalServerUserCreate;
import com.flexicore.security.SecurityContext;

import java.security.GeneralSecurityException;

public interface IExternalServerService extends Plugin {
	boolean updateExternalServerNoMerge(
			ExternalServerCreate externalServerCreate,
			ExternalServer externalServer);

	void validate(ExternalServerUserCreate externalServerUserCreate,
			SecurityContext securityContext);

	boolean updateExternalServerUserNoMerge(
			ExternalServerUserCreate externalServerCreate,
			ExternalServerUser externalServerUser);

	String getDecryptedPassword(String encryptedPassword)
			throws GeneralSecurityException;
}
