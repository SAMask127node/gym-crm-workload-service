package com.example.workload.bdd;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/workload_component.feature")
@ConfigurationParameter(key = "cucumber.glue", value = "com.example.workload.bdd")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, summary")
public class CucumberComponentTest { }