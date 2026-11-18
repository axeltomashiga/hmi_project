# Módulo de mapa 2D (visualización del patrullaje)

Muestra el recorrido del robot sobre el plano del Pabellón A dentro de la pantalla **Supervisión**.
Es un agregado al enunciado: cubre el "esquema del entorno o recorrido" (§1) y hace visible la
telemetría de ubicación (§2.1).

## Flujo de datos

```
SimulatedRobotRepository  --(StateFlow<TelemetryData>, floorMap)-->  RobotViewModel  -->  RobotUiState
   (cinemática, rutas)                                                                          |
                                                                                                v
                                                                       DashboardScreen -> PatrolMap (Canvas)
```

- `data/model/MapModel.kt`: `FloorMap` (zonas, waypoints, base, rutas por misión) y `BuildingMap.default`.
- `RobotRepository.floorMap`: el plano llega por el Repository; la UI no conoce al simulador.
- `RobotViewModel`: arma `plannedRoute` (ruta de la misión activa, o de la seleccionada si no hay) y `routeReached`.
- `ui/components/PatrolMap.kt`: solo dibuja estado. Recibe plano, ruta, avance y `TelemetryData`.

## Convenciones

- Unidades en metros; origen en la Estación Base (0, 0). Eje X a la derecha, eje Y hacia abajo.
- `headingDeg`: 0° apunta a +X y crece hacia +Y (se dibuja tal cual en el Canvas).
- Tick del simulador: 200 ms. Las variables ambientales y de enlace se refrescan cada 1 s.

## Rutas de misión

| Misión | Waypoints (índices) | Cantidad |
|---|---|---|
| MIS-A Ronda Integral | 0–7 | 8 |
| MIS-B Inspección Técnica de Tableros | 2, 3, 4, 5, 7 | 5 |
| MIS-C Patrullaje Perimetral y Accesos | 0, 2, 3, 4 | 4 |

## Qué muestra el mapa

- Zonas del edificio, grilla de 5 m y barra de escala.
- Ruta planificada (punteada) y tramo recorrido (sólido).
- Waypoints numerados: visitado (verde), siguiente (ámbar), pendiente (gris). Al tocar uno se ve su zona y la distancia al robot.
- Robot con flecha de orientación, cono de visión (la "guardia" barre el entorno al detenerse en cada punto) y rastro.
- Escenarios: gas → zona en rojo con pulso; obstáculo → marca delante del robot; batería baja → anillo en la base; desconexión → robot gris con borde punteado y la leyenda "Última posición conocida".
- Personas detectadas: puntos delante del robot (solo conteo, nunca imágenes).

## Decisiones y limitaciones

- **Tiempo comprimido:** el simulador usa `TIME_SCALE = 2`, así que una ronda dura ~40 s. La velocidad que se informa (0,85 m/s) es la del robot simulado, no la del reloj de pared.
- **Sin colisiones ni planificación de ruta:** el robot va en línea recta entre waypoints. El plano está dibujado para que esos tramos queden dentro de zonas, pero el simulador no lo verifica.
- **Retorno a base:** vuelve sobre sus pasos (waypoints visitados en orden inverso y luego la base).
- **Personas:** se generan de forma determinista al inspeccionar Oficinas (2) y Depósito (1). Es una simulación, no detección real.
- **Desconexión:** el simulador congela la posición; el mapa la marca como antigua. Mientras dura el corte no se simula movimiento.
- **Sin pantalla completa:** el mapa ocupa el ancho de la tarjeta en Supervisión.
- Los tests de cinemática están en `SimulatedRobotRepositoryTest`.
