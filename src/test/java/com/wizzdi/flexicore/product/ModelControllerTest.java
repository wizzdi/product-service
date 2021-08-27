package com.wizzdi.flexicore.product;

import com.wizzdi.flexicore.product.app.App;
import com.wizzdi.flexicore.product.model.Model;
import com.wizzdi.flexicore.product.request.ModelCreate;
import com.wizzdi.flexicore.product.request.ModelFilter;
import com.wizzdi.flexicore.product.request.ModelUpdate;
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

public class ModelControllerTest {

    private Model model;
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
    public void testModelCreate() {
        String name = UUID.randomUUID().toString();
        ModelCreate request = new ModelCreate()
                .setName(name);
        ResponseEntity<Model> modelResponse = this.restTemplate.postForEntity("/plugins/Model/createModel", request, Model.class);
        Assertions.assertEquals(200, modelResponse.getStatusCodeValue());
        model = modelResponse.getBody();
        assertModel(request, model);

    }

    @Test
    @Order(2)
    public void testGetAllModels() {
        ModelFilter request=new ModelFilter();
        ParameterizedTypeReference<PaginationResponse<Model>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Model>> modelResponse = this.restTemplate.exchange("/plugins/Model/getAllModels", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, modelResponse.getStatusCodeValue());
        PaginationResponse<Model> body = modelResponse.getBody();
        Assertions.assertNotNull(body);
        List<Model> models = body.getList();
        Assertions.assertNotEquals(0,models.size());
        Assertions.assertTrue(models.stream().anyMatch(f->f.getId().equals(model.getId())));


    }

    public void assertModel(ModelCreate request, Model model) {
        Assertions.assertNotNull(model);
        Assertions.assertEquals(request.getName(), model.getName());
    }

    @Test
    @Order(3)
    public void testModelUpdate(){
        String name = UUID.randomUUID().toString();
        ModelUpdate request = new ModelUpdate()
                .setId(model.getId())
                .setName(name);
        ResponseEntity<Model> modelResponse = this.restTemplate.exchange("/plugins/Model/updateModel",HttpMethod.PUT, new HttpEntity<>(request), Model.class);
        Assertions.assertEquals(200, modelResponse.getStatusCodeValue());
        model = modelResponse.getBody();
        assertModel(request, model);

    }

}
