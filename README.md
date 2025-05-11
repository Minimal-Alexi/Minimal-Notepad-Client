# Minimal Notepad (Frontend)
<p align="center">
  <img width="300" alt="Logo" src="https://github.com/user-attachments/assets/6362cfd7-3c9d-45a3-b4b7-c5bfd64b99c8" />
</p>


### Purpose
- The purpose of the project is to learn how to use Agile with Scrum Methodlogy to work on a software project.
- At the same time, practice creating diagrams such as UML, class diagram, sequence diagram, use case diagram.
- Learn how to use Git and Github for version control and collaboration between team members.

### Summary
Minimal Notepad is a JavaFX and Spring Boot-based desktop application designed to take, organize and share notes. It is built for students, teachers, and professionals and provides a user-friendly interface. With features like tagging, annotations, and collaboration, it enhances learning and teamwork and offers a seamless and efficient note-taking experience.

This project was created as part of SEP1 (Software Engineering Project 1) and SEP2 (Software Engineering Project 2) at Metropolia UAS. 

- This is the Frontend part of the Minimal Notepad Application.
- The BE part can be found here:[link](https://github.com/Minimal-Alexi/Minimal-Notepad)

📆 January - May, 2025

<img width="4400" alt="Minimal_Notepad_UI" src="https://github.com/user-attachments/assets/689d9b2f-b829-4998-a762-81aca88b9db2" />

### Features
- Create and Login user
  - User can edit user's detail or password after logged in
- Create, Read, Update and Delete notes
  - User can add photos, change the background color
  - User can filter notes by categories
  - User can search notes by title 
- Create, Read, Update and Delete Groups
- Join group to read notes from that group
- Share notes through sharing in a group. Whoever in the same group can read the note that you just shared
- Localization:
  - Translations available in English, Finnish, Russian, and Chinese
  - Localized components: User Interface (UI), error messages, and notes' categories

<img width="4400" alt="Notepad_Localization" src="https://github.com/user-attachments/assets/0c08d79f-92ec-44a3-a3ab-06bbe93fd7c2" />


### Tools used
- Intellij for IDE
- SceneBuilder for designing the GUI
- GIMP for designing the icons
- Git for version control
- Github for hosting the project

### Tech Stack
- Java 17 – Java version for development
- JavaFx -  GUI application platform for client such as desktop, mobile and embedded systems
- SonarQube – Static code analysis and quality inspection
- Checkstyle – Code style enforcement
- BugSpot – Identification of risky code areas
- Javadoc – Automated code documentation

## Project Structure

| **Folder/File**                           | **Purpose**                            | **Responsibilities**                                                                             |
|-------------------------------------------|----------------------------------------|--------------------------------------------------------------------------------------------------|
| `HelloApplication`                        | File to run application                | This is the file to run the application                                                          |
| `controller/`                             | Handle pages controller                | This folder stores all the pages'controllers, handle how the page interact with users            |
| `model/`                                  | Transfers data between layers          | Contains Singleton class to pass data between pages, and abstraction of entities in the application      |
| `utils/`                                  | Contains utilites                      | Contains helper classes to be used in multiple controllers, reduce redundance code               |
| `view/`                                   | Contains the Javafx file to load the application | Represents database tables and relationships, includes fields, getters, and setters.   |
| `resources/CSS`                           | Contains CSS files                     | Contains CSS files for the style of client's components                                          |
| `resources/img`                           | Contains images                        | Folders to store images to be used in the application                                            |
| `resources/fxml`                          | Contains FXML pages                    | Contains FXML pages for JavaFx to render                                                         |
| `resources/Resource Bundle 'message'`     | Handles localization                   | Store translation of each languages: Finnish, Russian, Chinese, and English
## How to Run Locally

### Pre-settings
Before running the application locally, make sure you have the following tools installed:
- **Java 17** (or newer) - [Install Java](https://adoptopenjdk.net/)
- Clone the Backend repository
- Start the Backend Server following the instruction in the `README.md`in the Backend repository

### Steps to Run Locally
- Clone the repository
- cd to the project directory
- open the project with Intellij
- In the project tree directory, navigate to folder `src/main/java`
- Right click on the `HelloApplication` class and click on `Run 'HelloApplication.main()`

## Future Features
- Add Favorite notes
- Enhance UI/UX 
- Implement Search/Filter for Groups (all groups, my groups) and Users in groups( my groups)
- Add Unit Testing 
- Add Jenkin and Docker for CI/CD and deployment

## Authors
- [Viktoriia Beloborodova](https://github.com/Viktoriia-code)
- [Alex Pop](https://github.com/Minimal-Alexi)
- [Trang Vu](https://github.com/cindy3377)
- [Trung Doan](https://github.com/viettrung2103)
- [Runzhou Zhu](https://github.com/RunzhouZHu)
