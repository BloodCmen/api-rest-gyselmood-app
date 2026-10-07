package gym.ada.api.service.implement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import gym.ada.api.dto.ImagenCreateDto;
import gym.ada.api.dto.ImagenDto;
import gym.ada.api.dto.ImagenUpdateDto;
import gym.ada.api.dto.ProductCreateDto;
import gym.ada.api.dto.ProductData;
import gym.ada.api.dto.ProductUpdateDto;
import gym.ada.api.dto.ProductoTallaDto;
import gym.ada.api.dto.TallaDto;
import gym.ada.api.model.Categoria;
import gym.ada.api.model.Imagen;
import gym.ada.api.model.Producto;
import gym.ada.api.model.ProductoTalla;
import gym.ada.api.model.Talla;
import gym.ada.api.repository.ICategoriaRepository;
import gym.ada.api.repository.IImagenRepository;
import gym.ada.api.repository.IProductoRepository;
import gym.ada.api.repository.IProductoTallaRepository;
import gym.ada.api.repository.ITallaRepository;
import gym.ada.api.service.ICloudinaryService;
import gym.ada.api.service.IProductoService;
import jakarta.transaction.Transactional;


@Service
public class ProductoServiceImplement implements IProductoService {

    private final IProductoRepository productoRepository;
    private final ICategoriaRepository categoriaRepository;
    private final ITallaRepository tallaRepository;
    private final IProductoTallaRepository productoTallaRepository;
    private final IImagenRepository imagenRepository;
    private final ICloudinaryService cloudinaryService;
    
    

