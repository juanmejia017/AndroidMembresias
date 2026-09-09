"""
Repositorio de Plan.

Aísla las consultas SQLAlchemy del resto de la aplicación (patrón
Repository). Si en el futuro se cambia el ORM o el motor de base de
datos, solo este archivo debería modificarse.
"""
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.plan import Plan


class PlanRepository:
    def __init__(self, db: Session):
        self.db = db

    def crear(self, plan: Plan) -> Plan:
        self.db.add(plan)
        self.db.commit()
        self.db.refresh(plan)
        return plan

    def obtener_por_id(self, plan_id: int) -> Plan | None:
        return self.db.get(Plan, plan_id)

    def listar(self, solo_activos: bool = False) -> list[Plan]:
        stmt = select(Plan)
        if solo_activos:
            stmt = stmt.where(Plan.activo.is_(True))
        return list(self.db.execute(stmt).scalars().all())

    def actualizar(self, plan: Plan) -> Plan:
        self.db.commit()
        self.db.refresh(plan)
        return plan

    def eliminar(self, plan: Plan) -> None:
        self.db.delete(plan)
        self.db.commit()

    def tiene_membresias_activas(self, plan_id: int) -> bool:
        from app.models.membresia import Membresia, EstadoMembresia

        stmt = select(Membresia).where(
            Membresia.plan_id == plan_id,
            Membresia.estado == EstadoMembresia.ACTIVA,
        )
        return self.db.execute(stmt).scalars().first() is not None
