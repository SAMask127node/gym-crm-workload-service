package com.example.workload.bdd.hooks;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest // loads the Spring context for steps
public class SpringCucumberConfig { }