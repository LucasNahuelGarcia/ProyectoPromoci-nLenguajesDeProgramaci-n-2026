Lenguaje de implementacion: Scala

Implementar una aplicación de consola que lea todos los archivos de texto (`.txt`) de un directorio y produzca un resumen extractivo del contenido.

Los archivos de texto tendrán el estilo de un documento de Wikipedia y pueden descargarse desde el enlace provisto por la cátedra.

La lógica de procesamiento debe estar separada de la entrada/salida.

El resumen se construye mediante un algoritmo basado en **TF-IDF** (*Term Frequency - Inverse Document Frequency*).

De manera breve, el algoritmo funciona como sigue:

1. Se reúnen todos los documentos y se segmenta el texto en oraciones.

2. Cada oración se tokeniza:
   - Se convierte a minúsculas.
   - Se elimina la puntuación.
   - Se descartan *stopwords*.

   **Nota:** En este contexto, un token es una palabra o un símbolo de puntuación.

3. Se calcula el valor IDF de cada token `t` sobre el conjunto de oraciones:

   ```
   IDF(t) = log(N / (1 + df(t)))
   ```

   donde:

   - `N` es el total de oraciones.
   - `df(t)` es la cantidad de oraciones que contienen al token `t`.

4. Cada oración `s` recibe un puntaje igual a la suma de:

   ```
   TF(t,s) × IDF(t)
   ```

   para todos sus tokens, donde:

   ```
   TF(t,s)
   ```

   es la frecuencia relativa del token `t` en esa oración, es decir:

   ```
   cantidad de apariciones de t en s
   ---------------------------------
      total de tokens de s
   ```

5. Se seleccionan las oraciones de mayor puntaje (considerar un máximo de 10 oraciones).

El resultado deberá mostrarse al usuario en la consola.