package com.unla.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{

    public List<User> findAll();

    public Optional<User> findById(Integer id);

    public void deleteById(Integer id);

    public Optional<User> findByMail(String mail);

    Page<User> findAll(Pageable pageable);
}
