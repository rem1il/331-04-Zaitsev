namespace project8;

partial class Form1
{
    /// <summary>
    ///  Required designer variable.
    /// </summary>
    private System.ComponentModel.IContainer components = null;

    private System.Windows.Forms.TextBox textBoxLastName;
    private System.Windows.Forms.TextBox textBoxFirstName;
    private System.Windows.Forms.TextBox textBoxMiddleName;
    private System.Windows.Forms.TextBox textBoxCourse;
    private System.Windows.Forms.TextBox textBoxGroup;
    private System.Windows.Forms.DateTimePicker dateTimePickerBirthDate;
    private System.Windows.Forms.TextBox textBoxEmail;
    private System.Windows.Forms.Button buttonAdd;
    private System.Windows.Forms.Button buttonEdit;
    private System.Windows.Forms.Button buttonDelete;
    private System.Windows.Forms.DataGridView dataGridViewStudents;
    private System.Windows.Forms.Button buttonSave;
    private System.Windows.Forms.Button buttonLoad;
    private System.Windows.Forms.Button buttonExport;
    private System.Windows.Forms.Button buttonImport;
    private System.Windows.Forms.Button buttonSearch;
    private System.Windows.Forms.Button buttonFilter;
    private System.Windows.Forms.Button buttonResetFilter;

    /// <summary>
    ///  Clean up any resources being used.
    /// </summary>
    /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
    protected override void Dispose(bool disposing)
    {
        if (disposing && (components != null))
        {
            components.Dispose();
        }
        base.Dispose(disposing);
    }

    #region Windows Form Designer generated code

