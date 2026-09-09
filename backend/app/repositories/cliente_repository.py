from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.cliente import Cliente


class ClienteRepository:
    def __init__(self, db: Session):
        self.db = db

    def crear(self, cliente: Cliente) -> Cliente:
        self.db.add(cliente)
        self.db.commit()
        self.db.refresh(cliente)
        return cliente

    def obtener_por_id(self, cliente_id: int) -> Cliente | None:
        return self.db.get(Cliente, cliente_id)

    def obtener_por_cedula(self, cedula: str) -> Cliente | None:
        stmt = select(Cliente).where(Cliente.cedula == cedula)
        return self.db.execute(stmt).scalars().first()

    def listar(self) -> list[Cliente]:
        return list(self.db.execute(select(Cliente)).scalars().all())
