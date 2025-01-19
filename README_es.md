# 💰 Gestor de Gastos

## 📝 Descripción
Una aplicación de escritorio moderna para gestionar gastos personales con información y análisis impulsados por IA. Desarrollada con Java y con una interfaz de usuario intuitiva, esta aplicación ayuda a los usuarios a rastrear, analizar y optimizar sus hábitos de gasto.

## ✨ Características Principales
- 🔐 Sistema de autenticación seguro
- 💳 Seguimiento y gestión de gastos fácil
- 📊 Análisis visual de gastos con gráficos
- 🗂️ Categorización inteligente de gastos
- 📅 Seguimiento de gastos mensuales y totales
- 🌍 Soporte multilenguaje (Inglés, Español, Francés, Alemán)
- 🎨 Soporte de tema claro/oscuro
- 🤖 Información financiera impulsada por IA usando Deepseek AI

## 🛠️ Requisitos Técnicos
- ☕ Java 11 o superior
- 📦 Base de datos MySQL
- 💻 Sistema Operativo: Windows, Linux o macOS

## 🚀 Inicio Rápido

### 1️⃣ Instalación
```bash
# Clonar el repositorio
git clone [url-del-repositorio]

# Navegar al directorio del proyecto
cd GestorDeGastos

# Ejecutar script de configuración de MySQL
./setup_mysql.bat

# Ejecutar la aplicación
./run.bat
```

### 2️⃣ Configuración
1. 🔑 Configurar su clave API de Deepseek AI:
   - Obtenga su clave API desde [platform.deepseek.ai](https://platform.deepseek.ai)
   - Introdúzcala en la aplicación cuando se le solicite
   - O agréguela a `config.properties`:
     ```properties
     deepseek.api.key=su-clave-api-aqui
     ```

2. 🗄️ Configuración de Base de Datos:
   - Actualice `config.properties` con sus credenciales de MySQL:
     ```properties
     db.url=jdbc:mysql://localhost:3306/gestor_gastos
     db.user=su-usuario
     db.password=su-contraseña
     ```

## 💡 Guía de Uso

### 👤 Primeros Pasos
1. Inicie la aplicación
2. Registre una nueva cuenta o inicie sesión
3. ¡Comience a rastrear sus gastos!

### 📱 Características Principales
- 📝 **Agregar Gastos**: Haga clic en el botón "+" para añadir nuevos gastos
- 📊 **Ver Análisis**: Vea los patrones de gasto en la pestaña de Gráficos
- 🔍 **Buscar**: Filtre gastos por fecha, categoría o monto
- 💬 **Asistente de IA**: Obtenga información y respuestas sobre sus gastos
- 🌙 **Cambio de Tema**: Cambie entre temas claro y oscuro
- 🌐 **Idioma**: Cambie el idioma de la aplicación en configuraciones

## 🤝 Contribuciones
¡Las contribuciones son bienvenidas! No dude en enviar una Solicitud de Extracción.

## 📄 Licencia
Este proyecto está licenciado bajo la Licencia MIT - consulte el archivo LICENSE para más detalles.

## 🙏 Reconocimientos
- 🎨 Diseño de UI: FlatLaf Look and Feel
- 📊 Gráficos: JFreeChart
- 🤖 Integración de IA: Deepseek AI API
- 📅 Calendario: JCalendar

## 📞 Soporte
Si encuentra algún problema o tiene preguntas:
1. 📩 Abra un problema en el repositorio
2. 📧 Contacte al equipo de desarrollo
3. 📚 Consulte la documentación

## 🔄 Historial de Versiones
- v1.0.0 - Lanzamiento Inicial
  - 🎯 Seguimiento básico de gastos
  - 📊 Panel de análisis
  - 🤖 Integración de IA
  - 🌍 Soporte multilenguaje