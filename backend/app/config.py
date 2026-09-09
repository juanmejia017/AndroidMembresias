"""
Configuración central de la aplicación.

Usa pydantic-settings para leer variables de entorno (y el archivo .env)
de forma tipada y validada, en lugar de leer os.environ directamente
en cada módulo. Esto es una buena práctica porque:
  - Centraliza la configuración en un único punto de la app.
  - Valida tipos automáticamente (si DEBUG no es booleano, falla rápido).
  - Facilita las pruebas (se puede inyectar una configuración distinta).
"""
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "Zona Fit Evolution - Módulo de Membresías"
    app_version: str = "1.0.0"
    database_url: str = "sqlite:///./zona_fit_membresias.db"
    debug: bool = True
    cors_origins: str = "http://localhost:5500,http://127.0.0.1:5500"

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    @property
    def cors_origins_list(self) -> list[str]:
        return [origin.strip() for origin in self.cors_origins.split(",")]


# Instancia única (patrón Singleton simplificado): toda la app importa
# este mismo objeto ya construido en lugar de crear Settings() en cada módulo.
settings = Settings()
