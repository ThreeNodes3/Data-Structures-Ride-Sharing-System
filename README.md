<div align="center">

# 🚗 RideShare
### Data Structures × Real-World Ride Management

A Java-based ride-sharing management system built from the ground up using  
**custom data structures, object-oriented design, and algorithmic analysis.**

**CSC 212 — Data Structures | King Saud University**

![Java](https://img.shields.io/badge/Java-Data%20Structures-orange?style=for-the-badge&logo=openjdk)
![Status](https://img.shields.io/badge/Phase%201-In%20Progress-blue?style=for-the-badge)
![University](https://img.shields.io/badge/KSU-Software%20Engineering-green?style=for-the-badge)

</div>

---

## ✨ About the Project

**RideShare** is a Java-based ride-sharing management system designed to put core data structure concepts into practice.

Instead of relying on Java's built-in collection classes, the system is built around a **custom Linked List implementation**, giving us direct control over how riders, drivers, and rides are stored, searched, sorted, and removed.

The project is developed across multiple phases, with each phase extending the system as new data structure concepts are introduced.

---

## 🚀 Phase 1 — Linked Lists

> **Current Status:** 🟡 In Progress

Phase 1 focuses on building the core ride-sharing system using custom linked-list-based structures.

### 👤 Rider Management
- Add and store riders
- Search by ID, name, email, or home city
- Remove riders using multiple criteria
- Maintain riders sorted by ID

### 🚘 Driver Management
- Add and store drivers
- Search by ID, name, vehicle plate, or vehicle type
- Remove drivers using multiple criteria
- Maintain drivers sorted by ID

### 🛣️ Ride Management
The system supports two ride models:

**Private Ride**  
A ride assigned to exactly one rider.

**Shared Ride**  
A ride that allows multiple riders to participate.

Rides are maintained alphabetically by pickup location.

### ⏱️ Smart Scheduling
Before a ride is scheduled, the system verifies:

- The selected driver exists
- The required rider or riders exist
- The driver has no overlapping ride
- Participating riders have no overlapping ride

This prevents scheduling conflicts across the system.

### 🗑️ Cascade Removal
Removing a rider or driver also updates related rides to keep the system consistent.

For example, removing a driver removes the rides assigned to that driver, while removing a rider updates their associated private and shared rides according to the system rules.

### 📂 CSV Integration
The application loads its initial data from:

- `riders_100.csv`
- `drivers_30.csv`
- `rides_40.csv`

---

## 🧠 What We're Applying

This project combines several fundamental software engineering and data structure concepts:

| Area | Concepts |
|---|---|
| **Data Structures** | Custom Linked Lists, Nodes, Sorted Insertion |
| **OOP** | Inheritance, Polymorphism, Encapsulation, Abstraction |
| **Java** | Interfaces, Abstract Classes, Enums, Comparable |
| **Algorithms** | Searching, Sorting, Removal, Conflict Detection |
| **Data Processing** | CSV File Handling |
| **Analysis** | Worst-Case Time Complexity & Big-O |

---

## 🏗️ System Architecture

```text
Person
├── Rider
└── Driver

Ride
├── PrivateRide
└── SharedRide

Custom Linked List
├── RiderList
├── DriverList
└── RideList

RideSharingSystem
├── Rider Management
├── Driver Management
├── Ride Scheduling
├── Conflict Detection
└── CSV Data Loading
```

---

## 🔗 Data Structure Design

Phase 1 is built using a **custom Linked List implementation**.

```text
             Custom Linked List
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
   RiderList     DriverList     RideList
        │            │            │
        ▼            ▼            ▼
     Riders        Drivers       Rides
```

The system maintains:

- **Riders** → sorted by Rider ID
- **Drivers** → sorted by Driver ID
- **Rides** → sorted alphabetically by pickup location

> Java's built-in collection data structures are not used for the project implementation.

---

## ⚡ Ride Conflict Detection

Two rides overlap when:

```text
pickup1 < dropoff2
        &&
pickup2 < dropoff1
```

This rule is used to prevent a rider or driver from being assigned to conflicting rides.

---

## 📈 Complexity Analysis

Efficiency is an important part of the project.

Implemented operations are analyzed using **worst-case Big-O notation**, including their growth-rate reasoning.

This allows us to evaluate how operations such as insertion, searching, removal, and traversal scale as the amount of stored data increases.

---

## 📁 Project Structure

```text
Data-Structures-Ride-Sharing-System/
│
├── src/
│   ├── Person
│   ├── Rider
│   ├── RiderList
│   ├── Driver
│   ├── DriverList
│   ├── DateTime
│   ├── Ride
│   ├── PrivateRide
│   ├── SharedRide
│   ├── RideList
│   ├── RideSharingSystem
│   └── Main
│
├── data/
│   ├── riders_100.csv
│   ├── drivers_30.csv
│   └── rides_40.csv
│
└── README.md
```

> Project structure may evolve as development progresses.

---

## 🗺️ Project Roadmap

### Phase 1
`Custom Linked Lists` → `Core Entities` → `Ride Management` → `Scheduling` → `CSV Integration` → `Testing` → `Complexity Analysis`

**Currently in development.**

### Phase 2
🔒 **Coming Next**

Phase 2 will extend the system based on the next set of project requirements.

---

## 🛠️ Tech Stack

<p align="center">

**Java • OOP • Data Structures • Git • GitHub**

</p>

---

## 👩🏻‍💻 Team

Built collaboratively by **three Software Engineering students at King Saud University**.

The project is developed through collaborative version control using Git and GitHub.

---

## 🎓 Academic Context

**Course:** CSC 212 — Data Structures  
**Program:** Software Engineering  
**College:** College of Computer and Information Sciences  
**University:** King Saud University  
**Semester:** Fall 2026

---

<div align="center">

### Built to understand what happens beneath the abstraction.

**From nodes and links to a complete ride-sharing system. 🚗**

</div>
