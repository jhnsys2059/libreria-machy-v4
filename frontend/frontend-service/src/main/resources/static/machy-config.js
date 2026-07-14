const MACHY_CONFIG = {
  negocio: {
    nombre: "Librería Machy",
    ruc: "20XXXXXXXXXX",
    direccion: "Av. Principal 123, Lima, Perú",
    telefono: "01-XXXXXXX",
    correo: "contacto@libreriamachy.com",
  },
  ventas: {
    igv: 18,
    montoMinimoBoleta: 5.00,
    descuentoMaxVendedor: 10,
    stockMinimoGlobal: 5,
  },
  sesion: {
    inactividadMinutos: 30,
    avisoMinutos: 25,
    maxIntentos: 5,
    bloqueoMinutos: 15,
    jwtExpiracionHoras: 8,
  },
  turnos: {
    manana:  { label: "Turno Mañana",  inicio: "08:00", fin: "13:30" },
    tarde:   { label: "Turno Tarde",   inicio: "14:00", fin: "19:00" },
    completo:{ label: "Turno Completo",inicio: "08:00", fin: "19:00" },
  },
  version: "4.0.0-microservices",
};
