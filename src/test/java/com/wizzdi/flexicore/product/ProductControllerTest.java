package com.wizzdi.flexicore.product;

import com.wizzdi.flexicore.product.app.App;
import com.wizzdi.flexicore.product.model.Product;
import com.wizzdi.flexicore.product.request.ProductCreate;
import com.wizzdi.flexicore.product.request.ProductFilter;
import com.wizzdi.flexicore.product.request.ProductUpdate;
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

public class ProductControllerTest {

    private Product product;
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
    public void testProductCreate() {
        String name = UUID.randomUUID().toString();
        ProductCreate request = new ProductCreate()
                .setName(name);
        ResponseEntity<Product> productResponse = this.restTemplate.postForEntity("/plugins/Product/createProduct", request, Product.class);
        Assertions.assertEquals(200, productResponse.getStatusCodeValue());
        product = productResponse.getBody();
        assertProduct(request, product);

    }

    @Test
    @Order(2)
    public void testGetAllProducts() {
        ProductFilter request=new ProductFilter();
        ParameterizedTypeReference<PaginationResponse<Product>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Product>> productResponse = this.restTemplate.exchange("/plugins/Product/getAllProducts", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, productResponse.getStatusCodeValue());
        PaginationResponse<Product> body = productResponse.getBody();
        Assertions.assertNotNull(body);
        List<Product> products = body.getList();
        Assertions.assertNotEquals(0,products.size());
        Assertions.assertTrue(products.stream().anyMatch(f->f.getId().equals(product.getId())));


    }

    public void assertProduct(ProductCreate request, Product product) {
        Assertions.assertNotNull(product);
        Assertions.assertEquals(request.getName(), product.getName());
    }

    @Test
    @Order(3)
    public void testProductUpdate(){
        String name = UUID.randomUUID().toString();
        ProductUpdate request = new ProductUpdate()
                .setId(product.getId())
                .setName(name);
        ResponseEntity<Product> productResponse = this.restTemplate.exchange("/plugins/Product/updateProduct",HttpMethod.PUT, new HttpEntity<>(request), Product.class);
        Assertions.assertEquals(200, productResponse.getStatusCodeValue());
        product = productResponse.getBody();
        assertProduct(request, product);

    }

}
