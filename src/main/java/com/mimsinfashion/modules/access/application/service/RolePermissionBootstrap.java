package com.mimsinfashion.modules.access.application.service;

import com.mimsinfashion.modules.access.domain.entity.Permission;
import com.mimsinfashion.modules.access.domain.entity.Role;
import com.mimsinfashion.modules.access.infrastructure.persistence.PermissionJpaRepository;
import com.mimsinfashion.modules.access.infrastructure.persistence.RoleJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RolePermissionBootstrap implements ApplicationRunner {

    private final RoleJpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;

    public RolePermissionBootstrap(
            RoleJpaRepository roleRepository,
            PermissionJpaRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, String> permissions = new LinkedHashMap<>();

        add(permissions, "DASHBOARD_VIEW", "View dashboard");
        add(permissions, "USER_VIEW", "View users");
        add(permissions, "USER_CREATE", "Create users");
        add(permissions, "USER_UPDATE", "Update users");
        add(permissions, "USER_DELETE", "Deactivate users");
        add(permissions, "ROLE_VIEW", "View roles");
        add(permissions, "ROLE_MANAGE", "Manage roles and permissions");

        add(permissions, "PRODUCT_VIEW", "View products");
        add(permissions, "PRODUCT_CREATE", "Create products");
        add(permissions, "PRODUCT_UPDATE", "Update products");
        add(permissions, "PRODUCT_DELETE", "Archive products");
        add(permissions, "CATEGORY_MANAGE", "Manage categories");
        add(permissions, "BRAND_MANAGE", "Manage brands");
        add(permissions, "MEDIA_MANAGE", "Manage product media");

        add(permissions, "INVENTORY_VIEW", "View inventory");
        add(permissions, "INVENTORY_MANAGE", "Manage inventory");
        add(permissions, "STOCK_ADJUST", "Adjust stock");
        add(permissions, "PROCUREMENT_MANAGE", "Manage procurement");

        add(permissions, "POS_ACCESS", "Access point of sale");
        add(permissions, "SALES_VIEW", "View sales");
        add(permissions, "SALES_CREATE", "Create sales");
        add(permissions, "SALES_VOID", "Void sales");

        add(permissions, "CUSTOMER_VIEW", "View customers");
        add(permissions, "CUSTOMER_MANAGE", "Manage customers");
        add(permissions, "ORDER_VIEW", "View orders");
        add(permissions, "ORDER_MANAGE", "Manage orders");
        add(permissions, "PAYMENT_MANAGE", "Manage payments");
        add(permissions, "RETURN_MANAGE", "Manage returns");

        add(permissions, "FINANCE_VIEW", "View financial information");
        add(permissions, "FINANCE_MANAGE", "Manage financial records");
        add(permissions, "REPORT_VIEW", "View reports");
        add(permissions, "REPORT_EXPORT", "Export reports");

        add(permissions, "CONTENT_MANAGE", "Manage storefront content");
        add(permissions, "PROMOTION_MANAGE", "Manage promotions");
        add(permissions, "SHIPPING_MANAGE", "Manage shipping");
        add(permissions, "SETTINGS_MANAGE", "Manage system settings");
        add(permissions, "AUDIT_VIEW", "View audit logs");

        Map<String, String> roles = new LinkedHashMap<>();
        roles.put("SUPER_ADMIN", "Super Administrator");
        roles.put("ADMIN", "Administrator");
        roles.put("MANAGER", "Manager");
        roles.put("STAFF", "Staff");
        roles.put("CASHIER", "Cashier");
        roles.put("CUSTOMER", "Customer");

        Map<String, Set<String>> assignments = new LinkedHashMap<>();

        assignments.put("ADMIN", Set.of(
            "DASHBOARD_VIEW", "USER_VIEW", "USER_CREATE", "USER_UPDATE",
            "ROLE_VIEW", "PRODUCT_VIEW", "PRODUCT_CREATE", "PRODUCT_UPDATE",
            "PRODUCT_DELETE", "CATEGORY_MANAGE", "BRAND_MANAGE", "MEDIA_MANAGE",
            "INVENTORY_VIEW", "INVENTORY_MANAGE", "STOCK_ADJUST",
            "PROCUREMENT_MANAGE", "POS_ACCESS", "SALES_VIEW", "SALES_CREATE",
            "SALES_VOID", "CUSTOMER_VIEW", "CUSTOMER_MANAGE", "ORDER_VIEW",
            "ORDER_MANAGE", "PAYMENT_MANAGE", "RETURN_MANAGE", "FINANCE_VIEW",
            "FINANCE_MANAGE", "REPORT_VIEW", "REPORT_EXPORT", "CONTENT_MANAGE",
            "PROMOTION_MANAGE", "SHIPPING_MANAGE", "SETTINGS_MANAGE", "AUDIT_VIEW"
        ));

        assignments.put("MANAGER", Set.of(
            "DASHBOARD_VIEW", "USER_VIEW", "PRODUCT_VIEW", "PRODUCT_CREATE",
            "PRODUCT_UPDATE", "CATEGORY_MANAGE", "BRAND_MANAGE", "MEDIA_MANAGE",
            "INVENTORY_VIEW", "INVENTORY_MANAGE", "STOCK_ADJUST",
            "PROCUREMENT_MANAGE", "POS_ACCESS", "SALES_VIEW", "SALES_CREATE",
            "CUSTOMER_VIEW", "CUSTOMER_MANAGE", "ORDER_VIEW", "ORDER_MANAGE",
            "RETURN_MANAGE", "FINANCE_VIEW", "REPORT_VIEW", "REPORT_EXPORT"
        ));

        assignments.put("STAFF", Set.of(
            "DASHBOARD_VIEW", "PRODUCT_VIEW", "INVENTORY_VIEW",
            "CUSTOMER_VIEW", "ORDER_VIEW"
        ));

        assignments.put("CASHIER", Set.of(
            "DASHBOARD_VIEW", "PRODUCT_VIEW", "POS_ACCESS",
            "SALES_VIEW", "SALES_CREATE", "CUSTOMER_VIEW",
            "CUSTOMER_MANAGE", "ORDER_VIEW"
        ));

        assignments.put("CUSTOMER", Set.of(
            "PRODUCT_VIEW", "ORDER_VIEW"
        ));

        Map<String, Permission> savedPermissions = new LinkedHashMap<>();

        permissions.forEach((code, name) -> {
            Permission permission = permissionRepository.findByCode(code)
                .orElseGet(() -> {
                    return permissionRepository.save(Permission.create(code, name));
                });
            savedPermissions.put(code, permission);
        });

        roles.forEach((code, name) -> {
            Role role = roleRepository.findByCode(code)
                .orElseGet(() -> {
                    return Role.create(code, name);
                });

            if ("SUPER_ADMIN".equals(code)) {
                role.getPermissions().clear();
                role.getPermissions().addAll(savedPermissions.values());
            } else {
                Set<String> allowed = assignments.getOrDefault(code, Set.of());
                allowed.forEach(permissionCode ->
                    role.getPermissions().add(savedPermissions.get(permissionCode))
                );
            }

            roleRepository.save(role);
        });
    }

    private void add(Map<String, String> permissions, String code, String name) {
        permissions.put(code, name);
    }
}
