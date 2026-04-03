package ar.uba.fi.ingsoft1.product_example.user;

import ar.uba.fi.ingsoft1.product_example.common.exception.InvalidPasswordException;
import ar.uba.fi.ingsoft1.product_example.common.exception.UserAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Tag(name = "3 - Admin Users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserRestController {

    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserRestController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/kitchen-staff")
    @Operation(summary = "Get all kitchen staff users")
    public ResponseEntity<List<UserDTO>> getKitchenStaff() {
        List<UserDTO> kitchenStaff = adminUserService.getKitchenStaff();
        return ResponseEntity.ok(kitchenStaff);
    }

    @PostMapping("/kitchen-staff")
    @Operation(summary = "Create a new kitchen staff user")
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    public ResponseEntity<String> createKitchenStaff(
            @Valid @NonNull @RequestBody UserCreateDTO data
    ) {
        try {
            adminUserService.createKitchenStaff(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/kitchen-staff/{id}")
    @Operation(summary = "Update a kitchen staff user")
    public ResponseEntity<String> updateKitchenStaff(
            @PathVariable Long id,
            @Valid @NonNull @RequestBody UserUpdateDTO data
    ) {
        try {
            adminUserService.updateKitchenStaff(id, data);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/kitchen-staff/{id}")
    @Operation(summary = "Delete a kitchen staff user")
    public ResponseEntity<String> deleteKitchenStaff(@PathVariable Long id) {
        try {
            adminUserService.deleteKitchenStaff(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/kitchen-staff/{id}")
    @Operation(summary = "Get kitchen staff user by ID")
    public ResponseEntity<UserDTO> getKitchenStaffById(@PathVariable Long id) {
        try {
            UserDTO user = adminUserService.getKitchenStaffById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}