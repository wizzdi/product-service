package com.wizzdi.flexicore.product;

import com.wizzdi.flexicore.product.app.App;
import com.wizzdi.flexicore.product.model.Feature;
import com.wizzdi.flexicore.product.request.FeatureCreate;
import com.wizzdi.flexicore.product.request.FeatureFilter;
import com.wizzdi.flexicore.product.request.FeatureUpdate;
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

public class FeatureControllerTest {

    private Feature feature;
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
    public void testFeatureCreate() {
        String name = UUID.randomUUID().toString();
        FeatureCreate request = new FeatureCreate()
                .setName(name);
        ResponseEntity<Feature> featureResponse = this.restTemplate.postForEntity("/plugins/Feature/createFeature", request, Feature.class);
        Assertions.assertEquals(200, featureResponse.getStatusCodeValue());
        feature = featureResponse.getBody();
        assertFeature(request, feature);

    }

    @Test
    @Order(2)
    public void testGetAllFeatures() {
        FeatureFilter request=new FeatureFilter();
        ParameterizedTypeReference<PaginationResponse<Feature>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Feature>> featureResponse = this.restTemplate.exchange("/plugins/Feature/getAllFeatures", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, featureResponse.getStatusCodeValue());
        PaginationResponse<Feature> body = featureResponse.getBody();
        Assertions.assertNotNull(body);
        List<Feature> features = body.getList();
        Assertions.assertNotEquals(0,features.size());
        Assertions.assertTrue(features.stream().anyMatch(f->f.getId().equals(feature.getId())));


    }

    public void assertFeature(FeatureCreate request, Feature feature) {
        Assertions.assertNotNull(feature);
        Assertions.assertEquals(request.getName(), feature.getName());
    }

    @Test
    @Order(3)
    public void testFeatureUpdate(){
        String name = UUID.randomUUID().toString();
        FeatureUpdate request = new FeatureUpdate()
                .setId(feature.getId())
                .setName(name);
        ResponseEntity<Feature> featureResponse = this.restTemplate.exchange("/plugins/Feature/updateFeature",HttpMethod.PUT, new HttpEntity<>(request), Feature.class);
        Assertions.assertEquals(200, featureResponse.getStatusCodeValue());
        feature = featureResponse.getBody();
        assertFeature(request, feature);

    }

}
