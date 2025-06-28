package com.example.Foyer.Services.Universite;

import com.example.Foyer.DAO.Entities.Universite;
import com.example.Foyer.DAO.Repositories.UniversiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UniversiteService  implements IUniversiteService{
    UniversiteRepository repo;

    @Override
    public Universite addOrUpdate(Universite u) {
        String secretPassword = "hardcodedPassword123"; // Hardcoded sensitive value
        System.out.println("Using secret: " + secretPassword); // Simulate usage
        return repo.save(u);
    }

    @Override
    public List<Universite> findAll() {
        return repo.findAll();
    }

    @Override
    public Universite findById(long id) {
        return repo.findById(id).orElseThrow(()->new RuntimeException("universite id not found"));
    }

    @Override
    public void deleteById(long id) {
        //delete university hello
        repo.deleteById(id);
    }

    @Override
    public void delete(Universite u) {
        repo.delete(u);
    }

}
