# -*- coding: utf-8 -*-
"""
Script generador del paquete de diseño Figma para el TP Unitree G1 HMI.
Genera:
1. SVGs vectoriales editables listos para arrastrar y soltar en Figma.
2. Prototipo Web Interactivo de alta fidelidad (HTML/CSS/JS autónomo).
3. Documentación completa del Design System y Guía de Entrega.
"""

import os
import json

SVG_DIR = os.path.join("figma", "export_svg")
os.makedirs(SVG_DIR, exist_ok=True)

# -------------------------------------------------------------
# DEFINICIÓN DE PANTALLAS EN SVG
# Medidas: 390 x 844 (Frame móvil estándar Figma)
# -------------------------------------------------------------

def svg_header(title="Screen", width=390, height=844):
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}" height="{height}">
  <defs>
    <style>
      @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&amp;display=swap');
      text {{ font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }}
    </style>
    <filter id="cardShadow" x="-10%" y="-5%" width="120%" height="115%" filterUnits="userSpaceOnUse">
      <feDropShadow dx="0" dy="2" stdDeviation="4" flood-color="#0F172A" flood-opacity="0.06"/>
    </filter>
    <filter id="modalShadow" x="-20%" y="-20%" width="140%" height="140%" filterUnits="userSpaceOnUse">
      <feDropShadow dx="0" dy="10" stdDeviation="15" flood-color="#000000" flood-opacity="0.35"/>
    </filter>
  </defs>
  <!-- Background -->
  <rect width="{width}" height="{height}" fill="#F8FAFC" rx="36"/>
  <!-- Status Bar Android -->
  <rect x="0" y="0" width="{width}" height="40" fill="#F8FAFC" rx="36"/>
  <text x="32" y="26" font-size="12" font-weight="600" fill="#0F172A">22:00</text>
  <g transform="translate(310, 15)">
    <!-- Wifi Icon -->
    <path d="M0 10 A10 10 0 0 1 14 10 M2 12 A7 7 0 0 1 12 12 M5 14 A3 3 0 0 1 9 14" stroke="#0F172A" stroke-width="1.8" fill="none" stroke-linecap="round"/>
    <!-- Battery Icon -->
    <rect x="22" y="3" width="22" height="11" rx="2.5" fill="none" stroke="#0F172A" stroke-width="1.5"/>
    <rect x="24" y="5" width="15" height="7" rx="1.5" fill="#16A34A"/>
    <path d="M45 6.5 C45 6.5 46 6.5 46 8.5 C46 10.5 45 10.5 45 10.5" stroke="#0F172A" stroke-width="1.5" fill="none"/>
  </g>
'''

def svg_bottom_nav(active_tab="dashboard"):
    tabs = [
        ("dashboard", "Supervisión", "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"),
        ("alerts", "Alertas", "M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"),
        ("history", "Historial", "M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"),
        ("scenarios", "Escenarios", "M20.8 18.4L15 8V4h1c.55 0 1-.45 1-1s-.45-1-1-1H8c-.55 0-1 .45-1 1s.45 1 1 1h1v4L3.2 18.4C2.45 19.41 3.17 21 4.43 21h15.14c1.26 0 1.98-1.59 1.23-2.6z"),
        ("robot", "Unitree G1", "M12 2c1.1 0 2 .9 2 2s-.9 2-2 2-2-.9-2-2 .9-2 2-2zm9 7h-6v13h-2v-6h-2v6H9V9H3V7h18v2z")
    ]
    res = '''
  <!-- Bottom Navigation Bar -->
  <g id="BottomNavBar" transform="translate(0, 764)">
    <rect width="390" height="80" fill="#FFFFFF" filter="url(#cardShadow)"/>
    <line x1="0" y1="0" x2="390" y2="0" stroke="#E2E8F0" stroke-width="1"/>
'''
    x_positions = [39, 117, 195, 273, 351]
    for i, (tab_id, label, icon_path) in enumerate(tabs):
        cx = x_positions[i]
        is_active = (tab_id == active_tab)
        color = "#4F46E5" if is_active else "#64748B"
        font_weight = "700" if is_active else "500"
        
        # Pill indicator if active
        if is_active:
            res += f'<rect x="{cx - 24}" y="6" width="48" height="26" rx="13" fill="#EEF2FF"/>\n'
            
        res += f'''
    <g transform="translate({cx - 10}, 8)">
      <path d="{icon_path}" fill="{color}" transform="scale(0.85)"/>
    </g>
    <text x="{cx}" y="48" font-size="9.5" font-weight="{font_weight}" fill="{color}" text-anchor="middle">{label}</text>
'''
        # Badge on Alerts tab if active or has pending
        if tab_id == "alerts" and active_tab != "alerts":
            res += f'<circle cx="{cx + 10}" cy="10" r="7" fill="#DC2626"/>\n'
            res += f'<text x="{cx + 10}" y="13.5" font-size="9" font-weight="700" fill="#FFFFFF" text-anchor="middle">2</text>\n'
            
    res += '''  </g>
  <!-- Home Indicator bar -->
  <rect x="135" y="830" width="120" height="4" rx="2" fill="#94A3B8"/>
</svg>'''
    return res

# -------------------------------------------------------------------
# SCREEN 1: SELECCIÓN DEL ROBOT (RobotSelectionScreen)
# -------------------------------------------------------------------
def generate_screen_01_robot_selection():
    svg = svg_header("01_seleccion_robot")
    svg += '''
  <!-- Top App Bar -->
  <g transform="translate(20, 52)">
    <text x="0" y="20" font-size="18" font-weight="800" fill="#0F172A">Ficha Técnica: Unitree G1</text>
    <text x="0" y="38" font-size="12" font-weight="500" fill="#64748B">Plataforma Robótica de Inspección y Seguridad</text>
  </g>

  <!-- Robot Hero Card -->
  <g transform="translate(20, 106)">
    <rect width="350" height="150" rx="16" fill="#EEF2FF" stroke="#C7D2FE" stroke-width="1.5" filter="url(#cardShadow)"/>
    <!-- Robot Avatar circle -->
    <circle cx="65" cy="75" r="42" fill="#4F46E5"/>
    <!-- Robot humanoid stylized icon -->
    <path d="M65 48 A6 6 0 1 1 65 60 A6 6 0 1 1 65 48 M52 66 H78 V84 H72 V102 H68 V86 H62 V102 H58 V84 H52 Z" fill="#FFFFFF"/>
    
    <text x="122" y="48" font-size="16" font-weight="800" fill="#1E1B4B">Unitree G1 (Humanoid)</text>
    <rect x="122" y="56" width="95" height="20" rx="10" fill="#DCFCE7"/>
    <text x="169" y="70" font-size="10" font-weight="700" fill="#15803D" text-anchor="middle">✓ VINCULADO</text>

    <text x="122" y="94" font-size="11" font-weight="500" fill="#4338CA">Estatura: 1.27 m  •  Peso: 35 kg</text>
    <text x="122" y="110" font-size="11" font-weight="500" fill="#4338CA">Grados de Libertad: 23 - 43 DoF</text>
    <text x="122" y="126" font-size="11" font-weight="500" fill="#4338CA">Batería: 9000 mAh (25.2 V Li-ion)</text>
  </g>

  <!-- Justificación HRI Card -->
  <g transform="translate(20, 272)">
    <rect width="350" height="106" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="26" font-size="13" font-weight="700" fill="#0F172A">Justificación de Plataforma (G1 vs Go2)</text>
    <text x="16" y="48" font-size="11" font-weight="400" fill="#475569">
      <tspan x="16" dy="0">• La interacción bípeda resulta amigable y no invasiva en</tspan>
      <tspan x="16" dy="16">  edificios educativos y plantas corporativas.</tspan>
      <tspan x="16" dy="16">• La silueta humana permite inspección visual de tableros</tspan>
      <tspan x="16" dy="16">  a 1.20m de altura, extintores y señalética vertical.</tspan>
    </text>
  </g>

  <!-- Sensores Nativos vs Adicionales -->
  <g transform="translate(20, 394)">
    <rect width="350" height="136" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="26" font-size="13" font-weight="700" fill="#0F172A">Dotación de Sensores y Carga Útil</text>
    
    <!-- Native sensors -->
    <rect x="16" y="40" width="150" height="78" rx="8" fill="#F1F5F9"/>
    <text x="24" y="56" font-size="10.5" font-weight="700" fill="#334155">Sensores Nativos</text>
    <text x="24" y="72" font-size="9.5" fill="#64748B">• LiDAR 3D (SLAM 360°)</text>
    <text x="24" y="86" font-size="9.5" fill="#64748B">• Intel RealSense Depth</text>
    <text x="24" y="100" font-size="9.5" fill="#64748B">• IMU 6 ejes auto-balance</text>

    <!-- Additional service sensors -->
    <rect x="184" y="40" width="150" height="78" rx="8" fill="#FEF3C7"/>
    <text x="192" y="56" font-size="10.5" font-weight="700" fill="#92400E">Sensores del Servicio</text>
    <text x="192" y="72" font-size="9.5" fill="#78350F">• Sensor Gas/Humo MQ-2</text>
    <text x="192" y="86" font-size="9.5" fill="#78350F">• Termografía IR (42°C+)</text>
    <text x="192" y="100" font-size="9.5" fill="#78350F">• Enlace RF largo alcance</text>
  </g>

  <!-- Entorno y Recorrido -->
  <g transform="translate(20, 546)">
    <rect width="350" height="110" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="26" font-size="13" font-weight="700" fill="#0F172A">Entorno Real y Mapeo SLAM</text>
    <text x="16" y="46" font-size="11" fill="#475569">Entorno: Universidad / Campus Central Pabellón A</text>
    <text x="16" y="64" font-size="11" fill="#475569">Ruta: 8 Waypoints pre-mapeados con estación de carga.</text>
    
    <rect x="16" y="74" width="318" height="24" rx="6" fill="#F8FAFC" stroke="#CBD5E1"/>
    <text x="24" y="90" font-size="10" font-weight="600" fill="#4F46E5">📍 Base → WP1 Aulas → WP3 Servidores → WP7 Final</text>
  </g>

  <!-- Botón Confirmar / Conectar -->
  <g transform="translate(20, 672)">
    <rect width="350" height="46" rx="12" fill="#4F46E5" filter="url(#cardShadow)"/>
    <text x="175" y="28" font-size="14" font-weight="700" fill="#FFFFFF" text-anchor="middle">Ir al Panel de Supervisión →</text>
  </g>
