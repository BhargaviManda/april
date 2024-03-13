package com.mounika.bha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mounika.bha.entity.Servicer;

@Repository
public interface ServicerRepository extends JpaRepository<Servicer, Long> {
	Servicer findByEmail (String email);
}
