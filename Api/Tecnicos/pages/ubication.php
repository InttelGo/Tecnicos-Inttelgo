<?php
if (isset($_POST["id_usuario"]) && isset($_POST["altitud"]) && isset($_POST["latitud"])) {
    $idUsuario = $_POST["id_usuario"];
    $altitud = $_POST["altitud"];
    $latitud = $_POST["latitud"];
    $color = $_POST["color"];

    // Ruta del archivo donde se almacenará la información
    $filePath = "users/usuarios.json";

    // Leer el contenido actual del archivo
    $usuarios = file_exists($filePath) ? json_decode(file_get_contents($filePath), true) : [];

    // Buscar si el usuario ya existe
    $usuarioEncontrado = false;
    foreach ($usuarios as &$usuario) {
        if ($usuario["id_usuario"] === $idUsuario) {
            // Actualizar la información del usuario existente
            $usuario["altitud"] = $altitud;
            $usuario["latitud"] = $latitud;
            $usuario["color"] = $color;
            $usuarioEncontrado = true;
            break;
        }
    }

    // Si el usuario no existe, agregarlo al array
    if (!$usuarioEncontrado) {
        $usuarios[] = [
            "id_usuario" => $idUsuario,
            "color" => $color,
            "altitud" => $altitud,
            "latitud" => $latitud
        ];
    }

    // Guardar de nuevo en el archivo
    file_put_contents($filePath, json_encode($usuarios));

    echo $usuarioEncontrado ? "Usuario actualizado correctamente." : "Usuario agregado correctamente.";
} else {
    echo "Faltan datos: id_usuario, altitud o latitud no fueron proporcionados.";
}

?>

