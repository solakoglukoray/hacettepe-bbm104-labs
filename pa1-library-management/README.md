# PA1 — University Library Management System

> BBM104 Introduction to Programming Laboratory II · Spring 2025

## Overview

A simulation of a university library system with different user roles (Student, Academic Staff, Guest) and item types (Book, Magazine, DVD), each with distinct borrowing rules.

## Class Hierarchy

```
LibraryItem
├── Book       (author, genre)
├── Magazine   (publisher, category)
└── DVD        (director, category, runtime)

User
├── Student    (faculty, department, grade)
├── AcademicMember (faculty, department, title)
└── GuestUser  (occupation)
```

## Borrowing Rules

| User Type | Max Items | Overdue (days) | Restrictions |
|-----------|-----------|----------------|--------------|
| Student | 5 | 30 | No Reference items |
| Academic | 3 | 15 | None |
| Guest | 1 | 7 | No Rare/Limited items |

- Penalty: $2 per overdue item, borrowing blocked at $6+

## Run

```bash
javac Main.java
java Main items.txt users.txt commands.txt output.txt
```

## Topics

`Inheritance` `Encapsulation` `File I/O` `ArrayList` `JavaDoc`
