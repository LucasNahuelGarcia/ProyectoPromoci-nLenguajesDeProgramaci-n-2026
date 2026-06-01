# Instrucciones de Implementación: Sistema TF-IDF en Scala.

## 1. Objetivo del Sistema.
Eres un agente de desarrollo de software por terminal, operando en un entorno de trabajo funcional. Tu tarea es programar desde cero un motor de resumen extractivo utilizando el algoritmo TF-IDF, escrito enteramente en Scala.
El sistema debe leer archivos de texto plano, calcular la relevancia matemática de cada oración, y devolver las diez oraciones más importantes.

## 2. Restricciones Arquitectónicas Fundamentales.
Este es un proyecto académico de Ciencias de la Computación, diseñado para evaluar el dominio avanzado del paradigma funcional.
Queda estrictamente prohibido utilizar patrones de diseño orientados a objetos como interfaces abstractas, repositorios, o sistemas de inyección de dependencias.
Debes utilizar exclusivamente funciones puras, inmutabilidad estricta y constructores nativos de Scala.

## 3. Estructura de Directorios Permitida.

Para garantizar la separación conceptual y la correcta incorporación de unidades lógicas, sin generar una sobreingeniería, solo tienes autorización para crear los siguientes archivos de código fuente.

### Código fuente

1. `src/main/scala/domain/Model.scala`
    
2. `src/main/scala/domain/Engine.scala`
    
3. `src/main/scala/infrastructure/IOHandler.scala`
    
4. `src/main/scala/Main.scala`
    

### Recursos

También se permite agregar archivos de recursos bajo:

```text
src/main/resources/
```

Estos recursos deben contener únicamente datos de configuración o datos estáticos utilizados por el sistema.

Ejemplos válidos:

```text
src/main/resources/stopwords.txt
```

```text
src/main/resources/stopwords-es.txt
```

Ejemplos inválidos:

```text
src/main/resources/Engine.scala
src/main/resources/Tokenizer.scala
```

No se permite agregar nuevos archivos fuente (`.scala`) fuera de los cuatro definidos anteriormente.

Cualquier otro archivo de código será considerado una alucinación y un error crítico de diseño.

## 4. Unidad de Dominio, Model.scala.
Este archivo define la máquina de estados del programa mediante el sistema de tipos.
Debes definir explícitamente los tipos para los resultados intermedios y las fases de transformación del texto, asegurando que los estados ilegales sean irrepresentables por el compilador.

* **Ocultamiento de tipos base.**
  * Define `opaque type Token = String`. Provee su objeto compañero para la instanciación segura y la extracción de su valor.
* **Fases del procesamiento de texto.**
  * `case class RawSentence(text: String)`: Representa la oración recién segmentada del original.
  * `case class TokenizedSentence(original: RawSentence, tokens: List[Token])`: Representa la oración procesada.
  * `case class ScoredSentence(sentence: TokenizedSentence, score: Double)`: Representa la oración con su puntaje final evaluado.
* **Tipos intermedios para estructuras matemáticas.**
  * `type IdfModel = Map[Token, Double]`
  * `type TfModel = Map[Token, Double]`

## 5. Unidad de Dominio, Engine.scala.
Este archivo consolida toda la lógica pura de la aplicación.
Al ser referencialmente transparente, no sabe que el disco rígido existe. Las dependencias externas (como las stopwords) deben ingresar como parámetros de función `Set[String]`. No puedes importar `scala.io.Source` ni utilizar `println` aquí.

* **Responsabilidad de Limpieza Textual.**
  * Implementa una función que reciba un `String` crudo y devuelva una `List[RawSentence]` utilizando expresiones regulares.
  * Implementa una función que transforme una `RawSentence` en una `TokenizedSentence`. Debe recibir un `Set[String]` con las stopwords. Convierte a minúsculas, elimina puntuación y filtra los tokens.
* **Responsabilidad Matemática TF-IDF.**
  * Implementa `computeIdf`. Recibe una `List[TokenizedSentence]` y devuelve un `IdfModel`. Aplica: `log(N / (1 + df(t)))`.
  * Implementa `scoreSentence`. Recibe una `TokenizedSentence` y el `IdfModel`, y devuelve una `ScoredSentence` aplicando la sumatoria del producto `TF * IDF`.

## 6. Unidad de Infraestructura, IOHandler.scala.

Este archivo actúa como el caparazón impuro del sistema. Centraliza toda la interacción con el sistema operativo y la consola.

No debe contener lógica de segmentación, limpieza de puntuación ni cálculos matemáticos.

### Entrada de Datos

Implementa una función con la siguiente firma conceptual:

```scala
def loadTexts(directory: String): LazyList[String]
```

#### Responsabilidad

- Recorrer un directorio.
    
- Detectar archivos con extensión `.txt`.
    
- Leer su contenido.
    
- Devolver los textos crudos.
    

#### Requisitos

- Utilizar evaluación perezosa mediante `LazyList` o vistas (`.view`) cuando resulte apropiado.
    
- No realizar tokenización.
    
- No realizar limpieza textual.
    
- No calcular TF-IDF.
    

### Salida de Datos

Implementa una función con la siguiente firma conceptual:

```scala
def printSummary(
    summary: List[ScoredSentence]
): Unit
```

#### Responsabilidad

Recibir las diez mejores oraciones ya puntuadas y mostrarlas al usuario mediante un formato legible.

#### Prohibiciones

No debe:

- ordenar resultados;
    
- calcular puntajes;
    
- modificar el contenido de las oraciones;
    
- ejecutar transformaciones matemáticas.

## 7. Orquestador, Main.scala.
Punto de entrada principal. Conecta los efectos secundarios con el núcleo puro.

* **Flujo de Ejecución Secuencial.**
  1. Llama a `IOHandler` para cargar el diccionario de stopwords desde los recursos.
  2. Solicita a `IOHandler` la carga perezosa de los textos.
  3. Pasa los textos y el set de stopwords a `Engine` para generar las `TokenizedSentence`.
  4. Solicita a `Engine` el cálculo del `IdfModel` global.
  5. Mapea cada oración con `scoreSentence` para obtener las `ScoredSentence`.
  6. Ordena matemáticamente de mayor a menor, extrae las diez principales y las envía a `IOHandler` para su visualización.****