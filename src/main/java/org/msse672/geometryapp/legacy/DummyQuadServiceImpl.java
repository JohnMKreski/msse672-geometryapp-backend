package org.msse672.geometryapp.legacy;

import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.service.QuadService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class DummyQuadServiceImpl implements QuadService {

    private final List<Quadrilateral> dummyData = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public DummyQuadServiceImpl() {
        insertQuad(3, 3, 3, 3); // Square
        insertQuad(4, 4, 2, 2); // Rectangle
    }

    // Basic ID getter
    @Override
    public Quadrilateral getById(Long id) {
        return dummyData.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Helper: Basic type classifier
    private String classify(double a, double b, double c, double d) {
        if (a == b && b == c && c == d) return "Square";
        else if (a == c && b == d) return "Rectangle";
        else return "Quadrilateral";
    }

    @Override
    public void insertQuad(double a, double b, double c, double d) {
        String type = classify(a, b, c, d);
        long id = idGenerator.getAndIncrement();
        dummyData.add(new Quadrilateral(id, a, b, c, d, type));
    }

    @Override
    public void updateQuadById(Long id, double a, double b, double c, double d) {
        Quadrilateral q = getById(id);
        if (q != null) {
            q.setSideA(a);
            q.setSideB(b);
            q.setSideC(c);
            q.setSideD(d);
            q.setType(classify(a, b, c, d));
        }
    }

    @Override
    public void deleteById(Long id) {
        dummyData.removeIf(q -> q.getId() == id);
    }

    @Override
    public List<Quadrilateral> getAllSubmittedQuads() {
        return new ArrayList<>(dummyData);
    }

    @Override
    public List<Quadrilateral> getOnlySquares() {
        List<Quadrilateral> squares = new ArrayList<>();
        for (Quadrilateral q : dummyData) {
            if ("Square".equalsIgnoreCase(q.getType())) {
                squares.add(q);
            }
        }
        return squares;
    }

    @Override
    public double getLargestSideEverSubmitted() {
        return dummyData.stream()
                .flatMapToDouble(q -> Arrays.stream(new double[]{q.getSideA(), q.getSideB(), q.getSideC(), q.getSideD()}))
                .max()
                .orElse(0);
    }

    @Override
    public Map<String, Long> countByType() {
        Map<String, Long> result = new HashMap<>();
        for (Quadrilateral q : dummyData) {
            result.merge(q.getType(), 1L, Long::sum);
        }
        return result;
    }

    @Override
    public Quadrilateral getLastSubmittedQuad() {
        if (dummyData.isEmpty()) return null;
        return dummyData.get(dummyData.size() - 1);
    }

    @Override
    public void reset() {
        dummyData.clear();
        idGenerator.set(1);
    }

    // Additional methods from interface (assumed to reflect current quad only)
    @Override
    public void updateSides(double a, double b, double c, double d) {
        // For demo: Insert a new quad instead
        insertQuad(a, b, c, d);
    }

    @Override
    public boolean isInitialized() {
        return !dummyData.isEmpty();
    }

    @Override
    public double getSideA() {
        return getLastSide("A");
    }

    @Override
    public double getSideB() {
        return getLastSide("B");
    }

    @Override
    public double getSideC() {
        return getLastSide("C");
    }

    @Override
    public double getSideD() {
        return getLastSide("D");
    }

    private double getLastSide(String side) {
    if (dummyData.isEmpty()) return 0;
    Quadrilateral last = dummyData.get(dummyData.size() - 1);
    switch (side) {
        case "A":
            return last.getSideA();
        case "B":
            return last.getSideB();
        case "C":
            return last.getSideC();
        case "D":
            return last.getSideD();
        default:
            return 0;
    }
}
}
