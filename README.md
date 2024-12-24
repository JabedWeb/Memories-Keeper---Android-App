### **Memories Keeper - Android App**  

---

## **About This Project**  

The **Memories Keeper** app is a modern Android application designed to store, manage, and organize personal memories. It allows users to securely log in, create, edit, delete, and view memories with details such as titles, descriptions, tags, and dates. Built with a focus on user-friendliness, it includes features like sorting, searching, and grid-based layouts for displaying content beautifully.

---

### **Key Features**  

1. **User Authentication:**  
   - Secure login and registration with email validation.  
   - Session management for seamless navigation.  

2. **Memory Management (CRUD):**  
   - Create, Read, Update, and Delete memories.  
   - Supports image storage and displays memory details.  

3. **Search and Filter:**  
   - Keyword-based search for fast access to specific memories.  
   - Sorting by date for chronological organization.  

4. **Responsive Design:**  
   - Scrollable grid layout with two-column design for better visualization.  
   - Optimized for different screen sizes and orientations.  

5. **Session Handling:**  
   - Automatically keeps users logged in until they log out.  
   - Ensures data privacy with session expiration.  

6. **Offline Storage with SQLite:**  
   - All data is stored locally using SQLite for fast and reliable performance.  
   - No internet dependency for core features.  

---

## **How to Use This Project**  

### **Step 1: Download or Clone the Repository**  
- **Download ZIP:** Click on the **Code** button, then select **Download ZIP**.  
- **Git Clone:**  
   ```
   git clone https://github.com/JabedWeb/Memories-Keeper---Android-App.git
   ```  

---

### **Step 2: Create a New Android Studio Project**  

1. Open **Android Studio**.  
2. Click **File → New → New Project**.  
3. Select **Empty Activity** and click **Next**.  
4. Set the project name as **MemoriesKeeper**.  
5. Choose **Java** as the language and **Minimum SDK: 21 (Android 5.0)**.  
6. Click **Finish** to create the project.  

---

### **Step 3: Replace Code Files**  

1. Replace the following folders inside your Android Studio project:

```
MemoriesKeeper/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/memories/activities/
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── PreviewActivity.java
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_preview.xml
│   │   │   │   │   ├── dialog_memory_details.xml
│   │   │   │   │   ├── grid_item.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── styles.xml
│   │   │   ├── AndroidManifest.xml
```

2. Copy Java files into the **java/com/example/memories/activities/** folder.  
3. Copy XML files into the **res/layout/** folder.  
4. Copy **drawable resources** into **res/drawable/**.  
5. Replace the **AndroidManifest.xml** with the provided version.  

---

### **Step 4: Build the Project**  

1. Click **File → Sync Project with Gradle Files**.  
2. Resolve any dependencies if prompted.  
3. Click **Build → Rebuild Project** to verify the setup.  

---

### **Step 5: Run the App**  

1. Connect your Android device via USB or use an emulator.   
2. Test the app features such as login, adding memories, sorting, editing, and deleting.  

---

## **Folder Structure**  

```
MemoriesKeeper/
├── java/                     # Java source code
│   ├── LoginActivity.java    # Handles login and session management
│   ├── RegisterActivity.java # Manages user registration
│   ├── MainActivity.java     # Adds new memories
│   ├── PreviewActivity.java  # Displays and manages memories
├── res/
│   ├── drawable/             # Icons and vector images
│   ├── layout/               # XML design files for UI
│   ├── values/               # Colors, strings, and styles
├── AndroidManifest.xml       # App configuration and permissions
├── screenshots/
│   ├── login.png
│   ├── register.png
│   ├── add_memory.png
│   ├── view_allmemory.png
│   ├── details_memory.png
└── README.md
```
