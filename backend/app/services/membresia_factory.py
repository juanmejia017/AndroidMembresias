"""
MembresiaFactory (patrón Factory Method).

Tal como se describe en el documento maestro del proyecto (sección 5.1,
"Factory Method"): centraliza y encapsula las reglas de negocio que
dependen de la categoría del plan (Normal, Estudiante, VIP, Corporativo)
al momento de construir una nueva membresía: cálculo de precio final
(con descuentos) y cálculo de la fecha de vencimiento según la duración
del plan.

Mantener esta lógica en un único punto evita duplicar reglas de
descuento/vigencia dentro de los routers o en múltiples servicios.
"""
from dataclasses import dataclass
from datetime import date, timedelta

from app.models.plan import Plan, DURACION_DIAS, CategoriaPlan

# Reglas de descuento por categoría comercial del plan.
DESCUENTOS_POR_CATEGORIA: dict[CategoriaPlan, float] = {
    CategoriaPlan.NORMAL: 0.0,
    CategoriaPlan.ESTUDIANTE: 0.20,
    CategoriaPlan.VIP: 0.0,
    CategoriaPlan.CORPORATIVO: 0.15,
}


@dataclass(frozen=True)
class DatosMembresia:
    """Resultado inmutable calculado por la fábrica, listo para persistir."""
    precio_pagado: float
    fecha_inicio: date
    fecha_vencimiento: date


class MembresiaFactory:
    """Fábrica responsable de construir los datos de una nueva membresía."""

    @staticmethod
    def crear(plan: Plan, fecha_inicio: date | None = None) -> DatosMembresia:
        fecha_inicio = fecha_inicio or date.today()
        duracion_dias = DURACION_DIAS[plan.tipo_duracion]
        fecha_vencimiento = fecha_inicio + timedelta(days=duracion_dias)

        descuento = DESCUENTOS_POR_CATEGORIA.get(plan.categoria, 0.0)
        precio_pagado = round(plan.precio_base * (1 - descuento), 2)

        return DatosMembresia(
            precio_pagado=precio_pagado,
            fecha_inicio=fecha_inicio,
            fecha_vencimiento=fecha_vencimiento,
        )
