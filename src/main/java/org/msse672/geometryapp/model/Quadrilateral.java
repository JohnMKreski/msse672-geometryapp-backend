package org.msse672.geometryapp.model;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quadrilateral represents a four‑sided shape defined by its side lengths.
 *
 * It provides:
 *  - input validation (all sides > 0 and must form a valid quadrilateral)
 *  - type determination (square, rectangle, rhombus placeholder, unknown)
 *
 * A quadrilateral is valid if and only if the sum of any three sides
 * is strictly greater than the remaining side.
 */

@Entity
@Table(name="quads")
public class Quadrilateral {
    // Id is used for persistence (e.g. database)
    // GeneratedValue with IDENTITY strategy allows the database to auto‑generate unique IDs
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double sideA;

    @Column(nullable = false)
    private double sideB;

    @Column(nullable = false)
    private double sideC;

    @Column(nullable = false)
    private double sideD;

    @Column(name = "type", nullable = false)
    private String type;

    // Default constructor for Hibernate
    public Quadrilateral() {
    }

    // Used in most user-facing cases
    public Quadrilateral(double sideA, double sideB, double sideC, double sideD) {
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
        this.sideD = sideD;
        this.type = getType(); // Auto-calculate upon construction
    }

    // Used for JDBC or other logic where ID is provided
    public Quadrilateral(Long id, double sideA, double sideB, double sideC, double sideD) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
        this.sideD = sideD;
        this.type = getType();
    }

    // Used for Dummy or testing purposes where type is also provided
    public Quadrilateral(Long id, double sideA, double sideB, double sideC, double sideD, String type) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
        this.sideD = sideD;
        this.type = type;
    }

    // Getter and setter for ID
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    //Getters and Setters for side lengths
    public double getSideA() { return sideA; }
    public void setSideA(double sideA) { this.sideA = sideA; }
    public double getSideB() { return sideB; }
    public void setSideB(double sideB) { this.sideB = sideB; }
    public double getSideC() { return sideC; }
    public void setSideC(double sideC) { this.sideC = sideC; }
    public double getSideD() { return sideD; }
    public void setSideD(double sideD) { this.sideD = sideD; }

    // Getter and setter for type
    public String getType() {
        String err = validate(sideA, sideB, sideC, sideD);
        if (err != null) return "Invalid Quadrilateral: " + err + ".";
        if (allSidesEqual(sideA, sideB, sideC, sideD)) return "Square";
        if (isRectangle(sideA, sideB, sideC, sideD)) return "Rectangle";
        if (isRhombus(sideA, sideB, sideC, sideD)) return "Rhombus";
        if (isParallelogram(sideA, sideB, sideC, sideD)) return "Parallelogram";
        if (isKite(sideA, sideB, sideC, sideD)) return "Kite";
        return "Generic Quadrilateral";
    }

    public void setType(String type) {
        this.type = type;
    }

//    public String getType() {
//        String err = validate(sideA, sideB, sideC, sideD);
//        if (err != null) {
//            return "Invalid: " + err;
//        }
//        if (allSidesEqual(sideA, sideB, sideC, sideD)) {
//            return "Square";
//        }
//        if (isRectangle(sideA, sideB, sideC, sideD)) {
//            return "Rectangle";
//        }
//        if (isRhombus(sideA, sideB, sideC, sideD)) {
//            return "Rhombus";
//        }
//        if (isParallelogram(sideA, sideB, sideC, sideD)) {
//            return "Parallelogram";
//        }
//        if (isKite(sideA, sideB, sideC, sideD)) {
//            return "Kite";
//        }
//        return "Generic";
//    }

    // Logger for debugging and information
    private static final Logger logger = LoggerFactory.getLogger(Quadrilateral.class);


    @Override
    public String toString() {
        return String.format(
                "Quadrilateral{id=%d, sideA=%.2f, sideB=%.2f, sideC=%.2f, sideD=%.2f, type=Type of Quadrilateral: %s}",
                id, sideA, sideB, sideC, sideD, getType()
        );
    }

    //======= Validation and Type Determination Methods =======
    public static boolean hasNullOrInvalid(Object a, Object b, Object c, Object d) {
        try {
            if (a == null || b == null || c == null || d == null) return true;
            Double.parseDouble(a.toString());
            Double.parseDouble(b.toString());
            Double.parseDouble(c.toString());
            Double.parseDouble(d.toString());
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
    

    /**
     * Validates that:
     * 1. a, b, c, d are all > 0.
     * 2. The sum of any three sides is greater than the fourth.
     *
     * @return null if valid, otherwise a descriptive error message using a/b/c/d notation
     */
    public static String validate(double sideA, double sideB, double sideC, double sideD) {
        if (sideA <= 0 || sideB <= 0 || sideC <= 0 || sideD <= 0) {
            logger.debug("Validation failed: at least one side is <= 0");
            return "all sides (a, b, c, d) must be > 0";
        }
        if ((sideA + sideB + sideC) <= sideD) {
            logger.debug("Validation failed: a + b + c <= d");
            return "sum of a + b + c must be > d";
        }
        if ((sideA + sideB + sideD) <= sideC) return "sum of a + b + d must be > c";
        if ((sideA + sideC + sideD) <= sideB) return "sum of a + c + d must be > b";
        if ((sideB + sideC + sideD) <= sideA) return "sum of b + c + d must be > a";
        return null;
    }

    /**
     * All four sides equal.
     */
    public static boolean allSidesEqual(double sideA, double sideB, double sideC, double sideD) {
        return sideA == sideB && sideB == sideC && sideC == sideD;
    }

    /**
     * Opposite sides equal (a == c and b == d) and adjacent not equal.
     */
    public static boolean isRectangle(double sideA, double sideB, double sideC, double sideD) {
        return (sideA == sideC && sideB == sideD) && (sideA != sideB);
    }

    /**
     * All sides equal: potential rhombus (angles not considered).
     */
    public static boolean isRhombus(double sideA, double sideB, double sideC, double sideD) {
        return allSidesEqual(sideA, sideB, sideC, sideD);
    }

    // Reusable "is this a valid quadrilateral?" check
    public static boolean isValidQuadrilateral(double sideA, double sideB, double sideC, double sideD) {
        return validate(sideA, sideB, sideC, sideD) == null;
    }

    /**
     * A parallelogram has opposite sides equal (a == c, b == d)
     */
    public static boolean isParallelogram(double sideA, double sideB, double sideC, double sideD) {
        return (sideA == sideC) && (sideB == sideD);
    }

    /**
     * A kite has two distinct pairs of adjacent sides equal
     */
    public static boolean isKite(double sideA, double sideB, double sideC, double sideD) {
        return (sideA == sideB && sideC == sideD && sideA != sideC) ||
            (sideB == sideC && sideD == sideA && sideB != sideD);
    }

}