from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models.membresia import EstadoMembresia
from app.schemas.membresia_schema import MembresiaCreate, MembresiaOut, ValidarAccesoOut
from app.services.membresia_service import MembresiaService

router = APIRouter(prefix="/membresias", tags=["Membresías"])


@router.post("", response_model=MembresiaOut, status_code=status.HTTP_201_CREATED)
def asignar_membresia(data: MembresiaCreate, db: Session = Depends(get_db)):
    """Asigna un plan a un cliente. HU01 / HU03: registro y asignación de plan."""
    return MembresiaService(db).asignar_membresia(data)


@router.get("", response_model=list[MembresiaOut])
def listar_membresias(estado: EstadoMembresia | None = None, db: Session = Depends(get_db)):
    return MembresiaService(db).listar_membresias(estado=estado)


@router.get("/{membresia_id}", response_model=MembresiaOut)
def obtener_membresia(membresia_id: int, db: Session = Depends(get_db)):
    return MembresiaService(db).obtener_membresia(membresia_id)


@router.get("/cliente/{cliente_id}", response_model=list[MembresiaOut])
def listar_membresias_de_cliente(cliente_id: int, db: Session = Depends(get_db)):
    return MembresiaService(db).listar_membresias_de_cliente(cliente_id)


@router.put("/{membresia_id}/renovar", response_model=MembresiaOut)
def renovar_membresia(membresia_id: int, db: Session = Depends(get_db)):
    """HU04: recarga/renovación de membresías, actualizando la fecha de vencimiento."""
    return MembresiaService(db).renovar_membresia(membresia_id)


@router.put("/{membresia_id}/cancelar", response_model=MembresiaOut)
def cancelar_membresia(membresia_id: int, db: Session = Depends(get_db)):
    return MembresiaService(db).cancelar_membresia(membresia_id)


@router.get("/validar-acceso/{cliente_id}", response_model=ValidarAccesoOut)
def validar_acceso(cliente_id: int, db: Session = Depends(get_db)):
    """CU-04 · Validar Acceso: usado por control de acceso / recepción."""
    return MembresiaService(db).validar_acceso(cliente_id)
