"""
Modelo Membresia.

Representa la asignación concreta de un Plan a un Cliente, con su
vigencia y estado. El estado (Activa/Vencida/Morosa/Cancelada)
corresponde a la entidad "Socio" descrita en el patrón State del
documento maestro (sección 5.3): el comportamiento de validación de
acceso cambia según este campo.
"""
import enum
from datetime import datetime, date, timezone

from sqlalchemy import Integer, Float, Date, DateTime, ForeignKey, Enum as SAEnum
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class EstadoMembresia(str, enum.Enum):
    ACTIVA = "activa"
    VENCIDA = "vencida"
    MOROSA = "morosa"
    CANCELADA = "cancelada"


class Membresia(Base):
    __tablename__ = "membresias"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    cliente_id: Mapped[int] = mapped_column(ForeignKey("clientes.id"), nullable=False)
    plan_id: Mapped[int] = mapped_column(ForeignKey("planes.id"), nullable=False)

    precio_pagado: Mapped[float] = mapped_column(Float, nullable=False)
    fecha_inicio: Mapped[date] = mapped_column(Date, nullable=False)
    fecha_vencimiento: Mapped[date] = mapped_column(Date, nullable=False)
    estado: Mapped[EstadoMembresia] = mapped_column(
        SAEnum(EstadoMembresia), default=EstadoMembresia.ACTIVA, nullable=False
    )
    fecha_creacion: Mapped[datetime] = mapped_column(
        DateTime, default=lambda: datetime.now(timezone.utc)
    )

    cliente = relationship("Cliente", back_populates="membresias")
    plan = relationship("Plan", back_populates="membresias")
