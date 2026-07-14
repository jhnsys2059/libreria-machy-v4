package com.machy.product.config;

import com.machy.product.entity.Category;
import com.machy.product.entity.Supplier;
import com.machy.product.repository.CategoryRepository;
import com.machy.product.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public DatabaseSeeder(CategoryRepository categoryRepository, SupplierRepository supplierRepository) {
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            seedCategories();
            seedSuppliers();
        }
        log.info("Product Service - Database seeding completed");
    }

    private void seedCategories() {
        String[][] cats = {
            {"Utiles escolares", "Cuadernos, lapiceros, lapices, colores y mas"},
            {"Papeleria", "Hojas, sobres, folders, resmas y articulos de oficina"},
            {"Libros", "Textos escolares, novelas y material de lectura"},
            {"Manualidades", "Tijeras, goma, escarcha, cartulinas y mas"},
            {"Juguetes", "Juguetes educativos y recreativos"},
            {"Otros", "Productos varios"}
        };
        for (String[] c : cats) {
            Category cat = new Category();
            cat.setNombre(c[0]);
            cat.setDescripcion(c[1]);
            cat.setActivo(true);
            categoryRepository.save(cat);
        }
        log.info("{} categories created", cats.length);
    }

    private void seedSuppliers() {
        String[][] provs = {
            {"Distribuidora ABC", "20123456789", "Carlos Lopez", "999888777", "abc@proveedores.com", "Av. Principal 123"},
            {"Papeles del Peru", "20987654321", "Maria Garcia", "987654321", "papeles@proveedores.com", "Jr. Comercio 456"},
            {"Libros Mundo SAC", "20456789123", "Pedro Sanchez", "976543210", "libros@mundo.com", "Calle Real 789"}
        };
        for (String[] p : provs) {
            Supplier prov = new Supplier();
            prov.setNombre(p[0]);
            prov.setRuc(p[1]);
            prov.setContacto(p[2]);
            prov.setTelefono(p[3]);
            prov.setEmail(p[4]);
            prov.setDireccion(p[5]);
            prov.setActivo(true);
            supplierRepository.save(prov);
        }
        log.info("{} suppliers created", provs.length);
    }
}
