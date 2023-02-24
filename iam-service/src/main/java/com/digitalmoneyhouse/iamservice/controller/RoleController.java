package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.model.Role;
import com.digitalmoneyhouse.iamservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoleController {
    @Autowired
    private RoleService service;

    @PostMapping
    public ResponseEntity<Role> save(@RequestBody Role role) {
        return ResponseEntity.status(201).body(service.save(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> editById(@RequestBody Role role, @PathVariable Integer id) {
        return ResponseEntity.ok(service.editById(role, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Role>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        service. deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