'''
    svg += svg_bottom_nav("robot")
    return svg

# -------------------------------------------------------------------
# SCREEN 2: SUPERVISIÓN DASHBOARD & MISIONES (STANDBY - NORMAL)
# -------------------------------------------------------------------
def generate_screen_02_supervision_standby():
    svg = svg_header("02_supervision_standby_misiones")
    svg += '''
  <!-- Top App Bar -->
  <g transform="translate(20, 52)">
    <text x="0" y="18" font-size="17" font-weight="800" fill="#0F172A">HMI Unitree G1 - Supervisión</text>
    <text x="0" y="34" font-size="12" font-weight="500" fill="#64748B">Robot: Unitree G1 (ID: UADE-G1-01)</text>
    <!-- Badge Operational State: STANDBY -->
    <rect x="235" y="4" width="115" height="28" rx="14" fill="#EEF2FF" stroke="#C7D2FE" stroke-width="1"/>
    <circle cx="249" cy="18" r="4.5" fill="#4F46E5"/>
    <text x="260" y="22" font-size="11" font-weight="700" fill="#4338CA">EN REPOSO</text>
  </g>

  <!-- Quick Status: Conectividad y Batería (NORMAL) -->
  <g transform="translate(20, 102)">
    <!-- Connectivity Badge (ONLINE) -->
    <rect width="170" height="48" rx="12" fill="#DCFCE7" stroke="#86EFAC" stroke-width="1" filter="url(#cardShadow)"/>
    <circle cx="20" cy="24" r="5" fill="#16A34A"/>
    <text x="34" y="21" font-size="11" font-weight="700" fill="#15803D">CONECTADO (ONLINE)</text>
    <text x="34" y="36" font-size="9.5" font-weight="500" fill="#166534">-55 dBm  •  Lat: 18 ms</text>

    <!-- Battery Badge (100% Recarga) -->
    <g transform="translate(180, 0)">
      <rect width="170" height="48" rx="12" fill="#DCFCE7" stroke="#86EFAC" stroke-width="1" filter="url(#cardShadow)"/>
      <path d="M16 16 H28 V32 H16 Z M20 12 H24 V16 H20 Z" fill="#16A34A"/>
      <text x="36" y="21" font-size="12" font-weight="800" fill="#15803D">100% (25.2V)</text>
      <text x="36" y="36" font-size="9.5" font-weight="600" fill="#166534">⚡ En Base (Carga)</text>
    </g>
  </g>

  <!-- Selector de Misión (Requisito Misiones) -->
  <g transform="translate(20, 164)">
    <rect width="350" height="196" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="24" font-size="13" font-weight="700" fill="#0F172A">Seleccionar Misión de Inspección</text>
    <text x="334" y="24" font-size="11" font-weight="700" fill="#4F46E5" text-anchor="end">~15 min</text>

    <!-- Mission 1 (Selected) -->
    <rect x="14" y="38" width="322" height="66" rx="10" fill="#EEF2FF" stroke="#6366F1" stroke-width="1.5"/>
    <circle cx="34" cy="71" r="10" fill="#4F46E5"/>
    <path d="M30 71 L33 74 L39 67" stroke="#FFFFFF" stroke-width="2" fill="none" stroke-linecap="round"/>
    <text x="52" y="58" font-size="12.5" font-weight="700" fill="#1E1B4B">Inspección Crítica: Servidores y Tableros</text>
    <text x="52" y="74" font-size="10.5" fill="#475569">Pabellón A, Hall Central, Lab IoT y Tablero B</text>
    <rect x="52" y="80" width="75" height="16" rx="8" fill="#C7D2FE"/>
    <text x="89" y="92" font-size="9" font-weight="700" fill="#312E81" text-anchor="middle">8 WAYPOINTS</text>

    <!-- Mission 2 (Unselected) -->
    <rect x="14" y="114" width="322" height="66" rx="10" fill="#F8FAFC" stroke="#E2E8F0" stroke-width="1"/>
    <circle cx="34" cy="147" r="10" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1.5"/>
    <text x="52" y="134" font-size="12.5" font-weight="600" fill="#334155">Ronda Perimetral Ligera (Aulas 101-105)</text>
    <text x="52" y="150" font-size="10.5" fill="#64748B">Control nocturno de accesos y ventanales</text>
    <rect x="52" y="156" width="75" height="16" rx="8" fill="#E2E8F0"/>
    <text x="89" y="168" font-size="9" font-weight="600" fill="#475569" text-anchor="middle">4 WAYPOINTS</text>
  </g>

  <!-- Variables de Servicio (Inspección Ambiental en Reposo) -->
  <g transform="translate(20, 374)">
    <text x="0" y="14" font-size="12" font-weight="700" fill="#0F172A">Variables del Servicio (En Reposo)</text>
    
    <!-- Gas / Humo -->
    <g transform="translate(0, 24)">
      <rect width="110" height="74" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
      <text x="12" y="20" font-size="10" font-weight="600" fill="#64748B">Gas / Humo</text>
      <text x="12" y="44" font-size="16" font-weight="800" fill="#0F172A">12.4 <tspan font-size="10" font-weight="500">ppm</tspan></text>
      <rect x="12" y="52" width="60" height="14" rx="7" fill="#DCFCE7"/>
      <text x="42" y="62.5" font-size="8" font-weight="700" fill="#15803D" text-anchor="middle">NORMAL</text>
    </g>

    <!-- Temperatura -->
    <g transform="translate(120, 24)">
      <rect width="110" height="74" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
      <text x="12" y="20" font-size="10" font-weight="600" fill="#64748B">Temperatura</text>
      <text x="12" y="44" font-size="16" font-weight="800" fill="#0F172A">21.8 <tspan font-size="10" font-weight="500">°C</tspan></text>
      <rect x="12" y="52" width="60" height="14" rx="7" fill="#DCFCE7"/>
      <text x="42" y="62.5" font-size="8" font-weight="700" fill="#15803D" text-anchor="middle">ESTABLE</text>
    </g>

    <!-- Personas -->
    <g transform="translate(240, 24)">
      <rect width="110" height="74" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
      <text x="12" y="20" font-size="10" font-weight="600" fill="#64748B">Personas</text>
      <text x="12" y="44" font-size="16" font-weight="800" fill="#0F172A">0 <tspan font-size="10" font-weight="500">det.</tspan></text>
      <rect x="12" y="52" width="60" height="14" rx="7" fill="#F1F5F9"/>
      <text x="42" y="62.5" font-size="8" font-weight="700" fill="#475569" text-anchor="middle">DESPEJADO</text>
    </g>
  </g>

  <!-- Panel de Comandos de Operación (ESTADOS VISUALES) -->
  <g transform="translate(20, 490)">
    <text x="0" y="14" font-size="12" font-weight="700" fill="#0F172A">Comandos de Control (HMI)</text>

    <!-- Fila 1: Iniciar (NORMAL/HABILITADO), Pausar (DESHABILITADO), Reanudar (DESHABILITADO) -->
    <g transform="translate(0, 24)">
      <!-- INICIAR (HABILITADO / NORMAL) -->
      <rect width="110" height="42" rx="10" fill="#4F46E5" filter="url(#cardShadow)"/>
      <text x="55" y="26" font-size="12" font-weight="700" fill="#FFFFFF" text-anchor="middle">▶ Iniciar</text>

      <!-- PAUSAR (DESHABILITADO) -->
      <g transform="translate(120, 0)">
        <rect width="110" height="42" rx="10" fill="#E2E8F0" opacity="0.6"/>
        <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">⏸ Pausar</text>
      </g>

      <!-- REANUDAR (DESHABILITADO) -->
      <g transform="translate(240, 0)">
        <rect width="110" height="42" rx="10" fill="#E2E8F0" opacity="0.6"/>
        <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">⏯ Reanudar</text>
      </g>
    </g>

    <!-- Fila 2: Cancelar (DESHABILITADO) y Regreso Seguro (DESHABILITADO EN BASE) -->
    <g transform="translate(0, 74)">
      <rect width="170" height="38" rx="10" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1" opacity="0.5"/>
      <text x="85" y="24" font-size="11" font-weight="600" fill="#94A3B8" text-anchor="middle">✕ Cancelar Misión</text>

      <g transform="translate(180, 0)">
        <rect width="170" height="38" rx="10" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1" opacity="0.5"/>
        <text x="85" y="24" font-size="11" font-weight="600" fill="#94A3B8" text-anchor="middle">⌂ Regreso Seguro</text>
      </g>
    </g>

    <!-- Fila 3: Paro de Software (CRÍTICO / HABILITADO) -->
    <g transform="translate(0, 120)">
      <rect width="350" height="42" rx="10" fill="#DC2626" filter="url(#cardShadow)"/>
      <text x="175" y="26" font-size="12" font-weight="800" fill="#FFFFFF" text-anchor="middle">🛑 DETENCIÓN OPERATIVA (SOFTWARE)</text>
    </g>
  </g>

  <!-- Estado de Última Orden Emitida -->
  <g transform="translate(20, 680)">
    <rect width="350" height="48" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <circle cx="24" cy="24" r="6" fill="#16A34A"/>
    <text x="38" y="21" font-size="11" font-weight="700" fill="#0F172A">Última orden: SISTEMA_LISTO</text>
    <text x="38" y="36" font-size="9.5" fill="#64748B">Estado: CONFIRMED  •  Latencia: 14 ms</text>
    <rect x="260" y="14" width="76" height="20" rx="10" fill="#DCFCE7"/>
    <text x="298" y="28" font-size="9" font-weight="700" fill="#15803D" text-anchor="middle">CONFIRMADO</text>
  </g>