    public ProductoServiceImplement(
            IProductoRepository productoRepository,
            ICategoriaRepository categoriaRepository,
            ITallaRepository tallaRepository,
            IProductoTallaRepository productoTallaRepository,
            IImagenRepository imagenRepository,
            ICloudinaryService cloudinaryService) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.tallaRepository = tallaRepository;
        this.productoTallaRepository = productoTallaRepository;
        this.imagenRepository = imagenRepository;
        this.cloudinaryService = cloudinaryService;
    }


    // =========================================================
    // LISTAR PRODUCTOS ACTIVOS
    // =========================================================

    @Override
    public List<ProductData> listarProductosActivos() {
        List<Producto> productos = productoRepository.findByActivoTrueConDetalles(); // ← cambia esto
        return productos.stream()
                .map(this::convertirProducto)
                .collect(Collectors.toList());
    }


    // =========================================================
    // LISTAR TODOS CON DETALLES
    // =========================================================

    @Override
    public List<ProductData> obtenerTodosLosProductos() {

        List<Producto> productos = productoRepository.findAllConDetalles();

        return productos.stream()
                .map(this::convertirProducto)
                .collect(Collectors.toList());
    }


    // =========================================================
    // CREAR PRODUCTO
    // =========================================================

    @Override
    @Transactional
    public ProductData crearProducto(ProductCreateDto dto) {

        Producto producto = new Producto();

        producto.setTitulo(dto.getTitulo());
        producto.setDescripcion(dto.getDescripcion());
        producto.setAdicional(dto.getAdicional());
        producto.setPrecioUnidad(dto.getPrecioUnidad());
        producto.setPrecioTotal(dto.getPrecioTotal());
        producto.setActivo(true);


        // -----------------------------------------------------
        // CATEGORIA
        // -----------------------------------------------------

        Categoria categoria = categoriaRepository
                .findById(dto.getCategoriaId())
                .orElseThrow(() ->
                        new RuntimeException("Categoría no encontrada"));

        producto.setCategoria(categoria);

        producto = productoRepository.save(producto);


        // -----------------------------------------------------
        // TALLAS
        // -----------------------------------------------------

        if (dto.getTallas() != null) {

            for (ProductoTallaDto tallaDto : dto.getTallas()) {

                Talla talla = tallaRepository
                        .findById(tallaDto.getTallaId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Talla no encontrada: "
                                        + tallaDto.getTallaId()
                                ));

                ProductoTalla productoTalla = new ProductoTalla();

                productoTalla.setProducto(producto);
                productoTalla.setTalla(talla);
                productoTalla.setStock(tallaDto.getStock());

                productoTallaRepository.save(productoTalla);
            }
        }


        // -----------------------------------------------------
        // IMAGENES
        // -----------------------------------------------------

        if (dto.getImagenes() != null) {

            for (ImagenCreateDto imagenDto : dto.getImagenes()) {

                Imagen imagen = new Imagen();

                imagen.setProducto(producto);
                imagen.setUrl(imagenDto.getUrl());
                imagen.setPublicId(imagenDto.getPublicId());
                imagen.setEsPrincipal(
                        Boolean.TRUE.equals(imagenDto.getEsPrincipal())
                );

                imagenRepository.save(imagen);
            }
        }


        return convertirAProductData(producto.getId());
    }


    // =========================================================
    // PATCH - ACTUALIZAR PARCIALMENTE
    // =========================================================

    @Override
    @Transactional
    public ProductData actualizarProductoParcial(Long id, ProductUpdateDto dto) {

        // =========================================================
        // BUSCAR PRODUCTO
        // =========================================================
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // =========================================================
        // DATOS BÁSICOS
        // =========================================================
        if (dto.getTitulo() != null) {
            producto.setTitulo(dto.getTitulo());
        }
        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getAdicional() != null) {
            producto.setAdicional(dto.getAdicional());
        }
        if (dto.getPrecioUnidad() != null) {
            producto.setPrecioUnidad(dto.getPrecioUnidad());
        }
        if (dto.getPrecioTotal() != null) {
            producto.setPrecioTotal(dto.getPrecioTotal());
        }

        // =========================================================
        // CATEGORÍA
        // =========================================================
        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        // =========================================================
        // TALLAS
        // =========================================================
        if (dto.getTallas() != null) {

            // Eliminar las relaciones que ya no vienen
            producto.getProductoTallas().removeIf(productoTalla -> {
                boolean existe = dto.getTallas().stream()
                        .anyMatch(tallaDto -> tallaDto.getTallaId()
                                .equals(productoTalla.getTalla().getId()));

                if (!existe) {
                    productoTallaRepository.delete(productoTalla);
                }
                return !existe;
            });

            // Actualizar o agregar tallas
            for (ProductoTallaDto tallaDto : dto.getTallas()) {

                ProductoTalla productoTalla = producto.getProductoTallas().stream()
                        .filter(pt -> pt.getTalla().getId().equals(tallaDto.getTallaId()))
                        .findFirst()
                        .orElse(null);

                // Talla existente
                if (productoTalla != null) {
                    productoTalla.setStock(tallaDto.getStock());
                }
                // Talla nueva
                else {
                    Talla talla = tallaRepository.findById(tallaDto.getTallaId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Talla no encontrada: " + tallaDto.getTallaId()));

                    ProductoTalla nueva = new ProductoTalla();
                    nueva.setProducto(producto);
                    nueva.setTalla(talla);
                    nueva.setStock(tallaDto.getStock());

                    producto.getProductoTallas().add(nueva);
                    productoTallaRepository.save(nueva);
                }
            }
        }

        // =========================================================
        // IMÁGENES
        // =========================================================
        if (dto.getImagenes() != null) {

            // IDs de imágenes que deben permanecer
            List<Long> idsRecibidos = dto.getImagenes().stream()
                    .map(ImagenUpdateDto::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()); 

            // Eliminar imágenes que ya no vienen
            producto.getImagenes().removeIf(imagen -> {
                boolean debePermanecer = idsRecibidos.contains(imagen.getId());
                if (!debePermanecer) {

                    System.out.println("=================================");
                    System.out.println("ELIMINANDO IMAGEN");
                    System.out.println("ID DB: " + imagen.getId());
                    System.out.println("PUBLIC ID: " + imagen.getPublicId());
                    System.out.println("URL: " + imagen.getUrl());
                    System.out.println("=================================");

                    cloudinaryService.eliminarImagen(imagen.getPublicId());

                    imagenRepository.delete(imagen);

                    return true;
                }
                return false;
            });

            // Actualizar / agregar imágenes
            boolean principalEncontrada = false;

            for (ImagenUpdateDto imagenDto : dto.getImagenes()) {

                // Imagen existente
                if (imagenDto.getId() != null) {
                    Imagen imagen = producto.getImagenes().stream()
                            .filter(img -> img.getId().equals(imagenDto.getId()))
                            .findFirst()
                            .orElse(null);

                    if (imagen != null) {
                        imagen.setUrl(imagenDto.getUrl());
                        imagen.setPublicId(imagenDto.getPublicId());

                        boolean esPrincipal = Boolean.TRUE.equals(imagenDto.getEsPrincipal());
                        imagen.setEsPrincipal(esPrincipal);

                        if (esPrincipal) {
                            principalEncontrada = true;
                        }
                    }
                }
                // Imagen nueva
                else {
                    Imagen nueva = new Imagen();
                    nueva.setProducto(producto);
                    nueva.setUrl(imagenDto.getUrl());
                    nueva.setPublicId(imagenDto.getPublicId());

                    boolean esPrincipal = Boolean.TRUE.equals(imagenDto.getEsPrincipal());
                    nueva.setEsPrincipal(esPrincipal);

                    if (esPrincipal) {
                        principalEncontrada = true;
                    }

                    producto.getImagenes().add(nueva);
                }
            }

            // Garantizar una sola principal
            if (principalEncontrada) {
                boolean principalYaAsignada = false;

                for (Imagen imagen : producto.getImagenes()) {
                    if (imagen.isEsPrincipal()) {
                        if (!principalYaAsignada) {
                            principalYaAsignada = true;
                        } else {
                            imagen.setEsPrincipal(false);
                        }
                    }
                }
            } else if (!producto.getImagenes().isEmpty()) {
                // Si ninguna fue marcada como principal, hacemos principal la primera
                producto.getImagenes().iterator().next().setEsPrincipal(true);
            }
        }

        // =========================================================
        // GUARDAR
        // =========================================================
        productoRepository.save(producto);
        return convertirAProductData(id);
    }


    // =========================================================
    // DESACTIVAR PRODUCTO
    // =========================================================

    @Override
    @Transactional
    public void desactivarProducto(Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        producto.setActivo(false);

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public ProductData activarProducto(Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        producto.setActivo(true);

        productoRepository.save(producto);

        return convertirAProductData(id);
    }

    // =========================================================
    // CONVERTIR PRODUCTO A ProductData
    // =========================================================

    private ProductData convertirAProductData(Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        return convertirProducto(producto);
    }


    // =========================================================
    // CONVERTIR PRODUCTO -> DTO
    // =========================================================

    private ProductData convertirProducto(Producto producto) {

        ProductData dto = new ProductData();

        dto.setId(producto.getId());
        dto.setTitulo(producto.getTitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setAdicional(producto.getAdicional());
        dto.setPrecioUnidad(producto.getPrecioUnidad());
        dto.setPrecioTotal(producto.getPrecioTotal());
        dto.setActivo(producto.isActivo());


        // -----------------------------------------------------
        // CATEGORIA
        // -----------------------------------------------------

        if (producto.getCategoria() != null) {

            dto.setCategoriaId(
                producto.getCategoria().getId()
            );

            dto.setCategoria(
                producto.getCategoria().getNombre()
            );
        }


        // -----------------------------------------------------
        // IMAGENES
        // -----------------------------------------------------

        List<ImagenDto> imagenesDto =
                producto.getImagenes()
                        .stream()
                        .map(img -> {

                        	ImagenDto imgDto = new ImagenDto();

                        	imgDto.setId(img.getId());
                        	imgDto.setUrl(img.getUrl());
                        	imgDto.setPublicId(img.getPublicId());
                        	imgDto.setEsPrincipal(img.isEsPrincipal());
                           
                        	return imgDto;

                        })
                        .collect(Collectors.toList());

        dto.setImagenes(imagenesDto);


        // -----------------------------------------------------
        // TALLAS
        // -----------------------------------------------------

        List<TallaDto> tallasDto =
                producto.getProductoTallas()
                        .stream()
                        .map(pt -> {

                            TallaDto tallaDto = new TallaDto();

                            tallaDto.setId(
                                    pt.getTalla().getId()
                            );

                            tallaDto.setNombre(
                                    pt.getTalla().getNombre()
                            );

                            tallaDto.setStock(
                                    pt.getStock()
                            );

                            return tallaDto;

                        })
                        .collect(Collectors.toList());

        dto.setTallas(tallasDto);


        return dto;
    }
}