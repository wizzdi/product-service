package com.wizzdi.flexicore.product.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.model.Product;
import com.wizzdi.flexicore.product.model.Product_;
import com.wizzdi.flexicore.product.request.ProductCreate;
import com.wizzdi.flexicore.product.request.ProductFilter;
import com.wizzdi.flexicore.product.request.ProductUpdate;
import com.wizzdi.flexicore.product.service.ProductService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Product")

@Tag(name = "Product")
@Extension
@RestController
public class ProductController implements Plugin {

    @Autowired
    private ProductService service;


    @Operation(summary = "getAllProducts", description = "Lists all Product")
    @IOperation(Name = "getAllProducts", Description = "Lists all Product")
    @PostMapping("/getAllProducts")
    public PaginationResponse<Product> getAllProducts(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ProductFilter productFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(productFilter, securityContext);
        return service.getAllProducts(securityContext, productFilter);
    }


    @PostMapping("/createProduct")
    @Operation(summary = "createProduct", description = "Creates Product")
    @IOperation(Name = "createProduct", Description = "Creates Product")
    public Product createProduct(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ProductCreate productCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(productCreate, securityContext);

        return service.createProduct(productCreate, securityContext);
    }


    @PutMapping("/updateProduct")
    @Operation(summary = "updateProduct", description = "Updates Product")
    @IOperation(Name = "updateProduct", Description = "Updates Product")
    public Product updateProduct(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ProductUpdate productUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(productUpdate, securityContext);
        Product product = service.getByIdOrNull(productUpdate.getId(),
                Product.class, Product_.security, securityContext);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Product with id "
                    + productUpdate.getId());
        }
        productUpdate.setProduct(product);

        return service.updateProduct(productUpdate, securityContext);
    }
}