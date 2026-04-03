package ar.uba.fi.ingsoft1.product_example.config.security;

import ar.uba.fi.ingsoft1.product_example.alimentos.Alimento;
import ar.uba.fi.ingsoft1.product_example.ingredients.Ingredient;
import ar.uba.fi.ingsoft1.product_example.ingredients.IngredientRepository;
import ar.uba.fi.ingsoft1.product_example.menu.MenuComedor;
import ar.uba.fi.ingsoft1.product_example.menu.MenuComedorRepository;
import ar.uba.fi.ingsoft1.product_example.products.Product;
import ar.uba.fi.ingsoft1.product_example.products.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Component
public class MenuInitializer {

    private final MenuInitializerService menuInitializerService;

    public MenuInitializer(MenuInitializerService menuInitializerService) {
        this.menuInitializerService = menuInitializerService;
    }

    @Bean
    public CommandLineRunner initMenuDeHoy() {
        return args -> {
            menuInitializerService.inicializarMenu();
        };
    }
}

@Service
class MenuInitializerService {

    private final MenuComedorRepository menuRepo;
    private final ProductRepository productRepo;
    private final IngredientRepository ingredientRepo;

    public MenuInitializerService(MenuComedorRepository menuRepo, 
                                  ProductRepository productRepo, 
                                  IngredientRepository ingredientRepo) {
        this.menuRepo = menuRepo;
        this.productRepo = productRepo;
        this.ingredientRepo = ingredientRepo;
    }

