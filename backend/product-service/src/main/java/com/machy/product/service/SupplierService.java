package com.machy.product.service;

import com.machy.product.entity.Supplier;
import com.machy.product.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public List<Supplier> findActive() {
        return supplierRepository.findByActivoTrue();
    }

    public Supplier findById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    public Supplier create(Supplier supplier) {
        supplier.setActivo(true);
        return supplierRepository.save(supplier);
    }

    public Supplier update(UUID id, Supplier supplier) {
        Supplier existing = findById(id);
        if (supplier.getNombre() != null) existing.setNombre(supplier.getNombre());
        if (supplier.getRuc() != null) existing.setRuc(supplier.getRuc());
        if (supplier.getContacto() != null) existing.setContacto(supplier.getContacto());
        if (supplier.getTelefono() != null) existing.setTelefono(supplier.getTelefono());
        if (supplier.getEmail() != null) existing.setEmail(supplier.getEmail());
        if (supplier.getDireccion() != null) existing.setDireccion(supplier.getDireccion());
        return supplierRepository.save(existing);
    }

    public Supplier toggleStatus(UUID id) {
        Supplier supplier = findById(id);
        supplier.setActivo(!supplier.getActivo());
        return supplierRepository.save(supplier);
    }
}
