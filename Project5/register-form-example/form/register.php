<?php
require_once('../db.php');
session_start();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username'] ?? '');
    $password = $_POST['password'] ?? '';
    $agreement = isset($_POST['agreement']);
    
    $errors = [];
    
    // Валидация имени  
    if (empty($username)) {
        $errors[] = "Имя пользователя обязательно";
    } elseif (!preg_match('/^[a-zA-Z0-9]{2,32}$/', $username)) {
        $errors[] = "Имя пользователя должно содержать только латинские буквы и цифры (2-32 символа)";
    }
    
    // Проверка уникальности
    $stmt = $conn->prepare("SELECT COUNT(*) FROM users WHERE username = ?");
    $stmt->execute([$username]);
    if ($stmt->fetchColumn() > 0) {
        $errors[] = "Это имя пользователя уже занято";
    }
    
    // Валидация пароля
    if (empty($password)) {
        $errors[] = "Пароль обязателен";
    } elseif (!preg_match('/^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{4,16}$/', $password)) {
        $errors[] = "Пароль должен содержать от 4 до 16 символов, включая минимум одну заглавную букву, цифру и спецсимвол";
    }
    
    if (!$agreement) {
        $errors[] = "Необходимо принять пользовательское соглашение";
    }
    
    if (empty($errors)) {
        $hashed_password = password_hash($password, PASSWORD_BCRYPT);
        try {
            $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
            $stmt->execute([$username, $hashed_password]);
            $_SESSION['success'] = "Регистрация успешно завершена!";
            header("Location: login.php");
            exit;
        } catch(PDOException $e) {
            $errors[] = "Ошибка при регистрации";
        }
    }
}
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация пользователя</title>
    <style>
        .error { color: red; }
        .hint { color: gray; font-size: 0.8em; }
    </style>
</head>
<body>
    <h2>Регистрация нового пользователя</h2>
    
    <?php if (!empty($errors)): ?>
        <div class="error">
            <?php foreach($errors as $error): ?>
                <p><?= htmlspecialchars($error) ?></p>
            <?php endforeach; ?>
        </div>
    <?php endif; ?>
    
    <form method="post">
        <div>
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" 
                   value="<?= htmlspecialchars($username ?? '') ?>" required>
            <span class="hint">Только латинские буквы и цифры (от 2 до 32 символов)</span>
        </div>
        <div>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
            <button type="button" onclick="togglePassword()">👁</button>
            <span class="hint">От 4 до 16 символов, минимум одна заглавная буква, цифра и специальный символ</span>
        </div>
        <div>
            <input type="checkbox" id="agreement" name="agreement" required>
            <label for="agreement">Я принимаю <a href="agreement.php" target="_blank">пользовательское соглашение</a></label>
        </div>
        <button type="submit">Зарегистрироваться</button>
    </form>
    <p><a href="login.php">Уже зарегистрированы? Войти</a></p>

    <script>
    function togglePassword() {
        const pwd = document.getElementById("password");
        pwd.type = pwd.type === "password" ? "text" : "password";
    }
    </script>
</body>
</html>
