# Nombre de Tu Proyecto

RUNA - Sistema de Business Intelligence para mejorar la toma de decisiones en negocios rurales del Perú

---

## Estructura del Proyecto

RUNA/
├── .gitignore
├── README.md
├── docs/                     # Documentación del proyecto (// proximamente...)
│   ├── arquitectura.md
│   └── manual-usuario.md
├── backend/                  # API REST en Java Spring Boot
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/runa/api/
│       └── main/resources/application.properties
├── frontend/                 # Interfaz gráfica en Angular
│   ├── package.json
│   └── src/
│       ├── app/
│       └── assets/
├── analytics/                # Módulo predictivo en Python (// proximamente...)
│   ├── requirements.txt
│   ├── main.py
│   └── models/
└── database/                 # Scripts SQL de estructura e inicialización (// proximamente...)
    ├── schema.sql
    └── data_seed.sql

---

## Flujo de Trabajo y Ramas

- **`main`**: Aloja el código de producción / backend.
- **`frontend`**: Rama dedicada al desarrollo de la interfaz de usuario.
- Para colaborar, crea tu propia rama siguiendo la convención: `nombre-funcionalidad`.