# TF-IDF resumen (Scala)

## Crear un build

Requisitos: Java 17+ y sbt 1.10.x.

Desde la raiz del proyecto:

```bash
sbt compile
```

## Utilizar el programa

El programa requiere un unico argumento: el directorio con archivos `.txt`.

```bash
sbt "run \"/ruta/al/directorio\""
```

Ejemplo con el set de prueba:

```bash
sbt "run \"/home/lucas/Documentos/NotasDeLucas/UNS/LenguajesDeProgramacion/Proyectos/Promo/Etapa 2/Desarrollo/TestsCatedra\""
```

La salida se imprime en consola, una oracion por linea.