'''
    svg += svg_bottom_nav("dashboard")
    return svg

# -------------------------------------------------------------------
# SCREEN 3: TELEMETRÍA Y SUPERVISIÓN ACTIVA EN MARCHA (PATROLLING)
# -------------------------------------------------------------------
def generate_screen_03_telemetry_patrolling():
    svg = svg_header("03_supervision_patrullaje_telemetria")
    svg += '''
  <!-- Top App Bar -->
  <g transform="translate(20, 52)">
    <text x="0" y="18" font-size="17" font-weight="800" fill="#0F172A">HMI Unitree G1 - Supervisión</text>
    <text x="0" y="34" font-size="12" font-weight="500" fill="#64748B">Ruta Activa: Servidores y Tableros</text>
    <!-- Badge Operational State: PATROLLING -->
    <rect x="235" y="4" width="115" height="28" rx="14" fill="#DCFCE7" stroke="#86EFAC" stroke-width="1"/>
    <circle cx="249" cy="18" r="4.5" fill="#16A34A"/>
    <text x="260" y="22" font-size="11" font-weight="700" fill="#15803D">PATRULLANDO</text>
  </g>

  <!-- Quick Status (Normal en marcha) -->
  <g transform="translate(20, 102)">
    <rect width="170" height="48" rx="12" fill="#DCFCE7" stroke="#86EFAC" stroke-width="1" filter="url(#cardShadow)"/>
    <circle cx="20" cy="24" r="5" fill="#16A34A"/>
    <text x="34" y="21" font-size="11" font-weight="700" fill="#15803D">CONECTADO (ONLINE)</text>
    <text x="34" y="36" font-size="9.5" font-weight="500" fill="#166534">-58 dBm  •  Lat: 22 ms</text>

    <g transform="translate(180, 0)">
      <rect width="170" height="48" rx="12" fill="#DCFCE7" stroke="#86EFAC" stroke-width="1" filter="url(#cardShadow)"/>
      <path d="M16 16 H28 V32 H16 Z M20 12 H24 V16 H20 Z" fill="#16A34A"/>
      <text x="36" y="21" font-size="12" font-weight="800" fill="#15803D">87% (24.8V)</text>
      <text x="36" y="36" font-size="9.5" font-weight="600" fill="#166534">Consumo: ~1.2 A</text>
    </g>
  </g>

  <!-- Misión Activa y Progreso (Waypoints 4/8 - 50%) -->
  <g transform="translate(20, 164)">
    <rect width="350" height="116" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="24" font-size="13" font-weight="700" fill="#0F172A">Inspección Servidores y Tableros</text>
    <text x="334" y="24" font-size="14" font-weight="800" fill="#4F46E5" text-anchor="end">50%</text>

    <!-- Progress bar -->
    <rect x="16" y="36" width="318" height="10" rx="5" fill="#EEF2FF"/>
    <rect x="16" y="36" width="159" height="10" rx="5" fill="#4F46E5"/>

    <g transform="translate(16, 62)">
      <!-- Zone -->
      <path d="M4 14 A6 6 0 1 1 16 14 A6 6 0 0 1 4 14 M10 4 V16" stroke="#4F46E5" stroke-width="1.5" fill="none"/>
      <text x="24" y="16" font-size="11.5" font-weight="600" fill="#1E293B">Sala Servidores y Tablero B</text>
      
      <!-- Waypoint counter -->
      <rect x="220" y="2" width="98" height="22" rx="11" fill="#F1F5F9"/>
      <text x="269" y="16" font-size="10.5" font-weight="700" fill="#475569" text-anchor="middle">Waypoint 4 de 8</text>
    </g>

    <text x="16" y="104" font-size="10" font-weight="500" fill="#64748B">Siguiente punto: Salida de Emergencia Este (X: 22.0m, Y: 12.0m)</text>
  </g>

  <!-- Esquema Visual del Recorrido (SLAM Waypoints) -->
  <g transform="translate(20, 294)">
    <rect width="350" height="88" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="20" font-size="11" font-weight="700" fill="#0F172A">Mapa de Ruta Activa (SLAM)</text>
    
    <!-- Mini waypoint track -->
    <line x1="30" y1="52" x2="320" y2="52" stroke="#CBD5E1" stroke-width="3" stroke-linecap="round"/>
    <line x1="30" y1="52" x2="175" y2="52" stroke="#4F46E5" stroke-width="4" stroke-linecap="round"/>

    <!-- Point Base -->
    <circle cx="30" cy="52" r="7" fill="#16A34A"/>
    <text x="30" y="74" font-size="8.5" font-weight="600" fill="#166534" text-anchor="middle">Base</text>

    <!-- Point WP2 -->
    <circle cx="102" cy="52" r="6" fill="#4F46E5"/>
    <text x="102" y="74" font-size="8.5" fill="#475569" text-anchor="middle">WP2</text>

    <!-- Point WP4 (Current) -->
    <circle cx="175" cy="52" r="9" fill="#4F46E5" stroke="#EEF2FF" stroke-width="3"/>
    <text x="175" y="74" font-size="9" font-weight="700" fill="#4F46E5" text-anchor="middle">WP4 (G1)</text>

    <!-- Point WP6 -->
    <circle cx="248" cy="52" r="5" fill="#CBD5E1"/>
    <text x="248" y="74" font-size="8.5" fill="#94A3B8" text-anchor="middle">WP6</text>

    <!-- Point WP8 -->
    <circle cx="320" cy="52" r="5" fill="#CBD5E1"/>
    <text x="320" y="74" font-size="8.5" fill="#94A3B8" text-anchor="middle">Fin</text>
  </g>

  <!-- Telemetría Dinámica: 3 Variables del Servicio (NORMALES) -->
  <g transform="translate(20, 396)">
    <text x="0" y="14" font-size="12" font-weight="700" fill="#0F172A">Telemetría de Servicio (Ambiente)</text>
    
    <g transform="translate(0, 24)">
      <!-- Gas / Humo -->
      <rect width="110" height="74" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
      <text x="12" y="20" font-size="10" font-weight="600" fill="#64748B">Gas / Humo</text>
      <text x="12" y="44" font-size="16" font-weight="800" fill="#16A34A">18.2 <tspan font-size="10" font-weight="500">ppm</tspan></text>
      <rect x="12" y="52" width="60" height="14" rx="7" fill="#DCFCE7"/>
      <text x="42" y="62.5" font-size="8" font-weight="700" fill="#15803D" text-anchor="middle">NORMAL</text>

      <!-- Temperatura -->
      <g transform="translate(120, 0)">
        <rect width="110" height="74" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
        <text x="12" y="20" font-size="10" font-weight="600" fill="#64748B">Temperatura</text>
        <text x="12" y="44" font-size="16" font-weight="800" fill="#0F172A">23.4 <tspan font-size="10" font-weight="500">°C</tspan></text>
        <rect x="12" y="52" width="60" height="14" rx="7" fill="#DCFCE7"/>
        <text x="42" y="62.5" font-size="8" font-weight="700" fill="#15803D" text-anchor="middle">NORMAL</text>
      </g>

      <!-- Personas -->
      <g transform="translate(240, 0)">
        <rect width="110" height="74" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
        <text x="12" y="20" font-size="10" font-weight="600" fill="#64748B">Personas</text>
        <text x="12" y="44" font-size="16" font-weight="800" fill="#0F172A">0 <tspan font-size="10" font-weight="500">det.</tspan></text>
        <rect x="12" y="52" width="60" height="14" rx="7" fill="#F1F5F9"/>
        <text x="42" y="62.5" font-size="8" font-weight="700" fill="#475569" text-anchor="middle">DESPEJADO</text>
      </g>
    </g>
  </g>

  <!-- Métricas Cinemáticas Secundarias (Velocidad, Orientación, Coordenadas) -->
  <g transform="translate(20, 510)">
    <rect width="170" height="48" rx="10" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="12" y="20" font-size="9.5" fill="#64748B">Velocidad de Marcha</text>
    <text x="12" y="38" font-size="13" font-weight="800" fill="#0F172A">0.72 m/s <tspan font-size="9.5" font-weight="500" fill="#16A34A">(Marcha OK)</tspan></text>

    <g transform="translate(180, 0)">
      <rect width="170" height="48" rx="10" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
      <text x="12" y="20" font-size="9.5" fill="#64748B">Orientación / Posición</text>
      <text x="12" y="38" font-size="13" font-weight="800" fill="#0F172A">145° (X:18.0, Y:8.0)</text>
    </g>
  </g>

  <!-- Panel de Comandos de Operación (EN PATRULLAJE) -->
  <g transform="translate(20, 574)">
    <!-- Botón Pausar (ACTIVO / AMARILLO), Iniciar (DESHABILITADO) -->
    <g transform="translate(0, 0)">
      <rect width="110" height="42" rx="10" fill="#E2E8F0" opacity="0.6"/>
      <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">▶ Iniciar</text>

      <!-- PAUSAR (HABILITADO) -->
      <g transform="translate(120, 0)">
        <rect width="110" height="42" rx="10" fill="#D97706" filter="url(#cardShadow)"/>
        <text x="55" y="26" font-size="12" font-weight="700" fill="#FFFFFF" text-anchor="middle">⏸ Pausar</text>
      </g>

      <!-- REANUDAR (DESHABILITADO) -->
      <g transform="translate(240, 0)">
        <rect width="110" height="42" rx="10" fill="#E2E8F0" opacity="0.6"/>
        <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">⏯ Reanudar</text>
      </g>
    </g>

    <!-- Cancelar Misión (HABILITADO) y Regreso Seguro (HABILITADO) -->
    <g transform="translate(0, 50)">
      <rect width="170" height="38" rx="10" fill="#FFFFFF" stroke="#DC2626" stroke-width="1.2" filter="url(#cardShadow)"/>
      <text x="85" y="24" font-size="11" font-weight="700" fill="#DC2626" text-anchor="middle">✕ Cancelar Misión</text>

      <g transform="translate(180, 0)">
        <rect width="170" height="38" rx="10" fill="#7C3AED" filter="url(#cardShadow)"/>
        <text x="85" y="24" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">⌂ Regreso Seguro</text>
      </g>
    </g>

    <!-- Paro de Software -->
    <g transform="translate(0, 96)">
      <rect width="350" height="42" rx="10" fill="#DC2626" filter="url(#cardShadow)"/>
      <text x="175" y="26" font-size="12" font-weight="800" fill="#FFFFFF" text-anchor="middle">🛑 DETENCIÓN OPERATIVA (SOFTWARE)</text>
    </g>
  </g>
