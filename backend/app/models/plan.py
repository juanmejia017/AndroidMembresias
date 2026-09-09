"""
Modelo Plan.

Representa los planes tarifarios parametrizables exigidos por RF02:
"Crear y administrar planes tarifarios flexibles: diario, quincenal,
mensual y trimestral", combinables con una categoría comercial
(Normal, Estudiante, VIP, Corporativo) usada por el MembresiaFactory
para aplicar reglas de negocio específicas (ver services/membresia_factory.py).
"""
import enum

from sqlalchemy import Integer, String, Float, Boolean, Enum as SAEnum
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class TipoDuracion(str, enum.Enum):
    DIARIO = "diario"
    QUINCENAL = "quincenal"
    MENSUAL = "mensual"
    TRIMESTRAL = "trimestral"


class CategoriaPlan(str, enum.Enum):
    NORMAL = "normal"
    ESTUDIANTE = "estudiante"
    VIP = "vip"
    CORPORATIVO = "corporativo"


# Duración en días asociada a cada tipo de plan (regla de negocio del RF02).
DURACION_DIAS: dict[TipoDuracion, int] = {
    TipoDuracion.DIARIO: 1,
    TipoDuracion.QUINCENAL: 15,
    TipoDuracion.MENSUAL: 30,
    TipoDuracion.TRIMESTRAL: 90,
}


class Plan(Base):
    __tablename__ = "planes"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    nombre: Mapped[str] = mapped_column(String(80), nullable=False)
    tipo_duracion: Mapped[TipoDuracion] = mapped_column(SAEnum(TipoDuracion), nullable=False)
    categoria: Mapped[CategoriaPlan] = mapped_column(
        SAEnum(CategoriaPlan), default=CategoriaPlan.NORMAL, nullable=False
    )
    precio_base: Mapped[float] = mapped_column(Float, nullable=False)
    activo: Mapped[bool] = mapped_column(Boolean, default=True)

    membresias = relationship("Membresia", back_populates="plan")
