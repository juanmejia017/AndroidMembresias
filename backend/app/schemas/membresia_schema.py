from datetime import datetime, date

from pydantic import BaseModel, ConfigDict

from app.models.membresia import EstadoMembresia
from app.schemas.plan_schema import PlanOut
from app.schemas.cliente_schema import ClienteOut


class MembresiaCreate(BaseModel):
    cliente_id: int
    plan_id: int


class MembresiaOut(BaseModel):
    id: int
    cliente: ClienteOut
    plan: PlanOut
    precio_pagado: float
    fecha_inicio: date
    fecha_vencimiento: date
    estado: EstadoMembresia
    fecha_creacion: datetime

    model_config = ConfigDict(from_attributes=True)


class ValidarAccesoOut(BaseModel):
    """Respuesta del endpoint de validación de acceso (CU-04)."""
    cliente_id: int
    nombre_cliente: str
    acceso_permitido: bool
    estado: EstadoMembresia
    fecha_vencimiento: date | None
    mensaje: str
