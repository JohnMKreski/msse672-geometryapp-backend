package org.msse672.geometryapp.repository;

import org.msse672.geometryapp.model.Quadrilateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuadRepository  extends JpaRepository<Quadrilateral, Long> {
    // This interface extends JpaRepository to provide CRUD operations for Quadrilateral entities
}
