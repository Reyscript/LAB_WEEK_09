package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

data class Student(
    var name: String
)

private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val studentListType = Types.newParameterizedType(List::class.java, Student::class.java)

object StudentListJsonConverter {
    private val adapter = moshi.adapter<List<Student>>(studentListType)

    fun toJson(students: List<Student>): String {
        return adapter.toJson(students)
    }

    fun fromJson(json: String): List<Student> {
        return adapter.fromJson(json) ?: emptyList()
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Home { listData ->
                val jsonData = StudentListJsonConverter.toJson(listData)
                navController.navigate("resultContent/?listData=${jsonData}")
            }
        }
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            ResultContent(
                backStackEntry.arguments?.getString("listData").orEmpty()
            )
        }
    }
}

@Composable
fun Home(
    navigateFromHomeToResult: (List<Student>) -> Unit
) {
    val listData = remember { mutableStateListOf(
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    )}
    var inputField = remember { mutableStateOf(Student("")) }

    HomeContent(
        listData,
        inputField.value,
        { input -> inputField.value = Student(input) },
        {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        },
        {
            navigateFromHomeToResult(listData.toList())
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = {
                        onInputValueChange(it)
                    }
                )

                Row {
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click),
                        onClick = {
                            onButtonClick()
                        }
                    )
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_navigate),
                        onClick = {
                            navigateFromHomeToResult()
                        }
                    )
                }

                if (listData.isEmpty()) {
                    OnBackgroundItemText(text = "No students added yet")
                }
            }
        }
        if (listData.isNotEmpty()) {
            items(listData) { item ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnBackgroundItemText(text = item.name)
                }
            }
        }
    }
}

@Composable
fun ResultContent(listDataJson: String) {
    val studentList = try {
        StudentListJsonConverter.fromJson(listDataJson)
    } catch (e: Exception) {
        emptyList<Student>()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = "Student List (JSON Format)")

        OnBackgroundItemText(
            text = "JSON Data: $listDataJson",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (studentList.isNotEmpty()) {
            OnBackgroundTitleText(
                text = "Parsed Students (${studentList.size} items)",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(studentList) { student ->
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OnBackgroundItemText(text = "Student: ${student.name}")
                    }
                }
            }
        } else {
            OnBackgroundItemText(text = "No student data available")
        }
    }
}

@Composable
fun OnBackgroundItemText(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        OnBackgroundItemText(text = text)
    }
}

@Composable
fun OnBackgroundTitleText(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        OnBackgroundTitleText(text = text)
    }
}

@Composable
fun LAB_WEEK_09Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_09Theme {
        Home { }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResultContent() {
    LAB_WEEK_09Theme {
        ResultContent("""[{"name":"Tanu"},{"name":"Tina"},{"name":"Tono"}]""")
    }
}