package com.shoppinglist.shoppinglist.shopping_list;

import com.shoppinglist.shoppinglist.shopping_list_item.ShoppingListItemEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shopping_list")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    @Column(name = "name")
    private String name;

    @Size(max = 10, message = "List code cannot exceed 10 characters")
    @Column(name = "list_code")
    private String listCode;

    @Column(name = "owner_id", unique = true, nullable = false)
    private UUID ownerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<ShoppingListItemEntity> shoppingItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
