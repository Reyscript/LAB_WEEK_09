# LAB_WEEK_09 - Jetpack Compose dengan Navigation & State Management

## Link Google Drive
[Keseluruhan Project](https://drive.google.com/drive/u/5/folders/1JMcj0gDXT1e40JY2uBAx8rf4tXi9PfeC)

[Images & Screenshots](https://drive.google.com/drive/u/5/folders/1DgPPZBwKW4VFAslNp4Xw-3qTigmS1OSl) 

[APK File](https://drive.google.com/drive/u/5/folders/15czRblpw4ijjO9AwY0G13CvP3-_-k0uV)

## Commit History
- **Commit No. 1** - Building Jetpack Compose UI
- **Commit No. 2** - State and Event Handler
- **Commit No. 3** - UI Element and Theme
- **Commit No. 4** - Navigation
- **Commit No. 5** - Assignment Fixes + Moshi Bonus

## Fitur Aplikasi

### Modern UI dengan Jetpack Compose
- **Declarative UI Approach** - Membangun UI dengan pendekatan deklaratif menggantikan XML
- **Material Design 3** - Menggunakan komponen Material 3 terbaru
- **Custom UI Components** - Reusable composable functions dengan theming konsisten
- **LazyColumn Implementation** - Efficient list rendering menggantikan RecyclerView

### State Management & Reactivity
- **Mutable State Management** - Menggunakan `mutableStateOf` dan `mutableStateListOf` untuk state handling
- **Event Handlers** - `onClick` dan `onValueChange` untuk user interactions
- **Input Validation** - Validasi mencegah submit empty string
- **Remember Function** - Mempertahankan state selama recomposition

### Navigation & Multi-Screen
- **Navigation Compose** - Multi-screen navigation dengan NavHost dan NavController
- **Parameter Passing** - Data passing antar screen menggunakan JSON serialization
- **Route Management** - Structured navigation dengan defined routes
- **Back Stack Management** - Automatic back stack handling

### JSON Serialization dengan Moshi
- **JSON Conversion** - Konversi object ke JSON dan sebaliknya menggunakan Moshi
- **Data Persistence** - Data tetap utuh selama navigation transitions
- **Type Safety** - Type-safe JSON parsing dengan Kotlin reflection
- **Error Handling** - Robust error handling untuk JSON parsing

## Fitur Interaksi

### Home Screen Flow
1. **Input Student** - TextField untuk memasukkan nama student
2. **Submit Action** - Tambah student ke list dengan validasi input
3. **Real-time Update** - Dynamic list update dengan LazyColumn
4. **Navigation** - Finish button untuk navigasi ke Result screen dengan data

### Result Screen Flow
1. **JSON Display** - Menampilkan raw JSON data yang dikirim
2. **Parsed Data** - Menampilkan parsed student list dalam LazyColumn
3. **Back Navigation** - Kembali ke Home screen dengan preserved state

### UI Components Features
- **Custom Text Components** - TitleText dan ItemText dengan Material theming
- **Primary Button** - Custom styled button dengan dark gray background
- **Responsive Layout** - Column dan Row arrangements dengan proper alignment
- **Empty State Handling** - Message ketika list kosong

## Teknologi yang Digunakan

### Android Jetpack Components
- **Jetpack Compose** - Modern UI toolkit dengan declarative approach
- **Navigation Compose** - Type-safe navigation component
- **Material3** - Material Design components terbaru
- **Compose Runtime** - State management dan recomposition system

### UI & Layout Components
- **LazyColumn** - Efficient scrolling list replacement untuk RecyclerView
- **TextField** - Input field dengan keyboard options dan validation
- **Button** - Custom styled buttons dengan event handling
- **Column/Row** - Layout containers untuk composable arrangement

### Data Management
- **Moshi** - JSON serialization/deserialization library
- **Kotlin Reflection** - Runtime type inspection untuk JSON parsing
- **Data Classes** - Immutable data models untuk type safety
- **State Hoisting** - Proper state management patterns di Compose

### Navigation System
- **NavHostController** - Navigation state management dan control
- **Composable Routes** - Type-safe route definitions dengan arguments
- **Argument Passing** - Data transfer between screens menggunakan parameters
- **Deep Linking Support** - Support untuk deep link navigation

## Struktur Data & Components

### Data Models
```kotlin
data class Student(var name: String)
```

### Custom UI Components
- **OnBackgroundTitleText** - Title text dengan theme color scheme
- **OnBackgroundItemText** - Item text dengan theme color scheme
- **PrimaryTextButton** - Custom styled button dengan dark gray background
- **TitleText/ItemText** - Base text components dengan Material typography

### State Management Components
- **Home Composable** - Parent component untuk state management dan business logic
- **HomeContent Composable** - UI component untuk display dan user interactions
- **StudentListJsonConverter** - JSON serialization utility class
- **App Composable** - Root navigation component

### Navigation Components
- **NavHost** - Navigation container dengan route definitions
- **Composable Routes** - Screen definitions dengan parameter handling
- **NavController** - Navigation state controller

## Execution Chronology

### Phase 1: Home Screen Interaction
```
App Launch: Home screen dengan sample data ("Tanu", "Tina", "Tono")
User Input: Memasukkan nama student di TextField
Submit Action: Klik Submit button untuk tambah ke list
Real-time Update: List update secara real-time dengan LazyColumn
Input Clear: TextField otomatis clear setelah successful submission
```

### Phase 2: Navigation & Data Transfer
```
Navigation Trigger: Klik Finish button
JSON Conversion: List data dikonversi ke JSON format
Parameter Passing: JSON data dikirim sebagai navigation argument
Screen Transition: Smooth transition ke Result screen
```

### Phase 3: Result Screen Display
```
JSON Parsing: JSON data diparsing kembali ke List<Student>
Dual Display: Menampilkan raw JSON dan parsed list
LazyColumn Rendering: Efficient rendering student list
Back Navigation: User kembali ke Home screen dengan preserved state
```

## Key Features Implementation

### State Management Pattern
```kotlin
val listData = remember { mutableStateListOf<Student>() }
var inputField = remember { mutableStateOf(Student("")) }
```

### Navigation Setup
```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") { 
        Home { listData ->
            val jsonData = StudentListJsonConverter.toJson(listData)
            navController.navigate("resultContent/?listData=${jsonData}")
        }
    }
    composable(
        "resultContent/?listData={listData}",
        arguments = listOf(navArgument("listData") { type = NavType.StringType })
    ) { backStackEntry ->
        ResultContent(backStackEntry.arguments?.getString("listData").orEmpty())
    }
}
```

### JSON Serialization
```kotlin
object StudentListJsonConverter {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter<List<Student>>(Types.newParameterizedType(List::class.java, Student::class.java))
    
    fun toJson(students: List<Student>): String {
        return adapter.toJson(students)
    }
    
    fun fromJson(json: String): List<Student> {
        return adapter.fromJson(json) ?: emptyList()
    }
}
```

### Custom Theming & UI Components
```kotlin
@Composable
fun PrimaryTextButton(text: String, onClick: () -> Unit) {
    CustomTextButton(
        text = text,
        textColor = Color.White,
        onClick = onClick
    )
}

@Composable
fun CustomTextButton(text: String, textColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.DarkGray,
            contentColor = textColor
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}
```
