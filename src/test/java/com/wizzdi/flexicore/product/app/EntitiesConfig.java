package com.wizzdi.flexicore.product.app;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Clazz;
import com.flexicore.model.security.SecurityPolicy;
import com.flexicore.model.territories.Address;
import com.flexicore.organization.model.Site;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.pricing.model.price.PriceList;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import com.wizzdi.flexicore.product.model.Model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class EntitiesConfig {

	@Bean
	public EntitiesHolder entitiesHolder(){
		return new EntitiesHolder(new HashSet<>(Arrays.asList(Baseclass.class, Basic.class, SecurityPolicy.class, Model.class, Site.class, Address.class, PriceList.class, Clazz.class, FileResource.class, PricedProduct.class)));
	}
}
