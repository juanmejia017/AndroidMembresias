"""
Patrón State aplicado al ciclo de vida de la membresía.

El documento maestro (sección 5.3, "State") describe: "El ciclo de vida
de la entidad 'Socio' varía según las fechas del sistema. Si el estado
es 'Activo', el validador otorga acceso; si cambia a 'Vencido' o
'Moroso', el sistema bloquea el ingreso físico y emite una alerta".

Cada estado encapsula su propio comportamiento de acceso (permite_acceso)
y su mensaje para el recepcionista/administrador, en lugar de resolver
esa lógica con múltiples `if/elif` dispersos por el código.
"""
from abc import ABC, abstractmethod
from datetime import date

from app.models.membresia import EstadoMembresia


class EstadoMembresiaBase(ABC):
    nombre: EstadoMembresia

    @abstractmethod
    def permite_acceso(self) -> bool: ...

    @abstractmethod
    def mensaje(self) -> str: ...


class EstadoActiva(EstadoMembresiaBase):
    nombre = EstadoMembresia.ACTIVA

    def permite_acceso(self) -> bool:
        return True

    def mensaje(self) -> str:
        return "Membresía activa. Acceso permitido."


class EstadoVencida(EstadoMembresiaBase):
    nombre = EstadoMembresia.VENCIDA

    def permite_acceso(self) -> bool:
        return False

    def mensaje(self) -> str:
        return "Membresía vencida. Acceso denegado, se requiere renovación."


class EstadoMorosa(EstadoMembresiaBase):
    nombre = EstadoMembresia.MOROSA

    def permite_acceso(self) -> bool:
        return False

    def mensaje(self) -> str:
        return "Membresía en mora. Acceso denegado, se requiere regularizar el pago."


class EstadoCancelada(EstadoMembresiaBase):
    nombre = EstadoMembresia.CANCELADA

    def permite_acceso(self) -> bool:
        return False

    def mensaje(self) -> str:
        return "Membresía cancelada. Acceso denegado."


_ESTADOS: dict[EstadoMembresia, EstadoMembresiaBase] = {
    EstadoMembresia.ACTIVA: EstadoActiva(),
    EstadoMembresia.VENCIDA: EstadoVencida(),
    EstadoMembresia.MOROSA: EstadoMorosa(),
    EstadoMembresia.CANCELADA: EstadoCancelada(),
}


def obtener_estado(estado: EstadoMembresia) -> EstadoMembresiaBase:
    """Devuelve el objeto de estado (patrón State) para el enum dado."""
    return _ESTADOS[estado]


def calcular_estado_vigente(
    fecha_vencimiento: date, estado_actual: EstadoMembresia
) -> EstadoMembresia:
    """
    Recalcula el estado real de la membresía comparando la fecha de
    vencimiento con la fecha del sistema (RF03: "Denegación automática
    e inmediata si la fecha actual es estrictamente mayor a la de
    vencimiento").

    Los estados Cancelada y Morosa son decisiones administrativas
    explícitas y no se sobrescriben automáticamente por fecha.
    """
    if estado_actual in (EstadoMembresia.CANCELADA, EstadoMembresia.MOROSA):
        return estado_actual
    if date.today() > fecha_vencimiento:
        return EstadoMembresia.VENCIDA
    return EstadoMembresia.ACTIVA
