# Datalake to Datamart (AEMET)

Este proyecto es una solución modular diseñada para la ingesta, procesamiento y exposición de datos meteorológicos (temperaturas) de la isla de Gran Canaria, utilizando la API abierta de AEMET.

El sistema implementa una arquitectura ETL (Extract, Transform, Load) dividida en tres módulos independientes para capturar datos en bruto (Datalake), procesarlos analíticamente (Datamart) y servirlos mediante una API REST.

## Módulos del Proyecto

El proyecto está estructurado en tres módulos Maven:

### 1. Datalake (Ingesta)
Se encarga de la extracción de datos desde la API de AEMET.
- **Funcionalidad:** Descarga datos de temperatura cada hora.
- **Filtrado:** Selecciona estaciones meteorológicas dentro de las coordenadas de Gran Canaria (Lat: 27.5-28.4, Lon: -16 a -15).
- **Almacenamiento:** Guarda la información en bruto en ficheros diarios con formato JSON (YYYY-MM-DD.events) en la carpeta `datalakes`.

### 2. Datamart (Procesamiento)
Procesa los datos en bruto para generar información de valor.
- **Funcionalidad:** Lee los ficheros de eventos generados por el Datalake.
- **Transformación:** Calcula y extrae las temperaturas máximas y mínimas registradas por estación y hora.
- **Persistencia:** Almacena los resultados estructurados en una base de datos SQLite (`datamart.db`) dentro de la carpeta `datamarts`.

### 3. API AEMET (Servicio)
Interfaz REST para consultar los datos procesados.
- **Funcionalidad:** Expone los datos almacenados en el Datamart.
- **Endpoints:**
  - `GET /v1/places`: Devuelve todos los lugares disponibles.
  - `GET /v1/places/with-max-temperature?from={fecha1}&to={fecha2}`: Lugares con temperaturas máximas en un rango de fechas.
  - `GET /v1/places/with-min-temperature?from={fecha1}&to={fecha2}`: Lugares con temperaturas mínimas en un rango de fechas.

## Tecnologías Utilizadas

* **Lenguaje:** Java 19.
* **Gestión de dependencias:** Maven.
* **Librerías principales:**
    * `Gson` (2.10) - Procesamiento de JSON.
    * `Jsoup` (1.11.3) - Conexión HTTP y crawling.
    * `SparkJava` (2.9.4) - Microframework para aplicaciones web.
    * `SQLite JDBC` (3.23.1) - Base de datos ligera.
    * `Jakarta EE` (RESTful Web Services).

## Requisitos Previos

* JDK 11 o superior (El proyecto original usa JDK 19).
* Maven instalado.
* **API Key de AEMET:** Necesitas registrarte en AEMET OpenData para obtener una clave válida.

## Instalación y Ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/datalake-to-datamart-aemet.git](https://github.com/tu-usuario/datalake-to-datamart-aemet.git)
    cd datalake-to-datamart-aemet
    ```

2.  **Compilar el proyecto:**
    Ejecuta el siguiente comando en la raíz para construir todos los módulos:
    ```bash
    mvn clean install
    ```

3.  **Ejecutar el Datalake:**
    El Crawler necesita tu API Key como argumento.
    ```bash
    cd datalake
    mvn exec:java -Dexec.mainClass="main.Main" -Dexec.args="TU_API_KEY_AEMET"
    # Nota: Si falla la primera vez, intentar un par de veces.
    ```

4.  **Ejecutar el Datamart:**
    Una vez generados los eventos, procesa los datos:
    ```bash
    cd datamart
    mvn exec:java -Dexec.mainClass="main.DataMart_create"
    ```

5.  **Desplegar la API:**
    El módulo `api_aemet` genera un archivo WAR que puede desplegarse en un servidor de aplicaciones (como Tomcat) o ejecutarse localmente si se configura el runner adecuado.

## Estructura de Directorios
├── datalake/ # Crawler y generación de eventos JSON 
├── datamart/ # Procesamiento ETL y base de datos SQLite 
├── api_aemet/ # API REST (Jakarta EE) 
├── aemet_data/ # Directorio raíz de datos │ 
├── datalakes/ # Salida de archivos .events │ 
    └── datamarts/ # Salida de base de datos .db 
    └── pom.xml # Configuración padre de Maven

## Autor

**Jorge Cubero Toribio**
*Desarrollo de Aplicaciones para la Ciencia de Datos (ULPGC)*
