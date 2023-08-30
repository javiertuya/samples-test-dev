[![Build Status](https://github.com/javiertuya/samples-test-dev/actions/workflows/test.yml/badge.svg)](https://github.com/javiertuya/samples-test-dev/actions/workflows/test.yml)
[![Javadoc](https://img.shields.io/badge/%20-javadoc-blue)](https://javiertuya.github.io/samples-test-dev/)

# samples-test-dev

Este proyecto es utilizado como proyecto base o plantilla para el desarrollo y a modo de ejemplo para ilustrar algunos aspectos del desarrollo y automatización de pruebas para las asignaturas relacionadas con ingenieria del software, sistemas de información y pruebas de software.

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

Este proyecto requiere un mínimo de Java 8 JDK.

Preparación del proyecto:
- Si se va a utilizar solamente para pruebas y experimentación, clonar/descargar el zip
  (opción `<> Code` en esta página) o desde [Releases](https://github.com/javiertuya/samples-test-dev/releases)
- Si se va a utilizar como plantilla para un proyecto propio en GitHub,
  Crear el repositorio directamente en Git usando este proyecto como plantilla
  (opción `Use this template` en esta página)
- En este segundo caso es imprescindible cambiar `samples-test-dev` por el nombre del nuevo proyecto
  (se puede hacer desde el propio repositorio creado) en los ficheros:
  - `.project`: cambiar `<name>samples-test-dev</name>` para incluir el nombre del proyecto
  - `pom.xml`: cambiar `<artifactId>samples-test-dev</artifactId>` para incluir el nombre del proyecto

Ejecución del proyecto:
- Desde línea de comandos con [Apache Maven](https://maven.apache.org/download.cgi):
  - Asegurarse de que JAVA_HOME apunta a un JDK y no JRE
  - Ejecución completa: `mvn install`, incluye generación del Javadoc
  - Solo pruebas unitarias: `mvn test`, todas las pruebas: `mvn verify`
  - Ejecución sin tests: `mvn install -DskipTests=true`, genera todos los jar incluyendo javadoc

- Desde Eclipse con M2Eclipse instalado (las distribuciones recientes ya lo incluyen).
  Desde la raiz del proyecto:
  - Asegurarse de que esta configurado JDK: Desde build path, editar JRE System Library y en Environment
	comprobar que JavaSE-1.8 apunta a un JDK en vez de un JRE
  - *Maven->Update Project*: Actualiza todas las dependencias y permite usar el proyecto como 
    si hubiera sido creado desde el propio Eclipse
  - *Run As->Maven install*: Ejecuta este (o otros) comandos maven desde Eclipse
  - Ejecutar los tests en `src/main/test` o el programa principal (aplicación swing)
    en la clase `giis.demo.util.SwingMain`

## Reports

La instalacion anterior compilará, ejecutará pruebas y dispondrá de los reports en carpetas dentro de `target`:
- `site/testapidocs/index.html`: javadoc del proyecto
- `site/surefire-report.html`: report de las pruebas unitarias (ut)
- `site/junit*`: report de todas las pruebas con el formato que genera junit
- `site/jacoco-ut`: reports de cobertura de código

## GitHub Actions y Dependabot

Este proyecto está configurado con los correspondientes scripts:
- La ejecución del workflow está configurada para que se ejecute tras push y pull request. 
  Para desactivarla comentar las primeras líneas de `.github/workflows/test.yml`
- Las actualizaciones de Dependabot están activadas. 
  Para desactivarlas eliminar `.github/dependabot.yml`
