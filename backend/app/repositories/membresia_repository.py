from sqlalchemy import select
from sqlalchemy.orm import Session, selectinload

from app.models.membresia import Membresia, EstadoMembresia


class MembresiaRepository:
    def __init__(self, db: Session):
        self.db = db

    def _query_base(self):
        return select(Membresia).options(
            selectinload(Membresia.cliente), selectinload(Membresia.plan)
        )

    def crear(self, membresia: Membresia) -> Membresia:
        self.db.add(membresia)
        self.db.commit()
        self.db.refresh(membresia)
        return membresia

    def obtener_por_id(self, membresia_id: int) -> Membresia | None:
        stmt = self._query_base().where(Membresia.id == membresia_id)
        return self.db.execute(stmt).scalars().first()

    def listar(self, estado: EstadoMembresia | None = None) -> list[Membresia]:
        stmt = self._query_base()
        if estado is not None:
            stmt = stmt.where(Membresia.estado == estado)
        return list(self.db.execute(stmt).scalars().all())

    def listar_por_cliente(self, cliente_id: int) -> list[Membresia]:
        stmt = self._query_base().where(Membresia.cliente_id == cliente_id)
        return list(self.db.execute(stmt).scalars().all())

    def obtener_ultima_por_cliente(self, cliente_id: int) -> Membresia | None:
        stmt = (
            self._query_base()
            .where(Membresia.cliente_id == cliente_id)
            .order_by(Membresia.fecha_vencimiento.desc())
        )
        return self.db.execute(stmt).scalars().first()

    def guardar(self, membresia: Membresia) -> Membresia:
        self.db.commit()
        self.db.refresh(membresia)
        return membresia
