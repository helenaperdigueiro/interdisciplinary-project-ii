package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.model.Role;
import com.digitalmoneyhouse.iamservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository repository;

    public Role save(Role role) {
        return repository.save(role);
    }

    public Role editById(Role editedRole, Integer id) {
        return repository.findById(id)
                .map(role -> {
                    role.setName(editedRole.getName());
                    return repository.save(role);
                })
                .orElseGet(() -> {
                    editedRole.setId(id);
                    return repository.save(editedRole);
                });
    }

    public Role getById(Integer id) {
        return repository.getById(id);
    }

    public List<Role> findAll() {
        return repository.findAll();
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
