package com.smartstock.backend.user;

import com.smartstock.backend.category.Category;
import com.smartstock.backend.category.CategoryRepository;
import com.smartstock.backend.product.Product;
import com.smartstock.backend.product.ProductRepository;
import com.smartstock.backend.inventory.InventoryTransaction;
import com.smartstock.backend.inventory.InventoryTransactionRepository;
import com.smartstock.backend.inventory.InventoryTransactionType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(2)
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           InventoryTransactionRepository transactionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        createDefaultUsers();
        createSampleCategories();
        createSampleProducts();
        createSampleInventoryTransactions();
    }

    private void createDefaultUsers() {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));

        createUserIfMissing("admin", "admin123", Set.of(adminRole));
        createUserIfMissing("user", "user123", Set.of(userRole));
    }

    private void createUserIfMissing(String username, String password, Set<Role> roles) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = new User(username, passwordEncoder.encode(password), LocalDateTime.now(), LocalDateTime.now());
        user.setRoles(roles);
        userRepository.save(user);
    }

    private void createSampleCategories() {
        createCategoryIfMissing("Electronics", "Devices, accessories, and electronic equipment.");
        createCategoryIfMissing("Office Supplies", "Stationery and workplace essentials.");
        createCategoryIfMissing("Furniture", "Office furniture and workspace fixtures.");
    }

    private void createCategoryIfMissing(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            return;
        }

        Category category = new Category(name, description, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(5));
        categoryRepository.save(category);
    }

    private void createSampleProducts() {
        Category electronics = categoryRepository.findByName("Electronics").orElseThrow();
        Category officeSupplies = categoryRepository.findByName("Office Supplies").orElseThrow();
        Category furniture = categoryRepository.findByName("Furniture").orElseThrow();

        createProductIfMissing("Wireless Keyboard", "KEY-001", "Mechanical wireless keyboard with backlight.", BigDecimal.valueOf(49.99), 12, 5, electronics, LocalDateTime.now().minusDays(4));
        createProductIfMissing("Ergonomic Mouse", "MOU-001", "Comfort mouse designed for all-day use.", BigDecimal.valueOf(29.99), 20, 5, electronics, LocalDateTime.now().minusDays(4));
        createProductIfMissing("Standing Desk", "DESK-001", "Height-adjustable desk for ergonomic workstations.", BigDecimal.valueOf(399.99), 8, 3, furniture, LocalDateTime.now().minusDays(4));
        createProductIfMissing("Notebook Paper", "NOTE-001", "500-sheet notebook paper pack for notes and printing.", BigDecimal.valueOf(5.49), 55, 15, officeSupplies, LocalDateTime.now().minusDays(4));
        createProductIfMissing("24-inch Monitor", "MON-001", "Full HD 24-inch monitor with slim bezel.", BigDecimal.valueOf(179.99), 5, 2, electronics, LocalDateTime.now().minusDays(4));
    }

    private void createProductIfMissing(String name,
                                        String sku,
                                        String description,
                                        BigDecimal price,
                                        int stock,
                                        int lowStockThreshold,
                                        Category category,
                                        LocalDateTime timestamp) {
        if (productRepository.findBySku(sku).isPresent()) {
            return;
        }

        Product product = new Product(
                name,
                sku,
                description,
                price,
                stock,
                lowStockThreshold,
                category,
                timestamp,
                timestamp
        );
        productRepository.save(product);
    }

    private void createSampleInventoryTransactions() {
        if (transactionRepository.count() > 0) {
            return;
        }

        Product keyboard = productRepository.findBySku("KEY-001").orElseThrow();
        Product desk = productRepository.findBySku("DESK-001").orElseThrow();
        Product notepad = productRepository.findBySku("NOTE-001").orElseThrow();
        Product monitor = productRepository.findBySku("MON-001").orElseThrow();

        saveTransactionIfMissing(keyboard, InventoryTransactionType.ADD, 20, "admin", "Initial stock load", LocalDateTime.now().minusDays(10));
        saveTransactionIfMissing(keyboard, InventoryTransactionType.REMOVE, 3, "admin", "Order shipment", LocalDateTime.now().minusDays(5));
        saveTransactionIfMissing(desk, InventoryTransactionType.ADD, 10, "admin", "New furniture shipment", LocalDateTime.now().minusDays(7));
        saveTransactionIfMissing(notepad, InventoryTransactionType.REMOVE, 2, "user", "Office use", LocalDateTime.now().minusDays(1));
        saveTransactionIfMissing(monitor, InventoryTransactionType.CORRECTION, 1, "admin", "Stock correction after audit", LocalDateTime.now().minusDays(2));
    }

    private void saveTransactionIfMissing(Product product,
                                          InventoryTransactionType type,
                                          int quantity,
                                          String changedBy,
                                          String note,
                                          LocalDateTime timestamp) {
        InventoryTransaction transaction = new InventoryTransaction(product, type, quantity, timestamp, changedBy, note);
        transactionRepository.save(transaction);
    }
}
