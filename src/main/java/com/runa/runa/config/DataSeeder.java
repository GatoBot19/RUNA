package com.runa.runa.config;

import com.runa.runa.model.entity.Categoria;
import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.model.entity.Usuario;
import com.runa.runa.repository.CategoriaRepository;
import com.runa.runa.repository.InventarioRepository;
import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        log.info("========================================");
        log.info("INICIANDO DATOS INICIALES DE RUNA");
        log.info("========================================");

        // ========================================
        // USUARIOS INICIALES
        // ========================================

        log.info("Verificando usuarios iniciales...");

        createOrUpdateUser(
                "admin@runa.local",
                "admin123",
                "Admin",
                "Principal",
                "915213426",
                "ADMIN",
                "11111111"
        );

        createOrUpdateUser(
                "cliente@runa.local",
                "cliente123",
                "Cliente",
                "Prueba",
                "915213426",
                "CLIENTE",
                "42410784"
        );

        // ========================================
        // CATEGORÍAS INICIALES
        // ========================================

        log.info("Verificando categorías iniciales...");

        if (categoriaRepository.count() == 0) {
            seedCategorias();
        } else {
            log.info("Las categorías ya existen. No se crearán nuevamente.");
        }

        // ========================================
        // PRODUCTOS E INVENTARIO INICIALES
        // ========================================

        log.info("Verificando productos e inventario iniciales...");

        if (productoRepository.count() == 0) {
            seedProductos();
        } else {
            log.info("Los productos ya existen. No se crearán nuevamente.");
        }

        log.info("========================================");
        log.info("DATOS INICIALES DE RUNA LISTOS");
        log.info("========================================");
    }

    /**
     * Crea o actualiza un usuario inicial.
     *
     * IMPORTANTE:
     * La contraseña siempre se vuelve a encriptar con BCrypt
     * para evitar problemas de autenticación durante el desarrollo.
     */
    private void createOrUpdateUser(
            String email,
            String rawPassword,
            String nombre,
            String apellido,
            String telefono,
            String rol,
            String dni) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {

            Usuario usuario = usuarioOpt.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);
            usuario.setRol(rol);
            usuario.setDni(dni);
            usuario.setActivo(true);

            // IMPORTANTE:
            // Siempre vuelve a encriptar la contraseña.
            usuario.setPassword(passwordEncoder.encode(rawPassword));

            usuarioRepository.save(usuario);

            log.info(
                    "Usuario {} actualizado correctamente con rol {}.",
                    email,
                    rol
            );

            // Verificación temporal de BCrypt
            boolean passwordCorrecta =
                    passwordEncoder.matches(
                            rawPassword,
                            usuario.getPassword()
                    );

            log.info(
                    "Verificación BCrypt para {}: {}",
                    email,
                    passwordCorrecta
            );

        } else {

            log.info(
                    "Usuario {} no encontrado. Creando usuario inicial...",
                    email
            );

            Usuario nuevoUsuario = Usuario.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .nombre(nombre)
                    .apellido(apellido)
                    .telefono(telefono)
                    .rol(rol)
                    .dni(dni)
                    .activo(true)
                    .build();

            usuarioRepository.save(nuevoUsuario);

            log.info(
                    "Usuario {} creado exitosamente con rol {}.",
                    email,
                    rol
            );

            // Verificación temporal de BCrypt
            boolean passwordCorrecta =
                    passwordEncoder.matches(
                            rawPassword,
                            nuevoUsuario.getPassword()
                    );

            log.info(
                    "Verificación BCrypt para {}: {}",
                    email,
                    passwordCorrecta
            );
        }
    }

    /**
     * Crea las categorías iniciales de productos de Runa.
     */
    private void seedCategorias() {

        crearCategoria(
                "Botellas",
                "Botellas plásticas de diferentes capacidades y presentaciones."
        );

        crearCategoria(
                "Vasos",
                "Vasos plásticos para diferentes usos."
        );

        crearCategoria(
                "Platos",
                "Platos plásticos de diferentes tamaños."
        );

        crearCategoria(
                "Cucharas",
                "Cucharas plásticas para uso comercial."
        );

        crearCategoria(
                "Bolsas",
                "Bolsas plásticas para diferentes necesidades."
        );

        log.info("Categorías iniciales creadas correctamente.");
    }

    /**
     * Crea una categoría.
     */
    private void crearCategoria(
            String nombre,
            String descripcion) {

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build();

        categoriaRepository.save(categoria);

        log.info(
                "Categoría creada: {}",
                nombre
        );
    }

    /**
     * Crea productos e inventarios iniciales para el catálogo.
     */
    private void seedProductos() {
        List<Categoria> categorias = categoriaRepository.findAll();
        if (categorias.isEmpty()) return;

        Categoria botellas = categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase("Botellas")).findFirst().orElse(categorias.get(0));
        Categoria vasos = categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase("Vasos")).findFirst().orElse(categorias.get(0));
        Categoria platos = categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase("Platos")).findFirst().orElse(categorias.get(0));
        Categoria cucharas = categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase("Cucharas")).findFirst().orElse(categorias.get(0));
        Categoria bolsas = categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase("Bolsas")).findFirst().orElse(categorias.get(0));

        crearProductoConInventario("Botella PET 500ml", "Botella transparente para bebidas y jugos", new BigDecimal("0.90"), botellas, 120, 20);
        crearProductoConInventario("Envase plástico 1L", "Envase de polietileno transparente de alta resistencia", new BigDecimal("1.20"), botellas, 80, 15);
        crearProductoConInventario("Tapa rosca 28mm", "Tapa rosca con precinto de seguridad hermético", new BigDecimal("0.35"), vasos, 450, 50);
        crearProductoConInventario("Plato descartable térmico", "Plato hondo térmico para alimentos", new BigDecimal("0.45"), platos, 300, 30);
        crearProductoConInventario("Cuchara plástica comercial", "Cuchara plástica resistente para delivery", new BigDecimal("0.15"), cucharas, 600, 100);
        crearProductoConInventario("Bolsa de alta densidad 10x15", "Bolsa plástica para empaque y distribución comercial", new BigDecimal("0.25"), bolsas, 500, 50);

        log.info("Productos e inventarios iniciales creados exitosamente.");
    }

    private void crearProductoConInventario(String nombre, String desc, BigDecimal precio, Categoria cat, int stock, int minimo) {
        Producto p = Producto.builder()
                .nombre(nombre)
                .descripcion(desc)
                .precio(precio)
                .categoria(cat)
                .estado("activo")
                .build();
        Producto guardado = productoRepository.save(p);

        Inventario inv = Inventario.builder()
                .producto(guardado)
                .stockTotal(stock)
                .stockDisponible(stock)
                .stockMinimo(minimo)
                .build();
        inventarioRepository.save(inv);
    }
}
