package com.wizzdi.flexicore.product;

import com.wizzdi.flexicore.product.app.App;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.product.request.ProductTypeCreate;
import com.wizzdi.flexicore.product.request.ProductTypeFilter;
import com.wizzdi.flexicore.product.request.ProductTypeUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class ProductTypeControllerTest {

    private ProductType productType;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testProductTypeCreate() {
        String name = UUID.randomUUID().toString();
        ProductTypeCreate request = new ProductTypeCreate()
                .setName(name);
        ResponseEntity<ProductType> productTypeResponse = this.restTemplate.postForEntity("/plugins/ProductType/createProductType", request, ProductType.class);
        Assertions.assertEquals(200, productTypeResponse.getStatusCodeValue());
        productType = productTypeResponse.getBody();
        assertProductType(request, productType);

    }

    @Test
    @Order(2)
    public void testGetAllProductTypes() {
        ProductTypeFilter request=new ProductTypeFilter();
        ParameterizedTypeReference<PaginationResponse<ProductType>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<ProductType>> productTypeResponse = this.restTemplate.exchange("/plugins/ProductType/getAllProductTypes", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, productTypeResponse.getStatusCodeValue());
        PaginationResponse<ProductType> body = productTypeResponse.getBody();
        Assertions.assertNotNull(body);
        List<ProductType> productTypes = body.getList();
        Assertions.assertNotEquals(0,productTypes.size());
        Assertions.assertTrue(productTypes.stream().anyMatch(f->f.getId().equals(productType.getId())));


    }

    public void assertProductType(ProductTypeCreate request, ProductType productType) {
        Assertions.assertNotNull(productType);
        Assertions.assertEquals(request.getName(), productType.getName());
    }

    @Test
    @Order(3)
    public void testProductTypeUpdate(){
        String name = UUID.randomUUID().toString();
        ProductTypeUpdate request = new ProductTypeUpdate()
                .setId(productType.getId())
                .setName(name);
        ResponseEntity<ProductType> productTypeResponse = this.restTemplate.exchange("/plugins/ProductType/updateProductType",HttpMethod.PUT, new HttpEntity<>(request), ProductType.class);
        Assertions.assertEquals(200, productTypeResponse.getStatusCodeValue());
        productType = productTypeResponse.getBody();
        assertProductType(request, productType);

    }

}