'''
    svg += svg_bottom_nav("dashboard")
    return svg

# -------------------------------------------------------------------
# SCREEN 4: CONTROL, COMANDOS Y MODAL DE CONFIRMACIÓN (PARADA PREVENTIVA)
# -------------------------------------------------------------------
def generate_screen_04_control_modal():
    svg = svg_header("04_control_comandos_modal_paro")
    # Base background identical to screen 3
    svg += '''
  <!-- Background content dimmed under modal -->
  <g opacity="0.35">
    <text x="20" y="70" font-size="17" font-weight="800" fill="#0F172A">HMI Unitree G1 - Supervisión</text>
    <rect x="20" y="102" width="170" height="48" rx="12" fill="#DCFCE7"/>
    <rect x="200" y="102" width="170" height="48" rx="12" fill="#DCFCE7"/>
    <rect x="20" y="164" width="350" height="116" rx="14" fill="#FFFFFF"/>
    <rect x="20" y="294" width="350" height="88" rx="14" fill="#FFFFFF"/>
    <rect x="20" y="396" width="350" height="150" rx="14" fill="#FFFFFF"/>
  </g>

  <!-- Modal Backdrop Scrim -->
  <rect width="390" height="844" fill="#000000" opacity="0.55"/>

  <!-- Modal Dialog Window (M3 Confirmation Dialog) -->
  <g transform="translate(30, 260)" filter="url(#modalShadow)">
    <rect width="330" height="290" rx="20" fill="#FFFFFF"/>
    
    <!-- Emergency Icon Header -->
    <circle cx="165" cy="48" r="28" fill="#FEE2E2"/>
    <path d="M165 34 V48 M165 56 V58" stroke="#DC2626" stroke-width="3" stroke-linecap="round"/>

    <!-- Title -->
    <text x="165" y="104" font-size="16" font-weight="800" fill="#991B1B" text-anchor="middle">Confirmar Detención Operativa</text>

    <!-- Message & Safety Warning -->
    <text x="165" y="132" font-size="12" font-weight="500" fill="#334155" text-anchor="middle">¿Desea detener inmediatamente todos los</text>
    <text x="165" y="150" font-size="12" font-weight="500" fill="#334155" text-anchor="middle">actuadores del Unitree G1 por software?</text>

    <!-- Safety Disclaimer Box (Requisito Crítico HMI/HRI) -->
    <rect x="20" y="170" width="290" height="44" rx="8" fill="#FEF2F2" stroke="#FCA5A5" stroke-width="1"/>
    <text x="32" y="188" font-size="9.5" font-weight="700" fill="#B91C1C">⚠️ ADVERTENCIA DE SEGURIDAD FÍSICA:</text>
    <text x="32" y="202" font-size="9" fill="#7F1D1D">Esta orden NO reemplaza la parada de emergencia física.</text>

    <!-- Action Buttons -->
    <g transform="translate(20, 230)">
      <!-- Cancel / Dismiss Button -->
      <rect width="135" height="42" rx="10" fill="#F1F5F9"/>
      <text x="67.5" y="26" font-size="12" font-weight="700" fill="#475569" text-anchor="middle">Volver / Cancelar</text>

      <!-- Confirm Stop Button -->
      <g transform="translate(150, 0)">
        <rect width="140" height="42" rx="10" fill="#DC2626"/>
        <text x="70" y="26" font-size="12" font-weight="800" fill="#FFFFFF" text-anchor="middle">Detener Robot</text>
      </g>
    </g>
  </g>
'''
    svg += svg_bottom_nav("dashboard")
    return svg

# -------------------------------------------------------------------
# SCREEN 5: ALERTAS & DECISIÓN DE INCIDENTE (CRITICAL / CONTINGENCIA)
# -------------------------------------------------------------------
def generate_screen_05_alerts_incident():
    svg = svg_header("05_alertas_decision_incidente")
    svg += '''
  <!-- Top App Bar -->
  <g transform="translate(20, 52)">
    <text x="0" y="20" font-size="18" font-weight="800" fill="#0F172A">Centro de Alertas (2)</text>
    <rect x="230" y="0" width="120" height="32" rx="16" fill="#4F46E5" filter="url(#cardShadow)"/>
    <text x="290" y="20" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">✓ Reconocer Todas</text>
  </g>

  <!-- Banner de Decisión de Incidente (Componente Reutilizable Clave) -->
  <g transform="translate(20, 104)">
    <rect width="350" height="152" rx="14" fill="#FEF3C7" stroke="#F59E0B" stroke-width="1.5" filter="url(#cardShadow)"/>
    <circle cx="34" cy="30" r="14" fill="#F59E0B"/>
    <text x="34" y="36" font-size="14" font-weight="800" fill="#FFFFFF" text-anchor="middle">!</text>
    
    <text x="56" y="25" font-size="12" font-weight="800" fill="#92400E">INCIDENTE DETECTADO - INTERVENCIÓN</text>
    <text x="56" y="42" font-size="11" fill="#78350F">El robot detuvo su marcha preventivamente en WP4.</text>

    <text x="16" y="70" font-size="11" font-weight="700" fill="#92400E">¿Qué decisión desea tomar como operador?</text>

    <!-- Botones de Decisión HRI -->
    <g transform="translate(14, 82)">
      <!-- Reanudar -->
      <rect width="98" height="38" rx="8" fill="#16A34A"/>
      <text x="49" y="24" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">▶ Reanudar</text>

      <!-- Cancelar -->
      <g transform="translate(108, 0)">
        <rect width="102" height="38" rx="8" fill="#DC2626"/>
        <text x="51" y="24" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">✕ Cancelar</text>
      </g>

      <!-- A Base -->
      <g transform="translate(220, 0)">
        <rect width="102" height="38" rx="8" fill="#7C3AED"/>
        <text x="51" y="24" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">⌂ A Base</text>
      </g>
    </g>

    <text x="16" y="138" font-size="9" fill="#B45309">Al presionar una opción, se acusará recibo y se emitirá la orden.</text>
  </g>

  <!-- Lista de Alertas Activas con Estados Visuales -->
  <g transform="translate(20, 276)">
    <text x="0" y="14" font-size="13" font-weight="700" fill="#0F172A">Alertas Pendientes y No Reconocidas</text>

    <!-- Alerta 1: CRITICAL (Gas Leak) - Estado Visual ERROR -->
    <g transform="translate(0, 26)">
      <rect width="350" height="96" rx="14" fill="#FFFFFF" stroke="#EF4444" stroke-width="1.5" filter="url(#cardShadow)"/>
      <!-- Severity Badge -->
      <rect x="14" y="14" width="76" height="20" rx="10" fill="#FEE2E2"/>
      <text x="52" y="28" font-size="9.5" font-weight="800" fill="#B91C1C" text-anchor="middle">CRÍTICA</text>
      <text x="98" y="28" font-size="10" font-weight="600" fill="#64748B">GAS_LEAK_DETECTED  •  21:58:12</text>

      <text x="14" y="52" font-size="12" font-weight="700" fill="#1E293B">Concentración de gas crítica: 88.5 ppm</text>
      <text x="14" y="68" font-size="10.5" fill="#64748B">Ubicación: Sala Servidores y Tablero B (Límite: 50 ppm)</text>

      <!-- Single Acknowledge Button -->
      <rect x="236" y="56" width="100" height="28" rx="8" fill="#FEF2F2" stroke="#F87171" stroke-width="1"/>
      <text x="286" y="74" font-size="10" font-weight="700" fill="#DC2626" text-anchor="middle">Acusar Recibo</text>
    </g>

    <!-- Alerta 2: WARNING (Obstacle / Temp) - Estado Visual ADVERTENCIA -->
    <g transform="translate(0, 134)">
      <rect width="350" height="96" rx="14" fill="#FFFFFF" stroke="#F59E0B" stroke-width="1.2" filter="url(#cardShadow)"/>
      <rect x="14" y="14" width="94" height="20" rx="10" fill="#FEF3C7"/>
      <text x="61" y="28" font-size="9.5" font-weight="800" fill="#B45309" text-anchor="middle">ADVERTENCIA</text>
      <text x="116" y="28" font-size="10" font-weight="600" fill="#64748B">OBSTACLE_PERSISTENT  •  21:57:40</text>

      <text x="14" y="52" font-size="12" font-weight="700" fill="#1E293B">Bloqueo de pasillo por carro de limpieza</text>
      <text x="14" y="68" font-size="10.5" fill="#64748B">Obstáculo inmóvil durante >10 segundos en ruta.</text>

      <rect x="236" y="56" width="100" height="28" rx="8" fill="#FFFBEB" stroke="#FCD34D" stroke-width="1"/>
      <text x="286" y="74" font-size="10" font-weight="700" fill="#D97706" text-anchor="middle">Acusar Recibo</text>
    </g>

    <!-- Alerta 3: INFO (Acknowledge state) - Estado Visual NORMAL -->
    <g transform="translate(0, 242)">
      <rect width="350" height="80" rx="14" fill="#F8FAFC" stroke="#E2E8F0" stroke-width="1"/>
      <rect x="14" y="14" width="76" height="20" rx="10" fill="#E2E8F0"/>
      <text x="52" y="28" font-size="9.5" font-weight="700" fill="#475569" text-anchor="middle">INFO</text>
      <text x="98" y="28" font-size="10" fill="#94A3B8">MISSION_STARTED  •  21:50:00</text>

      <text x="14" y="52" font-size="11.5" font-weight="600" fill="#475569">Misión Servidores iniciada por Operador</text>
      <text x="14" y="66" font-size="9.5" fill="#16A34A">✓ Reconocida a las 21:50:02</text>
    </g>
  </g>
