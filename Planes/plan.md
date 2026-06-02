# Plan de implementacion (capas)

## Capa 0 - Fundacion del proyecto
1) Crear esqueleto sbt minimo (build.sbt y project/build.properties).
2) Crear estructura de carpetas para codigo y recursos.
3) Agregar recurso stopwords-es.txt con formato "una palabra por linea".

## Capa 1 - Modelo de dominio (puro)
4) Definir tipos en src/main/scala/domain/Model.scala (Token, RawSentence, TokenizedSentence, ScoredSentence, IdfModel, TfModel).
5) Verificar que los tipos habiliten el flujo del modelo sin estados ilegales.

## Capa 2 - Motor puro (Engine)
6) Implementar segmentacion de oraciones en Engine en dos fases (split por \\R+ y luego por (?<=[.!?])\\s+, trim).
7) Implementar tokenizacion (minusculas, split por [^\p{L}\p{N}]+, filtrar stopwords).
	- Puntuacion: eliminarla segun enunciado, aunque la nota defina token en abstracto.
8) Implementar computeIdf (N sobre oraciones validas, df por presencia).
	- Oracion valida: RawSentence que, tras tokenizacion y filtrado de stopwords, tenga al menos un Token.
9) Implementar scoreSentence (TF por frecuencia relativa, sumatoria por token distinto).
	- Tokens repetidos: TF usa la frecuencia relativa; al sumar, usar tokens distintos para no duplicar el peso.

## Capa 3 - Infraestructura (IO)
10) Implementar carga de stopwords desde classpath en IOHandler (Set[String]).
11) Implementar carga de textos desde directorio en IOHandler (LazyList[String], solo .txt, orden por nombre).
12) Implementar printSummary (una oracion por linea, sin numeracion).

## Capa 4 - Orquestacion (Main)
13) Parsear argumentos posicionales (1 argumento) y mostrar uso en error.
14) Encadenar IO -> Engine -> ranking -> IO (ordenar por -score y index).
15) Manejar errores con Either y codigos de salida.

## Capa 5 - Verificacion manual
16) Ejecutar con un directorio real de .txt.
17) Probar casos de error: sin argumentos, directorio invalido, sin .txt, stopwords faltante.
