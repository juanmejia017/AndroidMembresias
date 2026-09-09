from pydantic import BaseModel, Field, ConfigDict

from app.models.plan import TipoDuracion, CategoriaPlan


class PlanCreate(BaseModel):
    nombre: str = Field(min_length=3, max_length=80)
    tipo_duracion: TipoDuracion
    categoria: CategoriaPlan = CategoriaPlan.NORMAL
    precio_base: float = Field(gt=0, description="Precio base antes de reglas por categoría")


class PlanUpdate(BaseModel):
    nombre: str | None = Field(default=None, min_length=3, max_length=80)
    precio_base: float | None = Field(default=None, gt=0)
    activo: bool | None = None


class PlanOut(BaseModel):
    id: int
    nombre: str
    tipo_duracion: TipoDuracion
    categoria: CategoriaPlan
    precio_base: float
    activo: bool

    model_config = ConfigDict(from_attributes=True)