'''
    svg += svg_bottom_nav("alerts")
    return svg

# -------------------------------------------------------------------
# SCREEN 6: HISTORIAL Y AUDITORÍA LOCAL ROOM (HistoryScreen)
# -------------------------------------------------------------------
def generate_screen_06_history_audit():
    svg = svg_header("06_historial_auditoria")
    svg += '''
  <!-- Top App Bar -->
  <g transform="translate(20, 52)">
    <text x="0" y="18" font-size="18" font-weight="800" fill="#0F172A">Auditoría e Historial Room</text>
    <text x="0" y="34" font-size="11.5" font-weight="500" fill="#64748B">Base de Datos Local SQLite: unitree_g1_hmi.db</text>
  </g>

  <!-- Tab Selector: Misiones vs Eventos de Auditoría -->
  <g transform="translate(20, 102)">
    <rect width="350" height="42" rx="10" fill="#E2E8F0"/>
    <!-- Active tab: Misiones -->
    <rect x="4" y="4" width="168" height="34" rx="8" fill="#FFFFFF" filter="url(#cardShadow)"/>
    <text x="88" y="26" font-size="12" font-weight="700" fill="#4F46E5" text-anchor="middle">Misiones (Room DB)</text>

    <!-- Inactive tab: Logs -->
    <text x="260" y="26" font-size="12" font-weight="600" fill="#64748B" text-anchor="middle">Eventos y Auditoría</text>
  </g>

  <!-- Mission History Cards -->
  <g transform="translate(20, 162)">
    <!-- Mission 1: COMPLETED (Normal Flow Result) -->
    <g transform="translate(0, 0)">
      <rect width="350" height="120" rx="14" fill="#FFFFFF" stroke="#86EFAC" stroke-width="1.5" filter="url(#cardShadow)"/>
      <rect x="14" y="14" width="94" height="22" rx="11" fill="#DCFCE7"/>
      <text x="61" y="29" font-size="10" font-weight="800" fill="#15803D" text-anchor="middle">COMPLETADA</text>
      <text x="334" y="29" font-size="10.5" font-weight="500" fill="#64748B" text-anchor="end">Hoy, 21:30</text>

      <text x="14" y="54" font-size="13" font-weight="700" fill="#0F172A">Ronda Nocturna Aulas 101-105</text>
      
      <g transform="translate(14, 64)">
        <text x="0" y="14" font-size="10.5" fill="#475569">Duración: 14m 20s  •  Puntos: 8/8 waypoints</text>
        <text x="0" y="28" font-size="10.5" fill="#475569">Gas máx: 18.2 ppm  •  Temp máx: 23.1 °C</text>
        <text x="0" y="42" font-size="10" font-weight="600" fill="#16A34A">✓ Inspección completa sin novedades registradas</text>
      </g>
    </g>

    <!-- Mission 2: CANCELLED (Failure Flow Result) -->
    <g transform="translate(0, 134)">
      <rect width="350" height="120" rx="14" fill="#FFFFFF" stroke="#FCA5A5" stroke-width="1.5" filter="url(#cardShadow)"/>
      <rect x="14" y="14" width="86" height="22" rx="11" fill="#FEE2E2"/>
      <text x="57" y="29" font-size="10" font-weight="800" fill="#B91C1C" text-anchor="middle">ABORTADA</text>
      <text x="334" y="29" font-size="10.5" font-weight="500" fill="#64748B" text-anchor="end">Hoy, 20:15</text>

      <text x="14" y="54" font-size="13" font-weight="700" fill="#0F172A">Inspección Servidores y Tablero B</text>
      
      <g transform="translate(14, 64)">
        <text x="0" y="14" font-size="10.5" fill="#475569">Duración: 06m 45s  •  Puntos: 4/8 waypoints</text>
        <text x="0" y="28" font-size="10.5" fill="#B91C1C">Pico Gas: 92.4 ppm (Alarma Crítica)  •  Temp: 44.5 °C</text>
        <text x="0" y="42" font-size="10" font-weight="600" fill="#DC2626">⚠️ Interrupción por operador: repliegue a base</text>
      </g>
    </g>

    <!-- Mission 3: COMPLETED -->
    <g transform="translate(0, 268)">
      <rect width="350" height="110" rx="14" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1" filter="url(#cardShadow)"/>
      <rect x="14" y="14" width="94" height="22" rx="11" fill="#DCFCE7"/>
      <text x="61" y="29" font-size="10" font-weight="800" fill="#15803D" text-anchor="middle">COMPLETADA</text>
      <text x="334" y="29" font-size="10.5" font-weight="500" fill="#64748B" text-anchor="end">Ayer, 23:00</text>

      <text x="14" y="54" font-size="13" font-weight="700" fill="#0F172A">Ronda Perimetral Pabellón A</text>
      <text x="14" y="76" font-size="10.5" fill="#475569">Duración: 18m 05s  •  Puntos: 8/8 waypoints</text>
      <text x="14" y="92" font-size="10" font-weight="600" fill="#16A34A">✓ Todos los sensores reportaron normalidad</text>
    </g>
  </g>

  <!-- Room DB Integrity Badge -->
  <g transform="translate(20, 684)">
    <rect width="350" height="42" rx="10" fill="#F1F5F9" stroke="#CBD5E1" stroke-width="1"/>
    <text x="175" y="26" font-size="10.5" font-weight="600" fill="#475569" text-anchor="middle">💾 Persistencia Room Activa: Transacciones ACID garantizadas</text>
  </g>
'''
    svg += svg_bottom_nav("history")
    return svg

# -------------------------------------------------------------------
# SCREEN 7: DESCONEXIÓN DE RED & DATOS CONGELADOS (OFFLINE / ERROR)
# -------------------------------------------------------------------
def generate_screen_07_disconnection_failure():
    svg = svg_header("07_desconexion_red_stale")
    svg += '''
  <!-- Top App Bar -->
  <g transform="translate(20, 52)">
    <text x="0" y="18" font-size="17" font-weight="800" fill="#0F172A">HMI Unitree G1 - Supervisión</text>
    <text x="0" y="34" font-size="12" font-weight="500" fill="#DC2626">CONEXIÓN PERDIDA CON EL ROBOT</text>
    <!-- Badge Operational State: OFFLINE / FAULT -->
    <rect x="235" y="4" width="115" height="28" rx="14" fill="#FEE2E2" stroke="#FCA5A5" stroke-width="1"/>
    <circle cx="249" cy="18" r="4.5" fill="#DC2626"/>
    <text x="260" y="22" font-size="11" font-weight="700" fill="#991B1B">DESCONECTADO</text>
  </g>

  <!-- Quick Status (OFFLINE / ERROR) -->
  <g transform="translate(20, 102)">
    <!-- Connectivity Badge: OFFLINE -->
    <rect width="170" height="48" rx="12" fill="#FEE2E2" stroke="#F87171" stroke-width="1.5" filter="url(#cardShadow)"/>
    <circle cx="20" cy="24" r="5" fill="#DC2626"/>
    <text x="34" y="21" font-size="11" font-weight="800" fill="#991B1B">SIN CONEXIÓN (OFFLINE)</text>
    <text x="34" y="36" font-size="9.5" font-weight="600" fill="#B91C1C">0 dBm  •  Timeout enlace</text>

    <!-- Battery Badge (Congelada) -->
    <g transform="translate(180, 0)">
      <rect width="170" height="48" rx="12" fill="#FEF3C7" stroke="#FCD34D" stroke-width="1" filter="url(#cardShadow)"/>
      <text x="20" y="21" font-size="12" font-weight="800" fill="#92400E">74% (Congelado)</text>
      <text x="20" y="36" font-size="9.5" font-weight="600" fill="#B45309">Último reporte hace 42s</text>
    </g>
  </g>

  <!-- Banner Crítico de Telemetría Congelada (STALE DATA) -->
  <g transform="translate(20, 164)">
    <rect width="350" height="74" rx="14" fill="#FEE2E2" stroke="#EF4444" stroke-width="1.5" filter="url(#cardShadow)"/>
    <circle cx="34" cy="37" r="14" fill="#DC2626"/>
    <text x="34" y="43" font-size="14" font-weight="800" fill="#FFFFFF" text-anchor="middle">✕</text>
    <text x="56" y="28" font-size="11" font-weight="800" fill="#991B1B">ATENCIÓN: TELEMETRÍA CONGELADA</text>
    <text x="56" y="44" font-size="10" fill="#7F1D1D">Pérdida de señal WiFi/RF. Los datos en pantalla no reflejan</text>
    <text x="56" y="58" font-size="10" fill="#7F1D1D">el estado actual. Nuevas órdenes quedarán en timeout.</text>
  </g>

  <!-- Telemetría Congelada con Marca de Agua STALE -->
  <g transform="translate(20, 252)">
    <rect width="350" height="126" rx="14" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1" filter="url(#cardShadow)"/>
    <text x="16" y="24" font-size="13" font-weight="700" fill="#64748B">Último Reporte: WP4 (Sala Servidores)</text>
    <rect x="250" y="10" width="84" height="20" rx="10" fill="#FEF3C7"/>
    <text x="292" y="24" font-size="9" font-weight="700" fill="#92400E" text-anchor="middle">DATOS VIEJOS</text>

    <!-- Variables atenuadas -->
    <g transform="translate(16, 42)">
      <rect width="98" height="66" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
      <text x="10" y="20" font-size="9.5" fill="#94A3B8">Gas / Humo</text>
      <text x="10" y="42" font-size="15" font-weight="700" fill="#64748B">20.4 ppm</text>
      <text x="10" y="56" font-size="8.5" fill="#94A3B8">(Hace 42s)</text>

      <g transform="translate(110, 0)">
        <rect width="98" height="66" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
        <text x="10" y="20" font-size="9.5" fill="#94A3B8">Temperatura</text>
        <text x="10" y="42" font-size="15" font-weight="700" fill="#64748B">24.1 °C</text>
        <text x="10" y="56" font-size="8.5" fill="#94A3B8">(Hace 42s)</text>
      </g>

      <g transform="translate(220, 0)">
        <rect width="98" height="66" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
        <text x="10" y="20" font-size="9.5" fill="#94A3B8">Personas</text>
        <text x="10" y="42" font-size="15" font-weight="700" fill="#64748B">0 det.</text>
        <text x="10" y="56" font-size="8.5" fill="#94A3B8">(Hace 42s)</text>
      </g>
    </g>
  </g>

  <!-- Panel de Comandos: TODOS DESHABILITADOS (ESTADO VISUAL DESHABILITADO) -->
  <g transform="translate(20, 394)">
    <text x="0" y="14" font-size="12" font-weight="700" fill="#94A3B8">Comandos de Control (Bloqueados por Desconexión)</text>

    <!-- Fila 1: Iniciar / Pausar / Reanudar (TODOS DESHABILITADOS) -->
    <g transform="translate(0, 24)">
      <rect width="110" height="42" rx="10" fill="#F1F5F9" stroke="#E2E8F0"/>
      <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">▶ Iniciar</text>

      <g transform="translate(120, 0)">
        <rect width="110" height="42" rx="10" fill="#F1F5F9" stroke="#E2E8F0"/>
        <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">⏸ Pausar</text>
      </g>

      <g transform="translate(240, 0)">
        <rect width="110" height="42" rx="10" fill="#F1F5F9" stroke="#E2E8F0"/>
        <text x="55" y="26" font-size="12" font-weight="600" fill="#94A3B8" text-anchor="middle">⏯ Reanudar</text>
      </g>
    </g>

    <!-- Fila 2 -->
    <g transform="translate(0, 74)">
      <rect width="170" height="38" rx="10" fill="#F1F5F9" stroke="#E2E8F0"/>
      <text x="85" y="24" font-size="11" font-weight="600" fill="#94A3B8" text-anchor="middle">✕ Cancelar Misión</text>

      <g transform="translate(180, 0)">
        <rect width="170" height="38" rx="10" fill="#F1F5F9" stroke="#E2E8F0"/>
        <text x="85" y="24" font-size="11" font-weight="600" fill="#94A3B8" text-anchor="middle">⌂ Regreso Seguro</text>
      </g>
    </g>

    <!-- Paro de Software bloqueado sin red -->
    <g transform="translate(0, 120)">
      <rect width="350" height="42" rx="10" fill="#FCA5A5" opacity="0.6"/>
      <text x="175" y="26" font-size="12" font-weight="700" fill="#7F1D1D" text-anchor="middle">🛑 DETENCIÓN OPERATIVA (NO DISPONIBLE SIN RED)</text>
    </g>
  </g>

  <!-- Estado de Última Orden Emitida: NO_RESPONSE (Timeout) -->
  <g transform="translate(20, 584)">
    <rect width="350" height="74" rx="12" fill="#FFFFFF" stroke="#F59E0B" stroke-width="1.5" filter="url(#cardShadow)"/>
    <circle cx="24" cy="30" r="7" fill="#F59E0B"/>
    <text x="24" y="34" font-size="9" font-weight="800" fill="#FFFFFF" text-anchor="middle">?</text>
    
    <text x="38" y="24" font-size="11.5" font-weight="800" fill="#92400E">Última orden: PAUSE_MISSION</text>
    <text x="38" y="40" font-size="10" fill="#78350F">Estado: NO_RESPONSE  •  Timeout tras 2500 ms</text>
    <text x="38" y="56" font-size="9" fill="#B45309">No se recibió confirmación del controlador del Unitree G1.</text>

    <rect x="250" y="16" width="86" height="20" rx="10" fill="#FEF3C7"/>
    <text x="293" y="30" font-size="8.5" font-weight="800" fill="#92400E" text-anchor="middle">SIN RESPUESTA</text>
  </g>

  <!-- Botón Reconectar / Reintentar Enlace -->
  <g transform="translate(20, 674)">
    <rect width="350" height="44" rx="12" fill="#4F46E5" filter="url(#cardShadow)"/>
    <text x="175" y="27" font-size="13" font-weight="700" fill="#FFFFFF" text-anchor="middle">🔄 Reintentar Reconexión Automática</text>
  </g>
'''
    svg += svg_bottom_nav("dashboard")
    return svg

# -------------------------------------------------------------------
# SCREEN 8: UI KIT Y DESIGN SYSTEM (COMPONENTES REUTILIZABLES & ESTADOS)
# Dimensiones: 1000 x 920 px (Canvas amplio para Figma)
# -------------------------------------------------------------------
def generate_screen_08_design_system():
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 920" width="1000" height="920">
  <defs>
    <style>
      @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&amp;display=swap');
      text {{ font-family: 'Inter', sans-serif; }}
    </style>
    <filter id="shadow" x="-5%" y="-5%" width="110%" height="110%">
      <feDropShadow dx="0" dy="2" stdDeviation="4" flood-color="#000000" flood-opacity="0.06"/>
    </filter>
  </defs>

  <rect width="1000" height="920" fill="#F8FAFC" rx="16"/>

  <!-- Header -->
  <g transform="translate(40, 40)">
    <text x="0" y="24" font-size="24" font-weight="800" fill="#0F172A">Design System &amp; Componentes Reutilizables — Unitree G1 HMI</text>
    <text x="0" y="48" font-size="14" font-weight="500" fill="#64748B">Biblioteca de componentes Material 3 con especificación de los 4 Estados Visuales requeridos por consigna.</text>
  </g>

  <!-- SECCIÓN 1: LOS 4 ESTADOS VISUALES OBLIGATORIOS (COLUMNAS COMPARATIVAS) -->
  <g transform="translate(40, 120)">
    <text x="0" y="0" font-size="16" font-weight="800" fill="#0F172A">1. Matriz de Estados Visuales de Interfaz</text>
    
    <!-- Columna 1: NORMAL -->
    <g transform="translate(0, 16)">
      <rect width="215" height="170" rx="12" fill="#FFFFFF" stroke="#86EFAC" stroke-width="1.5" filter="url(#shadow)"/>
      <rect x="12" y="12" width="76" height="22" rx="11" fill="#DCFCE7"/>
      <text x="50" y="27" font-size="10" font-weight="800" fill="#15803D" text-anchor="middle">NORMAL</text>
      <text x="12" y="54" font-size="11.5" font-weight="700" fill="#1E293B">Valores Nominales / Activo</text>
      
      <!-- Componente Botón Normal -->
      <rect x="12" y="68" width="191" height="36" rx="8" fill="#4F46E5"/>
      <text x="107" y="90" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">Iniciar Patrullaje</text>

      <!-- Badge de Estado Normal -->
      <rect x="12" y="114" width="191" height="34" rx="8" fill="#DCFCE7"/>
      <circle cx="28" cy="131" r="5" fill="#16A34A"/>
      <text x="40" y="135" font-size="10.5" font-weight="700" fill="#15803D">ONLINE (-55 dBm)</text>
    </g>

    <!-- Columna 2: DESHABILITADO -->
    <g transform="translate(235, 16)">
      <rect width="215" height="170" rx="12" fill="#FFFFFF" stroke="#E2E8F0" stroke-width="1.5" filter="url(#shadow)"/>
      <rect x="12" y="12" width="105" height="22" rx="11" fill="#E2E8F0"/>
      <text x="64.5" y="27" font-size="10" font-weight="800" fill="#475569" text-anchor="middle">DESHABILITADO</text>
      <text x="12" y="54" font-size="11.5" font-weight="700" fill="#64748B">Precondición no cumplida</text>
      
      <!-- Componente Botón Deshabilitado -->
      <rect x="12" y="68" width="191" height="36" rx="8" fill="#F1F5F9" stroke="#E2E8F0"/>
      <text x="107" y="90" font-size="11" font-weight="600" fill="#94A3B8" text-anchor="middle">Iniciar (Batería &lt; 15%)</text>

      <!-- Badge Deshabilitado -->
      <rect x="12" y="114" width="191" height="34" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
      <circle cx="28" cy="131" r="5" fill="#94A3B8"/>
      <text x="40" y="135" font-size="10.5" font-weight="600" fill="#94A3B8">STANDBY (Comando Bloqueado)</text>
    </g>

    <!-- Columna 3: ADVERTENCIA -->
    <g transform="translate(470, 16)">
      <rect width="215" height="170" rx="12" fill="#FFFFFF" stroke="#FCD34D" stroke-width="1.5" filter="url(#shadow)"/>
      <rect x="12" y="12" width="100" height="22" rx="11" fill="#FEF3C7"/>
      <text x="62" y="27" font-size="10" font-weight="800" fill="#B45309" text-anchor="middle">ADVERTENCIA</text>
      <text x="12" y="54" font-size="11.5" font-weight="700" fill="#B45309">Batería baja / Obstáculo</text>
      
      <!-- Componente Botón Advertencia -->
      <rect x="12" y="68" width="191" height="36" rx="8" fill="#D97706"/>
      <text x="107" y="90" font-size="11" font-weight="700" fill="#FFFFFF" text-anchor="middle">⏸ Pausar por Obstáculo</text>

      <!-- Badge Advertencia -->
      <rect x="12" y="114" width="191" height="34" rx="8" fill="#FEF3C7"/>
      <circle cx="28" cy="131" r="5" fill="#D97706"/>
      <text x="40" y="135" font-size="10.5" font-weight="700" fill="#92400E">Batería 12% (Nivel Bajo)</text>
    </g>

    <!-- Columna 4: ERROR / CRÍTICO -->
    <g transform="translate(705, 16)">
      <rect width="215" height="170" rx="12" fill="#FFFFFF" stroke="#FCA5A5" stroke-width="1.5" filter="url(#shadow)"/>
      <rect x="12" y="12" width="86" height="22" rx="11" fill="#FEE2E2"/>
      <text x="55" y="27" font-size="10" font-weight="800" fill="#B91C1C" text-anchor="middle">ERROR / FAULT</text>
      <text x="12" y="54" font-size="11.5" font-weight="700" fill="#991B1B">Fuga de gas / Desconexión</text>
      
      <!-- Componente Botón Error / Stop -->
      <rect x="12" y="68" width="191" height="36" rx="8" fill="#DC2626"/>
      <text x="107" y="90" font-size="11" font-weight="800" fill="#FFFFFF" text-anchor="middle">🛑 Detención Operativa</text>

      <!-- Badge Error -->
      <rect x="12" y="114" width="191" height="34" rx="8" fill="#FEE2E2"/>
      <circle cx="28" cy="131" r="5" fill="#DC2626"/>
      <text x="40" y="135" font-size="10.5" font-weight="800" fill="#991B1B">OFFLINE (Datos Stale)</text>
    </g>
  </g>

  <!-- SECCIÓN 2: CATÁLOGO DE COMPONENTES REUTILIZABLES -->
  <g transform="translate(40, 340)">
    <text x="0" y="0" font-size="16" font-weight="800" fill="#0F172A">2. Componentes Reutilizables Modulares (Jetpack Compose / Figma UI Kit)</text>

    <!-- Fila 1: Badges y Estado de Conectividad -->
    <g transform="translate(0, 16)">
      <!-- Operational State Badges -->
      <rect width="450" height="150" rx="12" fill="#FFFFFF" stroke="#E2E8F0" filter="url(#shadow)"/>
      <text x="16" y="24" font-size="12" font-weight="700" fill="#0F172A">OperationalStateBadge.kt (Enums en Modelo)</text>

      <g transform="translate(16, 40)">
        <rect width="96" height="26" rx="13" fill="#EEF2FF"/>
        <text x="48" y="17" font-size="10" font-weight="700" fill="#4338CA" text-anchor="middle">STANDBY</text>

        <rect x="106" y="0" width="105" height="26" rx="13" fill="#DCFCE7"/>
        <text x="158.5" y="17" font-size="10" font-weight="700" fill="#15803D" text-anchor="middle">PATROLLING</text>

        <rect x="221" y="0" width="85" height="26" rx="13" fill="#FEF3C7"/>
        <text x="263.5" y="17" font-size="10" font-weight="700" fill="#92400E" text-anchor="middle">PAUSED</text>

        <rect x="316" y="0" width="105" height="26" rx="13" fill="#F3E8FF"/>
        <text x="368.5" y="17" font-size="10" font-weight="700" fill="#6B21A8" text-anchor="middle">RETURNING</text>
      </g>

      <g transform="translate(16, 76)">
        <rect width="130" height="26" rx="13" fill="#FEE2E2"/>
        <text x="65" y="17" font-size="10" font-weight="800" fill="#991B1B" text-anchor="middle">EMERGENCY_STOP</text>

        <rect x="140" y="0" width="80" height="26" rx="13" fill="#FEE2E2"/>
        <text x="180" y="17" font-size="10" font-weight="800" fill="#991B1B" text-anchor="middle">FAULT</text>
      </g>
      
      <text x="16" y="132" font-size="10.5" fill="#64748B">Cambia automáticamente según el flujo de simulación y telemetría en vivo.</text>
    </g>

    <!-- Fila 1 Derecha: CommandStatusCard (Ciclo de Vida de Órdenes) -->
    <g transform="translate(470, 16)">
      <rect width="450" height="150" rx="12" fill="#FFFFFF" stroke="#E2E8F0" filter="url(#shadow)"/>
      <text x="16" y="24" font-size="12" font-weight="700" fill="#0F172A">CommandStatusCard.kt (Ciclo de Vida de las 7 Órdenes)</text>

      <g transform="translate(16, 36)">
        <!-- PENDING -->
        <rect width="98" height="30" rx="6" fill="#FEF3C7" stroke="#F59E0B"/>
        <text x="49" y="19" font-size="9.5" font-weight="700" fill="#92400E" text-anchor="middle">PENDING (Enviando)</text>

        <!-- CONFIRMED -->
        <g transform="translate(108, 0)">
          <rect width="105" height="30" rx="6" fill="#DCFCE7" stroke="#16A34A"/>
          <text x="52.5" y="19" font-size="9.5" font-weight="700" fill="#15803D" text-anchor="middle">CONFIRMED (Aceptada)</text>
        </g>

        <!-- REJECTED -->
        <g transform="translate(222, 0)">
          <rect width="95" height="30" rx="6" fill="#FEE2E2" stroke="#DC2626"/>
          <text x="47.5" y="19" font-size="9.5" font-weight="700" fill="#991B1B" text-anchor="middle">REJECTED (Rechazo)</text>
        </g>

        <!-- NO_RESPONSE -->
        <g transform="translate(325, 0)">
          <rect width="95" height="30" rx="6" fill="#FFFBEB" stroke="#D97706"/>
          <text x="47.5" y="19" font-size="9.5" font-weight="700" fill="#B45309" text-anchor="middle">NO_RESPONSE</text>
        </g>
      </g>

      <text x="16" y="104" font-size="10.5" fill="#475569">Muestra el acuse de recibo visual y motivo de rechazo explícito (ej. batería insuficiente).</text>
      <text x="16" y="124" font-size="10" font-weight="600" fill="#4F46E5">Timeout a NO_RESPONSE tras 2500ms si el robot no responde por desconexión.</text>
    </g>

    <!-- Fila 2: ServiceMetricCard (Las 3 Variables del Servicio) -->
    <g transform="translate(0, 186)">
      <rect width="920" height="150" rx="12" fill="#FFFFFF" stroke="#E2E8F0" filter="url(#shadow)"/>
      <text x="16" y="24" font-size="12" font-weight="700" fill="#0F172A">ServiceMetricCard.kt (Variables Específicas de Inspección y Seguridad)</text>

      <g transform="translate(16, 40)">
        <!-- Variable 1: Gas / Humo -->
        <rect width="280" height="85" rx="10" fill="#F8FAFC" stroke="#E2E8F0"/>
        <text x="16" y="22" font-size="11" font-weight="700" fill="#0F172A">1. Gas / Humo (ppm)</text>
        <text x="16" y="46" font-size="18" font-weight="800" fill="#0F172A">18.2 <tspan font-size="11" font-weight="500">ppm</tspan></text>
        <text x="16" y="68" font-size="9.5" fill="#16A34A">Normal: &lt;25 ppm  •  Crítico: &gt;50 ppm</text>

        <!-- Variable 2: Temperatura Tableros -->
        <g transform="translate(300, 0)">
          <rect width="280" height="85" rx="10" fill="#F8FAFC" stroke="#E2E8F0"/>
          <text x="16" y="22" font-size="11" font-weight="700" fill="#0F172A">2. Temperatura Ambiental (°C)</text>
          <text x="16" y="46" font-size="18" font-weight="800" fill="#0F172A">23.4 <tspan font-size="11" font-weight="500">°C</tspan></text>
          <text x="16" y="68" font-size="9.5" fill="#16A34A">Normal: 21°C  •  Alerta tableros: &gt;40°C</text>
        </g>

        <!-- Variable 3: Conteo Personas -->
        <g transform="translate(600, 0)">
          <rect width="280" height="85" rx="10" fill="#F8FAFC" stroke="#E2E8F0"/>
          <text x="16" y="22" font-size="11" font-weight="700" fill="#0F172A">3. Personas Detectadas</text>
          <text x="16" y="46" font-size="18" font-weight="800" fill="#0F172A">0 <tspan font-size="11" font-weight="500">personas</tspan></text>
          <text x="16" y="68" font-size="9.5" fill="#475569">Área restringida (Horario nocturno)</text>
        </g>
      </g>
    </g>

    <!-- Fila 3: Banner de Incidente y Modal de Confirmación -->
    <g transform="translate(0, 356)">
      <rect width="920" height="160" rx="12" fill="#FFFFFF" stroke="#E2E8F0" filter="url(#shadow)"/>
      <text x="16" y="24" font-size="12" font-weight="700" fill="#0F172A">IncidentDecisionBanner.kt &amp; ConfirmDialog.kt (Toma de Decisiones y Seguridad HRI)</text>

      <g transform="translate(16, 38)">
        <!-- Mini Banner Reusable -->
        <rect width="430" height="98" rx="10" fill="#FEF3C7" stroke="#F59E0B"/>
        <text x="16" y="24" font-size="11" font-weight="800" fill="#92400E">INCIDENTE DETECTADO - INTERVENCIÓN REQUERIDA</text>
        <text x="16" y="42" font-size="10" fill="#78350F">El robot detuvo su marcha preventivamente. Seleccione una acción:</text>
        
        <rect x="16" y="56" width="90" height="28" rx="6" fill="#16A34A"/>
        <text x="61" y="74" font-size="10" font-weight="700" fill="#FFFFFF" text-anchor="middle">Reanudar</text>

        <rect x="116" y="56" width="90" height="28" rx="6" fill="#DC2626"/>
        <text x="161" y="74" font-size="10" font-weight="700" fill="#FFFFFF" text-anchor="middle">Cancelar</text>

        <rect x="216" y="56" width="90" height="28" rx="6" fill="#7C3AED"/>
        <text x="261" y="74" font-size="10" font-weight="700" fill="#FFFFFF" text-anchor="middle">A Base</text>
      </g>

      <g transform="translate(470, 38)">
        <!-- Mini Dialog Reusable -->
        <rect width="430" height="98" rx="10" fill="#FFFFFF" stroke="#E2E8F0" filter="url(#shadow)"/>
        <text x="16" y="24" font-size="11.5" font-weight="800" fill="#B91C1C">ConfirmDialog: Detención Operativa por Software</text>
        <text x="16" y="42" font-size="10" fill="#475569">"¿Desea detener inmediatamente los actuadores del Unitree G1?"</text>
        <text x="16" y="58" font-size="9" font-weight="700" fill="#DC2626">Nota legal: La orden de app NO reemplaza la parada de emergencia física.</text>

        <rect x="280" y="68" width="65" height="24" rx="4" fill="#F1F5F9"/>
        <text x="312.5" y="84" font-size="9.5" fill="#475569" text-anchor="middle">Volver</text>

        <rect x="355" y="68" width="65" height="24" rx="4" fill="#DC2626"/>
        <text x="387.5" y="84" font-size="9.5" font-weight="700" fill="#FFFFFF" text-anchor="middle">Detener</text>
      </g>
    </g>
  </g>
</svg>'''
    return svg

# -------------------------------------------------------------------
# SCREEN 9: MAPA DE NAVEGACIÓN Y ARQUITECTURA DE INFORMACIÓN
# Dimensiones: 1000 x 680 px
# -------------------------------------------------------------------
def generate_screen_09_navigation_map():
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 680" width="1000" height="680">
  <defs>
    <style>
      @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&amp;display=swap');
      text {{ font-family: 'Inter', sans-serif; }}
    </style>
    <filter id="shadow" x="-5%" y="-5%" width="110%" height="110%">
      <feDropShadow dx="0" dy="2" stdDeviation="4" flood-color="#000000" flood-opacity="0.08"/>
    </filter>
  </defs>

  <rect width="1000" height="680" fill="#F8FAFC" rx="16"/>

  <!-- Header -->
  <g transform="translate(40, 36)">
    <text x="0" y="24" font-size="22" font-weight="800" fill="#0F172A">Mapa de Navegación y Flujos de Operación (HMI Unitree G1)</text>
    <text x="0" y="46" font-size="13" font-weight="500" fill="#64748B">Arquitectura de pantallas, transiciones modales y bifurcación entre Flujo Normal y Flujo con Falla.</text>
  </g>

  <!-- PANTALLA INICIAL: SELECCIÓN DEL ROBOT -->
  <g transform="translate(40, 110)">
    <rect width="180" height="80" rx="12" fill="#EEF2FF" stroke="#4F46E5" stroke-width="2" filter="url(#shadow)"/>
    <text x="14" y="28" font-size="12" font-weight="800" fill="#312E81">1. Ficha Robot</text>
    <text x="14" y="46" font-size="10.5" fill="#4338CA">Unitree G1 vs Go2</text>
    <text x="14" y="62" font-size="9.5" fill="#6366F1">Ficha técnica y sensores</text>
  </g>

  <!-- Flecha a Supervisión -->
  <path d="M220 150 L270 150" stroke="#4F46E5" stroke-width="2" stroke-dasharray="4,4" marker-end="url(#arrow)"/>
  <text x="245" y="142" font-size="9" font-weight="700" fill="#4F46E5" text-anchor="middle">Vincular</text>

  <!-- NÚCLEO: SUPERVISIÓN DASHBOARD -->
  <g transform="translate(280, 100)">
    <rect width="240" height="100" rx="14" fill="#FFFFFF" stroke="#4F46E5" stroke-width="2.5" filter="url(#shadow)"/>
    <text x="16" y="28" font-size="14" font-weight="800" fill="#0F172A">2. Supervisión (Dashboard)</text>
    <text x="16" y="48" font-size="11" fill="#475569">• Selector de Misiones (Standby)</text>
    <text x="16" y="64" font-size="11" fill="#475569">• Telemetría en vivo (8 gen. + 3 serv.)</text>
    <text x="16" y="80" font-size="11" fill="#475569">• Comandos discretos de control</text>
  </g>

  <!-- BIFURCACIÓN DE FLUJOS -->
  <!-- RUTA VERDE: FLUJO NORMAL -->
  <path d="M520 135 L620 135 L620 90 L680 90" stroke="#16A34A" stroke-width="2.5" fill="none"/>
  <rect x="540" y="118" width="115" height="18" rx="9" fill="#DCFCE7"/>
  <text x="597.5" y="131" font-size="9.5" font-weight="800" fill="#15803D" text-anchor="middle">FLUJO 1: NORMAL</text>

  <g transform="translate(680, 50)">
    <rect width="260" height="85" rx="12" fill="#FFFFFF" stroke="#16A34A" stroke-width="1.8" filter="url(#shadow)"/>
    <text x="16" y="26" font-size="12" font-weight="800" fill="#15803D">Patrullaje Waypoint 1 → 8</text>
    <text x="16" y="44" font-size="10.5" fill="#334155">Progreso 0% a 100% sin anomalías</text>
    <text x="16" y="60" font-size="10.5" fill="#334155">Llegada a destino → Modal Fin de Misión</text>
  </g>

  <!-- Flecha a Historial Normal -->
  <path d="M810 135 L810 175" stroke="#16A34A" stroke-width="2" fill="none"/>
  <g transform="translate(680, 175)">
    <rect width="260" height="70" rx="12" fill="#DCFCE7" stroke="#16A34A" stroke-width="1.5"/>
    <text x="16" y="26" font-size="12" font-weight="800" fill="#166534">Registro Room: COMPLETADA</text>
    <text x="16" y="44" font-size="10.5" fill="#14532D">8/8 waypoints alcanzados con éxito</text>
  </g>

  <!-- RUTA ROJA: FLUJO CON FALLA / CONTINGENCIA -->
  <path d="M520 165 L620 165 L620 320 L680 320" stroke="#DC2626" stroke-width="2.5" fill="none"/>
  <rect x="540" y="230" width="115" height="18" rx="9" fill="#FEE2E2"/>
  <text x="597.5" y="243" font-size="9.5" font-weight="800" fill="#B91C1C" text-anchor="middle">FLUJO 2: CON FALLA</text>

  <g transform="translate(680, 275)">
    <rect width="260" height="90" rx="12" fill="#FFFFFF" stroke="#DC2626" stroke-width="2" filter="url(#shadow)"/>
    <text x="16" y="26" font-size="12" font-weight="800" fill="#B91C1C">Incidente / Falla Detectada</text>
    <text x="16" y="44" font-size="10.5" fill="#334155">• Desconexión red / Gas 88 ppm / Obstáculo</text>
    <text x="16" y="60" font-size="10.5" fill="#334155">• Robot pasa a PAUSED o FAULT</text>
    <text x="16" y="76" font-size="10.5" fill="#B91C1C">• Despliegue de Banner de Decisión HRI</text>
  </g>

  <!-- Flecha a Resolución y Auditoría -->
  <path d="M810 365 L810 405" stroke="#DC2626" stroke-width="2" fill="none"/>
  <g transform="translate(680, 405)">
    <rect width="260" height="75" rx="12" fill="#FEE2E2" stroke="#DC2626" stroke-width="1.5"/>
    <text x="16" y="26" font-size="12" font-weight="800" fill="#7F1D1D">Registro Room: ABORTADA / LOG</text>
    <text x="16" y="44" font-size="10.5" fill="#991B1B">Acuse de alerta y repliegue seguro auditado</text>
  </g>

  <!-- NAVEGACIÓN GLOBAL POR BOTTOM BAR (TABS SECUNDARIOS) -->
  <g transform="translate(40, 260)">
    <rect width="480" height="220" rx="14" fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1.5" filter="url(#shadow)"/>
    <text x="20" y="28" font-size="13" font-weight="800" fill="#0F172A">Destinos Globales (Barra de Navegación Inferior)</text>
    
    <g transform="translate(20, 44)">
      <!-- Tab Alertas -->
      <rect width="200" height="65" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
      <text x="12" y="24" font-size="12" font-weight="700" fill="#0F172A">Alertas (Badge numérico)</text>
      <text x="12" y="42" font-size="10" fill="#64748B">Centro de contingencias, severidad y acuse de recibo de alarmas.</text>

      <!-- Tab Historial -->
      <g transform="translate(220, 0)">
        <rect width="210" height="65" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
        <text x="12" y="24" font-size="12" font-weight="700" fill="#0F172A">Historial y Auditoría</text>
        <text x="12" y="42" font-size="10" fill="#64748B">Consultas a Room DB: misiones, duraciones y logs de órdenes.</text>
      </g>
    </g>

    <g transform="translate(20, 125)">
      <!-- Tab Escenarios -->
      <rect width="200" height="65" rx="8" fill="#F8FAFC" stroke="#E2E8F0"/>
      <text x="12" y="24" font-size="12" font-weight="700" fill="#0F172A">Escenarios de Evaluación</text>
      <text x="12" y="42" font-size="10" fill="#64748B">Disparadores 1-clic de los 5 casos de prueba obligatorios del TP.</text>

      <!-- Modales Modales Transversales -->
      <g transform="translate(220, 0)">
        <rect width="210" height="65" rx="8" fill="#FEF2F2" stroke="#FCA5A5"/>
        <text x="12" y="24" font-size="12" font-weight="700" fill="#B91C1C">Modales de Confirmación</text>
        <text x="12" y="42" font-size="10" fill="#7F1D1D">Doble validación para Cancelar Misión y Detención Operativa.</text>
      </g>
    </g>
  </g>

  <!-- Footer resumen -->
  <g transform="translate(40, 520)">
    <rect width="920" height="120" rx="12" fill="#EEF2FF" stroke="#C7D2FE" stroke-width="1"/>
    <text x="24" y="28" font-size="13" font-weight="800" fill="#312E81">Resumen de Cumplimiento de la Sección 3 (Diseño en Figma):</text>
    <text x="24" y="52" font-size="11" fill="#4338CA">✓ Prototipo navegable: Vistas articuladas e interconectadas con Bottom Navigation Bar y modales.</text>
    <text x="24" y="70" font-size="11" fill="#4338CA">✓ Componentes reutilizables: UI Kit modular con badges, métricas, botones y diálogo de seguridad física.</text>
    <text x="24" y="88" font-size="11" fill="#4338CA">✓ Estados visuales: Normal (verde/azul), Deshabilitado (gris), Advertencia (amarillo) y Error (rojo).</text>
    <text x="24" y="106" font-size="11" fill="#4338CA">✓ Cobertura total de vistas y los dos flujos exigidos: Flujo Normal (completado) y Flujo con Falla (contingencia y repliegue).</text>
  </g>
</svg>'''
    return svg

# Escribir todos los SVGs
screens = {
    "01_seleccion_robot.svg": generate_screen_01_robot_selection(),
    "02_supervision_standby_misiones.svg": generate_screen_02_supervision_standby(),
    "03_supervision_patrullaje_telemetria.svg": generate_screen_03_telemetry_patrolling(),
    "04_control_comandos_modal_paro.svg": generate_screen_04_control_modal(),
    "05_alertas_decision_incidente.svg": generate_screen_05_alerts_incident(),
    "06_historial_auditoria.svg": generate_screen_06_history_audit(),
    "07_desconexion_red_stale.svg": generate_screen_07_disconnection_failure(),
    "08_design_system_componentes.svg": generate_screen_08_design_system(),
    "09_mapa_navegacion.svg": generate_screen_09_navigation_map()
}

for filename, content in screens.items():
    filepath = os.path.join(SVG_DIR, filename)
    with open(filepath, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Generado SVG: {filepath}")

print("Todos los SVGs vectoriales listos para Figma generados exitosamente.")
