# PA3 — University Student Management System

> BBM104 Introduction to Programming Laboratory II · Spring 2025

## Overview

A comprehensive academic data management system handling students, faculty, courses, departments, and programs. Generates detailed course and student reports including GPA calculation and grade distribution.

## Class Structure

```
Person (abstract)
├── Student        (enrollments, GPA calculation)
└── AcademicMember (taught courses, department head)

AcademicEntity (interface)
├── Course      (credits, semester, grade distribution)
├── Program     (course list, degree level)
└── Department  (head faculty, description)

StudentManagementSystem  ← core controller
```

## GPA Calculation

| Letter | 4-Point |
|--------|---------|
| A1 | 4.00 |
| A2 | 3.50 |
| B1 | 3.00 |
| B2 | 2.50 |
| C1 | 2.00 |
| C2 | 1.50 |
| D1 | 1.00 |
| D2 | 0.50 |
| F3 | 0.00 |

GPA = Σ(grade × credits) / Σ(credits)

## Run

```bash
javac Main.java
java Main persons.txt departments.txt programs.txt courses.txt assignments.txt grades.txt output.txt
```

## Topics

`All 4 OOP pillars` `Interfaces` `Exception handling` `Report generation` `JavaDoc`