    @Transactional
    public void inicializarMenu() {
            ZoneId BUE = ZoneId.of("America/Argentina/Buenos_Aires");
            LocalDate hoy = LocalDate.now(BUE);

            // ===== helpers =====

            // Trae (o crea) un ingrediente global por nombre (compartido entre productos).
            java.util.function.BiFunction<String, Integer, Ingredient> ensureIngredient =
                    (name, stock) -> ingredientRepo.findByNameIgnoreCase(name)
                            .map(ing -> {
                                // opcional: si querés "subir" stock mínimo en bootstrap

                                return ing;
                            })
                            .orElseGet(() -> {
                                Ingredient ing = new Ingredient(
                                        name,           // nombre
                                        name,           // descripción simple
                                        BigDecimal.ZERO,// precio simbólico para ingredientes
                                        stock
                                );
                                return ingredientRepo.save(ing); // persistimos primero
                            });

            // Trae (o crea) un producto por nombre
            java.util.function.BiFunction<String, String, Product> ensureProduct =
                    (name, desc) -> productRepo.findAll().stream()
                            .filter(p -> p.getName().equalsIgnoreCase(name))
                            .findFirst()
                            .orElseGet(() -> productRepo.save(new Product(name, desc, BigDecimal.ZERO, 0)));

            // Vincula un ingrediente EXISTENTE al producto, evitando duplicates en la colección
            java.util.function.BiConsumer<Product, Ingredient> linkIngredient =
                    (product, ing) -> {
                        boolean yaEsta = product.getAllIngredients().stream()
                                .anyMatch(i -> i.getId().equals(ing.getId()));
                        if (!yaEsta) {
                            // si tu Product tiene addElemento(ElementoMenu), usalo:
                            product.addElemento(ing);
                        }
                    };

            // Guarda el producto con su gráfico (join table). No creamos ingredientes acá,
            // porque son compartidos y ya están persistidos.
            java.util.function.Consumer<Product> persistProduct =
                    (product) -> productRepo.save(product);

            // ===== productos =====

            // Hamburguesa completa
            Product hamburguesa = ensureProduct.apply(
                    "Hamburguesa completa",
                    "Medallón, pan, queso, lechuga, tomate y huevo"
            );
            hamburguesa.setPrice(BigDecimal.valueOf(3500));
            hamburguesa.setStock(50);
            // ingredientes compartidos
            List.of(
                    ensureIngredient.apply("Pan de hamburguesa", 100),
                    ensureIngredient.apply("Medallón de carne", 80),
                    ensureIngredient.apply("Queso", 100),
                    ensureIngredient.apply("Lechuga", 100),
                    ensureIngredient.apply("Tomate", 100),
                    ensureIngredient.apply("Huevo", 100)
            ).forEach(ing -> linkIngredient.accept(hamburguesa, ing));
            persistProduct.accept(hamburguesa);

            // Milanesa con papas fritas
            Product milaConPapas = ensureProduct.apply(
                    "Milanesa con papas fritas",
                    "Milanesa de carne con guarnición de papas fritas"
            );
            milaConPapas.setPrice(BigDecimal.valueOf(4200));
            milaConPapas.setStock(40);
            List.of(
                    ensureIngredient.apply("Milanesa", 60),
                    ensureIngredient.apply("Papas fritas", 120),
                    ensureIngredient.apply("Aceite", 50),
                    ensureIngredient.apply("Sal", 200)
            ).forEach(ing -> linkIngredient.accept(milaConPapas, ing));
            persistProduct.accept(milaConPapas);

            // Pancho
            Product pancho = ensureProduct.apply(
                    "Pancho",
                    "Pan para pancho y salchicha"
            );
            pancho.setPrice(BigDecimal.valueOf(1800));
            pancho.setStock(80);
            List.of(
                    ensureIngredient.apply("Pan para pancho", 120),
                    ensureIngredient.apply("Salchicha", 120)
            ).forEach(ing -> linkIngredient.accept(pancho, ing));
            persistProduct.accept(pancho);

            // Sándwich de milanesa completo
            Product sangucheMila = ensureProduct.apply(
                    "Sándwich de milanesa completo",
                    "Pan, milanesa, lechuga, tomate, queso, huevo, mayonesa"
            );
            sangucheMila.setPrice(BigDecimal.valueOf(3900));
            sangucheMila.setStock(45);
            List.of(
                    ensureIngredient.apply("Pan francés", 100),
                    ensureIngredient.apply("Milanesa", 60),
                    ensureIngredient.apply("Lechuga", 100),
                    ensureIngredient.apply("Tomate", 100),
                    ensureIngredient.apply("Queso", 80),
                    ensureIngredient.apply("Huevo", 100),
                    ensureIngredient.apply("Mayonesa", 50)
            ).forEach(ing -> linkIngredient.accept(sangucheMila, ing));
            persistProduct.accept(sangucheMila);

            // Pizza Muzzarella
            Product pizza = ensureProduct.apply(
                    "Pizza Muzzarella",
                    "Pizza grande (8 porciones) con masa, salsa de tomate, muzzarella y aceitunas"
            );
            pizza.setPrice(BigDecimal.valueOf(6000));
            pizza.setStock(30);
            List.of(
                    ensureIngredient.apply("Masa de pizza", 50),
                    ensureIngredient.apply("Salsa de tomate", 80),
                    ensureIngredient.apply("Muzzarella", 60),
                    ensureIngredient.apply("Aceitunas", 40)
            ).forEach(ing -> linkIngredient.accept(pizza, ing));
            persistProduct.accept(pizza);

            // Milanesa Napolitana
            Product milaNapolitana = ensureProduct.apply(
                    "Milanesa Napolitana",
                    "Milanesa de carne con jamón, queso, salsa de tomate y papas fritas"
            );
            milaNapolitana.setPrice(BigDecimal.valueOf(5500));
            milaNapolitana.setStock(35);
            List.of(
                    ensureIngredient.apply("Milanesa", 60),
                    ensureIngredient.apply("Jamón", 50),
                    ensureIngredient.apply("Queso", 80),
                    ensureIngredient.apply("Salsa de tomate", 80),
                    ensureIngredient.apply("Papas fritas", 120)
            ).forEach(ing -> linkIngredient.accept(milaNapolitana, ing));
            persistProduct.accept(milaNapolitana);

            // Empanadas
            Product empanadas = ensureProduct.apply(
                    "Empanadas x 6",
                    "Media docena de empanadas caseras de carne, pollo o jamón y queso"
            );
            empanadas.setPrice(BigDecimal.valueOf(3600));
            empanadas.setStock(60);
            List.of(
                    ensureIngredient.apply("Masa de empanada", 120),
                    ensureIngredient.apply("Relleno variado", 80)
            ).forEach(ing -> linkIngredient.accept(empanadas, ing));
            persistProduct.accept(empanadas);

            // Ensaladas para la promo de los martes
            Product ensaladaCesar = ensureProduct.apply(
                    "Ensalada César",
                    "Ensalada con lechuga, pollo grillado, crutones, queso parmesano y salsa césar"
            );
            ensaladaCesar.setPrice(BigDecimal.valueOf(3800));
            ensaladaCesar.setStock(40);
            List.of(
                    ensureIngredient.apply("Lechuga", 100),
                    ensureIngredient.apply("Pollo grillado", 50),
                    ensureIngredient.apply("Crutones", 60),
                    ensureIngredient.apply("Queso parmesano", 40),
                    ensureIngredient.apply("Salsa César", 50)
            ).forEach(ing -> linkIngredient.accept(ensaladaCesar, ing));
            persistProduct.accept(ensaladaCesar);

            Product ensaladaCaprese = ensureProduct.apply(
                    "Ensalada Caprese",
                    "Ensalada con tomate, muzzarella, albahaca fresca y aceite de oliva"
            );
            ensaladaCaprese.setPrice(BigDecimal.valueOf(3800));
            ensaladaCaprese.setStock(40);
            List.of(
                    ensureIngredient.apply("Tomate", 100),
                    ensureIngredient.apply("Muzzarella", 60),
                    ensureIngredient.apply("Albahaca", 30),
                    ensureIngredient.apply("Aceite de oliva", 40)
            ).forEach(ing -> linkIngredient.accept(ensaladaCaprese, ing));
            persistProduct.accept(ensaladaCaprese);

            Product ensaladaMixta = ensureProduct.apply(
                    "Ensalada Mixta",
                    "Ensalada con lechuga, tomate, zanahoria, huevo duro y aceite de oliva"
            );
            ensaladaMixta.setPrice(BigDecimal.valueOf(3800));
            ensaladaMixta.setStock(50);
            List.of(
                    ensureIngredient.apply("Lechuga", 100),
                    ensureIngredient.apply("Tomate", 100),
                    ensureIngredient.apply("Zanahoria", 80),
                    ensureIngredient.apply("Huevo", 100),
                    ensureIngredient.apply("Aceite de oliva", 40)
            ).forEach(ing -> linkIngredient.accept(ensaladaMixta, ing));
            persistProduct.accept(ensaladaMixta);

            Product ensaladaRucula = ensureProduct.apply(
                    "Ensalada de Rúcula y Parmesano",
                    "Ensalada con rúcula, queso parmesano, tomates cherry, aceite de oliva y aceto balsámico"
            );
            ensaladaRucula.setPrice(BigDecimal.valueOf(4500));
            ensaladaRucula.setStock(35);
            List.of(
                    ensureIngredient.apply("Rúcula", 60),
                    ensureIngredient.apply("Queso parmesano", 40),
                    ensureIngredient.apply("Tomates cherry", 50),
                    ensureIngredient.apply("Aceite de oliva", 40),
                    ensureIngredient.apply("Aceto balsámico", 30)
            ).forEach(ing -> linkIngredient.accept(ensaladaRucula, ing));
            persistProduct.accept(ensaladaRucula);

            // Alfajores para la promo 3x2
            Product alfajorJorgito = ensureProduct.apply(
                    "Alfajor Jorgito",
                    "Alfajor argentino"
            );
            alfajorJorgito.setPrice(BigDecimal.valueOf(1200));
            alfajorJorgito.setStock(100);
            persistProduct.accept(alfajorJorgito);

            Product alfajorTerrabusi = ensureProduct.apply(
                    "Alfajor Terrabusi",
                    "Alfajor argentino"
            );
            alfajorTerrabusi.setPrice(BigDecimal.valueOf(1200));
            alfajorTerrabusi.setStock(100);
            persistProduct.accept(alfajorTerrabusi);

            Product alfajorHavanna = ensureProduct.apply(
                    "Alfajor Havanna",
                    "Alfajor argentino"
            );
            alfajorHavanna.setPrice(BigDecimal.valueOf(1200));
            alfajorHavanna.setStock(80);
            persistProduct.accept(alfajorHavanna);

            // Bebidas
            Product cocaCola = ensureProduct.apply(
                    "Coca Cola 1.5L",
                    "Gaseosa Coca Cola 1.5 litros"
            );
            cocaCola.setPrice(BigDecimal.valueOf(1800));
            cocaCola.setStock(100);
            persistProduct.accept(cocaCola);

            Product agua = ensureProduct.apply(
                    "Agua Mineral",
                    "Agua mineral sin gas 500ml"
            );
            agua.setPrice(BigDecimal.valueOf(800));
            agua.setStock(150);
            persistProduct.accept(agua);

            Product cafe = ensureProduct.apply(
                    "Café",
                    "Café expreso con azúcar"
            );
            cafe.setPrice(BigDecimal.valueOf(800));
            cafe.setStock(200);
            List.of(
                    ensureIngredient.apply("Café molido", 150),
                    ensureIngredient.apply("Azúcar", 200)
            ).forEach(ing -> linkIngredient.accept(cafe, ing));
            persistProduct.accept(cafe);

            // ===== menú de hoy =====
            MenuComedor menuHoy = menuRepo.findFirstByActivoTrueAndFechaOrderByIdAsc(hoy)
                    .orElseGet(() -> menuRepo.save(new MenuComedor(
                            "Menú del día", "Clásicos de comedor", hoy
                    )));
            menuHoy.setActivo(true);

            List<Alimento> actuales = menuHoy.getTodosLosAlimentos();
            java.util.function.Consumer<Product> addIfMissing = (p) -> {
                boolean yaEsta = actuales.stream()
                        .filter(a -> a instanceof Product)
                        .map(a -> ((Product) a).getId())
                        .anyMatch(id -> id.equals(p.getId()));
                if (!yaEsta) menuHoy.addAlimento(p);
            };

            addIfMissing.accept(hamburguesa);
            addIfMissing.accept(milaConPapas);
            addIfMissing.accept(pancho);
            addIfMissing.accept(sangucheMila);
            addIfMissing.accept(pizza);
            addIfMissing.accept(milaNapolitana);
            addIfMissing.accept(empanadas);
            addIfMissing.accept(ensaladaCesar);
            addIfMissing.accept(ensaladaCaprese);
            addIfMissing.accept(ensaladaMixta);
            addIfMissing.accept(ensaladaRucula);
            addIfMissing.accept(alfajorJorgito);
            addIfMissing.accept(alfajorTerrabusi);
            addIfMissing.accept(alfajorHavanna);
            addIfMissing.accept(cocaCola);
            addIfMissing.accept(agua);
            addIfMissing.accept(cafe);

            menuRepo.save(menuHoy);

            System.out.println("✅ Menú de hoy inicializado SIN duplicar ingredientes compartidos.");
    }
}