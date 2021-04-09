# Preparación

## Proyecto original
El proyecto original preparado para realizar la Kata se puede descargar del [Github de ADCSD](https://github.com/ADCSD/kata-string_calculator/tree/main/original) e importarlo directamente en el IDE de desarrollo. Debes descargar el proyecto que está dentro de la carpeta `original`.


## Estructura de proyecto
Se trata de un proyecto escrito en Java con maven, que consta de dos clases vacías: `TestStringCalculator` y `TestStringCalculator`.

## Aclaraciones
Vamos a tratar de ver con mucho detalle y paso por paso la implementación y su `Refactor` durante los primeros `Requisitos`, pero a medida que vayamos avanzando iremos resumiendo para que no se haga eterno el documento. 

Obviamente existen muchísimas formas de implementar este ejercicio, lo que veremos a continuación no es más que una de esas formas, ni mejor ni peor que cualquier otra. Es posible que tu tengas otra diferente, perfecto, también es válida.

Además, el nivel de `Refactoring` que vamos a ver en este ejercicio está pensado para explicar ciertas técnicas o acciones más habituales. Esto no quiere decir que siempre se deba llegar a este nivel, ni mucho menos, o que solo exista una forma de realizar el `Refactoring`. Al igual que las implementaciones, hay muchas formas de refactorizar, cada uno debería quedarse en el nivel de refactorización con el que se sienta más cómodo. Debemos buscar un compromiso entre el tiempo dedicado y la facilidad de lectura que estamos dejando, pero sin obsesionarnos.