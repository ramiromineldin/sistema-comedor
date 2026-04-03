package ar.uba.fi.ingsoft1.product_example.ingredients;

import ar.uba.fi.ingsoft1.product_example.products.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public IngredientDTO createIngredient(IngredientCreateDTO dto) {
        Ingredient ingredient = dto.asIngrdient();
        return IngredientDTO.fromIngredient(ingredientRepository.save(ingredient));
    }

    public IngredientDTO getIngredientbyId(long id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        return IngredientDTO.fromIngredient(ingredient);
    }

    public Ingredient getIngredient (long id){
        return ingredientRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
    }

    public Ingredient increment(Long ingredientId, Integer unit) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        ingredient.incrementStock(unit);
        return ingredientRepository.save(ingredient);
    }

    public Ingredient decrement(Long ingredientId, Integer unit) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        ingredient.decrementStock(unit);
        return ingredientRepository.save(ingredient);
    }

    public java.util.List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
            .map(IngredientDTO::fromIngredient)
            .collect(java.util.stream.Collectors.toList());
    }
}
