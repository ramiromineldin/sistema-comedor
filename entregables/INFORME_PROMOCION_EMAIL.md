# Informe: Implementación de Promoción "10% si tenés mail de FIUBA"

## Resumen ejecutivo

Esta promoción es factible de implementar. La complejidad adicional viene de la necesidad de obtener información del usuario autenticado (email) para mostrar el descuento en el preview del carrito.

## Comparación con TIME_RESTRICTED_BUY_X_PAY_Y

### Similitudes
- Ambas requieren agregar campos al modelo `Promotion`
- Ambas requieren agregar campos al `PromotionCreateDTO`
- Ambas necesitan lógica de validación
- Ambas necesitan actualizar el formulario de administración en el frontend

### Diferencia clave
**TIME_RESTRICTED_BUY_X_PAY_Y** usa información de contexto global (hora del sistema):
```java
LocalTime now = LocalTime.now(); // Disponible en cualquier momento
```

**EMAIL_DOMAIN_DISCOUNT** necesita información del usuario autenticado:
```java
String userEmail = // ¿De dónde viene?
```

Esta diferencia impacta en cómo se muestra el descuento en el carrito.

## Funcionamiento actual del carrito

Es importante entender cómo funciona el carrito para diseñar la solución:

1. **Al agregar productos**: NO hay request al backend, todo se maneja en estado local (React useState)
2. **Cálculo de descuentos**: Se hace localmente en el frontend usando la información de promociones obtenida previamente
3. **Confirmación del pedido**: Recién aquí se hace una request al backend con todos los items

**Descuento por monto mínimo (actual):**
- Frontend calcula si el total alcanza el mínimo
- Muestra el descuento en el preview del carrito
- Backend valida y aplica al confirmar

**Descuento por email (propuesto):**
- Frontend necesita saber el email del usuario
- Calcula si el email termina en `@fi.uba.ar`
- Muestra el descuento en el preview del carrito
- Backend valida y aplica al confirmar

## Solución propuesta

### Paso 1: Crear endpoint para obtener usuario actual

**Backend: Agregar endpoint GET /users/me**

```java
@RestController
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // El email del JWT
        
        // Opcionalmente buscar más datos del usuario en la BD
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getEmail(), user.getRole()));
    }
}
```

**Frontend: Consumir el endpoint al cargar la página**

```typescript
// En StudentMenu.tsx o donde se necesite el email
const { data: currentUser } = useQuery({
    queryKey: ["currentUser"],
    queryFn: async () => {
        const token = await getAccessToken();
        const response = await fetch(buildApiUrl("/users/me"), {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("Error al obtener usuario");
        return response.json(); // { id: 1, email: "usuario@fi.uba.ar", role: "STUDENT" }
    }
});
```

**Por qué funciona:**
- El JWT ya está en el header `Authorization`
- Spring Security lo valida automáticamente
- `SecurityContextHolder` extrae el email del JWT verificado
- Solo se hace una request al cargar la página
- El frontend usa el email para calcular el descuento en el carrito (preview local)
- Al confirmar el pedido, el backend re-valida con el JWT real (seguridad)

### Paso 2: Agregar campos al modelo

```java
// En Promotion.java
@Column(name = "email_domain")
private String emailDomain; // "@fi.uba.ar"
```

```java
// En PromotionCreateDTO.java
public record PromotionCreateDTO(
    // ... otros campos
    String emailDomain
) {}
```

### Paso 3: Agregar nuevo tipo de promoción

```java
// En PromotionType.java
public enum PromotionType {
    PERCENTAGE_DISCOUNT,
    AMOUNT_DISCOUNT,
    BUY_X_PAY_Y,
    RECURRING_DAY_DISCOUNT,
    TIME_RESTRICTED_BUY_X_PAY_Y,
    EMAIL_DOMAIN_DISCOUNT  // Nuevo
}
```

### Paso 4: Lógica de descuento en el carrito (frontend)

