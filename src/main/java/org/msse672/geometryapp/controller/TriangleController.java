package org.msse672.geometryapp.controller;

import org.msse672.geometryapp.model.Triangle;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/triangle")
public class TriangleController {

    @PostMapping("/type")
    public String getTriangleType(@RequestParam double side1, @RequestParam double side2, @RequestParam double side3) {
        Triangle triangle = new Triangle(side1, side2, side3);
        return triangle.getType();
    }
}