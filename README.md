htmlunit-runner-freemarker
==========================

Runner to execute freemarker templates using htmlunit-maven-plugin. This runner
processes test files as freemarker templates (usually .ftl files) and then it
writes processed result into the test runner file.

## Requirements
The following dependencies are marked as provided so they must be define by
the container.

Dependency for ```org.htmlunit.maven.runner.FreemarkerRunner```:

* freemarker 2.3.9 or later.

Dependencies for ```org.htmlunit.maven.runner.FreemarkerWebContextRunner```:

* servlet-api 2.2 or later.
* spring-web 2.0 or later.
* spring-webmvc 2.0 or later.
* spring-test 2.5 or later.

## Usage
Configure [htmlunit-maven-plugin](https://github.com/seykron/htmlunit-maven-plugin)
with one of the two runners provided by this project.

```org.htmlunit.maven.runner.FreemarkerRunner``` just processes test files as
Freemarker views and writes the output to each test runner file.

```org.htmlunit.maven.runner.FreemarkerWebContextRunner``` also processes test
files as Freemarker views and additionally it initializes a Spring
WebApplicationContext to emulate a Freemarker view inside a web application.

```
<plugin>
  <groupId>com.github.seykron</groupId>
  <artifactId>htmlunit-maven-plugin</artifactId>
  <version>1.1</version>
  <dependencies>
    <!-- Runner dependency. -->
    <dependency>
      <groupId>com.github.seykron</groupId>
      <artifactId>htmlunit-runner-freemarker</artifactId>
      <version>1.0</version>
    </dependency>
  </dependencies>
  <executions>
    <execution>
      <id>freemarker-tests</id>
      <phase>verify</phase>
      <goals>
        <goal>run</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    **<runnerClassName>org.htmlunit.maven.runner.FreemarkerWebContextRunner</runnerClassName>**
    <webClientConfiguration>
      <javaScriptEnabled>true</javaScriptEnabled>
    </webClientConfiguration>
    <runnerConfiguration>
      <outputDirectory>${project.build.directory}/htmlunit-tests</outputDirectory>
      <testFiles>
        file:${basedir}/src/test/resources/**/*Test.ftl;
        classpath:/org/htmlunit/maven/*TestSpring.ftl
      </testFiles>
    </runnerConfiguration>
  </configuration>
</plugin>
```

