from datetime import datetime

from pydantic import BaseModel, EmailStr, Field, ConfigDict


class ClienteCreate(BaseModel):
    nombre: str = Field(min_length=3, max_length=120)
    cedula: str = Field(min_length=5, max_length=20)
    correo: EmailStr
    telefono: str | None = Field(default=None, max_length=20)


class ClienteOut(BaseModel):
    id: int
    nombre: str
    cedula: str
    correo: EmailStr
    telefono: str | None
    fecha_registro: datetime

    model_config = ConfigDict(from_attributes=True)
