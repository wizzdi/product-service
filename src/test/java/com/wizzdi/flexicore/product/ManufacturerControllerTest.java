package com.wizzdi.flexicore.product;

import com.wizzdi.flexicore.product.app.App;
import com.wizzdi.flexicore.product.model.Manufacturer;
import com.wizzdi.flexicore.product.request.ManufacturerCreate;
import com.wizzdi.flexicore.product.request.ManufacturerFilter;
import com.wizzdi.flexicore.product.request.ManufacturerUpdate;
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

public class ManufacturerControllerTest {

    private Manufacturer manufacturer;
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
    public void testManufacturerCreate() {
        String name = UUID.randomUUID().toString();
        ManufacturerCreate request = new ManufacturerCreate()
                .setName(name);
        ResponseEntity<Manufacturer> manufacturerResponse = this.restTemplate.postForEntity("/plugins/Manufacturer/createManufacturer", request, Manufacturer.class);
        Assertions.assertEquals(200, manufacturerResponse.getStatusCodeValue());
        manufacturer = manufacturerResponse.getBody();
        assertManufacturer(request, manufacturer);

    }

    @Test
    @Order(2)
    public void testGetAllManufacturers() {
        ManufacturerFilter request=new ManufacturerFilter();
        ParameterizedTypeReference<PaginationResponse<Manufacturer>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Manufacturer>> manufacturerResponse = this.restTemplate.exchange("/plugins/Manufacturer/getAllManufacturers", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, manufacturerResponse.getStatusCodeValue());
        PaginationResponse<Manufacturer> body = manufacturerResponse.getBody();
        Assertions.assertNotNull(body);
        List<Manufacturer> manufacturers = body.getList();
        Assertions.assertNotEquals(0,manufacturers.size());
        Assertions.assertTrue(manufacturers.stream().anyMatch(f->f.getId().equals(manufacturer.getId())));


    }

    public void assertManufacturer(ManufacturerCreate request, Manufacturer manufacturer) {
        Assertions.assertNotNull(manufacturer);
        Assertions.assertEquals(request.getName(), manufacturer.getName());
    }

    @Test
    @Order(3)
    public void testManufacturerUpdate(){
        String name = UUID.randomUUID().toString();
        ManufacturerUpdate request = new ManufacturerUpdate()
                .setId(manufacturer.getId())
                .setName(name);
        ResponseEntity<Manufacturer> manufacturerResponse = this.restTemplate.exchange("/plugins/Manufacturer/updateManufacturer",HttpMethod.PUT, new HttpEntity<>(request), Manufacturer.class);
        Assertions.assertEquals(200, manufacturerResponse.getStatusCodeValue());
        manufacturer = manufacturerResponse.getBody();
        assertManufacturer(request, manufacturer);

    }

}