    /// <summary>
    ///  Required method for Designer support - do not modify
    ///  the contents of this method with the code editor.
    /// </summary>
    private void InitializeComponent()
    {
        this.textBoxLastName = new System.Windows.Forms.TextBox();
        this.textBoxFirstName = new System.Windows.Forms.TextBox();
        this.textBoxMiddleName = new System.Windows.Forms.TextBox();
        this.textBoxCourse = new System.Windows.Forms.TextBox();
        this.textBoxGroup = new System.Windows.Forms.TextBox();
        this.dateTimePickerBirthDate = new System.Windows.Forms.DateTimePicker();
        this.textBoxEmail = new System.Windows.Forms.TextBox();
        this.buttonAdd = new System.Windows.Forms.Button();
        this.buttonEdit = new System.Windows.Forms.Button();
        this.buttonDelete = new System.Windows.Forms.Button();
        this.dataGridViewStudents = new System.Windows.Forms.DataGridView();
        this.buttonSave = new System.Windows.Forms.Button();
        this.buttonLoad = new System.Windows.Forms.Button();
        this.buttonExport = new System.Windows.Forms.Button();
        this.buttonImport = new System.Windows.Forms.Button();
        this.buttonSearch = new System.Windows.Forms.Button();
        this.buttonFilter = new System.Windows.Forms.Button();
        this.buttonResetFilter = new System.Windows.Forms.Button();
        ((System.ComponentModel.ISupportInitialize)(this.dataGridViewStudents)).BeginInit();
        this.SuspendLayout();

        // textBoxLastName
        this.textBoxLastName.Location = new System.Drawing.Point(12, 12);
        this.textBoxLastName.Name = "textBoxLastName";
        this.textBoxLastName.Size = new System.Drawing.Size(200, 23);
        this.textBoxLastName.TabIndex = 0;
        this.textBoxLastName.PlaceholderText = "Фамилия";

        // textBoxFirstName
        this.textBoxFirstName.Location = new System.Drawing.Point(12, 41);
        this.textBoxFirstName.Name = "textBoxFirstName";
        this.textBoxFirstName.Size = new System.Drawing.Size(200, 23);
        this.textBoxFirstName.TabIndex = 1;
        this.textBoxFirstName.PlaceholderText = "Имя";

        // textBoxMiddleName
        this.textBoxMiddleName.Location = new System.Drawing.Point(12, 70);
        this.textBoxMiddleName.Name = "textBoxMiddleName";
        this.textBoxMiddleName.Size = new System.Drawing.Size(200, 23);
        this.textBoxMiddleName.TabIndex = 2;
        this.textBoxMiddleName.PlaceholderText = "Отчество";

        // textBoxCourse
        this.textBoxCourse.Location = new System.Drawing.Point(12, 99);
        this.textBoxCourse.Name = "textBoxCourse";
        this.textBoxCourse.Size = new System.Drawing.Size(200, 23);
        this.textBoxCourse.TabIndex = 3;
        this.textBoxCourse.PlaceholderText = "Курс";

        // textBoxGroup
        this.textBoxGroup.Location = new System.Drawing.Point(12, 128);
        this.textBoxGroup.Name = "textBoxGroup";
        this.textBoxGroup.Size = new System.Drawing.Size(200, 23);
        this.textBoxGroup.TabIndex = 4;
        this.textBoxGroup.PlaceholderText = "Группа";

        // dateTimePickerBirthDate
        this.dateTimePickerBirthDate.Location = new System.Drawing.Point(12, 157);
        this.dateTimePickerBirthDate.Name = "dateTimePickerBirthDate";
        this.dateTimePickerBirthDate.Size = new System.Drawing.Size(200, 23);
        this.dateTimePickerBirthDate.TabIndex = 5;

        // textBoxEmail
        this.textBoxEmail.Location = new System.Drawing.Point(12, 186);
        this.textBoxEmail.Name = "textBoxEmail";
        this.textBoxEmail.Size = new System.Drawing.Size(200, 23);
        this.textBoxEmail.TabIndex = 6;
        this.textBoxEmail.PlaceholderText = "Электронная почта";

        // buttonAdd
        this.buttonAdd.Location = new System.Drawing.Point(12, 215);
        this.buttonAdd.Name = "buttonAdd";
        this.buttonAdd.Size = new System.Drawing.Size(75, 23);
        this.buttonAdd.TabIndex = 7;
        this.buttonAdd.Text = "Добавить";
        this.buttonAdd.UseVisualStyleBackColor = true;

        // buttonEdit
        this.buttonEdit.Location = new System.Drawing.Point(93, 215);
        this.buttonEdit.Name = "buttonEdit";
        this.buttonEdit.Size = new System.Drawing.Size(75, 23);
        this.buttonEdit.TabIndex = 8;
        this.buttonEdit.Text = "Изменить";
        this.buttonEdit.UseVisualStyleBackColor = true;

        // buttonDelete
        this.buttonDelete.Location = new System.Drawing.Point(174, 215);
        this.buttonDelete.Name = "buttonDelete";
        this.buttonDelete.Size = new System.Drawing.Size(75, 23);
        this.buttonDelete.TabIndex = 9;
        this.buttonDelete.Text = "Удалить";
        this.buttonDelete.UseVisualStyleBackColor = true;

        // buttonSave
        this.buttonSave.Location = new System.Drawing.Point(255, 215);
        this.buttonSave.Name = "buttonSave";
        this.buttonSave.Size = new System.Drawing.Size(75, 23);
        this.buttonSave.TabIndex = 11;
        this.buttonSave.Text = "Сохранить";
        this.buttonSave.UseVisualStyleBackColor = true;
        this.buttonSave.Click += new System.EventHandler(this.ButtonSave_Click);

        // buttonLoad
        this.buttonLoad.Location = new System.Drawing.Point(336, 215);
        this.buttonLoad.Name = "buttonLoad";
        this.buttonLoad.Size = new System.Drawing.Size(75, 23);
        this.buttonLoad.TabIndex = 12;
        this.buttonLoad.Text = "Загрузить";
        this.buttonLoad.UseVisualStyleBackColor = true;
        this.buttonLoad.Click += new System.EventHandler(this.ButtonLoad_Click);

        // buttonExport
        this.buttonExport.Location = new System.Drawing.Point(417, 215);
        this.buttonExport.Name = "buttonExport";
        this.buttonExport.Size = new System.Drawing.Size(75, 23);
        this.buttonExport.TabIndex = 13;
        this.buttonExport.Text = "Экспорт";
        this.buttonExport.UseVisualStyleBackColor = true;
        this.buttonExport.Click += new System.EventHandler(this.ButtonExport_Click);

        // buttonImport
        this.buttonImport.Location = new System.Drawing.Point(498, 215);
        this.buttonImport.Name = "buttonImport";
        this.buttonImport.Size = new System.Drawing.Size(75, 23);
        this.buttonImport.TabIndex = 14;
        this.buttonImport.Text = "Импорт";
        this.buttonImport.UseVisualStyleBackColor = true;
        this.buttonImport.Click += new System.EventHandler(this.ButtonImport_Click);

        // buttonSearch
        this.buttonSearch.Location = new System.Drawing.Point(579, 215);
        this.buttonSearch.Name = "buttonSearch";
        this.buttonSearch.Size = new System.Drawing.Size(75, 23);
        this.buttonSearch.TabIndex = 15;
        this.buttonSearch.Text = "Поиск";
        this.buttonSearch.UseVisualStyleBackColor = true;
        this.buttonSearch.Click += new System.EventHandler(this.ButtonSearch_Click);

        // buttonFilter
        this.buttonFilter.Location = new System.Drawing.Point(660, 215);
        this.buttonFilter.Name = "buttonFilter";
        this.buttonFilter.Size = new System.Drawing.Size(75, 23);
        this.buttonFilter.TabIndex = 16;
        this.buttonFilter.Text = "Фильтр";
        this.buttonFilter.UseVisualStyleBackColor = true;
        this.buttonFilter.Click += new System.EventHandler(this.ButtonFilter_Click);

        // buttonResetFilter
        this.buttonResetFilter.Location = new System.Drawing.Point(12, 450);
        this.buttonResetFilter.Size = new System.Drawing.Size(150, 30);
        this.buttonResetFilter.Text = "Сбросить фильтр";
        this.buttonResetFilter.UseVisualStyleBackColor = true;
        this.buttonResetFilter.Click += new System.EventHandler(this.ButtonResetFilter_Click);

        // dataGridViewStudents
        this.dataGridViewStudents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
        this.dataGridViewStudents.Location = new System.Drawing.Point(12, 244);
        this.dataGridViewStudents.Name = "dataGridViewStudents";
        this.dataGridViewStudents.RowTemplate.Height = 25;
        this.dataGridViewStudents.Size = new System.Drawing.Size(760, 194);
        this.dataGridViewStudents.TabIndex = 10;

        // Form1
        this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
        this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
        this.ClientSize = new System.Drawing.Size(784, 485);
        this.Controls.Add(this.dataGridViewStudents);
        this.Controls.Add(this.buttonResetFilter);
        this.Controls.Add(this.buttonFilter);
        this.Controls.Add(this.buttonSearch);
        this.Controls.Add(this.buttonImport);
        this.Controls.Add(this.buttonExport);
        this.Controls.Add(this.buttonLoad);
        this.Controls.Add(this.buttonSave);
        this.Controls.Add(this.buttonDelete);
        this.Controls.Add(this.buttonEdit);
        this.Controls.Add(this.buttonAdd);
        this.Controls.Add(this.textBoxEmail);
        this.Controls.Add(this.dateTimePickerBirthDate);
        this.Controls.Add(this.textBoxGroup);
        this.Controls.Add(this.textBoxCourse);
        this.Controls.Add(this.textBoxMiddleName);
        this.Controls.Add(this.textBoxFirstName);
        this.Controls.Add(this.textBoxLastName);
        this.Name = "Form1";
        this.Text = "Студенты";
        ((System.ComponentModel.ISupportInitialize)(this.dataGridViewStudents)).EndInit();
        this.ResumeLayout(false);
        this.PerformLayout();
    }

    #endregion
}
