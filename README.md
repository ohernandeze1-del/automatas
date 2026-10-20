# Simulador de máquina expendedora mediante autómatas y lenguajes formales

Aplicación educativa de escritorio en **Java Swing** para demostrar:

- Simulación de máquina expendedora
- Autómata finito con estados **E0–E5**
- Lenguaje de instrucciones **`.vm`**
- Análisis **léxico, sintáctico y semántico**
- Ejecución secuencial con trazas
- Importación/exportación en **JSON**

## Requisitos

- Java 17+
- Maven 3.9+

## Ejecutar desde línea de comandos

```bash
mvn clean test
mvn exec:java
```

## Ejecutar desde IDE

1. Abrir el proyecto Maven.
2. Ejecutar `com.ohernandeze1.automatas.app.Main`.
3. Para pruebas, ejecutar `mvn test` o el runner JUnit del IDE.

## Estructura del proyecto

```text
src/main/java/com/ohernandeze1/automatas
  app/                # punto de entrada
  ui/                 # interfaz Swing
  model/              # modelo Producto
  simulator/          # lógica de inventario/saldo/compra/cambio/reembolso
  automata/           # estados E0-E5 y transiciones
  vm/
    lexer/            # tokens, lexer con línea/columna
    parser/           # parser y errores sintácticos
    semantic/         # validación semántica
    ast/              # instrucciones del lenguaje
    engine/           # ejecución secuencial y trazas
  io/                 # import/export JSON

src/main/resources/examples
  compra_exitosa.vm
  sin_stock.vm
  saldo_insuficiente.vm

src/test/java/com/ohernandeze1/automatas
  pruebas de lexer, parser, semántica, autómata y simulación
```

## Lenguaje `.vm`

Cada instrucción termina en `;`.

```text
PRODUCTO(codigo,nombre,precio,cantidad);
CARGAR(codigo,cantidad);
MONEDA(valor);
SELECCIONAR(codigo);
COMPRAR();
CANCELAR();
MOSTRAR();
```

### Reglas implementadas

- Monedas válidas: `1`, `5`, `10`
- `precio > 0`
- cantidad inicial `>= 0`
- código de producto único
- `CARGAR` de producto no declarado: error
- Compra duplicada sin nueva selección: error
- Casos reportados: saldo insuficiente, sin selección, producto desconocido, sin stock

## Autómata de estados

- `E0`: esperando monedas
- `E1`: moneda ingresada
- `E2`: producto seleccionado
- `E3`: procesando compra
- `E4`: entregando producto
- `E5`: devolviendo dinero

Tras compra/cancelación finaliza en `E0`.

## GUI (rúbrica visible)

La interfaz muestra:

- Tabla de inventario (código, nombre, precio, stock)
- Saldo actual
- Botones de moneda Q1/Q5/Q10
- Selección, comprar, cancelar y mostrar
- Estado y última transición del autómata
- Retroalimentación de dispensado/cambio/reembolso
- Editor `.vm` con abrir/guardar/ejecutar
- Tabla de tokens
- Errores léxicos/sintácticos/semánticos con número de línea
- Traza de ejecución secuencial
- Importación/exportación JSON

## Ejemplos `.vm`

- `src/main/resources/examples/compra_exitosa.vm`
- `src/main/resources/examples/sin_stock.vm`
- `src/main/resources/examples/saldo_insuficiente.vm`

## JSON

- **Exporta** inventario + saldo + selección actual
- **Importa** inventario + estado operativo compatible

## Capturas (guía)

Agregar en esta sección imágenes de:

1. Vista principal con inventario y controles
2. Ejecución exitosa de script
3. Error semántico/sintáctico resaltado
4. Exportación/importación JSON

