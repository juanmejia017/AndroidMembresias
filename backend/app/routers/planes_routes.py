from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.schemas.plan_schema import PlanCreate, PlanUpdate, PlanOut
from app.services.membresia_service import MembresiaService

router = APIRouter(prefix="/planes", tags=["Planes de Membresía"])


@router.post("", response_model=PlanOut, status_code=status.HTTP_201_CREATED)
def crear_plan(data: PlanCreate, db: Session = Depends(get_db)):
    """CU-03 · Escenario 1: creación exitosa de un plan."""
    return MembresiaService(db).crear_plan(data)


@router.get("", response_model=list[PlanOut])
def listar_planes(solo_activos: bool = False, db: Session = Depends(get_db)):
    return MembresiaService(db).listar_planes(solo_activos=solo_activos)


@router.get("/{plan_id}", response_model=PlanOut)
def obtener_plan(plan_id: int, db: Session = Depends(get_db)):
    return MembresiaService(db).obtener_plan(plan_id)


@router.put("/{plan_id}", response_model=PlanOut)
def actualizar_plan(plan_id: int, data: PlanUpdate, db: Session = Depends(get_db)):
    """CU-03 · Escenario 2: actualización de un plan existente."""
    return MembresiaService(db).actualizar_plan(plan_id, data)


@router.delete("/{plan_id}", status_code=status.HTTP_204_NO_CONTENT)
def eliminar_plan(plan_id: int, db: Session = Depends(get_db)):
    """CU-03 · Escenario 3: bloquea la eliminación si hay socios activos."""
    MembresiaService(db).eliminar_plan(plan_id)
