<div align="center">
  <img src="public/Ledgerly%20Logo.png" alt="Ledgerly Cover" width="200"/>

# Ledgerly

**Personal, Lightweight Ledger for Small Scale Businesses**

[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![SQLite](https://img.shields.io/badge/SQLite-%2307405e.svg?logo=sqlite&logoColor=white)](#)
[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?logo=intellij-idea&logoColor=white)](#)

</div>

## 📋 Overview

Ledgerly is a lightweight JavaFX-based desktop ledger for small businesses. It provides a simple, modern UI to manage users, products, and sales with local persistence (SQLite). The app focuses on quick data entry, clear dashboard tables, and reliable storage using a modular Java setup.

## ✨ Features

- **User management** — Add, edit, and delete users easily.
- **Product catalog** — Store and select products at the click of a button.
- **Sales recording** — Create and list sales entries with amount, description, product, and timestamp.
- **Dashboard table** — Clear overview of sales with sorting and filtering.
- **Sales reports** — Generate a sales report overview and export to XLSX format.
- **Small footprint** — Single-file database, desktop first, no server required.

## 🚀 Tech Stack

### Backend
- **SQLite**

### Frontend
- **Java** - using JavaFX for the desktop UI
- **FXML** - for defining UI layouts

### Development Tools
- **Maven**
- **Git**
- **Scene Builder**

## Screenshots

<img src="public/screenshots/Screenshot%202025-11-22%20151506.png" alt="User Select" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151523.png" alt="Dashboard Overview" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151549.png" alt="Manage Products" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151653.png" alt="Edit Product" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151603.png" alt="Add New Sale" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151613.png" alt="Sales Table View" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151622.png" alt="View Sale" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151631.png" alt="Edit Sale" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151640.png" alt="Delete Sale" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-23%20214012.png" alt="View Product Report" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-23%20214042.png" alt="Export to XLSX" width="800"/>

<img src="public/screenshots/Screenshot%202025-11-22%20151711.png" alt="Delete Profile" width="800"/>


## 📦 Installation

This section describes how to set up and run Ledgerly on Windows using IntelliJ IDEA.

### Requirements

- JDK 17 or newer installed and configured in IntelliJ (`Project SDK`).
- JavaFX SDK matching your JDK version if not using a build tool that provides OpenJFX.
- SQLite client (optional) to inspect the database file.
- IntelliJ IDEA (Windows).

### Prepare the project

1. Clone or open the project in IntelliJ: `File > Open` and select the project root.
2. Set the `Project SDK` to your installed JDK (17+).
3. Verify that `src/main/resources` is present and contains:
    - `ledgerly/app/css/styles.css`
    - `ledgerly/app/view/MainView.fxml`
    - `ledgerly/app/images/Ledgerly.png`
    - `ledgerly_db.sqlite` (see database step).

###  JavaFX configuration (manual SDK)

If you use the JavaFX SDK directly (no Maven/Gradle), download OpenJFX and unzip it. In your Run configuration for `ledgerly.app.Main` add the VM options:

`--module-path C:\path\to\javafx-sdk-XX\lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base`

Replace `C:\path\to\javafx-sdk-XX\lib` with your actual JavaFX `lib` folder path.

Optional: add JavaFX as a global library in IntelliJ and attach it to the project.

###  Run configuration in IntelliJ

- Main class: `ledgerly.app.Main`
- Working directory: project root (default)
- VM options: see JavaFX configuration above (if needed)
- Classpath/module settings: ensure `module-info.java` is compiled and `opens`/`exports` match the code.

### Database file

The app expects a SQLite file at the working directory named `ledgerly_db.sqlite` (see `src/main/java/ledgerly/app/db/Database.java`). Create it before first run:

- To create an empty file: open PowerShell/CMD in project root and run:

  `type NUL > ledgerly_db.sqlite` (CMD)  
  or  
  `New-Item ledgerly_db.sqlite -ItemType File` (PowerShell)

- To create schema/tables, use the `sqlite3` CLI or a DB browser and add the required tables. If the file is missing the app will throw `Database file not found: ledgerly_db.sqlite`.

###  Running the app

1. Build the project in IntelliJ (`Build > Build Project`).
2. Run the `ledgerly.app.Main` configuration.
3. The application window should appear; check console logs for `Connected to database successfully` if DB file and schema are ready.

### Troubleshooting

- "Cannot find FXML file 'MainView.fxml'": ensure `src/main/resources/ledgerly/app/view/MainView.fxml` exists and that resources are on the classpath.
- "Database file not found": create `ledgerly_db.sqlite` in the working directory (see Database file).
- "JavaFX runtime components are missing": verify the `--module-path` VM option points to the JavaFX `lib` folder and that `--add-modules` includes `javafx.controls,javafx.fxml` (and others as needed).
- Module reflection issues with `TableView`: `module-info.java` already `opens ledgerly.app.controller to javafx.fxml;` and `opens ledgerly.app.view to javafx.fxml;` — ensure the module system is enabled and the project uses the configured module SDK.

### Notes

- Paths shown are for Windows; replace with appropriate paths on other OSes.
- If you prefer, add JavaFX dependencies through Maven/Gradle to avoid manual VM options.


## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

