<?php
session_start();
require_once('db.php');

if (!isset($_SESSION['user_id'])) {
    header("Location: form/login.php");
    exit;
}

// Получаем данные пользователя
$stmt = $conn->prepare("SELECT username, created_at FROM users WHERE id = ?");
$stmt->execute([$_SESSION['user_id']]);
$user = $stmt->fetch();
?>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Профиль пользователя</title>
</head>
<body>
    <h1>Профиль пользователя</h1>
    <p>Имя пользователя: <?= htmlspecialchars($user['username']) ?></p>
    <p>Дата регистрации: <?= $user['created_at'] ?></p>
    <a href="logout.php">Выйти</a>
</body>
</html>