```typescript
// En CarritoFloating.tsx
const calcularDescuentoPorEmail = () => {
    const total = calcularTotal();
    
    // Buscar promoción de email activa
    const emailPromo = promotions.find(p => 
        p.active && 
        p.type === 'EMAIL_DOMAIN_DISCOUNT' &&
        userEmail?.endsWith(p.emailDomain)
    );
    
    if (emailPromo) {
        const descuento = total * (emailPromo.discountPercentage / 100);
        return {
            promo: emailPromo,
            descuento,
            totalConDescuento: total - descuento
        };
    }
    
    return null;
};
```

### Paso 5: Validación en el backend

```java
// En PedidoService.java
public PedidoResponse crearPedido(CrearPedidoRequest request) {
    // Extraer email del JWT
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = auth.getName(); // o auth.getPrincipal()
    
    // Calcular total
    BigDecimal total = calcularTotalItems(request.getDetalles());
    
    // Buscar y aplicar promoción de email
    Promotion emailPromo = promotionRepository.findActiveByType(EMAIL_DOMAIN_DISCOUNT)
        .stream()
        .filter(p -> userEmail.endsWith(p.getEmailDomain()))
        .findFirst()
        .orElse(null);
    
    if (emailPromo != null) {
        BigDecimal descuento = total.multiply(emailPromo.getDiscountPercentage())
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        total = total.subtract(descuento);
    }
    
    // Crear pedido...
}
```

### Paso 6: Formulario de administración

```tsx
// En AdminPromotions.tsx
{formData.type === 'EMAIL_DOMAIN_DISCOUNT' && (
    <>
        <input 
            type="text" 
            name="emailDomain" 
            placeholder="@fi.uba.ar"
            required 
        />
        <input 
            type="number" 
            name="discountPercentage" 
            placeholder="10"
            required 
        />
    </>
)}
```

## Archivos a modificar

### Backend (5 archivos)
1. `PromotionType.java` - Agregar enum EMAIL_DOMAIN_DISCOUNT
2. `Promotion.java` - Agregar campo emailDomain
3. `PromotionCreateDTO.java` - Agregar campo emailDomain
4. `PedidoService.java` - Lógica de aplicación del descuento
5. `UserController.java` - Endpoint /users/me (si no existe)

### Frontend (3 archivos)
1. `CarritoFloating.tsx` - Cálculo del descuento por email
2. `AdminPromotions.tsx` - Campos en formulario
3. `StudentMenu.tsx` - Query para obtener email del usuario

## Por qué requiere trabajo adicional

| Aspecto | TIME_RESTRICTED | EMAIL_DOMAIN |
|---------|----------------|--------------|
| Agregar campos al modelo | ✓ | ✓ |
| Crear nueva estrategia/lógica | ✓ | ✓ |
| Actualizar formulario admin | ✓ | ✓ |
| **Obtener email del usuario** | ✗ | ✓ (nuevo endpoint) |
| **Modificar cálculo en carrito** | ✗ | ✓ (agregar lógica de email) |
| **Modificar PedidoService** | ✗ | ✓ (extraer email del JWT) |

La diferencia viene principalmente de:
1. Crear el endpoint `/users/me` para obtener el email
2. Integrar la obtención del email en el frontend
3. Modificar el cálculo del carrito para considerar email
4. Testing de seguridad (verificar que no se puede falsificar)

## Consideraciones de seguridad

1. **El frontend muestra un preview**: El descuento se calcula localmente para UX
2. **El backend siempre valida**: Al confirmar el pedido, extrae el email del JWT real y valida
3. **No se puede falsificar**: Aunque el usuario manipule el frontend, el JWT tiene firma criptográfica que no puede falsificar
4. **Doble validación**: Frontend para UX, backend para seguridad

## Conclusión

La promoción de email es **perfectamente factible** de implementar. No requiere cambios arquitecturales profundos, solo:
- Agregar un campo nuevo al modelo
- Obtener el email del usuario autenticado
- Modificar el cálculo del carrito para considerar el email
- Validar en el backend al confirmar el pedido

La complejidad adicional respecto a TIME_RESTRICTED viene de necesitar información del usuario (que requiere hacer una request al endpoint `/users/me`), mientras que TIME_RESTRICTED usa información global del sistema (hora actual) que está siempre disponible.

