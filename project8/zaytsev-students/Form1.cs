using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.IO;
using System.Text.Json;
using System.Text;

namespace project8
{
    public partial class Form1 : Form
    {
        private List<Student> students = new List<Student>();
        private bool isLastNameAscending = true;
        private bool isGroupAscending = true;
        private bool isCourseAscending = true;
        private bool isDataModified = false;

        public Form1()
        {
            InitializeComponent();
            buttonAdd.Click += ButtonAdd_Click;
            buttonEdit.Click += ButtonEdit_Click;
            buttonDelete.Click += ButtonDelete_Click;
            AddSortingFunctionality();
            AddFilteringFunctionality();
            AddSearchFunctionality();
            InitializeSorting();
        }

        private void ButtonAdd_Click(object? sender, EventArgs e)
        {
            if (ValidateInputs())
            {
                var student = new Student
                {
                    LastName = textBoxLastName.Text,
                    FirstName = textBoxFirstName.Text,
                    MiddleName = textBoxMiddleName.Text,
                    Course = int.Parse(textBoxCourse.Text),
                    Group = textBoxGroup.Text,
                    BirthDate = dateTimePickerBirthDate.Value,
                    Email = textBoxEmail.Text
                };
                students.Add(student);
                UpdateStudentGrid();
                ClearInputs();
                isDataModified = true;
            }
        }

        private void ButtonEdit_Click(object? sender, EventArgs e)
        {
            if (dataGridViewStudents.CurrentRow != null && ValidateInputs())
            {
                var index = dataGridViewStudents.CurrentRow.Index;
                var student = students[index];
                student.LastName = textBoxLastName.Text;
                student.FirstName = textBoxFirstName.Text;
                student.MiddleName = textBoxMiddleName.Text;
                student.Course = int.Parse(textBoxCourse.Text);
                student.Group = textBoxGroup.Text;
                student.BirthDate = dateTimePickerBirthDate.Value;
                student.Email = textBoxEmail.Text;
                UpdateStudentGrid();
                ClearInputs();
                isDataModified = true;
            }
        }

        private void ButtonDelete_Click(object? sender, EventArgs e)
        {
            if (dataGridViewStudents.CurrentRow != null)
            {
                var index = dataGridViewStudents.CurrentRow.Index;
                students.RemoveAt(index);
                UpdateStudentGrid();
                isDataModified = true;
            }
        }

        private void UpdateStudentGrid()
        {
            dataGridViewStudents.DataSource = null;
            dataGridViewStudents.DataSource = students;
        }

        private void ClearInputs()
        {
            textBoxLastName.Clear();
            textBoxFirstName.Clear();
            textBoxMiddleName.Clear();
            textBoxCourse.Clear();
            textBoxGroup.Clear();
            textBoxEmail.Clear();
            dateTimePickerBirthDate.Value = DateTime.Now;
        }

