package com.flexicore.product.config;

import com.flexicore.interfaces.ServicePlugin;
import org.pf4j.Extension;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class ProductTransactionSupportSupport implements ServicePlugin {

}