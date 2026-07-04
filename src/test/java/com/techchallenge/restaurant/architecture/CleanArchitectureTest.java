package com.techchallenge.restaurant.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class CleanArchitectureTest {

  private static final String BASE_PACKAGE = "com.techchallenge.restaurant";

  private static JavaClasses classes;

  @BeforeAll
  static void importClasses() {
    classes = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages(BASE_PACKAGE);
  }

  @Test
  void domainMustNotDependOnPortsServicesAdaptersOrConfig() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application.domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "..application.port..",
            "..application.service..",
            "..adapter..",
            "..config..");

    rule.check(classes);
  }

  @Test
  void domainMustBeFreeOfFrameworkAnnotations() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application.domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "jakarta.persistence..",
            "javax.persistence..");

    rule.check(classes);
  }

  @Test
  void applicationLayerMustNotDependOnAdaptersOrConfig() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..config..");

    rule.check(classes);
  }

  @Test
  void controllersMustNotDependOnOutputAdaptersOrServicesDirectly() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..adapter.input.controller..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "..adapter.output..",
            "..application.service..");

    rule.check(classes);
  }

  @Test
  void jpaEntitiesMustOnlyLiveInThePersistenceAdapter() {
    ArchRule rule = classes()
        .that().areAnnotatedWith(Entity.class)
        .should().resideInAPackage("..adapter.output.postgres.model..");

    rule.check(classes);
  }

  @Test
  void outputPortImplementationsMustResideInOutputAdapter() {
    ArchRule rule = classes()
        .that().implement(JavaClass.Predicates.resideInAPackage("..application.port.output.."))
        .should().resideInAPackage("..adapter.output..");

    rule.check(classes);
  }

  @Test
  void inputPortImplementationsMustResideInApplicationService() {
    ArchRule rule = classes()
        .that().implement(JavaClass.Predicates.resideInAPackage("..application.port.input.."))
        .should().resideInAPackage("..application.service..");

    rule.check(classes);
  }

  @Test
  void outputAdaptersMustNotDependOnInputAdapters() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..adapter.output..")
        .should().dependOnClassesThat().resideInAPackage("..adapter.input..");

    rule.check(classes);
  }

  @Test
  void layersMustNotHaveCyclicDependencies() {
    slices()
        .matching(BASE_PACKAGE + ".(*)..")
        .should().beFreeOfCycles()
        .check(classes);
  }

  @Test
  void layeredArchitectureIsRespected() {
    layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("Domain").definedBy("..application.domain..")
        .layer("Application").definedBy("..application.port..", "..application.service..",
            "..application.mapper..", "..application.exception..", "..application.util..")
        .layer("Adapter").definedBy("..adapter..")
        .layer("Config").definedBy("..config..")

        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter", "Config")
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter", "Config")
        .whereLayer("Adapter").mayOnlyBeAccessedByLayers("Config")

        .check(classes);
  }
}
