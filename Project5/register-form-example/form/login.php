<?php
session_start();
require_once('../db.php');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username'] ?? '');
    $password = $_POST['password'] ?? '';
    $remember = isset($_POST['remember']);
    
    $stmt = $conn->prepare("SELECT id, password FROM users WHERE username = ?");
    $stmt->execute([$username]);
    $user = $stmt->fetch();
    
    if ($user && password_verify($password, $user['password'])) {
        $_SESSION['user_id'] = $user['id'];
        
        if ($remember) {
            $token = bin2hex(random_bytes(50));
            $stmt = $conn->prepare("UPDATE users SET remember_token = ? WHERE id = ?");
            $stmt->execute([$token, $user['id']]);
            setcookie('remember_token', $token, time() + 30*24*60*60, '/');
        }
        
        header("Location: ../index.php");
        exit;
    } else {
        $error = "Неверное имя пользователя или пароль";
    }
}
?>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вход в систему</title>
    <style>
        .error { color: red; }
    </style>
</head>
<body>
    <h2>Вход в систему</h2>
    
    <?php if (isset($error)): ?>
        <div class="error"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>
    
    <form method="post">
        <div>
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
            <button type="button" onclick="togglePassword()">👁</button>
        </div>
        <div>
            <input type="checkbox" id="remember" name="remember">
            <label for="remember">Запомнить меня</label>
        </div>
        <button type="submit">Войти</button>
    </form>
    <p><a href="register.php">Нет аккаунта? Зарегистрироваться</a></p>

    <script>
    function togglePassword() {
        const pwd = document.getElementById("password");
        const btn = document.querySelector('.toggle-password');
        if (pwd.type === "password") {
            pwd.type = "text";
            btn.setAttribute('aria-label', 'Скрыть пароль');
        } else {
            pwd.type = "password";
            btn.setAttribute('aria-label', 'Показать пароль');
        }
        pwd.focus();
    }
    </script>
</body>
</html>
