"""
Modelo Cliente.

NOTA DE ALCANCE: el módulo de Registro de Usuarios (RF01 / HU01) es un
módulo independiente en el proyecto Zona Fit Evolution. Aquí solo se
define la tabla mínima necesaria para poder asociar una membresía a un
cliente, ya que el alcance de esta entrega es exclusivamente el
Módulo de Gestión de Membresías (RF02, HU03, CU-03 y CU-04).
"""
from datetime import datetime, timezone

from sqlalchemy import Integer, String, DateTime
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class Cliente(Base):
    __tablename__ = "clientes"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    nombre: Mapped[str] = mapped_column(String(120), nullable=False)
    cedula: Mapped[str] = mapped_column(String(20), unique=True, index=True, nullable=False)
    correo: Mapped[str] = mapped_column(String(150), unique=True, index=True, nullable=False)
    telefono: Mapped[str] = mapped_column(String(20), nullable=True)
    fecha_registro: Mapped[datetime] = mapped_column(
        DateTime, default=lambda: datetime.now(timezone.utc)
    )

    membresias = relationship(
        "Membresia", back_populates="cliente", cascade="all, delete-orphan"
    )
