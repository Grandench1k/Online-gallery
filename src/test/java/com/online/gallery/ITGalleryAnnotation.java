package com.online.gallery;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = OnlineGalleryApplication.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public @interface ITGalleryAnnotation {
}
