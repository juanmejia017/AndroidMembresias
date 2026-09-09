"""
Capa de servicio (lógica de negocio) del módulo de Gestión de Membresías.

Orquesta: repositorios (acceso a datos), MembresiaFactory (creación con
reglas de negocio) y el patrón State (estado y permisos de acceso).
Los routers de FastAPI NO deben contener lógica de negocio: solo reciben
la petición HTTP, delegan aquí, y traducen el resultado a una respuesta.
"""
from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.plan import Plan
from app.models.membresia import Membresia, EstadoMembresia
from app.repositories.plan_repository import PlanRepository
from app.repositories.cliente_repository import ClienteRepository
from app.repositories.membresia_repository import MembresiaRepository
from app.schemas.plan_schema import PlanCreate, PlanUpdate
from app.schemas.membresia_schema import MembresiaCreate, ValidarAccesoOut
from app.services.membresia_factory import MembresiaFactory
from app.services.estado_membresia import obtener_estado, calcular_estado_vigente


class MembresiaService:
    def __init__(self, db: Session):
        self.db = db
        self.planes = PlanRepository(db)
        self.clientes = ClienteRepository(db)
        self.membresias = MembresiaRepository(db)

    # ---------- Planes (CU-03: Gestionar Membresías) ----------

    def crear_plan(self, data: PlanCreate) -> Plan:
        plan = Plan(**data.model_dump())
        return self.planes.crear(plan)

    def listar_planes(self, solo_activos: bool = False) -> list[Plan]:
        return self.planes.listar(solo_activos=solo_activos)

    def obtener_plan(self, plan_id: int) -> Plan:
        plan = self.planes.obtener_por_id(plan_id)
        if plan is None:
            raise HTTPException(status.HTTP_404_NOT_FOUND, "Plan no encontrado")
        return plan

    def actualizar_plan(self, plan_id: int, data: PlanUpdate) -> Plan:
        plan = self.obtener_plan(plan_id)
        for campo, valor in data.model_dump(exclude_unset=True).items():
            setattr(plan, campo, valor)
        return self.planes.actualizar(plan)

    def eliminar_plan(self, plan_id: int) -> None:
        plan = self.obtener_plan(plan_id)
        # Postcondición del CU-03 / Escenario 3: no se puede eliminar un
        # plan con membresías activas asociadas.
        if self.planes.tiene_membresias_activas(plan_id):
            raise HTTPException(
                status.HTTP_409_CONFLICT,
                "No es posible eliminar el plan: tiene membresías activas asociadas.",
            )
        self.planes.eliminar(plan)

    # ---------- Membresías ----------

    def asignar_membresia(self, data: MembresiaCreate) -> Membresia:
        cliente = self.clientes.obtener_por_id(data.cliente_id)
        if cliente is None:
            raise HTTPException(status.HTTP_404_NOT_FOUND, "Cliente no encontrado")

        plan = self.obtener_plan(data.plan_id)
        if not plan.activo:
            raise HTTPException(status.HTTP_400_BAD_REQUEST, "El plan seleccionado no está activo")

        # El Factory calcula precio (con reglas por categoría) y vigencia.
        datos = MembresiaFactory.crear(plan)

        membresia = Membresia(
            cliente_id=cliente.id,
            plan_id=plan.id,
            precio_pagado=datos.precio_pagado,
            fecha_inicio=datos.fecha_inicio,
            fecha_vencimiento=datos.fecha_vencimiento,
            estado=EstadoMembresia.ACTIVA,
        )
        return self.membresias.crear(membresia)

    def _refrescar_estado(self, membresia: Membresia) -> Membresia:
        """Recalcula el estado según la fecha (RF03) antes de exponerlo."""
        estado_vigente = calcular_estado_vigente(membresia.fecha_vencimiento, membresia.estado)
        if estado_vigente != membresia.estado:
            membresia.estado = estado_vigente
            membresia = self.membresias.guardar(membresia)
        return membresia

    def obtener_membresia(self, membresia_id: int) -> Membresia:
        membresia = self.membresias.obtener_por_id(membresia_id)
        if membresia is None:
            raise HTTPException(status.HTTP_404_NOT_FOUND, "Membresía no encontrada")
        return self._refrescar_estado(membresia)

    def listar_membresias(self, estado: EstadoMembresia | None = None) -> list[Membresia]:
        membresias = self.membresias.listar()
        membresias = [self._refrescar_estado(m) for m in membresias]
        if estado is not None:
            membresias = [m for m in membresias if m.estado == estado]
        return membresias

    def listar_membresias_de_cliente(self, cliente_id: int) -> list[Membresia]:
        membresias = self.membresias.listar_por_cliente(cliente_id)
        return [self._refrescar_estado(m) for m in membresias]

    def renovar_membresia(self, membresia_id: int) -> Membresia:
        membresia = self.obtener_membresia(membresia_id)
        plan = self.obtener_plan(membresia.plan_id)

        # Renueva desde la fecha de vencimiento actual (o desde hoy si ya venció).
        from datetime import date
        fecha_inicio = max(membresia.fecha_vencimiento, date.today())
        datos = MembresiaFactory.crear(plan, fecha_inicio=fecha_inicio)

        membresia.precio_pagado = datos.precio_pagado
        membresia.fecha_inicio = datos.fecha_inicio
        membresia.fecha_vencimiento = datos.fecha_vencimiento
        membresia.estado = EstadoMembresia.ACTIVA
        return self.membresias.guardar(membresia)

    def cancelar_membresia(self, membresia_id: int) -> Membresia:
        membresia = self.obtener_membresia(membresia_id)
        membresia.estado = EstadoMembresia.CANCELADA
        return self.membresias.guardar(membresia)

    # ---------- Validación de acceso (CU-04) ----------

    def validar_acceso(self, cliente_id: int) -> ValidarAccesoOut:
        cliente = self.clientes.obtener_por_id(cliente_id)
        if cliente is None:
            raise HTTPException(status.HTTP_404_NOT_FOUND, "Cliente no encontrado")

        membresia = self.membresias.obtener_ultima_por_cliente(cliente_id)
        if membresia is None:
            return ValidarAccesoOut(
                cliente_id=cliente.id,
                nombre_cliente=cliente.nombre,
                acceso_permitido=False,
                estado=EstadoMembresia.VENCIDA,
                fecha_vencimiento=None,
                mensaje="El cliente no tiene ninguna membresía registrada.",
            )

        membresia = self._refrescar_estado(membresia)
        estado_obj = obtener_estado(membresia.estado)

        return ValidarAccesoOut(
            cliente_id=cliente.id,
            nombre_cliente=cliente.nombre,
            acceso_permitido=estado_obj.permite_acceso(),
            estado=membresia.estado,
            fecha_vencimiento=membresia.fecha_vencimiento,
            mensaje=estado_obj.mensaje(),
        )
