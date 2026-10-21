export interface ResponseDTO<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  path: string | null;
}

export interface ProductoDTO {
  idProducto?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  estado: string; // 'activo' | 'inactivo'
  fechaCreacion?: string;
  idCategoria: number;
  nombreCategoria?: string;
  stock?: number;
}

export interface CategoriaDTO {
  idCategoria?: number;
  nombre: string;
  descripcion: string;
  fechaCreacion?: string;
}

export interface VentaDTO {
  idVenta?: number;
  idUsuario: number;
  nombreUsuario?: string;
  emailUsuario?: string;
  idProducto: number;
  nombreProducto?: string;
  idCategoria?: number;
  nombreCategoria?: string;
  cantidad: number;
  precioUnitario: number;
  total: number;
  estado: string;
  fechaVenta?: string;
  fechaCreacion?: string;
}

export interface VentaRequest {
  idProducto: number;
  cantidad: number;
}

export interface ResumenVentas {
  ventasCompletadas: number;
  productosVendidos: number;
  ingresos: number;
}

export interface InventarioDTO {
  idInventario?: number;
  stockTotal: number;
  stockDisponible: number;
  stockMinimo: number;
  necesitaReposicion?: boolean;
  fechaCreacion?: string;
  idProducto: number;
  nombreProducto?: string;
  idCategoria?: number;
  nombreCategoria?: string;
  precioProducto?: number;
}

export interface UsuarioDTO {
  idUsuario?: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  nacionalidad?: string;
  dni?: string;
  rol: string;
  activo?: boolean;
  fechaCreacion?: string;
  fechaActualizacion?: string;
  nombreCompleto?: string;
}

export interface UsuarioRequest {
  nombre: string;
  apellido: string;
  email: string;
  password?: string;
  telefono?: string;
  nacionalidad?: string;
  dni?: string;
  rol?: string;
}

export interface ClienteDTO {
  id: string;
  nombre: string;
  empresa: string;
  documento: string;
  email: string;
  telefono: string;
  direccion: string;
  pedidos: number;
  totalComprado: number;
  estado: 'Activo' | 'Inactivo';
}

export interface ProveedorDTO {
  id: string;
  razonSocial: string;
  ruc: string;
  contacto: string;
  telefono: string;
  email: string;
  categoria: string;
  estado: 'Activo' | 'Inactivo';
}

export interface PedidoDTO {
  id: string;
  cliente: string;
  productos: string;
  total: number;
  fecha: string;
  metodoPago: string;
  estado: 'Pendiente' | 'En preparación' | 'Enviado' | 'Entregado' | 'Cancelado';
}