        private bool ValidateInputs()
        {
            if (string.IsNullOrWhiteSpace(textBoxLastName.Text) ||
                string.IsNullOrWhiteSpace(textBoxFirstName.Text) ||
                string.IsNullOrWhiteSpace(textBoxMiddleName.Text) ||
                string.IsNullOrWhiteSpace(textBoxCourse.Text) ||
                string.IsNullOrWhiteSpace(textBoxGroup.Text) ||
                string.IsNullOrWhiteSpace(textBoxEmail.Text))
            {
                MessageBox.Show("Все поля обязательны для заполнения.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            if (!int.TryParse(textBoxCourse.Text, out _))
            {
                MessageBox.Show("Курс должен быть числом.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            if (textBoxEmail.Text.IndexOf('@') < 3)
            {
                MessageBox.Show("Электронная почта должна содержать не менее 3 символов перед знаком @.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            if (!textBoxEmail.Text.Contains("@") ||
                !(textBoxEmail.Text.EndsWith("yandex.ru") || textBoxEmail.Text.EndsWith("gmail.com") || textBoxEmail.Text.EndsWith("icloud.com")))
            {
                MessageBox.Show("Электронная почта должна быть на доменах yandex.ru, gmail.com или icloud.com.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            if (dateTimePickerBirthDate.Value < new DateTime(1992, 1, 1) || dateTimePickerBirthDate.Value > DateTime.Now)
            {
                MessageBox.Show("Дата рождения должна быть в диапазоне от 01.01.1992 до текущей даты.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            if (dateTimePickerBirthDate.Value.Date > DateTime.Now.Date)
            {
                MessageBox.Show("Дата рождения не может быть позже текущей даты.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            return true;
        }

        private void AddSortingFunctionality()
        {
            var sortMenu = new ContextMenuStrip();

            var sortByLastName = new ToolStripMenuItem("Сортировать по фамилии");
            sortByLastName.Click += (s, e) => {
                students = students.OrderBy(student => student.LastName).ToList();
                UpdateStudentGrid();
            };

            var sortByGroup = new ToolStripMenuItem("Сортировать по группе");
            sortByGroup.Click += (s, e) => {
                students = students.OrderBy(student => student.Group).ToList();
                UpdateStudentGrid();
            };

            var sortByCourse = new ToolStripMenuItem("Сортировать по курсу");
            sortByCourse.Click += (s, e) => {
                students = students.OrderBy(student => student.Course).ToList();
                UpdateStudentGrid();
            };

            sortMenu.Items.AddRange(new ToolStripItem[] { sortByLastName, sortByGroup, sortByCourse });

            dataGridViewStudents.ContextMenuStrip = sortMenu;
        }

        private void AddFilteringFunctionality()
        {
            var filterMenu = new ContextMenuStrip();

            var filterByCourse = new ToolStripMenuItem("Фильтровать по курсу");
            filterByCourse.Click += (s, e) => {
                string input = Microsoft.VisualBasic.Interaction.InputBox("Введите курс для фильтрации:", "Фильтрация по курсу", "");
                if (int.TryParse(input, out int course))
                {
                    var filteredStudents = students.Where(student => student.Course == course).ToList();
                    dataGridViewStudents.DataSource = null;
                    dataGridViewStudents.DataSource = filteredStudents;
                }
                else
                {
                    MessageBox.Show("Введите корректное число.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            };

            var filterByGroup = new ToolStripMenuItem("Фильтровать по группе");
            filterByGroup.Click += (s, e) => {
                string input = Microsoft.VisualBasic.Interaction.InputBox("Введите группу для фильтрации:", "Фильтрация по группе", "");
                var filteredStudents = students.Where(student => student.Group.Equals(input, StringComparison.OrdinalIgnoreCase)).ToList();
                dataGridViewStudents.DataSource = null;
                dataGridViewStudents.DataSource = filteredStudents;
            };

            filterMenu.Items.AddRange(new ToolStripItem[] { filterByCourse, filterByGroup });

            dataGridViewStudents.ContextMenuStrip = filterMenu;
        }

        private void AddSearchFunctionality()
        {
            var searchMenu = new ContextMenuStrip();

            var searchByLastName = new ToolStripMenuItem("Поиск по фамилии");
            searchByLastName.Click += (s, e) => {
                string input = Microsoft.VisualBasic.Interaction.InputBox("Введите фамилию для поиска:", "Поиск по фамилии", "");
                var searchResults = students.Where(student => student.LastName.Equals(input, StringComparison.OrdinalIgnoreCase)).ToList();
                if (searchResults.Any())
                {
                    dataGridViewStudents.DataSource = null;
                    dataGridViewStudents.DataSource = searchResults;
                }
                else
                {
                    MessageBox.Show("Студенты с такой фамилией не найдены.", "Результаты поиска", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            };

            searchMenu.Items.Add(searchByLastName);

            dataGridViewStudents.ContextMenuStrip = searchMenu;
        }

        private void SaveDataToFile()
        {
            var saveFileDialog = new SaveFileDialog
            {
                Filter = "JSON files (*.json)|*.json",
                Title = "Сохранить данные студентов"
            };

            if (saveFileDialog.ShowDialog() == DialogResult.OK)
            {
                var jsonData = JsonSerializer.Serialize(students);
                File.WriteAllText(saveFileDialog.FileName, jsonData);
                MessageBox.Show("Данные успешно сохранены!", "Успех", MessageBoxButtons.OK, MessageBoxIcon.Information);
                isDataModified = false;
            }
        }

        private void LoadDataFromFile()
        {
            var openFileDialog = new OpenFileDialog
            {
                Filter = "JSON files (*.json)|*.json",
                Title = "Загрузить данные студентов"
            };

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                var jsonData = File.ReadAllText(openFileDialog.FileName);
                students = JsonSerializer.Deserialize<List<Student>>(jsonData) ?? new List<Student>();
                UpdateStudentGrid();
                MessageBox.Show("Данные успешно загружены!", "Успех", MessageBoxButtons.OK, MessageBoxIcon.Information);
                isDataModified = false;
            }
        }

        private void ExportToCsv()
        {
            var saveFileDialog = new SaveFileDialog
            {
                Filter = "CSV files (*.csv)|*.csv",
                Title = "Экспортировать данные студентов"
            };

            if (saveFileDialog.ShowDialog() == DialogResult.OK)
            {
                var csvBuilder = new StringBuilder();
                csvBuilder.AppendLine("Фамилия;Имя;Отчество;Курс;Группа;Дата рождения;Электронная почта");

                foreach (var student in students)
                {
                    csvBuilder.AppendLine($"{student.LastName};{student.FirstName};{student.MiddleName};{student.Course};{student.Group};{student.BirthDate:dd.MM.yyyy};{student.Email}");
                }

                File.WriteAllText(saveFileDialog.FileName, csvBuilder.ToString(), Encoding.UTF8);
                MessageBox.Show("Данные успешно экспортированы!", "Успех", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        private void ImportFromCsv()
        {
            var openFileDialog = new OpenFileDialog
            {
                Filter = "CSV files (*.csv)|*.csv",
                Title = "Импортировать данные студентов"
            };

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                var csvLines = File.ReadAllLines(openFileDialog.FileName);
                students.Clear();

                foreach (var line in csvLines.Skip(1)) // Пропускаем заголовок
                {
                    var values = line.Split(',');
                    if (values.Length == 7)
                    {
                        students.Add(new Student
                        {
                            LastName = values[0],
                            FirstName = values[1],
                            MiddleName = values[2],
                            Course = int.Parse(values[3]),
                            Group = values[4],
                            BirthDate = DateTime.ParseExact(values[5], "dd.MM.yyyy", null),
                            Email = values[6]
                        });
                    }
                }

                UpdateStudentGrid();
                MessageBox.Show("Данные успешно импортированы!", "Успех", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        private void ImportFromJson()
        {
            var openFileDialog = new OpenFileDialog
            {
                Filter = "JSON files (*.json)|*.json",
                Title = "Импортировать данные студентов"
            };

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                var jsonData = File.ReadAllText(openFileDialog.FileName);
                students = JsonSerializer.Deserialize<List<Student>>(jsonData) ?? new List<Student>();
                UpdateStudentGrid();
                MessageBox.Show("Данные успешно импортированы!", "Успех", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        private void ButtonSave_Click(object sender, EventArgs e)
        {
            SaveDataToFile();
        }

        private void ButtonLoad_Click(object sender, EventArgs e)
        {
            LoadDataFromFile();
        }

        private void ButtonExport_Click(object sender, EventArgs e)
        {
            ExportToCsv();
        }

        private void ButtonImport_Click(object sender, EventArgs e)
        {
            ImportFromCsv();
        }

        private void ButtonSortByLastName_Click(object sender, EventArgs e)
        {
            students = students.OrderBy(student => student.LastName).ToList();
            UpdateStudentGrid();
        }

        private void ButtonSortByGroup_Click(object sender, EventArgs e)
        {
            students = students.OrderBy(student => student.Group).ToList();
            UpdateStudentGrid();
        }

        private void ButtonSortByCourse_Click(object sender, EventArgs e)
        {
            students = students.OrderBy(student => student.Course).ToList();
            UpdateStudentGrid();
        }

        private void InitializeSorting()
        {
            dataGridViewStudents.ColumnHeaderMouseClick += (sender, e) =>
            {
                if (e.ColumnIndex == 0) // Фамилия
                {
                    if (isLastNameAscending)
                    {
                        students = students.OrderBy(s => s.LastName).ToList();
                    }
                    else
                    {
                        students = students.OrderByDescending(s => s.LastName).ToList();
                    }
                    isLastNameAscending = !isLastNameAscending;
                }
                else if (e.ColumnIndex == 4) // Группа
                {
                    if (isGroupAscending)
                    {
                        students = students.OrderBy(s => s.Group).ToList();
                    }
                    else
                    {
                        students = students.OrderByDescending(s => s.Group).ToList();
                    }
                    isGroupAscending = !isGroupAscending;
                }
                else if (e.ColumnIndex == 3) // Курс
                {
                    if (isCourseAscending)
                    {
                        students = students.OrderBy(s => s.Course).ToList();
                    }
                    else
                    {
                        students = students.OrderByDescending(s => s.Course).ToList();
                    }
                    isCourseAscending = !isCourseAscending;
                }

                UpdateStudentGrid();
            };
        }

        private void ButtonSearch_Click(object? sender, EventArgs e)
        {
            SearchByLastName();
        }

        private void SearchByLastName()
        {
            string input = Microsoft.VisualBasic.Interaction.InputBox("Введите фамилию для поиска:", "Поиск по фамилии", "");
            var searchResults = students.Where(student => student.LastName.Equals(input, StringComparison.OrdinalIgnoreCase)).ToList();
            if (searchResults.Any())
            {
                dataGridViewStudents.DataSource = null;
                dataGridViewStudents.DataSource = searchResults;
            }
            else
            {
                MessageBox.Show("Студенты с такой фамилией не найдены.", "Результаты поиска", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        private void ButtonFilter_Click(object sender, EventArgs e)
        {
            string courseInput = Microsoft.VisualBasic.Interaction.InputBox("Введите курс для фильтрации:", "Фильтрация", "");
            string groupInput = Microsoft.VisualBasic.Interaction.InputBox("Введите группу для фильтрации:", "Фильтрация", "");

            if (int.TryParse(courseInput, out int course))
            {
                var filteredStudents = students.Where(student => student.Course == course && student.Group.Equals(groupInput, StringComparison.OrdinalIgnoreCase)).ToList();
                dataGridViewStudents.DataSource = null;
                dataGridViewStudents.DataSource = filteredStudents;
            }
            else
            {
                MessageBox.Show("Введите корректное значение для курса.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void ButtonResetFilter_Click(object sender, EventArgs e)
        {
            dataGridViewStudents.DataSource = null;
            dataGridViewStudents.DataSource = students;
        }

        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            if (isDataModified)
            {
                var result = MessageBox.Show("Данные были изменены. Хотите сохранить изменения перед выходом?", "Сохранение данных", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Warning);
                if (result == DialogResult.Yes)
                {
                    SaveDataToFile();
                }
                else if (result == DialogResult.Cancel)
                {
                    e.Cancel = true;
                }
            }
            base.OnFormClosing(e);
        }
    }

    public class Student
    {
        public string LastName { get; set; } = string.Empty;
        public string FirstName { get; set; } = string.Empty;
        public string MiddleName { get; set; } = string.Empty;
        public int Course { get; set; }
        public string Group { get; set; } = string.Empty;
        public DateTime BirthDate { get; set; }
        public string Email { get; set; } = string.Empty;
    }
}
