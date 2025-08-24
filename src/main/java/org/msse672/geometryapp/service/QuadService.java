package org.msse672.geometryapp.service;

import org.msse672.geometryapp.model.Quadrilateral;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface QuadService {
    boolean isInitialized();
    double getSideA();
    double getSideB();
    double getSideC();
    double getSideD();

    //GET ID Method in JDBC Service
    Quadrilateral getById(Long id);

    //PUT and POST Method in Memory Service
    void updateSides(double sideA, double sideB, double sideC, double sideD);

    //PUT and POST methods in JDBC
    void insertQuad(double sideA, double sideB, double sideC, double sideD);
    void updateQuadById(Long id, double sideA, double sideB, double sideC, double sideD);

    //DELETE methods
    void reset();
    void deleteById(Long id);

    //History and Statistics Methods
    Quadrilateral getLastSubmittedQuad();
    List<Quadrilateral> getAllSubmittedQuads();
    Map<String, Long> countByType();
    List<Quadrilateral> getOnlySquares();
    double getLargestSideEverSubmitted();

}
