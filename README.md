[![Build Status](https://github.com/javiertuya/samples-test-dev/actions/workflows/test.yml/badge.svg)](https://github.com/javiertuya/samples-test-dev/actions/workflows/test.yml)
[![Javadoc](https://img.shields.io/badge/%20-javadoc-blue)](https://javiertuya.github.io/samples-test-dev/)

# samples-test-dev

Este proyecto es utilizado como proyecto base para el desarrollo y a modo de ejemplo para ilustrar algunos aspectos del desarrollo y automatización de pruebas para las asignaturas relacionadas con ingenieria del software, sistemas de información y pruebas de software.

[Descargar la última versión](https://github.com/javiertuya/samples-test-dev/releases) - 
[Ver más detalles en el javadoc](https://javiertuya.github.io/samples-test-dev/)

## Contenido

Permite ilustrar, entre otros:
- Repaso del uso de JDBC para acceder a bases de datos
- Un conjunto de utilidades para simplificar el acceso a base de datos y el uso de tablas en Swing
- Implementación de MVC con Swing
- Automatización de pruebas unitarias con varias versiones de JUnit
- Estructura y configuración de un proyecto Maven y diferentes reports

Contiene los siguientes paquetes principales:
- `giis.demo.jdbc`: Repaso de acceso a base de datos con jdbc
- `giis.demo.tkrun`: Ilustra estructura de proyecto MVC con Swing (TicketRun)
- `giis.demo.tkrun.ut`: Ilustra pruebas con JUnit para TicketRun
- `giis.demo.util`: Diferentes utilidades de uso por parte de los anteriores

La estructura es la estándar de maven:
- `src/main/java`: Codigo fuente de aplicación
- `src/test/java`: Pruebas unitarias
- `target`: Generado con el codigo objeto y reports

## Requisitos e Instalación

- [Descargar la última versión](https://github.com/javiertuya/samples-test-dev/releases) 
  y disponer al menos de Java 8 JDK

- Opción 1: Apache Maven:
  - Asegurarse de que JAVA_HOME apunta a un JDK y no JRE
  - Ejecución completa: `mvn install`, incluye generación del Javadoc
  - Solo pruebas unitarias: `mvn test`
  - Ejecución sin tests: `mvn install -DskipTests=true`, genera todos los jar incluyendo javadoc

- Opción 2: Eclipse con M2Eclipse instalado (algunas distribuciones como Oxigen IDE for Java EE Developers ya lo incluyen).
  Desde la raiz del proyecto:
  - Asegurarse de que esta configurado JDK: Desde build path, editar JRE System Library y en Environment
	comprobar que JavaSE-1.8 apunta a un JDK en vez de un JRE
  - *Maven->Update Project*
  - *Run As->Maven install*

Programa principal (aplicaciones swing): `giis.demo.util.SwingMain`

## Reports

La instalacion anterior compilará, ejecutará pruebas y dispondrá de los reports en carpetas dentro de `target`:
- `apidocs/index.html`: javadoc del proyecto
- `site/surefire-report.html`: report de las pruebas unitarias (ut)
- `site/junit*`: report consolidado de todas las pruebas con el formato que genera junit
- `site/jacoco-ut`: reports de cobertura de código

## GitHub Actions y Dependabot

Este proyecto está configurado con los correspondientes scripts:
- La ejecución del workflow está configurada pero desactivada para que no se ejecute tras push y pull request. 
  Para reactivarla quitar los comentarios en las primeras líneas de `.github/workflows/test.yml`
- Las actualizaciones de Dependabot están activadas. 
  Para desactivarlas eliminar `.github/dependabot.yml`
